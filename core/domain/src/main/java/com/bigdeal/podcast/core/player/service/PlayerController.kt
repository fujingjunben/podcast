package com.bigdeal.podcast.core.player.service

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import kotlinx.coroutines.flow.MutableStateFlow
import java.time.Duration

abstract class PlayerController {
    val playerState = MutableStateFlow<PlayerEvent>(PlayerEvent.InitState(0))
    abstract fun init(context: Context)
    abstract fun release()
    abstract fun play(episode: PlayerEpisode, duration: Duration)

    abstract fun continuePlay()

    abstract fun pause()

    abstract fun seekTo(duration: Duration)

    abstract fun seekForward()

    abstract fun seekBack()

    abstract fun setRepeatMode(mode: Int)

    abstract fun addToQueue(episode: PlayerEpisode)

    /*
    * Flushes the queue
    */
    abstract fun removeAllFromQueue()

    abstract fun addPlayerListener(listener: Player.Listener)

    abstract fun getMediaController(): MediaController?

}

sealed class PlayerEvent {
    data class InitState(val state: Int): PlayerEvent()
    data class PlaybackStateChanged(val state: Int) : PlayerEvent()
    data class PlayerError(val error: PlaybackException) : PlayerEvent()
    data class IsPlayingChanged(val isPlaying: Boolean) : PlayerEvent()
    // 其他事件类型...
}