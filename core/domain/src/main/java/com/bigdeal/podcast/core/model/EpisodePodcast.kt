package com.bigdeal.podcast.core.model

import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.url
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import timber.log.Timber

data class EpisodeOfPodcast(
    val podcast: Podcast,
    val episode: EpisodeEntity
) {
    fun toEpisode(): PlayerEpisode {
        Timber.d("episodeOfPodcast: $episode")
        return PlayerEpisode(
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
    }
}
