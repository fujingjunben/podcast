package com.example.jetcaster.ui.v2.episode

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetcaster.Graph
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.EpisodeStore
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.play.PlayerController
import com.example.jetcaster.ui.v2.Destination
import com.example.jetcaster.ui.v2.common.EpisodeOfPodcast
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class EpisodeScreenViewModel(
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

    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            podcastStore: PodcastStore = Graph.podcastStore,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
            playerController: PlayerController = Graph.playerController
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return EpisodeScreenViewModel(
                        episodeStore,
                        podcastStore,
                        handle,
                        playerController
                    ) as T
                }
            }
    }
}

data class EpisodeUiState(
    val episodeOfPodcast: EpisodeOfPodcast? = null
)

private data class EpisodeViewModelState(
    val isLoading: Boolean = false,
    val episodeOfPodcast: EpisodeOfPodcast? = null
)