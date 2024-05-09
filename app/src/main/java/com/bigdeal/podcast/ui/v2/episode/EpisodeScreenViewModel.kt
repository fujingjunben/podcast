package com.bigdeal.podcast.ui.v2.episode

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.play.PlayerController
import com.bigdeal.podcast.ui.v2.Destination
import com.bigdeal.podcast.ui.v2.common.EpisodeOfPodcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EpisodeScreenViewModel @Inject constructor(
    val episodeStore: EpisodeStore,
    val podcastStore: PodcastStore,
    savedStateHandle: SavedStateHandle,
    private val playerController: PlayerController
): ViewModel(){
    private val podcastUri: String = Uri.decode(savedStateHandle.get<String>(Destination.PODCAST)!!)
    private val episodeUri: String = Uri.decode(savedStateHandle.get<String>(Destination.EPISODE)!!)

    private val viewModelState = MutableStateFlow(EpisodeUiState())
    val uiState: StateFlow<EpisodeUiState> = viewModelState


    init {
        viewModelScope.launch {
            val podcast = podcastStore.podcastWithUri(podcastUri).first()
            episodeStore.episodeWithUri(episodeUri).collect{ episodeEntity ->
                viewModelState.update { it.copy(episodeOfPodcast = EpisodeOfPodcast(podcast, episodeEntity))  }
            }
        }
    }

    fun play(episodeOfPodcast: EpisodeOfPodcast) {
        playerController.play(episodeOfPodcast.toEpisode())
    }
}

data class EpisodeUiState(
    val episodeOfPodcast: EpisodeOfPodcast? = null
)

private data class EpisodeViewModelState(
    val isLoading: Boolean = false,
    val episodeOfPodcast: EpisodeOfPodcast? = null
)