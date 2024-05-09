package com.bigdeal.core.data

import com.bigdeal.core.data.database.dao.EpisodeRecordDao
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
     * Returns a flow containing the episode given [episodeUri].
     */
    fun episodeWithUri(episodeUri: String): Flow<EpisodeEntity> {
        return episodesDao.episode(episodeUri)
    }

    fun episodeWithDownloadId(id: Long): Flow<EpisodeEntity> {
        return episodesDao.episodeWithDownloadId(id)
    }

    /**
     * Returns a flow containing the list of episodes associated with the podcast with the
     * given [podcastUri].
     */
    fun episodesInPodcast(
        podcastUri: String,
        limit: Int = Integer.MAX_VALUE
    ): Flow<List<EpisodeEntity>> {
        return episodesDao.episodesForPodcastUri(podcastUri, limit)
    }

    fun episodeWhichIsPlaying(): Flow<List<EpisodeToPodcast>> {
        return episodesDao.episodeWhichIsPlaying()
    }

    /**
     * Add a new [EpisodeEntity] to this store.
     *
     * This automatically switches to the main thread to maintain thread consistency.
     */
    suspend fun addEpisodes(episodes: Collection<com.bigdeal.core.data.EpisodeEntity>) = episodesDao.insertAll(episodes)

    suspend fun isEmpty(): Boolean = episodesDao.count() == 0

    suspend fun updateEpisode(episode: EpisodeEntity) = episodesDao.update(episode)

    suspend fun updateEpisodeState(episodeStateEntity: EpisodeStateEntity)
    = episodeRecordDao.update(episodeStateEntity)

    fun queryEpisodeState(url: String) : Flow<EpisodeStateEntity> {
        return episodeRecordDao.queryRecordByUri(url)
    }


    fun episodeAndPodcastWithUri(episodeUri: String): Flow<EpisodeToPodcast> {
        return episodesDao.episodeAndPodcast(episodeUri)
    }

}
