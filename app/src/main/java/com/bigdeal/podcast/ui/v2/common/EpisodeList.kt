package com.bigdeal.podcast.ui.v2.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.DownloadDone
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PauseCircleFilled
import androidx.compose.material.icons.filled.PauseCircleOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bigdeal.core.data.DownloadState
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.podcast.ui.theme.Keyline1
import timber.log.Timber
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import com.bigdeal.podcast.R
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.ui.v2.episode.PlayerUiState
import com.bigdeal.podcast.ui.v2.favourite.EpisodeActions

@Composable
fun EpisodeList(
    episodePlayerState: EpisodePlayerState,
    episodes: List<EpisodeOfPodcast>,
    navigateToEpisode: (String, String) -> Unit,
    episodeActions: EpisodeActions,
    showPodcastImage: Boolean = true,
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
                onDownload = { episodeActions.onDownload(item.episode) },
                onCancelDownload = { episodeActions.onCancelDownload(item.episode) },
                showPodcastImage = showPodcastImage,
                modifier = Modifier.fillParentMaxWidth()
            )
        }
    }
}

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
) {
    ConstraintLayout(modifier = modifier.clickable {
        onClick(playerEpisode.podcastId, playerEpisode.id)
    }) {
        val (
            divider, publishDate, episodeTitle, podcastTitle, image, playIcon,
            date, downloadIcon, addPlaylist, overflow
        ) = createRefs()

        Divider(
            Modifier.constrainAs(divider) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
                width = Dimension.fillToConstraints
            }
        )

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
                text = MediumDateFormatter.format(playerEpisode.published),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.constrainAs(publishDate) {
                    linkTo(
                        start = parent.start,
                        end = parent.end,
                        startMargin = Keyline1,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(divider.bottom, 16.dp)
                    height = Dimension.preferredWrapContent
                    width = Dimension.preferredWrapContent
                },
            )
        }

        // If we have an image Url, we can show it using Coil
        if (showPodcastImage) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(playerEpisode.podcastImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .constrainAs(image) {
                        end.linkTo(parent.end, 16.dp)
                        top.linkTo(publishDate.bottom, 16.dp)
                    },
            )
        }

        Text(
            text = playerEpisode.title,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.titleSmall,
            modifier = Modifier.constrainAs(episodeTitle) {
                linkTo(
                    start = parent.start,
                    end = image.start,
                    startMargin = Keyline1,
                    endMargin = 16.dp,
                    bias = 0f
                )
                top.linkTo(publishDate.bottom, 16.dp)
                height = Dimension.preferredWrapContent
                width = Dimension.preferredWrapContent
            }
        )

        val titleImageBarrier = createBottomBarrier(podcastTitle, image)

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Text(
                text = playerEpisode.podcastName,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.constrainAs(podcastTitle) {
                    linkTo(
                        start = parent.start,
                        end = image.start,
                        startMargin = Keyline1,
                        endMargin = 16.dp,
                        bias = 0f
                    )
                    top.linkTo(episodeTitle.bottom, 6.dp)
                    height = Dimension.preferredWrapContent
                    width = Dimension.preferredWrapContent
                }
            )
        }
        val isPlaying =
            episodePlayerState.currentEpisode?.id == playerEpisode.id && episodePlayerState.isPlaying
        val icon =
            if (isPlaying) Icons.Filled.PauseCircleOutline else Icons.Default.PlayCircleOutline
        val onClickEvent = if (isPlaying) onPause else onPlay
        Image(
            imageVector = icon,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = rememberRipple(bounded = false, radius = 24.dp)
                ) { onClickEvent() }
                .size(48.dp)
                .padding(6.dp)
                .semantics { role = Role.Button }
                .constrainAs(playIcon) {
                    start.linkTo(parent.start, Keyline1)
                    top.linkTo(titleImageBarrier, margin = 10.dp)
                    bottom.linkTo(parent.bottom, 10.dp)
                }
        )
        val text = if (episodePlayerState.currentEpisode?.id == playerEpisode.id
            && !episodePlayerState.timeElapsed.isZero
        ) {
            stringResource(
                R.string.episode_left_duration,
                playerEpisode.duration!!.minus(episodePlayerState.timeElapsed).toMinutes().toInt())

        } else {
            stringResource(
                R.string.episode_duration,
                playerEpisode.duration!!.toMinutes().toInt()

            )
        }

        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            if (playerEpisode.duration != null) {
                Text(
                    text = text,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.constrainAs(date) {
                        centerVerticallyTo(playIcon)
                        linkTo(
                            start = playIcon.end,
                            startMargin = 12.dp,
                            end = downloadIcon.start,
                            endMargin = 16.dp,
                            bias = 0f // float this towards the start
                        )
//                        width = Dimension.preferredWrapContent
                    }
                )
            }

            IconButton(onClick = {
                when (playerEpisode.downloadState) {
                    DownloadState.DOWNLOADING -> onCancelDownload()
                    DownloadState.NONE -> onDownload()
                    else -> Timber.d("download state is ${playerEpisode.downloadState}")
                }
            },
                modifier = Modifier.constrainAs(downloadIcon) {
                    end.linkTo(addPlaylist.start)
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(
                    imageVector = when (playerEpisode.downloadState) {
                        DownloadState.DOWNLOADING -> Icons.Default.Downloading
                        DownloadState.FAILED, DownloadState.NONE -> Icons.Default.Download
                        DownloadState.SUCCESS -> Icons.Default.DownloadDone
                    },
                    contentDescription = null
                )
            }

            IconButton(
                onClick = { onAddToQueue() },
                modifier = Modifier.constrainAs(addPlaylist) {
                    end.linkTo(overflow.start)
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = stringResource(R.string.cd_add)
                )
            }

            IconButton(
                onClick = { /* TODO */ },
                modifier = Modifier.constrainAs(overflow) {
                    end.linkTo(parent.end, 8.dp)
                    centerVerticallyTo(playIcon)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(R.string.cd_more)
                )
            }
        }
    }
}

private val MediumDateFormatter by lazy {
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM)
}

