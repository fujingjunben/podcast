package com.example.jetcaster.ui.v2.episode

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.ui.v2.common.EpisodeListItem
import com.example.jetcaster.ui.v2.common.PodcastTitleCard

@Composable
fun EpisodeScreen(
    onBackPress: () -> Unit,
    navigateToPodcast: (String) -> Unit,
    modifier: Modifier,
    episodeScreenViewModel: EpisodeScreenViewModel = viewModel()
) {
    val uiState by episodeScreenViewModel.uiState.collectAsState()
    val item = uiState.episodeOfPodcast
    val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.87f)
    Column(modifier = modifier.systemBarsPadding()) {
        AppBar(
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth(),
            onBackPress
        )

        if (item != null) {
            PodcastTitleCard(podcast = item.podcast)
            EpisodeListItem(
                episode = item.episode,
                podcast = item.podcast,
                onClick = { podcastUri, episodeUri -> navigateToPodcast(podcastUri)},
                onPlay = { episodeScreenViewModel.play(item) },
                onToggleDownload = {},
                showPodcastImage = false,
                modifier = Modifier.fillMaxWidth(),
            )

            if (!item.episode.summary.isNullOrEmpty()) {
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)) {
                    Text(text = item.episode.summary)
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
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
    )
}
