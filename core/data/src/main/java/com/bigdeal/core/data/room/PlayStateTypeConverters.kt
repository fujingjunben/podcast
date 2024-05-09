package com.bigdeal.core.data.room

import androidx.room.TypeConverter
import com.bigdeal.core.data.PlayState

class PlayStateTypeConverters {
    @TypeConverter
    fun fromPlayState(playState: PlayState): String {
        return playState.name
    }

    @TypeConverter
    fun toPlayState(playState: String) : PlayState {
        return PlayState.valueOf(playState)
    }
}