package com.bigdeal.core.download

import android.app.DownloadManager
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MimeTypes
import com.bigdeal.core.Dispatcher
import com.bigdeal.core.JetcasterDispatchers
import com.bigdeal.core.data.DownloadState
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.net.URLEncoder

class PodcastDownloader (
    private val downloadManager: DownloadManager,
    private val episodeStore: EpisodeStore,
    private val downloadDir: File?,
    @Dispatcher(JetcasterDispatchers.IO) ioDispatcher: CoroutineDispatcher
) {


    private val scope = CoroutineScope(ioDispatcher)

    fun downloadEpisode(episode: EpisodeEntity) {
        val file = File(downloadDir, "${episode.id}.mp3")
        val request = DownloadManager.Request(Uri.parse(episode.uri))
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .setTitle(episode.title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(file.toUri())

        Timber.d("download episode: ${file.path}")

        val id = downloadManager.enqueue(request)
        scope.launch {
            val episodeEntity = episodeStore.episodeWithId(episode.id).first()
            episodeStore.updateEpisode(
                episodeEntity.copy(
                    downloadId = id,
                    fileUri = "file:///${file.path}",
                    downloadState = DownloadState.DOWNLOADING
                )
            )
        }
    }

    fun cancelDownload(episode: EpisodeEntity) {
        Timber.d("download episode: cancel")
        scope.launch {
            queryDownloadStatus(episode)
            val episodeEntity = episodeStore.episodeWithId(episode.id).first()
//            downloadManager.remove(episodeEntity.downloadId)
            episodeStore.updateEpisode(
                episodeEntity.copy(
                    downloadId = -1L, fileUri = "",
                    downloadState = DownloadState.NONE
                )
            )
        }
    }

    fun deleteDownload(episode: EpisodeEntity) {}

    fun queryDownloadStatus(episode: EpisodeEntity) {
        val query = DownloadManager.Query()
        query.setFilterById(episode.downloadId)
        val cursor = downloadManager.query(query)
        if (cursor == null) {
            Timber.d("download manager doesn't find ${episode.downloadId}")
        }
        if (cursor.moveToFirst()) {
            val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val reason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON).toInt()
            Timber.d("download status: $status, reason: $reason")

        }
    }
}