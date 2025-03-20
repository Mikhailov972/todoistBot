package com.app.todoistbot.telegram

import com.app.todoistbot.telegram.validator.TextDataValidator
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
import org.springframework.stereotype.Service
import java.net.URL

@Service
class TelegramPolling(
    private val telegramConfig: TelegramConfig,
    private val todoistService: TodoistService,
    private val youtubeService: YoutubeService,
    private val todoistClientApi: TodoistClientApi,
    private val textDataValidator: TextDataValidator
) {

    val bot = bot {
        token = telegramConfig.apiKey

        dispatch {
            val userFilter = Filter.User(userId = telegramConfig.userId)
            val documentIsNotNullFilter = Filter.Custom { this.document != null }
            val youTubeFilter = Filter.Custom { runCatching { URL(this.text) }.isSuccess }

            message(userFilter.and(youTubeFilter)) {
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

            message(userFilter.and(documentIsNotNullFilter)) {
                val fileId = this.message.document!!.fileId
                val file = this.bot.downloadFileBytes(fileId)!!.toString(Charsets.UTF_8)

                val textData = textDataValidator.validate(file)

                textData.onSuccess {
                    val task = parseTextToTasks(textData.getOrNull()!!)
                    val responseTask = todoistService.createTasks(task, labels = setOf())
                    bot.sendMessage(
                        parseMode = ParseMode.MARKDOWN,
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Задача ${createTextLink(task.title, responseTask.url)} добавлена!",
                        disableWebPagePreview = true,
                    )
                    update.consume()
                }.onFailure {
                    bot.sendMessage(
                        parseMode = ParseMode.MARKDOWN,
                        chatId = ChatId.fromId(message.chat.id),
                        text = "${it.message}",
                        disableWebPagePreview = true,
                    )
                    update.consume()
                }
            }

            message(userFilter.and(Filter.Text)) {
                val textData = textDataValidator.validate(message.text!!)

                textData.onSuccess {
                    val task = parseTextToTasks(textData.getOrNull()!!)
                    val responseTask = todoistService.createTasks(task, labels = setOf())
                    bot.sendMessage(
                        parseMode = ParseMode.MARKDOWN,
                        chatId = ChatId.fromId(message.chat.id),
                        text = "Задача ${createTextLink(task.title, responseTask.url)} добавлена!",
                        disableWebPagePreview = true,
                    )
                    update.consume()
                }.onFailure {
                    bot.sendMessage(
                        parseMode = ParseMode.MARKDOWN,
                        chatId = ChatId.fromId(message.chat.id),
                        text = "${it.message}",
                        disableWebPagePreview = true,
                    )
                    update.consume()
                }
            }

        }
        logLevel = LogLevel.All()
    }.startPolling()


    /**
     * Создаёт ссылку в формате Markdown
     */
    private fun createTextLink(text: String, url: String) = "[$text]($url)"
}