package com.app.todoistbot.telegram.validator

import com.app.todoistbot.SpringBaseTest
import com.app.todoistbot.telegram.validator.TextDataValidator.Companion.IS_NOT_BLANK_OR_EMPTY_ERROR_MESSAGE
import com.app.todoistbot.telegram.validator.TextDataValidator.Companion.MAX_CHILD_LEVEL_IS_FIVE_ERROR_MESSAGE
import com.app.todoistbot.telegram.validator.TextDataValidator.Companion.ONLY_ONE_PARENT_ERROR_MESSAGE
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class TextDataValidatorTest : SpringBaseTest() {

    @Autowired
    lateinit var textDataValidator: TextDataValidator

    /**
     * Проверяем, что если проходит валидация, то нет ошибок
     *
     * Given: Дерево задач в текстовом виде
     * When: Проверяем задачу
     * Then: Убеждаемся, что нет ошибок
     */
    @Test
    fun happyPath() {
        // --- Given / When ---
        val actual = textDataValidator.validate(
            data = """
            Parent
                First Child
                    Second Child
                        Third Child
                            Fourth Child
        """.trimIndent(),
        )

        // --- Then ---
        val response = assertDoesNotThrow { actual.getOrThrow() }
        assertThat(actual.getOrNull()).isEqualTo(response)
    }

    /**
     * Проверяем, что если сообщение null, то получаем ошибку
     *
     * Given: Дерево задач null
     * When: Проверяем задачу
     * Then: Убеждаемся, что вернулась ошибка
     */
    @Test
    fun messageIsNullTest() {
        // --- Given / When ---
        val actual = textDataValidator.validate(data = null)

        // --- Then ---
        val exception = assertThrows<TextDataException> { actual.getOrThrow() }
        assertThat(exception.message).isEqualTo(IS_NOT_BLANK_OR_EMPTY_ERROR_MESSAGE)
    }

    /**
     * Проверяем, что если сообщение пустое, то получаем ошибку
     *
     * Given: Дерево задач пустое
     * When: Проверяем задачу
     * Then: Убеждаемся, что вернулась ошибка
     */
    @Test
    fun messageIsEmptyTest() {
        // --- Given / When ---
        val actual = textDataValidator.validate(data = "")

        // --- Then ---
        val exception = assertThrows<TextDataException> { actual.getOrThrow() }
        assertThat(exception.message).isEqualTo(IS_NOT_BLANK_OR_EMPTY_ERROR_MESSAGE)
    }

    /**
     * Проверяем, что если сообщение содержит только пробел, то получаем ошибку
     *
     * Given: Дерево задач из пробела
     * When: Проверяем задачу
     * Then: Убеждаемся, что вернулась ошибка
     */
    @Test
    fun messageIsBlankTest() {
        // --- Given / When ---
        val actual = textDataValidator.validate(data = " ")

        // --- Then ---
        val exception = assertThrows<TextDataException> { actual.getOrThrow() }
        assertThat(exception.message).isEqualTo(IS_NOT_BLANK_OR_EMPTY_ERROR_MESSAGE)
    }

    /**
     * Проверяем, что если сообщение содержит две родительские задачи, то получаем ошибку
     *
     * Given: Дерево задач с двумя родительскими задачами
     * When: Проверяем задачу
     * Then: Убеждаемся, что вернулась ошибка
     */
    @Test
    fun onlyOneParentTest() {
        // --- Given / When ---
        val actual = textDataValidator.validate(
            data = """
            First Parent
                First Child
            Second Parent
                Second Child
        """.trimIndent()
        )

        // --- Then ---
        val exception = assertThrows<TextDataException> { actual.getOrThrow() }
        assertThat(exception.message).isEqualTo(ONLY_ONE_PARENT_ERROR_MESSAGE)
    }

    /**
     * Проверяем, что если сообщение содержит дочернюю задачу уровнем 5 и выше, то возвращаем ошибку
     *
     * Given: Дерево задач с дочерней задачей уровнем 5 и выше
     * When: Проверяем задачу
     * Then: Убеждаемся, что вернулась ошибка
     */
    @Test
    fun maxChildLevelTest() {
        // --- Given / When ---
        val actual = textDataValidator.validate(
            data = """
            Parent
                First Child
                    Second Child
                        Third Child
                            Fourth Child
                                Fifth Child
        """.trimIndent(),
        )

        // --- Then ---
        val exception = assertThrows<TextDataException> { actual.getOrThrow() }
        assertThat(exception.message).isEqualTo(MAX_CHILD_LEVEL_IS_FIVE_ERROR_MESSAGE)
    }
}