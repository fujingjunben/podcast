package com.bigdeal.core.data

import com.bigdeal.core.Dispatcher
import com.bigdeal.core.PodcastDispatchers
import com.bigdeal.core.data.extension.toSHA256
import com.bigdeal.core.data.room.FeedDao
import com.bigdeal.core.data.room.TransactionRunner
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Data repository for Podcasts.
 */
class PodcastsRepository(
    private val podcastsFetcher: PodcastsFetcher,
    private val podcastStore: PodcastStore,
    private val episodeStore: EpisodeStore,
    private val categoryStore: CategoryStore,
    private val feedDao: FeedDao,
    private val transactionRunner: TransactionRunner,
    @Dispatcher(PodcastDispatchers.Main) mainDispatcher: CoroutineDispatcher
) {
    private var refreshingJob: Job? = null

    private val scope = CoroutineScope(mainDispatcher)

    suspend fun fetchFeeds(): Boolean {
        feedDao.insertAll(SampleFeeds.map { url ->
            FeedEntity(id = url.toSHA256(), url = url)
        })
        Timber.d("fetch feeds")
        return true
    }

    suspend fun sync(): Boolean {
        feedDao.queryAll()
            .map { feeds -> feeds.map { feed -> feed.url } }
            .take(1).collect { feedUrls ->
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
