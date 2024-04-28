package com.example.jetcaster.data

import com.example.jetcaster.play.Play
import com.example.jetcaster.play.PlayState
import com.example.jetcaster.play.PlayerAction
import java.time.Duration

const val THRESHOLD = 500

data class Episode(val playState: PlayState,
                   val playerAction: PlayerAction = Play,
                   val duration: Duration?,
                   val playbackPosition: Long,
                   val url: String,
                   val podcastName: String,
                   val podcastImageUrl: String?,
                   val title: String
) {
    fun isFinished(): Boolean {
        return if (duration == null){
            false
        } else {
            playbackPosition > duration.toMillis() - THRESHOLD
        }
    }
}
