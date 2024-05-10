package com.bigdeal.podcast.ui.v2.episode

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.ui.v2.Destination
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class EpisodeScreenViewModel @Inject constructor(
    val episodeStore: EpisodeStore,
    savedStateHandle: SavedStateHandle,
    private val episodePlayer: EpisodePlayer
) : ViewModel() {
    private val episodeUri: String = Uri.decode(savedStateHandle.get<String>(Destination.EPISODE)!!)

    private val viewModelState = MutableStateFlow(EpisodeUiState())
    val uiState: StateFlow<EpisodeUiState> = viewModelState


    init {
        viewModelScope.launch {
            Timber.d("episodeUri: $episodeUri")
            episodeStore.episodeAndPodcastWithId(episodeUri).collect { item ->
                Timber.d("episodeAndPodcast: $item")
                if (item != null) {
                    viewModelState.update {
                        it.copy(
                            episodeOfPodcast =
                            EpisodeOfPodcast(item.podcast, item.episode)
                        )
                    }
                }
            }
        }
    }

    fun play(episodeOfPodcast: EpisodeOfPodcast) {
    }
}

data class EpisodeUiState(
    val episodeOfPodcast: EpisodeOfPodcast? = null
)

private data class EpisodeViewModelState(
    val isLoading: Boolean = false,
    val episodeOfPodcast: EpisodeOfPodcast? = null
)