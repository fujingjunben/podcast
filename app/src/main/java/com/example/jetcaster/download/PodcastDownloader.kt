package com.example.jetcaster.download

import android.app.DownloadManager
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MimeTypes
import com.example.jetcaster.data.DownloadState
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.EpisodeStore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.io.File

class PodcastDownloader(
    private val downloadManager: DownloadManager,
    private val episodeStore: EpisodeStore,
    private val downloadDir: File?,
    ioDispatcher: CoroutineDispatcher
) {


    private val scope = CoroutineScope(ioDispatcher)

    fun downloadEpisode(episode: Episode) {
        val file = File(downloadDir, episode.title + ".mp3")
        val request = DownloadManager.Request(Uri.parse(episode.url))
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .setTitle(episode.title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationUri(file.toUri())

        val id = downloadManager.enqueue(request)
        scope.launch {
            episodeStore.episodeWithUri(episode.url).collect {
                episodeStore.updateEpisode(it.copy(downloadId = id,
                    fileUri = file.path,
                    downloadState = DownloadState.DOWNLOADING))
            }
        }
    }

    fun cancelDownload(episode: Episode) {
        scope.launch {
            episodeStore.episodeWithUri(episode.url).collect {
                downloadManager.remove(it.downloadId)
                episodeStore.updateEpisode(it.copy(downloadId = -1L, fileUri = "",
                    downloadState = DownloadState.NONE))
            }
        }
    }
}