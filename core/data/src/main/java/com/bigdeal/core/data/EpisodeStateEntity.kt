

package com.bigdeal.core.data

import androidx.compose.runtime.Immutable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "episodes_state_record",
    indices = [
        Index("episode_id", unique = true)
    ],
    foreignKeys = [
        ForeignKey(
            entity = EpisodeEntity::class,
            parentColumns = ["id"],
            childColumns = ["episode_id"],
            onUpdate = ForeignKey.CASCADE,
            onDelete = ForeignKey.CASCADE
        )
    ]
)
@Immutable
data class EpisodeStateEntity(
    @PrimaryKey @ColumnInfo(name = "episode_id") val id: String,
    @ColumnInfo(name = "time_elapsed") val timeElapsed: Long = 0L,
    @ColumnInfo(name = "is_playing") val isPlaying: Boolean = false,
    @ColumnInfo(name = "play_state") val playState: PlayState = PlayState.PREPARE,
)
