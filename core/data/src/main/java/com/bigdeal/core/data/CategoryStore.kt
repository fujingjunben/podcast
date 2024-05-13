

package com.bigdeal.core.data

import com.bigdeal.core.data.room.CategoriesDao
import com.bigdeal.core.data.room.EpisodesDao
import com.bigdeal.core.data.room.PodcastCategoryEntryDao
import com.bigdeal.core.data.room.PodcastsDao
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [Category] instances.
 */
class CategoryStore(
    private val categoriesDao: CategoriesDao,
    private val categoryEntryDao: PodcastCategoryEntryDao,
    private val episodesDao: EpisodesDao,
    private val podcastsDao: PodcastsDao
) {
    /**
     * Returns a flow containing a list of categories which is sorted by the number
     * of podcasts in each category.
     */
    fun categoriesSortedByPodcastCount(
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<Category>> {
        return categoriesDao.categoriesSortedByPodcastCount(limit)
    }

    /**
     * Returns a flow containing a list of podcasts in the category with the given [categoryId],
     * sorted by the their last episode date.
     */
    fun podcastsInCategorySortedByPodcastCount(
        categoryId: Long,
        limit: Int = Int.MAX_VALUE
    ): Flow<List<PodcastWithExtraInfo>> {
        return podcastsDao.podcastsInCategorySortedByLastEpisode(categoryId, limit)
    }

    /**
     * Returns a flow containing a list of episodes from podcasts in the category with the
     * given [categoryId], sorted by the their last episode date.
     */
    fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodesFromPodcastsInCategory(categoryId, limit)
    }

    /**
     * Adds the category to the database if it doesn't already exist.
     *
     * @return the id of the newly inserted/existing category
     */
    suspend fun addCategory(category: Category): Long {
        return when (val local = categoriesDao.getCategoryWithName(category.name)) {
            null -> categoriesDao.insert(category)
            else -> local.id
        }
    }

    suspend fun addPodcastToCategory(podcastId: String, categoryId: Long) {
        categoryEntryDao.insert(
            PodcastCategoryEntry(
                podcastId = podcastId,
                categoryId = categoryId
            )
        )
    }
}
