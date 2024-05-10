package com.bigdeal.podcast.core.player

import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow

class PlayerListenerHandler {

    fun createPlayerListenerFlow(player: Player) = callbackFlow<PlayerEvent> {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                // 发送播放状态变化事件
                trySend(PlayerEvent.PlaybackStateChanged(state)).isSuccess
            }

            override fun onPlayerError(error: PlaybackException) {
                // 发送播放器错误事件
                trySend(PlayerEvent.PlayerError(error)).isSuccess
            }

            // 其他回调方法...
        }

        player.addListener(listener)

        // 在 Flow 被取消时，移除监听器
        awaitClose {
            player.removeListener(listener)
        }
    }
}

sealed class PlayerEvent {
    data class PlaybackStateChanged(val state: Int) : PlayerEvent()
    data class PlayerError(val error: PlaybackException) : PlayerEvent()
    data class PositionDiscontinuity(val reason: Int) : PlayerEvent()
    // 其他事件类型...
}