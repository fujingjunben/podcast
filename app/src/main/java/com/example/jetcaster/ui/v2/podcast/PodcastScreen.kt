package com.example.jetcaster.ui.v2.podcast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.data.Podcast
import com.example.jetcaster.ui.v2.common.EpisodeList
import com.example.jetcaster.ui.v2.common.PodcastTitleCard

@Composable
fun PodcastScreen(
    onBackPress: () -> Unit,
    navigateToEpisode: (String, String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PodcastViewModel = viewModel()
) {

    val uiState = viewModel.uiState
    val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)
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
            header = {
                PodcastInfo(uiState.podcast)
            })
    }
}

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
                    contentDescription = stringResource(id = R.string.back)
                )
            }
        },
        backgroundColor = backgroundColor
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