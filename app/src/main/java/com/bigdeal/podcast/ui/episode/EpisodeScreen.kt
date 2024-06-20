package com.bigdeal.podcast.ui.episode

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigdeal.core.data.DownloadState
import com.bigdeal.podcast.R
import com.bigdeal.podcast.core.designsystem.component.HtmlTextContainer
import com.bigdeal.podcast.core.designsystem.component.PodcastImage
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.ui.common.EpisodeListItem
import com.bigdeal.podcast.ui.common.EpisodeListItemFooter
import com.bigdeal.podcast.ui.common.PodcastTitleCard
import com.bigdeal.podcast.ui.common.ScrollableText
import timber.log.Timber

@Composable
fun EpisodeScreen(
    onBackPress: () -> Unit,
    navigateToPodcast: (String) -> Unit,
    onPlay: (String) -> Unit,
    modifier: Modifier,
    viewModel: EpisodeScreenViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState
    val playState = viewModel.episodePlayerState.collectAsState()
    val playerEpisode = uiState.episode
    Column {
        AppBar(
            modifier = Modifier.fillMaxWidth(),
            onBackPress
        )
        if (playerEpisode != null) {
            PodcastTitleCard(
                podcastName = playerEpisode.podcastName,
                podcastImageUrl = playerEpisode.podcastImageUrl,
                onClick = { navigateToPodcast(playerEpisode.podcastId) })


            EpisodeCard(
                episodePlayerState = playState.value,
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
                modifier = Modifier.fillMaxWidth(),
            )

            if (playerEpisode.summary.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    ScrollableText {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppBar(
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

@Composable
fun EpisodeCard(
    episodePlayerState: EpisodePlayerState,
    playerEpisode: PlayerEpisode,
    onClick: (String, String) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onAddToQueue: () -> Unit,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(playerEpisode.podcastId, playerEpisode.id) })
        {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Part

                Text(
                    text = playerEpisode.title,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
                // Bottom Part
                EpisodeListItemFooter(
                    episodePlayerState = episodePlayerState,
                    episode = playerEpisode,
                    onPlay = onPlay,
                    onPause = onPause,
                    onAddToQueue = onAddToQueue,
                    onDownload = onDownload,
                    onCancelDownload = onCancelDownload,
                )
            }
        }
    }
}


@Composable
private fun EpisodeListItemImage(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    PodcastImage(
        podcastImageUrl = imageUrl,
        contentDescription = null,
        modifier = modifier,
    )
}

//@Composable
//private fun EpisodeListItemFooter(
//    episodePlayerState: EpisodePlayerState,
//    episode: PlayerEpisode,
//    onPlay: () -> Unit,
//    onPause: () -> Unit,
//    onAddToQueue: () -> Unit,
//    onDownload: () -> Unit,
//    onCancelDownload: () -> Unit,
//    modifier: Modifier = Modifier
//) {
//    val isPlaying =
//        episodePlayerState.currentEpisode?.id == episode.id && episodePlayerState.isPlaying
//    val icon =
//        if (isPlaying) Icons.Filled.PauseCircleOutline else Icons.Default.PlayCircleOutline
//    val onClickEvent = if (isPlaying) onPause else onPlay
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        modifier = modifier
//    ) {
//        Image(
//            imageVector = icon,
//            contentDescription = stringResource(R.string.cd_play),
//            contentScale = ContentScale.Fit,
//            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
//            modifier = Modifier
//                .clickable(
//                    interactionSource = remember { MutableInteractionSource() },
//                    indication = rememberRipple(bounded = false, radius = 24.dp)
//                ) { onClickEvent() }
//                .size(48.dp)
//                .padding(6.dp)
//                .semantics { role = Role.Button }
//        )
//
//        Text(
//            text = if (episodePlayerState.currentEpisode?.id == episode.id
//                && !episodePlayerState.timeElapsed.isZero
//            ) {
//                stringResource(
//                    R.string.episode_left_duration,
//                    episode.duration!!.minus(episodePlayerState.timeElapsed).toMinutes().toInt()
//                )
//
//            } else {
//                stringResource(
//                    R.string.episode_duration,
//                    episode.duration!!.toMinutes().toInt()
//
//                )
//            },
//            maxLines = 1,
//            overflow = TextOverflow.Ellipsis,
//            style = MaterialTheme.typography.bodySmall,
//            modifier = Modifier
//                .padding(horizontal = 8.dp)
//                .weight(1f)
//        )
//
//        IconButton(
//            onClick = {
//                when (episode.downloadState) {
//                    DownloadState.DOWNLOADING -> onCancelDownload()
//                    DownloadState.NONE -> onDownload()
//                    else -> Timber.d("download state is ${episode.downloadState}")
//                }
//            },
//        ) {
//            Icon(
//                imageVector = when (episode.downloadState) {
//                    DownloadState.DOWNLOADING -> Icons.Default.Downloading
//                    DownloadState.FAILED, DownloadState.NONE -> Icons.Default.Download
//                    DownloadState.SUCCESS -> Icons.Default.DownloadDone
//                },
//                contentDescription = null
//            )
//        }
//
//
//        IconButton(
//            onClick = {
//                onAddToQueue()
//            },
//        ) {
//            Icon(
//                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
//                contentDescription = stringResource(R.string.cd_add),
//                tint = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//
//        IconButton(
//            onClick = { /* TODO */ },
//        ) {
//            Icon(
//                imageVector = Icons.Default.MoreVert,
//                contentDescription = stringResource(R.string.cd_more),
//                tint = MaterialTheme.colorScheme.onSurfaceVariant
//            )
//        }
//    }
//}