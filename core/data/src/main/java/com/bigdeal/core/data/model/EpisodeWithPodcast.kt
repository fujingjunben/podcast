package com.bigdeal.core.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.Podcast

data class EpisodeWithPodcast(
    @Embedded val episode: EpisodeEntity,
    @Relation(
        parentColumn = "podcast_id",
        entityColumn = "id"
    )
    val podcast: Podcast
)