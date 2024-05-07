/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.util.UnstableApi
import com.example.jetcaster.Graph
import com.example.jetcaster.data.DownloadEntity
import com.example.jetcaster.data.DownloadState
import com.example.jetcaster.data.EpisodeEntity
import com.example.jetcaster.data.EpisodeStore
import com.example.jetcaster.play.PlayerController
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {
    private val controller: PlayerController
        get() = Graph.playerController

    private val episodeStore: EpisodeStore
        get() = Graph.episodeStore


    private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            //Fetching the download id received with the broadcast
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            //Checking if the received broadcast is for our enqueued download by matching download id
            lifecycleScope.launch {
                episodeStore.episodeWithDownloadId(id).collect {
                    updateDownloadStatus(it)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // This app draws behind the system bars, so we want to handle fitting system windows
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            PodcastApp()
        }

        registerReceiver(
            onDownloadComplete, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            RECEIVER_NOT_EXPORTED
        );


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

    private fun updateDownloadStatus(episodeEntity: EpisodeEntity) {
        val downloadManager = this.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(episodeEntity.downloadId)
        val cursor = downloadManager.query(query)
        if (cursor.moveToFirst()) {
            val status = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
            when (status) {
                DownloadManager.STATUS_SUCCESSFUL ->
                    lifecycleScope.launch {
                        episodeStore.updateEpisode(
                            episodeEntity.copy(
                                fileUri = episodeEntity.fileUri,
                                downloadState = DownloadState.SUCCESS
                            )
                        )
                    }

                DownloadManager.STATUS_FAILED ->
                    lifecycleScope.launch {
                        episodeStore.updateEpisode(
                            episodeEntity.copy(
                                downloadState = DownloadState.FAILED,
                                fileUri = ""
                            )
                        )
                    }

            }

        }
    }
}
