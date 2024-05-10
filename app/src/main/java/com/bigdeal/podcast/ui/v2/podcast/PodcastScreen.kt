package com.bigdeal.podcast.ui.v2.podcast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bigdeal.core.data.Podcast
import com.bigdeal.podcast.ui.v2.common.EpisodeList
import com.bigdeal.podcast.ui.v2.common.PodcastTitleCard
import com.bigdeal.podcast.R

@Composable
fun PodcastScreen(
    onBackPress: () -> Unit,
    navigateToEpisode: (String, String) -> Unit,
    onPlay: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PodcastViewModel = hiltViewModel()
) {

    val uiState = viewModel.uiState
    val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.87f)
    Column(modifier = modifier.systemBarsPadding()) {
        PodcastAppBar(
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth(),
            onBackPress
        )
        EpisodeList(
            episodes = uiState.episodeOfPodcasts,
            navigateToEpisode = navigateToEpisode,
            showPodcastImage = false,
            onPlay = { playerEpisode -> onPlay(playerEpisode.id) },
            header = {
                PodcastInfo(uiState.podcast)
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PodcastAppBar(
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
                    contentDescription = stringResource(id = R.string.cd_back)
                )
            }
        },
    )
}

@Composable
fun PodcastInfo(podcast: Podcast?) {
    if (podcast != null) {
        PodcastTitleCard(podcast = podcast)
        PodcastStatusBar()
        PodcastDescription(description = podcast.description)
    }
}

@Composable
fun PodcastStatusBar() {

}

@Composable
fun PodcastDescription(description: String?) {
    if (description != null) {
        Text(
            text = description,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}