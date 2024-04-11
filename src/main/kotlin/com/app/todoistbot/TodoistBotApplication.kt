package com.app.todoistbot

import com.app.todoistbot.telegram.TelegramConfig
import com.app.todoistbot.todoist.TodoistConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(TodoistConfig::class, TelegramConfig::class)
class TodoistBotApplication

fun main(args: Array<String>) {
    runApplication<TodoistBotApplication>(*args)
}
