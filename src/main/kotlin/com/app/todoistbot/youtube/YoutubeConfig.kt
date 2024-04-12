package com.app.todoistbot.youtube

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.youtube")
data class YoutubeConfig(
    var apiKey: String,
)