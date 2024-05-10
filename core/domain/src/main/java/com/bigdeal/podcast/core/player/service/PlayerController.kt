package com.bigdeal.podcast.core.player.service

import android.content.Context
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration

abstract class PlayerController {
    val positionState =  MutableStateFlow(Duration.ZERO)
    abstract fun init(context: Context)
    abstract fun release()
    abstract fun play(episode: PlayerEpisode)

    abstract fun continuePlay()

    abstract fun pause()

    abstract fun seekTo(duration: Duration)

    abstract fun seekForward()

    abstract fun seekBack()


}