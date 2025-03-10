package com.app.todoistbot.util

import com.app.todoistbot.util.dto.Task


/**
 * Парсит текст в задачи, ориентируясь на отступы (подзадача)
 * @param text Текст с вложенными задачами
 * @return Корневая задача с подзадачами
 */
fun parseTextToTasks(text: String): Task {

    val taskTree = mutableMapOf<Int, Task>()

    text.trim().split("\n").forEach {

        val currentCountSpaces = it.takeWhile { s -> s == ' ' }.length.div(4)

        taskTree[currentCountSpaces] = Task(it.trim(), mutableListOf())

        if (currentCountSpaces > 0) {
            taskTree.getValue(currentCountSpaces - 1).children.add(taskTree.getValue(currentCountSpaces))
        }
    }

    return taskTree.getValue(0)
}