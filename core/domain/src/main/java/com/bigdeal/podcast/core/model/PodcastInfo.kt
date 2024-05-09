package com.bigdeal.podcast.core.model

import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastWithExtraInfo
import java.time.OffsetDateTime

/**
 * External data layer representation of a podcast.
 */
data class PodcastInfo(
    val uri: String = "",
    val title: String = "",
    val author: String = "",
    val imageUrl: String = "",
    val description: String = "",
    val isSubscribed: Boolean? = null,
    val lastEpisodeDate: OffsetDateTime? = null,
)

fun Podcast.asExternalModel(): PodcastInfo =
    PodcastInfo(
        uri = this.uri,
        title = this.title,
        author = this.author ?: "",
        imageUrl = this.imageUrl ?: "",
        description = this.description ?: "",
    )

fun PodcastWithExtraInfo.asExternalModel(): PodcastInfo =
    this.podcast.asExternalModel().copy(
        isSubscribed = isFollowed,
        lastEpisodeDate = lastEpisodeDate,
    )
