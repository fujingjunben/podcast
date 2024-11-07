package com.bigdeal.podcast.core.download

import android.app.DownloadManager
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MimeTypes
import com.bigdeal.core.Dispatcher
import com.bigdeal.core.PodcastDispatchers
import com.bigdeal.core.data.DownloadState
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

class PodcastDownloader (
    private val downloadManager: DownloadManager,
    private val episodeStore: EpisodeStore,
    private val downloadDir: File?,
    @Dispatcher(PodcastDispatchers.IO) ioDispatcher: CoroutineDispatcher
) {


    private val scope = CoroutineScope(ioDispatcher)

    fun downloadEpisode(episode: PlayerEpisode) {
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

    fun cancelDownload(episode: PlayerEpisode) {
        Timber.d("download episode: cancel")
        scope.launch {
            queryDownloadStatus(episode.downloadId)
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

    fun queryDownloadStatus(downloadId: Long) {
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor = downloadManager.query(query)
        if (cursor == null) {
            Timber.d("download manager doesn't find $downloadId")
        }
        if (cursor.moveToFirst()) {
            val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val reason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON).toInt()
            Timber.d("download status: $status, reason: $reason")

        }
    }

    fun saveAs(episode: PlayerEpisode) {
        val file = File(episode.)
    }
}