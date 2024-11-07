package com.bigdeal.podcast.ui.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.model.toPlayerEpisode

data class EpisodeActions(
    val onPlay: (playerEpisode: PlayerEpisode) -> Unit,
    val onPause: () -> Unit,
    val onAddToQueue: (playerEpisode: PlayerEpisode) -> Unit,
    val onDownload: (playerEpisode: PlayerEpisode) -> Unit,
    val onCancelDownload: (playerEpisode: PlayerEpisode) -> Unit,
    val onDeleteDownload: (playerEpisode: PlayerEpisode) -> Unit,
    val onSaveAs: (playerEpisode: PlayerEpisode) -> Unit,
)

@Composable
fun EpisodeList(
    episodePlayerState: EpisodePlayerState,
    episodeWithPodcastsPagingItems: LazyPagingItems<EpisodeWithPodcast>,
    navigateToEpisode: (String, String) -> Unit,
    episodeActions: EpisodeActions,
    modifier: Modifier = Modifier,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = true,
    header: @Composable LazyItemScope.() -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            header(this)
        }
        items(episodeWithPodcastsPagingItems.itemCount) { index ->
            val episodeWithPodcast = episodeWithPodcastsPagingItems[index]
            episodeWithPodcast?.let {item ->
                EpisodeListItem(
                    episodePlayerState = episodePlayerState,
                    playerEpisode = item.toPlayerEpisode(),
                    onClick = navigateToEpisode,
                    onPlay = { episodeActions.onPlay(item.toPlayerEpisode()) },
                    onPause = episodeActions.onPause,
                    onAddToQueue = { episodeActions.onAddToQueue(item.toPlayerEpisode()) },
                    onDownload = { episodeActions.onDownload(item.toPlayerEpisode()) },
                    onCancelDownload = { episodeActions.onCancelDownload(item.toPlayerEpisode()) },
                    showPodcastImage = showPodcastImage,
                    showSummary = showSummary,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }

        episodeWithPodcastsPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.fillParentMaxSize())
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
                loadState.append is LoadState.Error -> {
                    item {
                        Text(text = "Error loading data")
                    }
                }
            }
        }

    }
}