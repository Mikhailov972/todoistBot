package com.app.todoistbot.telegram

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.telegram")
data class TelegramConfig(
    var apiKey: String,
    var userId: Long
)