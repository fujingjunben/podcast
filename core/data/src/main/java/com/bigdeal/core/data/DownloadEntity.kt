package com.bigdeal.core.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "DownloadEntities",
    indices = [
        Index("id", unique = true),
    Index("uri", unique = true)
    ],
)
@Immutable
data class DownloadEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: Long = 0L,
    @ColumnInfo(name = "uri") val uri: String = "",
    @ColumnInfo(name = "file_uri") val fileUri: String = "",
    @ColumnInfo(name = "state") val state: com.bigdeal.core.data.DownloadState = com.bigdeal.core.data.DownloadState.NONE
)

enum class DownloadState {
    FAILED,
    SUCCESS,
    DOWNLOADING,
    NONE
}
