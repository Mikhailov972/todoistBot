package com.app.todoistbot.telegram.validator

import org.springframework.stereotype.Component

@Component
class TextDataValidator {

    companion object {
        const val IS_NOT_BLANK_OR_EMPTY_ERROR_MESSAGE = "Ошибка: сообщение не может быть пустым!"
        const val ONLY_ONE_PARENT_ERROR_MESSAGE = "Ошибка: может быть только одна родительская задача!"
        const val MAX_CHILD_LEVEL_IS_FIVE_ERROR_MESSAGE = "Ошибка: максимальный уровень вложенности 4!"
    }

    fun validate(data: String?): Result<String> {
        return when {
            data.isNullOrBlank() -> Result.failure(TextDataException(IS_NOT_BLANK_OR_EMPTY_ERROR_MESSAGE))
            data.hasNotOnlyOneParent() -> Result.failure(TextDataException(ONLY_ONE_PARENT_ERROR_MESSAGE))
            data.maxLevelChildMoreFour() -> Result.failure(TextDataException(MAX_CHILD_LEVEL_IS_FIVE_ERROR_MESSAGE))
            else -> Result.success(data)
        }
    }

    private fun String.hasNotOnlyOneParent(): Boolean {
        val notBlankOrTabRegex = Regex("^\\S")
        return this.lines().filter { notBlankOrTabRegex.containsMatchIn(it) }.size != 1
    }

    private fun String.maxLevelChildMoreFour(): Boolean {
        val childWithLevelFiveAndMoreRegex = Regex("^( {20}|\\t{5}).*\$")
        return this.lines().any { childWithLevelFiveAndMoreRegex.containsMatchIn(it) }
    }
}