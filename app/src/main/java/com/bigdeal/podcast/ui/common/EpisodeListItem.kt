package com.bigdeal.podcast.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.PlaylistAdd
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.bigdeal.core.data.DownloadState
import com.bigdeal.podcast.R
import com.bigdeal.podcast.core.designsystem.component.HtmlTextContainer
import com.bigdeal.podcast.core.designsystem.component.PodcastImage
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import timber.log.Timber

@Composable
fun EpisodeListItem(
    episodePlayerState: EpisodePlayerState,
    playerEpisode: PlayerEpisode,
    onClick: (String, String) -> Unit,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onAddToQueue: () -> Unit,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    showPodcastImage: Boolean,
    modifier: Modifier = Modifier,
    showSummary: Boolean = true,
) {
    Box(modifier = modifier.padding(vertical = 8.dp, horizontal = 16.dp)) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = MaterialTheme.colorScheme.surfaceContainer,
            onClick = { onClick(playerEpisode.podcastId, playerEpisode.id)})
         {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Part
                EpisodeListItemHeader(
                    episode = playerEpisode,
                    showPodcastImage = showPodcastImage,
                    showSummary = showSummary,
                    modifier = Modifier.padding(bottom = 8.dp)
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
private fun EpisodeListItemHeader(
    episode: PlayerEpisode,
    showPodcastImage: Boolean,
    showSummary: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        Column(
            modifier =
            Modifier
                .weight(1f)
                .padding(end = 16.dp)
        ) {
            Text(
                text = episode.title,
                maxLines = 2,
                minLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 2.dp)
            )

            if (showSummary) {
                HtmlTextContainer(text = episode.summary) {
                    Text(
                        text = it,
                        maxLines = 2,
                        minLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.titleSmall,
                    )
                }
            } else {
                Text(
                    text = episode.podcastName,
                    maxLines = 2,
                    minLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall,
                )
            }
        }
        if (showPodcastImage) {
            EpisodeListItemImage(
                imageUrl = episode.podcastImageUrl,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
            )
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

@Composable
private fun EpisodeListItemFooter(
    episodePlayerState: EpisodePlayerState,
    episode: PlayerEpisode,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onAddToQueue: () -> Unit,
    onDownload: () -> Unit,
    onCancelDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isPlaying =
        episodePlayerState.currentEpisode?.id == episode.id && episodePlayerState.isPlaying
    val icon =
        if (isPlaying) Icons.Filled.PauseCircleOutline else Icons.Default.PlayCircleOutline
    val onClickEvent = if (isPlaying) onPause else onPlay
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Image(
            imageVector = icon,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) { onClickEvent() }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
        )

        Text(
            text = if (episodePlayerState.currentEpisode?.id == episode.id
                && !episodePlayerState.timeElapsed.isZero
            ) {
                stringResource(
                    R.string.episode_left_duration,
                    episode.duration!!.minus(episodePlayerState.timeElapsed).toMinutes().toInt()
                )

            } else {
                stringResource(
                    R.string.episode_duration,
                    episode.duration?.toMinutes()?.toInt() ?: 0

                )
            },
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier
                .padding(horizontal = 8.dp)
                .weight(1f)
        )

        IconButton(
            onClick = {
                when (episode.downloadState) {
                    DownloadState.DOWNLOADING -> onCancelDownload()
                    DownloadState.NONE -> onDownload()
                    else -> Timber.d("download state is ${episode.downloadState}")
                }
            },
        ) {
            Icon(
                imageVector = when (episode.downloadState) {
                    DownloadState.DOWNLOADING -> Icons.Default.Downloading
                    DownloadState.FAILED, DownloadState.NONE -> Icons.Default.Download
                    DownloadState.SUCCESS -> Icons.Default.DownloadDone
                },
                contentDescription = null
            )
        }


        IconButton(
            onClick = {
                onAddToQueue()
            },
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconButton(
            onClick = { /* TODO */ },
        ) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}