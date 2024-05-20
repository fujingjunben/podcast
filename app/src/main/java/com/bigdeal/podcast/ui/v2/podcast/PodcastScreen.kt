package com.bigdeal.podcast.ui.v2.podcast

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.bigdeal.core.data.Podcast
import com.bigdeal.podcast.ui.v2.common.EpisodeList
import com.bigdeal.podcast.ui.v2.common.PodcastTitleCard
import com.bigdeal.podcast.R
import com.bigdeal.podcast.core.designsystem.component.HtmlTextContainer
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.ui.v2.favourite.EpisodeActions

@Composable
fun PodcastScreen(
    onBackPress: () -> Unit,
    navigateToEpisode: (String, String) -> Unit,
    onPlay: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PodcastViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsState()

    val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.87f)
    Column(modifier = modifier.systemBarsPadding()) {
        PodcastAppBar(
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth(),
            onBackPress
        )

        when (val state = uiState) {
            is PodcastUiState.Loading -> {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }

            is PodcastUiState.Ready -> {
                EpisodeList(
                    episodePlayerState = state.episodePlayerState,
                    episodeWithPodcastsPagingItems = state.episodes.collectAsLazyPagingItems(),
                    navigateToEpisode = navigateToEpisode,
                    showPodcastImage = false,
                    showSummary = true,
                    episodeActions = EpisodeActions(
                        onPlay = { playerEpisode ->
                            run {
                                onPlay(playerEpisode.id)
                                viewModel.play(playerEpisode)
                            }
                        },
                        onPause = viewModel::pause,
                        onAddToQueue = { playerEpisode ->
                            viewModel.addToQueue(playerEpisode)
                        },
                        onDownload = { playerEpisode -> viewModel.download(playerEpisode) },
                        onCancelDownload = { playerEpisode -> viewModel.cancelDownload(playerEpisode) },
                        onDeleteDownload = { playerEpisode -> viewModel.deleteDownload(playerEpisode) },
                    ),

                    header = {
                        PodcastInfo(state.podcast)
                    })
            }
        }
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
fun PodcastInfo(podcast: Podcast) {
    podcast.imageUrl?.let {
        PodcastTitleCard(podcastName = podcast.title, podcastImageUrl = it)
    }
    PodcastStatusBar()
    PodcastDescription(description = podcast.description)
}

@Composable
fun PodcastStatusBar() {

}

@Composable
fun PodcastDescription(description: String?) {
    if (description != null) {
        HtmlTextContainer(text = description) {
            Text(
                text = it,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
            )
        }
    }
}