package com.bigdeal.core.data

import java.time.Duration

const val THRESHOLD = 500

data class Episode(
    val playState: com.bigdeal.core.data.PlayState,
    val playerAction: com.bigdeal.core.data.PlayerAction = com.bigdeal.core.data.Play,
    val duration: Duration?,
    val playbackPosition: Long,
    val url: String,
    val podcastName: String,
    val podcastImageUrl: String?,
    val title: String,
) {
    fun isFinished(): Boolean {
        return if (duration == null) {
            false
        } else {
            playbackPosition > duration.toMillis() - com.bigdeal.core.data.THRESHOLD
        }
    }
}
