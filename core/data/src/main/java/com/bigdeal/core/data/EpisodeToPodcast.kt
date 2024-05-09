/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bigdeal.core.data

import androidx.room.Embedded
import androidx.room.Ignore
import androidx.room.Relation
import java.util.Objects

class EpisodeToPodcast {
    @Embedded
    lateinit var episode: com.bigdeal.core.data.EpisodeEntity

    @Relation(parentColumn = "podcast_uri", entityColumn = "uri")
    lateinit var _podcasts: List<Podcast>

    @get:Ignore
    val podcast: Podcast
        get() = _podcasts[0]

    /**
     * Allow consumers to destructure this class
     */
    operator fun component1() = episode
    operator fun component2() = podcast

    override fun equals(other: Any?): Boolean = when {
        other === this -> true
        other is EpisodeToPodcast -> episode == other.episode && _podcasts == other._podcasts
        else -> false
    }

    override fun hashCode(): Int = Objects.hash(episode, _podcasts)
}
fun EpisodeToPodcast.toEpisode(): com.bigdeal.core.data.Episode {
    return com.bigdeal.core.data.Episode(
        playState = episode.playState,
        playbackPosition = episode.playbackPosition,
        title = episode.title,
        url = episode.url(),
        duration = episode.duration,
        podcastName = podcast.title,
        podcastImageUrl = podcast.imageUrl,
    )
}
