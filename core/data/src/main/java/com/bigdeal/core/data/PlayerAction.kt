package com.bigdeal.core.data


sealed class PlayerAction

class SeekTo(val position: Long): PlayerAction()
data object SeekBack: PlayerAction()
data object SeekForward: PlayerAction()
data object Play : PlayerAction()

enum class PlayState {
    PREPARE,
    PLAYING,
    PAUSE;
}
