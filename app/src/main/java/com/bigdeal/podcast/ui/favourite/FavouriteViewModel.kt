package com.bigdeal.podcast.ui.favourite

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.FeedRepository
import com.bigdeal.core.data.FollowedEpisodesToPodcast
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.PodcastWithExtraInfo
import com.bigdeal.core.data.PodcastsRepository
import com.bigdeal.core.data.model.EpisodeWithPodcast
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
    private val feedRepository: FeedRepository,
    private val podcastsRepository: PodcastsRepository,
    episodeStore: EpisodeStore,
    episodePlayer: EpisodePlayer,
    podcastDownloader: PodcastDownloader,
) : BaseViewModel(episodePlayer, podcastDownloader) {

    val followedEpisodes: Flow<PagingData<EpisodeWithPodcast>> =
        episodeStore.followedEpisodesPagingData().cachedIn(viewModelScope)
    val episodePlayerState = episodePlayer.playerState

    var followedPodcasts: MutableStateFlow<List<Podcast>> = MutableStateFlow<List<Podcast>>(
        emptyList()
    )

    init {
        viewModelScope.launch {
            podcastStore.followedPodcastsSortedByLastEpisode()
                .map { podcastsWithExtraInfoList ->
                    podcastsWithExtraInfoList.map { podcastWithExtraInfo ->
                        podcastWithExtraInfo.podcast
                    }
                }.collect {podcasts ->
                    followedPodcasts.value = podcasts
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
    val episodeOfPodcasts: List<EpisodeOfPodcast> = listOf(),
    var episodePlayerState: EpisodePlayerState? = null,
    val isLoading: Boolean = false
) {
    fun toUiState(): FavouriteUiState {
        println("FavouriteUiState episodeOfPodcasts: ${episodeOfPodcasts.size}")
        return if (episodeOfPodcasts.isEmpty()) {
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


