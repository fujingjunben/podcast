package com.example.jetcaster.download

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.media3.common.MimeTypes
import com.example.jetcaster.data.Episode

class PodcastDownloader(private val context: Context) {
    private val downloadManager: DownloadManager by lazy {
        context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    }

    fun downloadEpisode(episode: Episode) {
        val request = DownloadManager.Request(Uri.parse(episode.url))
            .setMimeType(MimeTypes.AUDIO_MPEG)
            .setTitle(episode.title)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, episode.title)

        downloadManager.enqueue(request)
    }

    fun cancelDownload(downloadId: Long) {
        downloadManager.remove(downloadId)
    }
}