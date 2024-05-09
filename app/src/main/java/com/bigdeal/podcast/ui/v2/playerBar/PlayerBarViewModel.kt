package com.bigdeal.podcast.ui.v2.playerBar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.toEpisode
import com.bigdeal.core.play.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerBarViewModel @Inject constructor(
    private val episodeStore: EpisodeStore,
    private val controller: PlayerController,
) : ViewModel() {
    private val viewModelState = MutableStateFlow(PlayerBarViewModelState())

    val uiState = viewModelState.map {
        it.toUiState()
    }.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        viewModelState.value.toUiState()
    )

    init {
        viewModelScope.launch {
            episodeStore.episodeWhichIsPlaying().collect { episodeToPodcasts ->
//                LogUtil.d(TAG, "episode: ${episodeToPodcasts.size}")
                if (episodeToPodcasts.isNotEmpty()) {
                    viewModelState.update { it.copy(episodeToPodcast = episodeToPodcasts[0]) }
                }
            }
        }
    }

    fun play() {
        return when (uiState.value) {
            is PlayerBarUiState.Success -> {
                val episodeToPodcast =
                    (uiState.value as PlayerBarUiState.Success).episodeToPodcast
                controller.play(episodeToPodcast.toEpisode())
            }
            else -> {}
        }
    }

    companion object {
        const val TAG:String = "PlayerBarViewModelState"
    }
}

private data class PlayerBarViewModelState(
    val isPlaying: Boolean = false,
    val episodeToPodcast: EpisodeToPodcast? = null
) {
    fun toUiState(): PlayerBarUiState {
        return if (episodeToPodcast == null) {
            PlayerBarUiState.Loading
        } else {
            PlayerBarUiState.Success(episodeToPodcast)
        }
    }
}

sealed interface PlayerBarUiState {
    data class Success(val episodeToPodcast: EpisodeToPodcast) : PlayerBarUiState
    object Loading : PlayerBarUiState

}