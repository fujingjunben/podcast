

package com.bigdeal.core.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.time.Duration
import java.time.OffsetDateTime

@Entity(
    tableName = "episodes",
    indices = [
        Index("uri", unique = true),
        Index("id", unique = true),
        Index("podcast_id")
    ],
    foreignKeys = [
        ForeignKey(
            entity = Podcast::class,
            parentColumns = ["id"],
            childColumns = ["podcast_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Immutable
data class EpisodeEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "uri") val uri: String,
    @ColumnInfo(name = "podcast_id") val podcastId: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "subtitle") val subtitle: String? = null,
    @ColumnInfo(name = "summary") val summary: String? = null,
    @ColumnInfo(name = "author") val author: String? = null,
    @ColumnInfo(name = "published") val published: OffsetDateTime,
    @ColumnInfo(name = "duration") val duration: Duration = Duration.ZERO,
    @ColumnInfo(name = "file_uri") val fileUri: String = "",
    @ColumnInfo(name = "download_state") val downloadState: DownloadState = DownloadState.NONE,
    @ColumnInfo(name = "download_id") val downloadId: Long = -1L
)

fun EpisodeEntity.url(): String {
    return if (downloadState == DownloadState.SUCCESS) fileUri else uri
}
