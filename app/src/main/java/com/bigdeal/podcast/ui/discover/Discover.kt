package com.bigdeal.podcast.ui.discover

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.bigdeal.core.data.Category
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.PodcastWithExtraInfo
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.podcast.R
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.ui.common.EpisodeListItem


@Composable
fun Discover(
    modifier: Modifier = Modifier,
    viewModel: DiscoverViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val playState = viewModel.episodePlayerState.collectAsState()

    when (val state = uiState) {
        is DiscoverUi.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
        }

        is DiscoverUi.Ready -> {
            val categories = state.podcastAndCategory.keys.toList()
            LazyColumn(modifier = modifier.systemBarsPadding()) {
                items(categories.size) { index ->
                    val category = categories[index]
                    val episodeWithPodcasts = state.podcastAndCategory[category]
                    episodeWithPodcasts?.let {
                        Column(modifier = modifier) {
                            CategoryHeader(
                                category = category,
                                podcasts = episodeWithPodcasts?.map { item -> item.podcast }
                                    ?: listOf(),
                                onPodcastUnfollowed = { id -> {} },
                                navigateToPodcast = { id -> {} }
                            )
                            PodcastInCategory(episodeWithPodcasts, playState.value)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PodcastInCategory(
    episodeWithPodcasts: List<EpisodeWithPodcast>,
    episodePlayerState: EpisodePlayerState
) {
    Column {
        episodeWithPodcasts.forEach { episodeWithPodcast ->
            EpisodeListItem(
                episodePlayerState = episodePlayerState,
                playerEpisode = episodeWithPodcast.toPlayerEpisode(),
                onClick = { _, _ -> },
                onPlay = { /*TODO*/ },
                onPause = { /*TODO*/ },
                onAddToQueue = { /*TODO*/ },
                onDownload = { /*TODO*/ },
                onCancelDownload = { /*TODO*/ },
                showPodcastImage = true
            )
        }
    }
}

@Composable
fun CategoryHeader(
    category: Category,
    podcasts: List<Podcast>,
    onPodcastUnfollowed: (id: String) -> Unit,
    navigateToPodcast: (id: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        Text(text = category.name)
        LazyRow(modifier = modifier.height(100.dp)) {
            items(podcasts, key = { podcast -> podcast.id }) { podcast ->
                PodcastCarouselItem(
                    podcastImageUrl = podcast.imageUrl,
                    podcastTitle = podcast.title,
                    onUnfollowedClick = { onPodcastUnfollowed(podcast.id) },
                    navigateToPodcast = { navigateToPodcast(podcast.id) },
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun PodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    podcastTitle: String? = null,
    navigateToPodcast: () -> Unit,
    onUnfollowedClick: () -> Unit,
) {
    Column(
        modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
        ) {
            if (podcastImageUrl != null) {
                AsyncImage(model = podcastImageUrl,
                    contentDescription = podcastTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { navigateToPodcast() })
            }

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
fun ToggleFollowPodcastIconButton(
    isFollowed: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(if (isFollowed) R.string.cd_unfollow else R.string.cd_follow)
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics {
            onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            // TODO: think about animating these icons
            imageVector = when {
                isFollowed -> Icons.Default.Check
                else -> Icons.Default.Add
            },
            contentDescription = when {
                isFollowed -> stringResource(R.string.cd_following)
                else -> stringResource(R.string.cd_not_following)
            },
            tint = animateColorAsState(
                when {
                    isFollowed -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            ).value,
            modifier = Modifier
                .shadow(
                    elevation = animateDpAsState(if (isFollowed) 0.dp else 1.dp).value,
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = animateColorAsState(
                        when {
                            isFollowed -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceContainerHighest
                        }
                    ).value,
                    shape = CircleShape
                )
                .padding(4.dp)
        )
    }
}

