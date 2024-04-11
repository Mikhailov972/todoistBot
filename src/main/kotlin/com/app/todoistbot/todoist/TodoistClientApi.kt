package com.app.todoistbot.todoist

import com.app.todoistbot.todoist.dto.TaskRequest
import com.app.todoistbot.todoist.dto.TaskResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.toEntity

@Component
class TodoistClientApi(todoistConfig: TodoistConfig) {

    val client = RestClient.builder()
        .baseUrl("https://api.todoist.com/rest/v2")
        .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer ${todoistConfig.apiKey}")
        .build()

    fun createTask(taskRequest: TaskRequest): Result<TaskResponse> {
        val request = client.post().uri("/tasks").contentType(MediaType.APPLICATION_JSON).body(taskRequest)
        return Result.success(request.retrieve()).map { it.toEntity<TaskResponse>().body!! }
    }
}