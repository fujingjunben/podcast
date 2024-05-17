

package com.bigdeal.core.data

import androidx.room.Embedded
import androidx.room.Relation
import java.util.Objects

class FollowedEpisodesToPodcast {
    @Embedded
    lateinit var podcast: Podcast

    @Relation(parentColumn = "id", entityColumn = "podcast_id")
    lateinit var episodes: List<EpisodeEntity>

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1() = podcast
    operator fun component2() = episodes

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is FollowedEpisodesToPodcast -> episodes == other.episodes && podcast == other.podcast
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(podcast, episodes)
}
