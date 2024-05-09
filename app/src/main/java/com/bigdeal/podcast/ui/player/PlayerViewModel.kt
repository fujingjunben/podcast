/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bigdeal.podcast.ui.player

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.Episode
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.PlayState
import com.bigdeal.core.data.PlayerAction
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.SeekTo
import com.bigdeal.core.data.url
import com.bigdeal.core.play.PlayerController
import com.bigdeal.core.util.LogUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.Duration
import javax.inject.Inject

data class PlayerUiState(
    val title: String = "",
    val subTitle: String = "",
    val duration: Duration? = null,
    val podcastName: String = "",
    val author: String = "",
    val summary: String = "",
    val podcastImageUrl: String = "",
    val url: String = "",
    val playState: PlayState = PlayState.PREPARE,
    val playbackPosition: Long = 0L
) {
    fun toEpisode(): Episode {
        return Episode(
            playState = playState,
            title = title,
            duration = duration,
            playbackPosition = playbackPosition,
            podcastImageUrl = podcastImageUrl,
            podcastName = podcastName,
            url = url
        )
    }
}

/**
 * ViewModel that handles the business logic and screen state of the Player screen
 */
@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
    savedStateHandle: SavedStateHandle,
    private val playerController: PlayerController
) : ViewModel() {

    // episodeUri should always be present in the PlayerViewModel.
    // If that's not the case, fail crashing the app!
    private val episodeUri: String = Uri.decode(savedStateHandle.get<String>("episodeUri")!!)

    var uiState by mutableStateOf(PlayerUiState())
        private set

    init {
        viewModelScope.launch {
            playerController.positionState.collect {
                LogUtil.d("positionState update: $it")
                fetchEpisode()
                val position = if (it == 0L) uiState.playbackPosition else it
                uiState = uiState.copy(playbackPosition = position)
            }
        }
    }

    private suspend fun fetchEpisode() {
        val episode = episodeStore.episodeWithUri(episodeUri).first()
        val podcast = podcastStore.podcastWithUri(episode.podcastUri).first()
        uiState = PlayerUiState(
            title = episode.title,
            duration = episode.duration,
            podcastName = podcast.title,
            summary = episode.summary ?: "",
            podcastImageUrl = podcast.imageUrl ?: "",
            url = episode.url(),
            playbackPosition = episode.playbackPosition,
            playState = episode.playState
        )
    }

    fun play(playerAction: PlayerAction) {
        val playbackPosition =
            if (playerAction is SeekTo) playerAction.position else uiState.playbackPosition
        playerController.play(
            uiState.toEpisode().copy(
                playerAction = playerAction,
                playbackPosition = playbackPosition
            )
        )
    }
}