package com.bigdeal.podcast.ui.v2.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.ui.v2.favourite.EpisodeActions

@Composable
fun EpisodeList(
    episodePlayerState: EpisodePlayerState,
    episodes: List<EpisodeOfPodcast>,
    navigateToEpisode: (String, String) -> Unit,
    episodeActions: EpisodeActions,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = true,
    header: @Composable LazyItemScope.() -> Unit = {},
) {
    LazyColumn(
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            header(this)
        }
        items(episodes, key = { it.episode.id }) { item ->
            EpisodeListItem(
                episodePlayerState = episodePlayerState,
                playerEpisode = item.toEpisode(),
                onClick = navigateToEpisode,
                onPlay = { episodeActions.onPlay(item.toEpisode()) },
                onPause = episodeActions.onPause,
                onAddToQueue = { episodeActions.onAddToQueue(item.toEpisode()) },
                onDownload = { episodeActions.onDownload(item.toEpisode()) },
                onCancelDownload = { episodeActions.onCancelDownload(item.toEpisode()) },
                showPodcastImage = showPodcastImage,
                showSummary = showSummary,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}