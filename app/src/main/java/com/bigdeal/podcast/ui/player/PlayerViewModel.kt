package com.bigdeal.podcast.ui.player

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.ui.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Duration
import javax.inject.Inject
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

data class PlayerUiState(
    val episodePlayerState: EpisodePlayerState = EpisodePlayerState()
)

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // episodeId should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val episodeId: String =
        Uri.decode(savedStateHandle.get<String>(Screen.ARG_EPISODE_ID)!!)

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            episodeStore.episodeAndPodcastWithId(episodeId).flatMapConcat {
                episodePlayer.currentEpisode = it.toPlayerEpisode()
                episodePlayer.playerState
            }.map {
                PlayerUiState(episodePlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }

    fun onPlay() {
        episodePlayer.continuePlay()
    }

    fun onPause() {
        episodePlayer.pause()
    }

    fun onStop() {
        episodePlayer.stop()
    }

    fun onPrevious() {
        episodePlayer.previous()
    }

    fun onNext() {
        episodePlayer.next()
    }

    fun onAdvanceBy(duration: Duration) {
        episodePlayer.advanceBy(duration)
    }

    fun onRewindBy(duration: Duration) {
        episodePlayer.rewindBy(duration)
    }

    fun onSeekingStarted() {
        episodePlayer.onSeekingStarted()
    }

    fun onSeekingFinished(duration: Duration) {
        episodePlayer.onSeekingFinished(duration)
    }

    fun onAddToQueue() {
        uiState.episodePlayerState.currentEpisode?.let {
            episodePlayer.addToQueue(it)
        }
    }

    fun onSetRepeatMode(mode: Int) {
        episodePlayer.setRepeatMode(mode)
    }
}
