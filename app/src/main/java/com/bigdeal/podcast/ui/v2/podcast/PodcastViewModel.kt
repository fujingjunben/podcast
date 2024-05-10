package com.bigdeal.podcast.ui.v2.podcast

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.podcast.ui.v2.Destination
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PodcastViewModel @Inject constructor(
    val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val podcastId = Uri.decode(savedStateHandle.get<String>(Destination.PODCAST))

    var uiState by mutableStateOf(PodcastUiState(isLoading = true))
    private set

    init {
        viewModelScope.launch {
            val podcast = podcastStore.podcastWithId(podcastId).first()
            val episodeEntities = episodeStore.episodesInPodcast(podcastId, 20).first()
            uiState = PodcastUiState(isLoading = false, podcast = podcast,
                episodeOfPodcasts = episodeEntities.map { episodeEntity ->
                    EpisodeOfPodcast(
                        podcast,
                        episodeEntity
                    )
                })
        }
    }
}

data class PodcastUiState(
    val isLoading: Boolean = true,
    val podcast: Podcast? = null,
    val episodeOfPodcasts: List<EpisodeOfPodcast> = listOf()
)