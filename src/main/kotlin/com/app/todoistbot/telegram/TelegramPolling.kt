package com.app.todoistbot.telegram

import com.app.todoistbot.telegram.validator.TextDataValidator
import com.app.todoistbot.todoist.TodoistClientApi
import com.app.todoistbot.todoist.dto.TaskRequest
import com.app.todoistbot.todoist.service.TodoistService
import com.app.todoistbot.util.parseTextToTasks
import com.app.todoistbot.youtube.service.YoutubeService
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.handlers.MessageHandlerEnvironment
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.ParseMode
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import java.net.URI
import org.springframework.stereotype.Service

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
            val youTubeFilter = Filter.Custom { runCatching { URI(this.text!!) }.isSuccess }

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
                handleTextData(this, textData)
            }

            message(userFilter.and(Filter.Text)) {
                val textData = textDataValidator.validate(message.text!!)
                handleTextData(this, textData)
            }

        }
        logLevel = LogLevel.All()
    }.startPolling()


    /**
     * Создаёт ссылку в формате Markdown
     */
    private fun createTextLink(text: String, url: String) = "[$text]($url)"

    private fun handleTextData(messageHandlerEnvironment: MessageHandlerEnvironment, textData: Result<String>) {
        textData.onSuccess {
            val task = parseTextToTasks(textData.getOrNull()!!)
            val responseTask = todoistService.createTasks(task, labels = setOf())
            messageHandlerEnvironment.bot.sendMessage(
                parseMode = ParseMode.MARKDOWN,
                chatId = ChatId.fromId(messageHandlerEnvironment.message.chat.id),
                text = "Задача ${createTextLink(task.title, responseTask.url)} добавлена!",
                disableWebPagePreview = true,
            )
            messageHandlerEnvironment.update.consume()
        }.onFailure {
            messageHandlerEnvironment.bot.sendMessage(
                parseMode = ParseMode.MARKDOWN,
                chatId = ChatId.fromId(messageHandlerEnvironment.message.chat.id),
                text = "${it.message}",
                disableWebPagePreview = true,
            )
            messageHandlerEnvironment.update.consume()
        }
    }
}