package com.bigdeal.podcast.ui.v2.episode

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigdeal.podcast.core.designsystem.component.HtmlTextContainer
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
            PodcastTitleCard(
                playerEpisode,
                onClick = { navigateToPodcast(playerEpisode.podcastId) })
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
                onDownload = { viewModel.download(playerEpisode) },
                onCancelDownload = {
                    viewModel.cancelDownload(playerEpisode)
                },
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
                    HtmlTextContainer(text = playerEpisode.summary) {
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
