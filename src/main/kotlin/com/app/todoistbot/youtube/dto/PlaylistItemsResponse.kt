package com.app.todoistbot.youtube.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaylistItemsResponse(
    @JsonProperty(value = "nextPageToken")
    val nextPageToken: String?,
    @JsonProperty(value = "items")
    val items: List<Item>,
    @JsonProperty(value = "pageInfo")
    val pageInfo: PageInfo,
)

data class PageInfo(
    @JsonProperty(value = "totalResults")
    val totalResults: Int,
    @JsonProperty(value = "resultsPerPage")
    val resultsPerPage: Int,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Item(
    @JsonProperty(value = "snippet")
    val snippet: Snippet,
    @JsonProperty(value = "contentDetails")
    val contentDetails: ContentDetails,
)


@JsonIgnoreProperties(ignoreUnknown = true)
data class ContentDetails(
    @JsonProperty(value = "videoId")
    val videoId: String,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class PlaylistTitleResponse(
    @JsonProperty(value = "items")
    val items: List<ItemTitle>,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ItemTitle(
    @JsonProperty(value = "snippet")
    val snippet: Snippet,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Snippet(
    @JsonProperty(value = "title")
    val title: String,
)