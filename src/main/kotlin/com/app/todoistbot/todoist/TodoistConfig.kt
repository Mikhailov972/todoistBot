package com.app.todoistbot.todoist

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.todoist")
data class TodoistConfig(
    var apiKey: String
)
