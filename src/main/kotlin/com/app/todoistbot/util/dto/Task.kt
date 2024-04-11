package com.app.todoistbot.util.dto

data class Task(
    val title: String,
    val children: MutableList<Task>
)