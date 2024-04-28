package com.example.jetcaster.data.room

import androidx.room.TypeConverter
import com.example.jetcaster.play.PlayState

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