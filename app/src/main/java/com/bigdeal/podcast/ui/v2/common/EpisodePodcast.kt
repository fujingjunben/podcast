package com.bigdeal.podcast.ui.v2.common

import com.bigdeal.core.data.Episode
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.url
import com.bigdeal.podcast.core.player.model.PlayerEpisode

data class EpisodeOfPodcast(
    val podcast: Podcast,
    val episode: EpisodeEntity
) {
    fun toEpisode(): PlayerEpisode {
        return PlayerEpisode(
            title = episode.title,
            uri = episode.url(),
            duration = episode.duration,
            podcastName = podcast.title,
            podcastImageUrl = podcast.imageUrl!!,
        )
    }
}
