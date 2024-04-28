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

package com.example.jetcaster.ui.home.category

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetcaster.data.PodcastWithExtraInfo
import com.example.jetcaster.ui.theme.Keyline1
import com.example.jetcaster.ui.v2.common.EpisodeList
import com.example.jetcaster.util.ToggleFollowPodcastIconButton
import com.example.jetcaster.util.viewModelProviderFactoryOf

@Composable
fun PodcastCategory(
    categoryId: Long,
    navigateToEpisode: (String, String) -> Unit,
    navigateToPodcast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    /**
     * CategoryEpisodeListViewModel requires the category as part of it's constructor, therefore
     * we need to assist with it's instantiation with a custom factory and custom key.
     */
    val viewModel: PodcastCategoryViewModel = viewModel(
        // We use a custom key, using the category parameter
        key = "category_list_$categoryId",
        factory = viewModelProviderFactoryOf { PodcastCategoryViewModel(categoryId) }
    )

    val viewState by viewModel.state.collectAsState()

    /**
     * TODO: reset scroll position when category changes
     */
    Column(modifier = modifier) {
        CategoryPodcasts(viewState.topPodcasts, navigateToPodcast, viewModel)
        EpisodeList(viewState.episodes, navigateToEpisode)
    }
}

@Composable
private fun CategoryPodcasts(
    topPodcasts: List<PodcastWithExtraInfo>,
    navigateToPodcast: (String) -> Unit,
    viewModel: PodcastCategoryViewModel
) {
    CategoryPodcastRow(
        podcasts = topPodcasts,
        onTogglePodcastFollowed = viewModel::onTogglePodcastFollowed,
        navigateToPodcast = navigateToPodcast,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun CategoryPodcastRow(
    podcasts: List<PodcastWithExtraInfo>,
    onTogglePodcastFollowed: (String) -> Unit,
    navigateToPodcast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lastIndex = podcasts.size - 1
    LazyRow(
        modifier = modifier,
        contentPadding = PaddingValues(start = Keyline1, top = 8.dp, end = Keyline1, bottom = 24.dp)
    ) {
        itemsIndexed(items = podcasts) { index: Int, (podcast, _, isFollowed): PodcastWithExtraInfo ->
            TopPodcastRowItem(
                podcastTitle = podcast.title,
                podcastImageUrl = podcast.imageUrl,
                isFollowed = isFollowed,
                navigateToPodcast = {navigateToPodcast(podcast.uri)},
                onToggleFollowClicked = { onTogglePodcastFollowed(podcast.uri) },
                modifier = Modifier.width(128.dp)
            )

            if (index < lastIndex) Spacer(Modifier.width(24.dp))
        }
    }
}

@Composable
private fun TopPodcastRowItem(
    podcastTitle: String,
    isFollowed: Boolean,
    modifier: Modifier = Modifier,
    navigateToPodcast: () -> Unit,
    onToggleFollowClicked: () -> Unit,
    podcastImageUrl: String? = null,
) {
    Column(
        modifier.semantics(mergeDescendants = true) {}
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .align(Alignment.CenterHorizontally)
        ) {
            if (podcastImageUrl != null) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(podcastImageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { navigateToPodcast() }
                )
            }

            ToggleFollowPodcastIconButton(
                onClick = onToggleFollowClicked,
                isFollowed = isFollowed,
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        Text(
            text = podcastTitle,
            style = MaterialTheme.typography.body2,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        )
    }
}