package com.bigdeal.podcast.ui.v2.playerBar

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.core.player.service.PlayerController
import com.bigdeal.podcast.ui.player.PlayerUiState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel(assistedFactory = PlayerBarViewModel.Factory::class)
class PlayerBarViewModel @AssistedInject constructor(
    private val episodeStore: EpisodeStore,
    private val episodePlayer: EpisodePlayer,
    @Assisted private val episodeUri: String
) : ViewModel() {
    private val decodedEpisodeUri = Uri.decode(episodeUri)

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            episodeStore.episodeAndPodcastWithUri(decodedEpisodeUri).flatMapConcat {
                episodePlayer.currentEpisode = it.toPlayerEpisode()
                episodePlayer.playerState
            }.map {
                PlayerUiState(episodePlayerState = it)
            }.collect {
                uiState = it
            }
        }
    }


    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.continuePlay()
    }

    fun pause() {
        episodePlayer.pause()
    }
    @AssistedFactory
    interface Factory {
        fun create(podcastUri: String): PlayerBarViewModel
    }
}