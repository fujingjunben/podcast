

package com.bigdeal.core.data

import com.bigdeal.core.data.extension.toSHA256
import com.bigdeal.core.data.room.PodcastFollowedEntryDao
import com.bigdeal.core.data.room.PodcastsDao
import com.bigdeal.core.data.room.TransactionRunner
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Podcast] instances.
 */
class PodcastStore(
    private val podcastDao: PodcastsDao,
    private val podcastFollowedEntryDao: PodcastFollowedEntryDao,
    private val transactionRunner: TransactionRunner
) {
    /**
     * Return a flow containing the [Podcast] with the given [id].
     */
    fun podcastWithId(id: String): Flow<Podcast> {
        return podcastDao.podcastWithId(id)
    }

    /**
     * Returns a flow containing the entire collection of podcasts, sorted by the last episode
     * publish date for each podcast.
     */
    fun podcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.podcastsSortedByLastEpisode(limit)
    }

    /**
     * Returns a flow containing a list of all followed podcasts, sorted by the their last
     * episode date.
     */
    fun followedPodcastsSortedByLastEpisode(
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastDao.followedPodcastsSortedByLastEpisode(limit)
    }


    suspend fun followPodcast(podcastId: String) {
        podcastFollowedEntryDao.insert(PodcastFollowedEntry(podcastId = podcastId))
    }

    suspend fun togglePodcastFollowed(podcastId: String) = transactionRunner {
        if (podcastFollowedEntryDao.isPodcastFollowed(podcastId)) {
            unfollowPodcast(podcastId)
        } else {
            followPodcast(podcastId)
        }
    }

    suspend fun unfollowPodcast(podcastId: String) {
        podcastFollowedEntryDao.deleteWithPodcastId(podcastId)
    }

    /**
     * Add a new [Podcast] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addPodcast(podcast: Podcast) {
        podcastDao.insert(podcast)
    }

    suspend fun isEmpty(): Boolean = podcastDao.count() == 0
}
