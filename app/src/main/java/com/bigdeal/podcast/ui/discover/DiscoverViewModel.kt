package com.bigdeal.podcast.ui.discover

import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.Category
import com.bigdeal.core.data.CategoryStore
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


sealed class DiscoverUi {
    data object Loading : DiscoverUi()
    data class Ready(val podcastAndCategory: Map<Category, List<EpisodeWithPodcast>>) :
        DiscoverUi()
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val categoryStore: CategoryStore,
    private val podcastStore: PodcastStore,
    episodePlayer: EpisodePlayer,
    podcastDownloader: PodcastDownloader,
) : BaseViewModel(episodePlayer, podcastDownloader) {

    private var _uiState = MutableStateFlow<DiscoverUi>(DiscoverUi.Loading)
    val podcastFollowedState: Flow<List<String>> =
        podcastStore.getAllPodcastFollowState().map { podcastFollowedStateList ->
            podcastFollowedStateList.map { it.podcastId }
        }

    val episodePlayerState = episodePlayer.playerState
    val uiState: StateFlow<DiscoverUi> = _uiState

    init {
        viewModelScope.launch {
            categoryStore.categoriesSortedByPodcastCount().collect { categories ->
                val data: MutableMap<Category, List<EpisodeWithPodcast>> =
                    mutableMapOf()
                val deferredResults = categories.map { category ->
                    Timber.d("category: $category")
                    async {
                        val podcasts =
                            categoryStore.getPodcastsAndLatestEpisodesByCategory(category.id)
                                .first()
                        data[category] = podcasts
                    }
                }

                // Await all deferred results to ensure all data is collected
                deferredResults.awaitAll()

                Timber.d("data: ${data.size}")
                _uiState.value = DiscoverUi.Ready(data)
            }
        }
    }

    fun onFollowToggle(podcastId: String) {
        viewModelScope.launch {
            podcastStore.togglePodcastFollowed(podcastId)
        }
    }
}