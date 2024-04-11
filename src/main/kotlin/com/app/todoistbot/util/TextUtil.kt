package com.app.todoistbot.util

import com.app.todoistbot.util.dto.Task


/**
 * Парсит текст в задачи, ориентируясь на отступы (новая задача) и табы (подзадача)
 * @param text Текст с вложенными задачами
 * @return Корневая задача с подзадачами
 */
fun parseTextToTasks(text: String): Task {

    val tasks = mutableListOf<Task>()

    val taskTree = mutableMapOf<Int, Task>()

    text.split("\n").forEach {
        val currentCountSpaces = countTabs(it)
        if (currentCountSpaces == 0) {
            taskTree.clear()
            val task = Task(it.trim(), mutableListOf())
            taskTree[currentCountSpaces] = task
            tasks.add(task)
        } else {
            val task = Task(it.trim(), mutableListOf())
            taskTree[currentCountSpaces] = task
            taskTree[currentCountSpaces - 1]!!.children.add(task)
        }
    }

    return tasks.first()
}

/**
 * Считает количество табов в строке
 * @param line строка
 * @return кол-во табов
 */
private fun countTabs(line: String): Int {
    var startTab = 0
    var finishTab = 3
    val tab = "    "

    var count = 0
    while (line.substring(IntRange(startTab, finishTab)) == tab) {
        startTab += 4
        finishTab += 4
        count += 1
    }

    return count
}