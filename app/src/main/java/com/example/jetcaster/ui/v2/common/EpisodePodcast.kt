package com.example.jetcaster.ui.v2.common

import com.example.jetcaster.data.DownloadState
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.EpisodeEntity
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.url

data class EpisodeOfPodcast(
    val podcast: Podcast,
    val episode: EpisodeEntity
) {
    fun toEpisode(): Episode {
        return Episode(
            playState = episode.playState,
            playbackPosition = episode.playbackPosition,
            title = episode.title,
            url = episode.url(),
            duration = episode.duration,
            podcastName = podcast.title,
            podcastImageUrl = podcast.imageUrl,
        )
    }
}
