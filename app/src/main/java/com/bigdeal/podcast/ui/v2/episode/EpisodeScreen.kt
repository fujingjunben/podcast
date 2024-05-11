package com.bigdeal.podcast.ui.v2.episode

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigdeal.core.data.url
import com.bigdeal.podcast.R
import com.bigdeal.podcast.core.designsystem.component.HtmlTextContainer
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.ui.v2.common.EpisodeListItem
import com.bigdeal.podcast.ui.v2.common.PodcastTitleCard

@Composable
fun EpisodeScreen(
    onBackPress: () -> Unit,
    navigateToPodcast: (String) -> Unit,
    onPlay: (String) -> Unit,
    modifier: Modifier,
    viewModel: EpisodeScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val playerEpisode = uiState.episodePlayerState.currentEpisode
    val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.87f)
    Column(modifier = modifier.systemBarsPadding()) {
        AppBar(
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth(),
            onBackPress
        )
        if (playerEpisode != null) {
            PodcastTitleCard(playerEpisode)
            EpisodeListItem(
                episodePlayerState = viewModel.uiState.episodePlayerState,
                playerEpisode = playerEpisode,
                onClick = { podcastId, _ -> navigateToPodcast(podcastId) },
                onPlay = {
                    onPlay(playerEpisode.id)
                    viewModel.play(playerEpisode)
                },
                onPause = viewModel::pause,
                onAddToQueue = { },
                onDownload = {},
                onCancelDownload = {},
                showPodcastImage = false,
                showSummary = true,
                modifier = Modifier.fillMaxWidth(),
            )

            if (playerEpisode.summary.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    HtmlTextContainer(text =  playerEpisode.summary) {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyMedium,
                            color = LocalContentColor.current
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
    backgroundColor: Color,
    modifier: Modifier,
    onBackPress: () -> Unit,
) {
    TopAppBar(
        modifier = modifier,
        title = {
            IconButton(onClick = onBackPress) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
    )
}
