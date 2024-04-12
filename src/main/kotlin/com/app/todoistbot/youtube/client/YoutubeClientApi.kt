package com.app.todoistbot.youtube.client

import com.app.todoistbot.youtube.YoutubeConfig
import com.app.todoistbot.youtube.dto.PlaylistItemsResponse
import com.app.todoistbot.youtube.dto.PlaylistTitleResponse
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import org.springframework.web.client.body

@Component
class YoutubeClientApi(val youtubeConfig: YoutubeConfig) {
    val client = RestClient.builder()
        .baseUrl("https://www.googleapis.com/youtube/v3")
        .build()

    fun getPlaylistContent(playlistId: String): List<Result<PlaylistItemsResponse>> {
        return generateSequence(getPlaylistContentByPage(playlistId, null)) {
            if (it.getOrNull()?.nextPageToken == null) return@generateSequence null
            getPlaylistContentByPage(playlistId, it.getOrNull()?.nextPageToken)
        }.toList()
    }

    private fun getPlaylistContentByPage(playlistId: String, pageToken: String? = null): Result<PlaylistItemsResponse> {
        val request = client.get()
            .uri { uriBuilder ->
                uriBuilder.path("/playlistItems")
                    .queryParam("key", youtubeConfig.apiKey)
                    .queryParam("playlistId", playlistId)
                    .queryParam("part", "snippet", "contentDetails")
                    .queryParam("fields", "nextPageToken, items(snippet(title), contentDetails(videoId)), pageInfo")
                    .queryParam("maxResults", 50)
                    .queryParam("pageToken", pageToken)
                    .build()
            }

        return Result.success(request.retrieve()).map { it.body<PlaylistItemsResponse>()!! }
    }

    fun getPlaylistTitle(playlistId: String): Result<PlaylistTitleResponse> {
        val request = client.get()
            .uri { uriBuilder ->
                uriBuilder.path("/playlists")
                    .queryParam("key", youtubeConfig.apiKey)
                    .queryParam("id", playlistId)
                    .queryParam("part", "snippet")
                    .queryParam("fields", "items(snippet(title))").build()
            }

        return Result.success(request.retrieve()).map { it.body<PlaylistTitleResponse>()!! }
    }
}