package com.bigdeal.podcast.ui.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.Category
import com.bigdeal.core.data.CategoryStore
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.PodcastWithExtraInfo
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


sealed class DiscoverUi {
    data object Loading : DiscoverUi()
    data class Ready(val podcastAndCategory: Map<Category, List<EpisodeWithPodcast>>) : DiscoverUi()
}

@HiltViewModel
class DiscoverViewModel @Inject constructor(
    private val categoryStore: CategoryStore,
    episodePlayer: EpisodePlayer,
    podcastDownloader: PodcastDownloader,
) : BaseViewModel(episodePlayer, podcastDownloader) {

    private var _uiState = MutableStateFlow<DiscoverUi>(DiscoverUi.Loading)

    val episodePlayerState = episodePlayer.playerState
    val uiState: StateFlow<DiscoverUi> = _uiState

    init {
        viewModelScope.launch {
            categoryStore.categoriesSortedByPodcastCount().collect { categories ->
                val data: MutableMap<Category, List<EpisodeWithPodcast>> = mutableMapOf()
                val deferredResults = categories.map { category ->
                    Timber.d("category: $category")
                    async {
                        val podcasts = categoryStore.getPodcastsAndLatestEpisodesByCategory(category.id).first()
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
}