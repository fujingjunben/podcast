package com.bigdeal.podcast.ui.v2.podcast

import android.net.Uri
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.model.EpisodeInfo
import com.bigdeal.podcast.ui.v2.Destination
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface PodcastUiState {
    data object Loading : PodcastUiState
    data class Ready(
        val podcast: Podcast,
        val episodes: Flow<PagingData<EpisodeWithPodcast>>,
        val episodePlayerState: EpisodePlayerState
    ) : PodcastUiState
}

@HiltViewModel
class PodcastViewModel @Inject constructor(
    val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
    episodePlayer: EpisodePlayer,
    podcastDownloader: PodcastDownloader,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel(episodePlayer, podcastDownloader) {
    private val podcastId = Uri.decode(savedStateHandle.get<String>(Destination.PODCAST))

    private val _uiState = MutableStateFlow<PodcastUiState>(PodcastUiState.Loading)
    val uiState: StateFlow<PodcastUiState> = _uiState

    init {
        viewModelScope.launch {
            val podcast = podcastStore.podcastWithId(podcastId).first()
            val episodesPagingData: Flow<PagingData<EpisodeWithPodcast>> =
                episodeStore.episodesInPodcastPagingData(podcastId).cachedIn(viewModelScope)
            episodePlayer.playerState.collect { playerState ->
                _uiState.value = PodcastUiState.Ready(
                    podcast = podcast,
                    episodes = episodesPagingData,
                    episodePlayerState = playerState
                )
            }
        }
    }
}

