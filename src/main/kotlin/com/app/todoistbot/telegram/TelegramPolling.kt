package com.app.todoistbot.telegram

import com.app.todoistbot.todoist.TodoistClientApi
import com.app.todoistbot.todoist.dto.TaskRequest
import com.app.todoistbot.todoist.service.TodoistService
import com.app.todoistbot.util.parseTextToTasks
import com.app.todoistbot.youtube.service.YoutubeService
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import java.net.URL
import org.springframework.stereotype.Service

@Service
class TelegramPolling(
    private val telegramConfig: TelegramConfig,
    private val todoistService: TodoistService,
    private val youtubeService: YoutubeService,
    private val todoistClientApi: TodoistClientApi
) {

    val bot = bot {
        token = telegramConfig.apiKey
        dispatch {
            val baseFilter = Filter.User(userId = telegramConfig.userId).and(Filter.Text)
            val youTubeFilter = Filter.Custom { runCatching { URL(this.text) }.isSuccess }

            message(baseFilter.and(youTubeFilter)) {
                val playlistId = message.text!!.substringAfter("=").substringBefore("&")
                val playlistDto = youtubeService.getPlaylist(playlistId)

                val parentId = todoistClientApi.createTask(
                    TaskRequest(
                        "[${playlistDto.title}](${playlistDto.url})",
                        null,
                        setOf("YouTube")
                    )
                ).getOrThrow()

                playlistDto.items.forEach {
                    todoistClientApi.createTask(TaskRequest("[${it.title}](${it.url})", parentId.id))
                }
                bot.sendMessage(
                    parseMode = ParseMode.MARKDOWN,
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Плейлист ${createTextLink(playlistDto.title, parentId.url)} добавлен!",
                    disableWebPagePreview = true,

                )
                update.consume()
            }
            message(baseFilter) {
                val task = parseTextToTasks(message.text!!)
                val responseTask = todoistService.createTasks(task, labels = setOf())
                bot.sendMessage(
                    parseMode = ParseMode.MARKDOWN,
                    chatId = ChatId.fromId(message.chat.id),
                    text = "Задача ${createTextLink(task.title, responseTask.url)} добавлена!",
                    disableWebPagePreview = true,
                )
                update.consume()
            }

        }
        logLevel = LogLevel.All()
    }.startPolling()

    /**
     * Создаёт ссылку в формате Markdown
     */
    private fun createTextLink(text: String, url: String) = "[$text]($url)"
}