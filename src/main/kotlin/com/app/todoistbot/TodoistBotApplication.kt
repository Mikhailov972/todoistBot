package com.app.todoistbot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TodoistBotApplication

fun main(args: Array<String>) {
    runApplication<TodoistBotApplication>(*args)
}
