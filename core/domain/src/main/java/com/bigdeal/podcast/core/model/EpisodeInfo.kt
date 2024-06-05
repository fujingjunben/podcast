package com.bigdeal.podcast.core.model

import com.bigdeal.core.data.EpisodeEntity
import java.time.Duration
import java.time.OffsetDateTime

/**
 * External data layer representation of an episode.
 */
data class EpisodeInfo(
    val id: String = "",
    val uri: String = "",
    val title: String = "",
    val subTitle: String = "",
    val summary: String = "",
    val author: String = "",
    val published: OffsetDateTime = OffsetDateTime.MIN,
    val duration: Duration = Duration.ZERO,
)

fun EpisodeEntity.asExternalModel(): EpisodeInfo =
    EpisodeInfo(
        id = id,
        uri = uri,
        title = title,
        subTitle = subtitle ?: "",
        summary = summary ?: "",
        author = author ?: "",
        published = published,
        duration = duration,
    )
