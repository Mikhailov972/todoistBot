package com.app.todoistbot.todoist.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class TaskResponse(
    @JsonProperty(value = "id")
    val id: Long,
    @JsonProperty(value = "parent_id")
    val parentId: Long?,
    @JsonProperty(value = "url")
    val url: String,
)