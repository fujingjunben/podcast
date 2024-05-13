

package com.bigdeal.core.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "podcast_followed_entries",
//    foreignKeys = [
//        ForeignKey(
//            entity = Podcast::class,
//            parentColumns = ["uri"],
//            childColumns = ["podcast_uri"],
//            onUpdate = ForeignKey.CASCADE,
//            onDelete = ForeignKey.CASCADE
//        )
//    ],
    indices = [
        Index("podcast_id", unique = true)
    ]
)
@Immutable
data class PodcastFollowedEntry(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Long = 0,
    @ColumnInfo(name = "podcast_id") val podcastId: String
)
