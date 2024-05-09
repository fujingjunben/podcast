package com.bigdeal.podcast.ui.v2.common

import com.bigdeal.core.data.Episode
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.url

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
