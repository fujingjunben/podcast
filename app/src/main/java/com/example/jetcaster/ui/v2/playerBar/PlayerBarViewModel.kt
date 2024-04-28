package com.example.jetcaster.ui.v2.playerBar

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.Graph
import com.example.jetcaster.data.EpisodeStore
import com.example.jetcaster.data.EpisodeToPodcast
import com.example.jetcaster.data.toEpisode
import com.example.jetcaster.play.PlayerController
import com.example.jetcaster.util.LogUtil
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PlayerBarViewModel(
    private val episodeStore: EpisodeStore = Graph.episodeStore,
    private val controller: PlayerController = Graph.playerController
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