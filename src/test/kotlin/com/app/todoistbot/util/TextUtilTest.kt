package com.app.todoistbot.util

import com.app.todoistbot.util.dto.Task
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

class TextUtilTest {

    @Test
    fun `happy path`() {
        val text = """
            Название задачи
                1. Заголовок
                    1.1. Подзаголовок
                        1.1.1 Подзаголовок подзаголовка
                        1.1.2 Подзаголовок подзаголовка
                    1.2. Подзаголовок
                        1.2.1 Подзаголовок подзаголовка
                        1.2.2 Подзаголовок подзаголовка
                2. Заголовок
                    2.1. Подзаголовок
                        2.1.1 Подзаголовок подзаголовка
                        2.1.2 Подзаголовок подзаголовка
                    2.2. Подзаголовок
                        2.2.1 Подзаголовок подзаголовка
                        2.2.2 Подзаголовок подзаголовка
        """.trimIndent()

        val firstParentTask = Task(
            title = "1. Заголовок",
            children = mutableListOf(
                Task(
                    title = "1.1. Подзаголовок",
                    children = mutableListOf(
                        Task("1.1.1 Подзаголовок подзаголовка", mutableListOf()),
                        Task("1.1.2 Подзаголовок подзаголовка", mutableListOf())
                    )
                ),
                Task(
                    title = "1.2. Подзаголовок",
                    children = mutableListOf(
                        Task("1.2.1 Подзаголовок подзаголовка", mutableListOf()),
                        Task("1.2.2 Подзаголовок подзаголовка", mutableListOf())
                    )
                ),
            )
        )

        val secondParentTask = Task(
            title = "2. Заголовок",
            children = mutableListOf(
                Task(
                    title = "2.1. Подзаголовок",
                    children = mutableListOf(
                        Task("2.1.1 Подзаголовок подзаголовка", mutableListOf()),
                        Task("2.1.2 Подзаголовок подзаголовка", mutableListOf())
                    )
                ),
                Task(
                    title = "2.2. Подзаголовок",
                    children = mutableListOf(
                        Task("2.2.1 Подзаголовок подзаголовка", mutableListOf()),
                        Task("2.2.2 Подзаголовок подзаголовка", mutableListOf())
                    )
                ),
            )
        )

        val expected = Task("Название задачи", mutableListOf(firstParentTask, secondParentTask))

        Assertions.assertEquals(expected, parseTextToTasks(text))
    }

    @Test
    fun `when line is short after spaces`() {
        val text = """
    Руководство создания приложений под Android
        Глава 3. Контейнеры компоновки
            Box
        """.trimIndent()

        val task = Task(
            title = "Глава 3. Контейнеры компоновки",
            children = mutableListOf(
                Task(
                    title = "Box",
                    children = mutableListOf()
                ),
            )

        )
        val expected = Task("Руководство создания приложений под Android", mutableListOf(task))
        Assertions.assertEquals(expected, parseTextToTasks(text))
    }
}