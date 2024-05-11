package com.bigdeal.podcast.core.player.model

import com.bigdeal.core.data.DownloadState
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.url
import com.bigdeal.podcast.core.model.EpisodeInfo
import com.bigdeal.podcast.core.model.PodcastInfo
import java.time.Duration
import java.time.OffsetDateTime

/**
 * Episode data with necessary information to be used within a player.
 */
data class PlayerEpisode(
    val id: String = "",
    val uri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = "",
    val podcastId: String = "",
    val downloadState: DownloadState = DownloadState.NONE,
) {
    constructor(podcastInfo: PodcastInfo, episodeInfo: EpisodeInfo) : this(
        title = episodeInfo.title,
        subTitle = episodeInfo.subTitle,
        published = episodeInfo.published,
        duration = episodeInfo.duration,
        podcastName = podcastInfo.title,
        author = episodeInfo.author,
        summary = episodeInfo.summary,
        podcastImageUrl = podcastInfo.imageUrl,
        uri = episodeInfo.uri,
        podcastId = podcastInfo.id,
        id = episodeInfo.id
    )
}

fun EpisodeToPodcast.toPlayerEpisode(): PlayerEpisode =
    PlayerEpisode(
        id = episode.id,
        uri = episode.url(),
        title = episode.title,
        subTitle = episode.subtitle ?: "",
        published = episode.published,
        duration = episode.duration,
        podcastName = podcast.title,
        author = episode.author ?: podcast.author ?: "",
        summary = episode.summary ?: "",
        podcastImageUrl = podcast.imageUrl ?: "",
        podcastId = podcast.id,
        downloadState = episode.downloadState
    )
