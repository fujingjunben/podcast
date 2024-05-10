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
            title = episode.title,
            uri = episode.url(),
            duration = episode.duration,
            podcastName = podcast.title,
            podcastImageUrl = podcast.imageUrl ?: "",
        )
    }
}
