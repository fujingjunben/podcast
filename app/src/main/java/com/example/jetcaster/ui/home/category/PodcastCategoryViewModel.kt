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

package com.example.jetcaster.ui.home.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.Graph
import com.example.jetcaster.data.*
import com.example.jetcaster.play.PlayerController
import com.example.jetcaster.ui.v2.common.EpisodeOfPodcast
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class PodcastCategoryViewModel(
    private val categoryId: Long,
    private val categoryStore: CategoryStore = Graph.categoryStore,
    private val podcastStore: PodcastStore = Graph.podcastStore,
    private val episodeStore: EpisodeStore = Graph.episodeStore,
    private val controller: PlayerController = Graph.playerController
) : ViewModel() {
    private val _state = MutableStateFlow(PodcastCategoryViewState())

    val state: StateFlow<PodcastCategoryViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val recentPodcastsFlow = categoryStore.podcastsInCategorySortedByPodcastCount(
                categoryId,
                limit = 10
            ).map { podcastWithExtraInfos ->
                podcastWithExtraInfos.filter { !it.isFollowed }
            }

            val episodesFlow = recentPodcastsFlow.flatMapLatest { podcasts ->
                val episodeAndPodcasts: List<Flow<List<Pair<Podcast, EpisodeEntity>>>> =
                    podcasts.map { podcastWithExtraInfo ->
                        episodeStore.episodesInPodcast(
                            podcastWithExtraInfo.podcast.uri,
                            limit = 20
                        ).map { episodeEntityList: List<EpisodeEntity> ->
                            episodeEntityList.map { episodeEntity ->
                                podcastWithExtraInfo.podcast to episodeEntity
                            }
                        }
                    }

                combine(episodeAndPodcasts) { group: Array<List<Pair<Podcast, EpisodeEntity>>> ->
                    group.map { it }.flatten()
                }
            }

            // Combine our flows and collect them into the view state StateFlow
            combine(recentPodcastsFlow, episodesFlow) { topPodcasts, episodes ->
                PodcastCategoryViewState(
                    topPodcasts = topPodcasts,
                    episodes = episodes.map { (podcast, episode)->
                        EpisodeOfPodcast(podcast, episode)
                    }
                )
            }.collect { _state.value = it }
        }
    }

    fun onTogglePodcastFollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastUri)
        }
    }

    fun play(episodeOfPodcast: EpisodeOfPodcast) {
        controller.play(episodeOfPodcast.toEpisode())
    }
}

data class PodcastCategoryViewState(
    val topPodcasts: List<PodcastWithExtraInfo> = emptyList(),
    val episodes: List<EpisodeOfPodcast> = emptyList()
)