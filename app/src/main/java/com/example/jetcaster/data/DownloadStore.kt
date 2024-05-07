package com.example.jetcaster.data

import com.example.jetcaster.data.room.DownloadDao
import kotlinx.coroutines.flow.Flow

class DownloadStore(
    private val downloadDao: DownloadDao
) {
    fun queryEpisodeById(id: Long): Flow<DownloadEntity> {
        return downloadDao.queryWithId(id)
    }

    fun queryEpisodeByUri(uri: String): Flow<DownloadEntity> {
        return downloadDao.queryWithEpisodeUri(uri)
    }

    suspend fun insert(entity: DownloadEntity) {
        downloadDao.insert(entity)
    }

    suspend fun update(entity: DownloadEntity) {
        downloadDao.update(entity)
    }

    suspend fun delete(id: Long) {
        downloadDao.delete(id)
    }

}