package com.app.todoistbot.todoist.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class TaskRequest(
    @JsonProperty(value = "content")
    val title: String,
    @JsonProperty(value = "parent_id")
    val parentId: Long?
)