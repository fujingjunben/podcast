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
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val feedRepository: FeedRepository,
    private val podcastsRepository: PodcastsRepository,
    private val episodePlayer: EpisodePlayer,
    podcastDownloader: PodcastDownloader,
) : BaseViewModel(episodePlayer, podcastDownloader) {

    private var viewModelState = MutableStateFlow(FavouriteViewModelState(isLoading = true))

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
                    Timber.d("podcastWithExtra: ${podcastWithExtraInfoList.size}")
                    val episodeToPodcasts: List<Flow<Pair<Podcast, List<EpisodeEntity>>>> =
                        podcastWithExtraInfoList.map { podcastWithExtraInfo ->
                            episodeStore.episodesInPodcast(
                                podcastWithExtraInfo.podcast.id,
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
                }
                .combine(episodePlayer.playerState) { episodes: List<EpisodeOfPodcast>, playerState: EpisodePlayerState ->
                    FavouriteViewModelState(
                        episodeOfPodcasts = episodes,
                        episodePlayerState = playerState,
                        isLoading = false
                    )
                }.collect { state ->
                    viewModelState.value = state
                }
        }
    }

    fun onPodcastUnfollowed(podcastId: String) {
        viewModelScope.launch {
            podcastStore.unfollowPodcast(podcastId)
        }
    }

    fun addFeed(podcastUrl: String) {
        viewModelScope.launch {
            val feedEntity = async { feedRepository.addFeed(podcastUrl) }.await()
            awaitAll(
                async { podcastsRepository.fetchPodcasts(listOf(podcastUrl)) },
                async { podcastStore.followPodcast(feedEntity.id) }
            )
        }
    }
}

data class FavouriteViewModelState(
    val episodeOfPodcasts: List<EpisodeOfPodcast>? = null,
    var episodePlayerState: EpisodePlayerState? = null,
    val isLoading: Boolean = false
) {
    fun toUiState(): FavouriteUiState {
        println("episodeOfPodcasts: ${episodeOfPodcasts?.size}")
        return if (episodeOfPodcasts.isNullOrEmpty()) {
            FavouriteUiState.Loading
        } else {
            FavouriteUiState.Success(episodeOfPodcasts, episodePlayerState = episodePlayerState)
        }
    }
}

sealed interface FavouriteUiState {
    @Immutable
    data class Success(
        val episodeOfPodcasts: List<EpisodeOfPodcast> = listOf(),
        var episodePlayerState: EpisodePlayerState?
    ) : FavouriteUiState

    data object Loading : FavouriteUiState
}


