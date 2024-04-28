package com.example.jetcaster.play

import android.content.Context
import com.example.jetcaster.data.Episode
import kotlinx.coroutines.flow.MutableStateFlow

abstract class PlayerController {
    val positionState =  MutableStateFlow(0L)
    abstract fun init(context: Context)
    abstract fun release()
    abstract fun play(episode: Episode)

}