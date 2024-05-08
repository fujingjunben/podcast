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

package com.example.jetcaster.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetcaster.Graph
import com.example.jetcaster.data.FeedEntity
import com.example.jetcaster.data.FeedRepository
import com.example.jetcaster.data.PodcastStore
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.data.PodcastsRepository
import com.example.jetcaster.data.SampleFeeds
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber

class HomeViewModel(
    private val podcastsRepository: PodcastsRepository = Graph.podcastRepository,
    private val feedRepository: FeedRepository = Graph.feedRepository
) : ViewModel() {

    private val refreshing = MutableStateFlow(false)

    init {
        refresh(force = false)
    }

    fun forceRefresh() {
        refresh(true)
    }

    private fun refresh(force: Boolean) {
        viewModelScope.launch {
            runCatching {
                feedRepository.feedFlow.map { feeds -> feeds.map { feed -> feed.url } }
                    .collect { feedUrls ->
                        Timber.d("refresh podcast: $feedUrls")
                        refreshing.value = true
                        podcastsRepository.updatePodcasts(SampleFeeds + feedUrls, true)
                    }
            }
            // TODO: look at result of runCatching and show any errors

            refreshing.value = false
        }
    }
}

