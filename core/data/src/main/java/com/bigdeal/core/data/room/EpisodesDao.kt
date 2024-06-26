

package com.bigdeal.core.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.FollowedEpisodesToPodcast
import com.bigdeal.core.data.database.dao.BaseDao
import com.bigdeal.core.data.model.EpisodeWithPodcast
import kotlinx.coroutines.flow.Flow

/**
 * [Room] DAO for [EpisodeEntity] related operations.
 */
@Dao
abstract class EpisodesDao : BaseDao<EpisodeEntity> {

    @Query(
        """
        SELECT * FROM episodes WHERE id = :id
        """
    )
    abstract fun episode(id: String): Flow<EpisodeEntity>

    @Query(
        """
            SELECT * FROM episodes WHERE download_id = :id
        """
    )
    abstract fun episodeWithDownloadId(id: Long): Flow<EpisodeEntity>

    @Query(
        """
        SELECT * FROM episodes WHERE podcast_id = :podcastId
        ORDER BY datetime(published) DESC
        LIMIT :limit
        """
    )
    abstract fun episodesForPodcastId(
        podcastId: String,
        limit: Int
    ): Flow<List<EpisodeEntity>>


    @Transaction
    @Query("""
            SELECT * FROM episodes INNER JOIN podcasts ON episodes.podcast_id = podcasts.id WHERE episodes.podcast_id =:podcastId ORDER BY episodes.published DESC
        """
    )
    abstract fun getEpisodesInPodcast(podcastId: String): PagingSource<Int, EpisodeWithPodcast>

    @Transaction
    @Query("SELECT * FROM podcasts WHERE id IN (SELECT podcast_id FROM podcast_followed_entries)")
    abstract fun getFollowedEpisodesWithPodcast(): PagingSource<Int, FollowedEpisodesToPodcast>

    @Transaction
    @Query("""
            SELECT * FROM episodes INNER JOIN podcasts ON episodes.podcast_id = podcasts.id WHERE episodes.podcast_id IN (SELECT podcast_id FROM podcast_followed_entries) ORDER BY episodes.published DESC
        """
    )
    abstract fun getFollowedEpisodes(): PagingSource<Int, EpisodeWithPodcast>


    @Transaction
    @Query(
        """
        SELECT episodes.* FROM episodes 
        INNER JOIN podcasts ON podcasts.id = episodes.podcast_id
        """
    )
    abstract fun episodeWhichIsPlaying(
    ): Flow<List<EpisodeToPodcast>>


    @Transaction
    @Query(
        """
        SELECT episodes.* FROM episodes
        INNER JOIN podcast_category_entries ON episodes.podcast_id = podcast_category_entries.podcast_id
        WHERE category_id = :categoryId
        ORDER BY datetime(published) DESC
        LIMIT :limit
        """
    )
    abstract fun episodesFromPodcastsInCategory(
        categoryId: Long,
        limit: Int
    ): Flow<List<EpisodeToPodcast>>


    @Transaction
    @Query("""
        SELECT podcasts.*, episodes.*
        FROM podcasts
        INNER JOIN episodes ON podcasts.id = episodes.podcast_id
        INNER JOIN (
            SELECT podcast_category_entries.podcast_id, MAX(datetime(published)) as latest_published
            FROM episodes
            INNER JOIN podcast_category_entries ON episodes.podcast_id = podcast_category_entries.podcast_id
            WHERE category_id = :categoryId
            GROUP BY podcast_category_entries.podcast_id
        ) latest_episodes
        ON episodes.podcast_id = latest_episodes.podcast_id AND datetime(episodes.published) = latest_episodes.latest_published
        ORDER BY datetime(episodes.published) DESC
    """)
    abstract fun getPodcastsAndLatestEpisodesByCategory(categoryId: Long): Flow<List<EpisodeWithPodcast>>

    @Query(
        """
        SELECT episodes.* FROM episodes
        INNER JOIN podcasts ON episodes.podcast_id = podcasts.id
        WHERE episodes.id = :episodeId
        """
    )
    abstract fun episodeAndPodcast(episodeId: String): Flow<EpisodeToPodcast>


    @Query("SELECT COUNT(*) FROM episodes")
    abstract suspend fun count(): Int
}

