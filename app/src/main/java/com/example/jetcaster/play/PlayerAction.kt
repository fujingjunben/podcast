package com.example.jetcaster.play


sealed class PlayerAction

class SeekTo(val position: Long): PlayerAction()
object SeekBack: PlayerAction()
object SeekForward: PlayerAction()
object Play : PlayerAction()

enum class PlayState {
    PREPARE,
    PLAYING,
    PAUSE;
}
