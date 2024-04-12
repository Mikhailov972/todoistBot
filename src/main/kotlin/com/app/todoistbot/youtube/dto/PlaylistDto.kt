package com.app.todoistbot.youtube.dto

data class PlaylistDto(
    val title: String,
    val url: String,
    val items: List<PlaylistItemDto>
)

data class PlaylistItemDto(
    val title: String,
    val url: String
)