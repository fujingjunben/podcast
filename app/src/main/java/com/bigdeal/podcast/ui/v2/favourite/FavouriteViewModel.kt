package com.bigdeal.podcast.ui.v2.favourite

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.FeedRepository
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.PodcastWithExtraInfo
import com.bigdeal.core.data.PodcastsRepository
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.MockEpisodePlayer
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.service.PlayerController
import com.bigdeal.podcast.ui.v2.common.EpisodeOfPodcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val feedRepository: FeedRepository,
    private val podcastsRepository: PodcastsRepository,
    private val episodePlayer: EpisodePlayer,
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FavouriteViewModelState(isLoading = true))

    val uiState = viewModelState
        .map { it.toUiState() }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )


    val pageSize = 5

    init {
        viewModelScope.launch {
            podcastStore.followedPodcastsSortedByLastEpisode()
                .flatMapLatest { podcastWithExtraInfoList: List<PodcastWithExtraInfo> ->
                    val episodeToPodcasts: List<Flow<Pair<Podcast, List<EpisodeEntity>>>> =
                        podcastWithExtraInfoList.map { podcastWithExtraInfo ->
                            episodeStore.episodesInPodcast(
                                podcastWithExtraInfo.podcast.uri,
                                limit = pageSize
                            )
                                .map { episodeEntities: List<EpisodeEntity> ->
                                    podcastWithExtraInfo.podcast to episodeEntities
                                }
                        }
                    combine(episodeToPodcasts) { combined: Array<Pair<Podcast, List<EpisodeEntity>>> ->
                        combined.map { (podcast, episodes) ->
                            episodes.map { episodeEntity ->
                                EpisodeOfPodcast(podcast, episodeEntity)
                            }
                        }.flatten()
                            .sortedByDescending { episodeOfPodcast -> episodeOfPodcast.episode.published }
                    }
                }.collect { episodeOfPodcasts ->
                    viewModelState.update {
                        it.copy(
                            episodeOfPodcasts = episodeOfPodcasts,
                            isLoading = false
                        )
                    }
                }
        }
    }

    fun onPodcastUnfollowed(podcastUri: String) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcastUri)
        }
    }

    fun addFeed(podcastUri: String) {
        viewModelScope.launch {
            awaitAll(
                async { feedRepository.addFeed(podcastUri) },
                async { podcastsRepository.fetchPodcasts(listOf(podcastUri)) },
                async { podcastStore.followPodcast(podcastUri) }
            )
        }
    }

    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }
}

data class FavouriteViewModelState(
    val episodeOfPodcasts: List<EpisodeOfPodcast>? = null,
    val isLoading: Boolean = false
) {
    fun toUiState(): FavouriteUiState {
        println("episodeOfPodcasts: ${episodeOfPodcasts?.size}")
        return if (episodeOfPodcasts.isNullOrEmpty()) {
            FavouriteUiState.Loading
        } else {
            FavouriteUiState.Success(episodeOfPodcasts)
        }
    }
}

sealed interface FavouriteUiState {
    @Immutable
    data class Success(
        val episodeOfPodcasts: List<EpisodeOfPodcast> = listOf()
    ) : FavouriteUiState

    data object Loading : FavouriteUiState
}
