

package com.bigdeal.core.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.database.dao.BaseDao
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

