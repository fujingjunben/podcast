/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    @ColumnInfo(name = "duration") val duration: Duration? = null,
    @ColumnInfo(name = "file_uri") val fileUri: String = "",
    @ColumnInfo(name = "download_state") val downloadState: DownloadState = DownloadState.NONE,
    @ColumnInfo(name = "download_id") val downloadId: Long = -1L
)

fun EpisodeEntity.url(): String {
    return if (downloadState == DownloadState.SUCCESS) fileUri else uri
}
