package com.app.todoistbot.service

import com.app.todoistbot.todoist.TodoistClientApi
import com.app.todoistbot.todoist.dto.TaskRequest
import com.app.todoistbot.util.dto.Task
import org.springframework.stereotype.Service

@Service
class TodoistService(val todoistClientApi: TodoistClientApi) {

    fun createTasks(task: Task, parentId: Long?) {
        val taskRequest = TaskRequest(title = task.title, parentId = parentId)
        val currentTaskId = todoistClientApi.createTask(taskRequest).getOrNull()!!.id

        if (task.children.isNotEmpty()) {
            task.children.forEach {
                createTasks(it, currentTaskId)
            }
        }
    }
}