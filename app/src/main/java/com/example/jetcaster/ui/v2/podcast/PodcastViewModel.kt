package com.example.jetcaster.ui.v2.podcast

import android.net.Uri
import android.os.Bundle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.example.jetcaster.Graph
import com.example.jetcaster.data.EpisodeEntity
import com.example.jetcaster.data.EpisodeStore
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.ui.v2.Destination
import com.example.jetcaster.ui.v2.common.EpisodeOfPodcast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

class PodcastViewModel(
    val episodeStore: EpisodeStore,
    private val podcastStore: PodcastStore,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val podcastUrI = Uri.decode(savedStateHandle.get<String>(Destination.PODCAST))

    var uiState by mutableStateOf(PodcastUiState(isLoading = true))
    private set

    init {
        viewModelScope.launch {
            val podcast = podcastStore.podcastWithUri(podcastUrI).first()
            val episodeEntities = episodeStore.episodesInPodcast(podcastUrI, 20).first()
            uiState = PodcastUiState(isLoading = false, podcast = podcast,
                episodeOfPodcasts = episodeEntities.map { episodeEntity ->
                    EpisodeOfPodcast(
                        podcast,
                        episodeEntity
                    )
                })
        }
    }

    companion object {
        fun provideFactory(
            episodeStore: EpisodeStore = Graph.episodeStore,
            podcastStore: PodcastStore = Graph.podcastStore,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null,
        ): AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return PodcastViewModel(
                        episodeStore,
                        podcastStore,
                        handle
                    ) as T
                }
            }
    }
}

data class PodcastUiState(
    val isLoading: Boolean = true,
    val podcast: Podcast? = null,
    val episodeOfPodcasts: List<EpisodeOfPodcast> = listOf()
)