package com.bigdeal.podcast.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.bigdeal.core.data.DownloadState
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.podcast.core.player.service.PlayerController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var controller: PlayerController

    @Inject lateinit var episodeStore: EpisodeStore


    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            Timber.d("onDownloadComplete: $id")
            //Checking if the received broadcast is for our enqueued download by matching download id
            lifecycleScope.launch {
                try {
                    val episodeEntity = episodeStore.episodeWithDownloadId(id).first()
                    if (episodeEntity == null) {
                        Timber.d("downloading episode not found")
                    } else {
                        updateDownloadStatus(episodeEntity)
                    }
                } catch (e: NoSuchElementException) {
                    Timber.d("not found download episode")
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            PodcastApp()
        }

        registerReceiver(
            onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
        );

        Timber.d("registerReceiver")

    }


    @androidx.annotation.OptIn(UnstableApi::class)
    override fun onStart() {
        super.onStart()
        controller?.init(this)
    }

    /* Overrides onStop from Fragment */
    override fun onDestroy() {
        super.onDestroy()
        controller?.release()
        unregisterReceiver(onDownloadComplete)
    }

    private suspend fun updateDownloadStatus(episodeEntity: EpisodeEntity) {
        val downloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(episodeEntity.downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            val reason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON).toInt()
            Timber.d("download status: $status, reason: $reason")
            when (status) {
                7, DownloadManager.STATUS_SUCCESSFUL -> {
                    val episode = episodeEntity.copy(
                        downloadState = DownloadState.SUCCESS
                    )
                    Timber.d("download success: ${episode.downloadState}")
                    episodeStore.updateEpisode(episode)
                }

                DownloadManager.STATUS_FAILED -> {
                    Timber.d("download failed")
                    episodeStore.updateEpisode(
                        episodeEntity.copy(
                            downloadState = DownloadState.FAILED,
                            fileUri = ""
                        )
                    )
                }

                else -> {
                    Timber.d("download unknown")
                    episodeStore.updateEpisode(
                        episodeEntity.copy(
                            downloadState = DownloadState.NONE,
                            fileUri = ""
                        )
                    )
                }
            }

        }
        cursor.close()
    }
}
