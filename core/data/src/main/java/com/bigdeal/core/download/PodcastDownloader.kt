package com.bigdeal.core.download

import android.app.DownloadManager
import android.net.Uri
import android.util.Base64
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
import javax.inject.Inject

class PodcastDownloader (
    private val downloadManager: DownloadManager,
    private val episodeStore: EpisodeStore,
    private val downloadDir: File?,
    @Dispatcher(JetcasterDispatchers.IO) ioDispatcher: CoroutineDispatcher
) {


    private val scope = CoroutineScope(ioDispatcher)

    fun downloadEpisode(episode: EpisodeEntity) {
        val fileName = URLEncoder.encode(episode.title, "UTF-8")
        val file = File(downloadDir, "$fileName.mp3")
        val request = DownloadManager.Request(Uri.parse(episode.uri))
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .setTitle(episode.title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(file.toUri())

        Timber.d("download episode: ${file.path}")

        val id = downloadManager.enqueue(request)
        scope.launch {
            val episodeEntity = episodeStore.episodeWithUri(episode.uri).first()
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
            val episodeEntity = episodeStore.episodeWithUri(episode.uri).first()
            downloadManager.remove(episodeEntity.downloadId)
            episodeStore.updateEpisode(
                episodeEntity.copy(
                    downloadId = -1L, fileUri = "",
                    downloadState = DownloadState.NONE
                )
            )
        }
    }
}