package com.bigdeal.podcast.ui.episode

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.ui.Destination
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.core.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PlayerUiState(
    val episode: PlayerEpisode? = null
)
@HiltViewModel
class EpisodeScreenViewModel @Inject constructor(
    val episodeStore: EpisodeStore,
    savedStateHandle: SavedStateHandle,
    episodePlayer: EpisodePlayer,
    podcastDownloader: PodcastDownloader
) : BaseViewModel(episodePlayer, podcastDownloader) {
    private val episodeId: String = Uri.decode(savedStateHandle.get<String>(Destination.EPISODE)!!)

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            episodeStore.episodeAndPodcastWithId(episodeId).collect {
                uiState = PlayerUiState(it.toPlayerEpisode())
            }
        }
    }
}