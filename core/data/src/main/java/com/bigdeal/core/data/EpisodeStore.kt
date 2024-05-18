package com.bigdeal.core.data

import androidx.paging.Config
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.bigdeal.core.data.config.PAGE_SIZE
import com.bigdeal.core.data.database.dao.EpisodeRecordDao
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.core.data.room.EpisodesDao
import kotlinx.coroutines.flow.Flow

/**
 * A data repository for [EpisodeEntity] instances.
 */
class EpisodeStore(
    private val episodesDao: EpisodesDao,
    private val episodeRecordDao: EpisodeRecordDao
) {
    /**
     * Returns a flow containing the episode given [episodeId].
     */
    fun episodeWithId(episodeId: String): Flow<EpisodeEntity> {
        return episodesDao.episode(episodeId)
    }

    fun episodeWithDownloadId(id: Long): Flow<EpisodeEntity> {
        return episodesDao.episodeWithDownloadId(id)
    }

    /**
     * Returns a flow containing the list of episodes associated with the podcast with the
     * given [podcastUri].
     */
    fun episodesInPodcast(
        podcastId: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeEntity>> {
        return episodesDao.episodesForPodcastId(podcastId, limit)
    }

    fun episodesInPodcastPagingData(podcastId: String ): Flow<PagingData<EpisodeWithPodcast>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { episodesDao.getEpisodesInPodcast(podcastId)}
        ).flow
    }


    fun followedEpisodesPagingData(
    ): Flow<PagingData<EpisodeWithPodcast>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = { episodesDao.getFollowedEpisodes()}
        ).flow
    }

    fun episodeWhichIsPlaying(): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodeWhichIsPlaying()
    }

    /**
     * Add a new [EpisodeEntity] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addEpisodes(episodes: Collection<EpisodeEntity>) = episodesDao.insertAll(episodes)

    suspend fun isEmpty(): Boolean = episodesDao.count() == 0

    suspend fun updateEpisode(episode: EpisodeEntity) = episodesDao.update(episode)

    suspend fun updateEpisodeState(episodeStateEntity: EpisodeStateEntity)
    = episodeRecordDao.update(episodeStateEntity)

    fun queryEpisodeState(id: String) : Flow<EpisodeStateEntity> {
        return episodeRecordDao.queryRecordById(id)
    }


    fun episodeAndPodcastWithId(episodeId: String): Flow<EpisodeToPodcast> {
        return episodesDao.episodeAndPodcast(episodeId)
    }

}
