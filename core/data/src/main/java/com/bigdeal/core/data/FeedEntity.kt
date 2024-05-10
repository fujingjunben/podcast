package com.bigdeal.core.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.bigdeal.core.data.extension.toSHA256

@Entity(
    tableName = "feed_entries",
    indices = [
        Index("url", unique = true),
        Index("id", unique = true)
    ]
)
@Immutable
data class FeedEntity(
    @PrimaryKey @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "url") val url: String
)