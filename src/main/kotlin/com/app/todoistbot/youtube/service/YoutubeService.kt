package com.app.todoistbot.youtube.service

import com.app.todoistbot.youtube.client.YoutubeClientApi
import com.app.todoistbot.youtube.dto.PlaylistDto
import com.app.todoistbot.youtube.dto.PlaylistItemDto
import org.springframework.stereotype.Service

@Service
class YoutubeService(private val youtubeClientApi: YoutubeClientApi) {
    private companion object {
        const val YOUTUBE_URL = "https://www.youtube.com"
    }

    fun getPlaylist(playlistId: String): PlaylistDto {
        val playlistTitle = youtubeClientApi.getPlaylistTitle(playlistId).getOrThrow().items.first().snippet.title

        val playlistItems = youtubeClientApi.getPlaylistContent(playlistId)

        val items = playlistItems.flatMap { it.getOrThrow().items }.map {
            PlaylistItemDto(
                it.snippet.title,
                "$YOUTUBE_URL/watch?v=${it.contentDetails.videoId}&list=$playlistId"
            )
        }

        val playlistUrl = "$YOUTUBE_URL/playlist?list=$playlistId"
        return PlaylistDto(playlistTitle, playlistUrl, items)
    }
}