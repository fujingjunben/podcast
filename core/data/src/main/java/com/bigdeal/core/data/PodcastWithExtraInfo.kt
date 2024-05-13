

package com.bigdeal.core.data

import androidx.room.ColumnInfo
import androidx.room.Embedded
import java.time.OffsetDateTime
import java.util.Objects

class PodcastWithExtraInfo {
    @Embedded
    lateinit var podcast: Podcast

    @ColumnInfo(name = "last_episode_date")
    var lastEpisodeDate: OffsetDateTime? = null

    @ColumnInfo(name = "is_followed")
    var isFollowed: Boolean = false

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1() = podcast
    operator fun component2() = lastEpisodeDate
    operator fun component3() = isFollowed

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is PodcastWithExtraInfo -> {
            podcast == other.podcast &&
                lastEpisodeDate == other.lastEpisodeDate &&
                isFollowed == other.isFollowed
        }
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(podcast, lastEpisodeDate, isFollowed)
}
