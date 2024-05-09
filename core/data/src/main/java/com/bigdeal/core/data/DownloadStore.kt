package com.bigdeal.core.data

import com.bigdeal.core.data.room.DownloadDao
import kotlinx.coroutines.flow.Flow

class DownloadStore(
    private val downloadDao: DownloadDao
) {
    fun queryEpisodeById(id: Long): Flow<com.bigdeal.core.data.DownloadEntity> {
        return downloadDao.queryWithId(id)
    }

    fun queryEpisodeByUri(uri: String): Flow<com.bigdeal.core.data.DownloadEntity> {
        return downloadDao.queryWithEpisodeUri(uri)
    }

    suspend fun insert(entity: com.bigdeal.core.data.DownloadEntity) {
        downloadDao.insert(entity)
    }

    suspend fun update(entity: com.bigdeal.core.data.DownloadEntity) {
        downloadDao.update(entity)
    }

    suspend fun delete(id: Long) {
        downloadDao.delete(id)
    }

}