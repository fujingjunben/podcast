/*
 * Copyright 2024 The Android Open Source Project
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

package com.bigdeal.podcast.core.di

import android.app.DownloadManager
import android.content.Context
import com.bigdeal.core.Dispatcher
import com.bigdeal.core.PodcastDispatchers
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.MockEpisodePlayer
import com.bigdeal.podcast.core.player.service.PlayerController
import com.bigdeal.podcast.core.player.service.PlayerControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DomainDiModule {

    @Provides
    @Singleton
    fun providePlayerController(
    ): PlayerController =
        PlayerControllerImpl()

    @Provides
    @Singleton
    fun provideEpisodePlayer(
        @Dispatcher(PodcastDispatchers.Main) mainDispatcher: CoroutineDispatcher,
        playerController: PlayerController
    ): EpisodePlayer = MockEpisodePlayer(mainDispatcher, playerController)


    @Provides
    @Singleton
    fun providePodcastDownloadManager(
        @ApplicationContext context: Context,
        episodeStore: EpisodeStore,
        @Dispatcher(PodcastDispatchers.IO) ioDispatcher: CoroutineDispatcher
    ): PodcastDownloader =
        PodcastDownloader(
            context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager,
            episodeStore,
            context.getExternalFilesDir("cache"),
            ioDispatcher
        )
}
