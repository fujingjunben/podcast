

package com.bigdeal.core.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastWithExtraInfo
import com.bigdeal.core.data.database.dao.BaseDao
import kotlinx.coroutines.flow.Flow

/**
 * [Room] DAO for [Podcast] related operations.
 */
@Dao
abstract class PodcastsDao: BaseDao<Podcast>{
    @Query("SELECT * FROM podcasts WHERE id = :id")
    abstract fun podcastWithId(id: String): Flow<Podcast>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_id IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_id, MAX(published) AS last_episode_date
            FROM episodes
            GROUP BY podcast_id
        ) episodes ON podcasts.id = episodes.podcast_id
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_id = episodes.podcast_id
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_id IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT episodes.podcast_id, MAX(published) AS last_episode_date
            FROM episodes
            INNER JOIN podcast_category_entries ON episodes.podcast_id = podcast_category_entries.podcast_id
            WHERE category_id = :categoryId
            GROUP BY episodes.podcast_id
        ) inner_query ON podcasts.id = inner_query.podcast_id
        LEFT JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_id = inner_query.podcast_id
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun podcastsInCategorySortedByLastEpisode(
        categoryId: Long,
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Transaction
    @Query(
        """
        SELECT podcasts.*, last_episode_date, (followed_entries.podcast_id IS NOT NULL) AS is_followed
        FROM podcasts 
        INNER JOIN (
            SELECT podcast_id, MAX(published) AS last_episode_date FROM episodes GROUP BY podcast_id
        ) episodes ON podcasts.id = episodes.podcast_id
        INNER JOIN podcast_followed_entries AS followed_entries ON followed_entries.podcast_id = episodes.podcast_id
        ORDER BY datetime(last_episode_date) DESC
        LIMIT :limit
        """
    )
    abstract fun followedPodcastsSortedByLastEpisode(
        limit: Int
    ): Flow<List<PodcastWithExtraInfo>>

    @Query("SELECT COUNT(*) FROM podcasts")
    abstract suspend fun count(): Int
}
