

package com.bigdeal.core.data

import com.bigdeal.core.Dispatcher
import com.bigdeal.core.JetcasterDispatchers
import com.bigdeal.core.data.room.FeedDao
import com.bigdeal.core.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

/**
 * Data repository for Podcasts.
 */
class PodcastsRepository (
    private val podcastsFetcher: PodcastsFetcher,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val categoryStore: CategoryStore,
    private val feedDao: FeedDao,
    private val transactionRunner: TransactionRunner,
    @Dispatcher(JetcasterDispatchers.Main) mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun sync():Boolean {
        feedDao.queryAll()
            .map { feeds -> feeds.map { feed -> feed.url } }
            .take(1).collect {feedUrls ->
                Timber.d("sync podcast: $feedUrls")
                fetchPodcasts(feedUrls)
            }

        return true
    }

    suspend fun updatePodcasts(feedUrls: List<String>, force: Boolean) {
        Timber.d("podcast feedUrls: $feedUrls")
        if (refreshingJob?.isActive == true) {
            refreshingJob?.join()
        } else if (force || podcastStore.isEmpty()) {
            refreshingJob = scope.launch {
                fetchPodcasts(feedUrls)
            }
        }
    }

    suspend fun fetchPodcasts(feedUrls: List<String>) {
        // Now fetch the podcasts, and add each to each store
        podcastsFetcher(feedUrls)
            .filter { it is PodcastRssResponse.Success }
            .map { it as PodcastRssResponse.Success }
            .collect { (podcast, episodes, categories) ->
                Timber.d("podcast fetch: ${podcast.title}")
                transactionRunner {
                    podcastStore.addPodcast(podcast)
                    episodeStore.addEpisodes(episodes)

                    categories.forEach { category ->
                        // First insert the category
                        val categoryId = categoryStore.addCategory(category)
                        // Now we can add the podcast to the category
                        categoryStore.addPodcastToCategory(
                            podcastId = podcast.id,
                            categoryId = categoryId
                        )
                    }
                }
            }
    }
}
