package com.app.todoistbot.telegram

import com.app.todoistbot.todoist.service.TodoistService
import com.app.todoistbot.util.parseTextToTasks
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.message
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.extensions.filters.Filter
import com.github.kotlintelegrambot.logging.LogLevel
import org.springframework.stereotype.Service

@Service
class TelegramPolling(private val telegramConfig: TelegramConfig, private val todoistService: TodoistService) {

    val bot = bot {
        token = telegramConfig.apiKey
        dispatch {
            message(Filter.User(userId = telegramConfig.userId).and(Filter.Text)) {
                val task = parseTextToTasks(message.text!!)
                todoistService.createTasks(task)
                bot.sendMessage(ChatId.fromId(message.chat.id), text = "Задача ${task.title} добавлена!")
            }
        }
        logLevel = LogLevel.All()
    }.startPolling()
}