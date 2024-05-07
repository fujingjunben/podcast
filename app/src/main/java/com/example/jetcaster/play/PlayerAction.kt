package com.example.jetcaster.play


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
