/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui.player

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.jetcaster.R
import com.example.jetcaster.play.*
import com.example.jetcaster.ui.theme.JetcasterTheme
import com.example.jetcaster.ui.theme.MinContrastOfPrimaryVsSurface
import com.example.jetcaster.util.*

/**
 * Stateful version of the Podcast player
 */
@Composable
fun PlayerScreen(
    viewModel: PlayerViewModel,
    onBackPress: () -> Unit,
) {
    val uiState = viewModel.uiState
    PlayerScreen(uiState, uiState.playbackPosition, onBackPress, play = { playerAction ->
        viewModel.play(playerAction)
    })
}

/**
 * Stateless version of the Player screen
 */
@Composable
private fun PlayerScreen(
    uiState: PlayerUiState,
    playbackPosition: Long,
    onBackPress: () -> Unit,
    play: (action: PlayerAction) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(modifier) {
        if (uiState.podcastName.isNotEmpty()) {
            PlayerContent(uiState, playbackPosition, onBackPress, play)
        } else {
            FullScreenLoading(modifier)
        }
    }
}

@Composable
fun PlayerContent(
    uiState: PlayerUiState,
    playbackPosition: Long,
    onBackPress: () -> Unit,
    play: (action: PlayerAction) -> Unit,
) {
    PlayerDynamicTheme(uiState.podcastImageUrl) {
        PlayerContentRegular(uiState, playbackPosition, onBackPress, play)
    }
}

@Composable
private fun PlayerContentRegular(
    uiState: PlayerUiState,
    playbackPosition: Long,
    onBackPress: () -> Unit,
    play: (playerAction: PlayerAction) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalGradientScrim(
                color = MaterialTheme.colors.primary.copy(alpha = 0.50f),
                startYPercentage = 1f,
                endYPercentage = 0f
            )
            .systemBarsPadding()
            .padding(horizontal = 8.dp)
    ) {
        TopAppBar(onBackPress = onBackPress)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {
            Spacer(modifier = Modifier.weight(1f))
            PlayerImage(
                podcastImageUrl = uiState.podcastImageUrl,
                modifier = Modifier.weight(10f)
            )
            Spacer(modifier = Modifier.height(32.dp))
            PodcastDescription(uiState.title, uiState.podcastName)
            Spacer(modifier = Modifier.height(32.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(10f)
            ) {
                PlayerSlider(uiState, play, playbackPosition)
                PlayerButtons(
                    Modifier.padding(vertical = 8.dp),
                    isPlaying = uiState.playState == PlayState.PLAYING,
                    play = play
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}


@Composable
private fun TopAppBar(onBackPress: () -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        IconButton(onClick = onBackPress) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = stringResource(R.string.cd_back)
            )
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.PlaylistAdd,
                contentDescription = stringResource(R.string.cd_add)
            )
        }
        IconButton(onClick = { /* TODO */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = stringResource(R.string.cd_more)
            )
        }
    }
}

@Composable
private fun PlayerImage(
    podcastImageUrl: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(podcastImageUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .sizeIn(maxWidth = 500.dp, maxHeight = 500.dp)
            .aspectRatio(1f)
            .clip(MaterialTheme.shapes.medium)
    )
}

@Composable
private fun PodcastDescription(
    title: String,
    podcastName: String,
    titleTextStyle: TextStyle = MaterialTheme.typography.h5
) {
    Text(
        text = title,
        style = titleTextStyle,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            text = podcastName,
            style = MaterialTheme.typography.body2,
            maxLines = 1
        )
    }
}

@Composable
private fun PlayerSlider(
    uiState: PlayerUiState,
    play: (playerAction: PlayerAction) -> Unit,
    playbackPosition: Long,
) {
    val episodeDuration = uiState.duration

    if (episodeDuration != null) {
        var position by remember(playbackPosition) {
            mutableStateOf(playbackPosition * 1f / episodeDuration.seconds / 1000)
        }
        Column(Modifier.fillMaxWidth()) {
            Slider(value = position,
                onValueChange = {
                    position = it
                },
                onValueChangeFinished = {
                    play(SeekTo((position * episodeDuration.seconds * 1000).toLong()))
                }
            )

            Row(Modifier.fillMaxWidth()) {
                Text(text = (position * episodeDuration.seconds).toLong().toHHMMSS())
                Spacer(modifier = Modifier.weight(1f))
                Text(episodeDuration.seconds.toHHMMSS())
            }
        }
    }
}

@Composable
private fun PlayerButtons(
    modifier: Modifier = Modifier,
    playerButtonSize: Dp = 72.dp,
    sideButtonSize: Dp = 48.dp,
    isPlaying: Boolean,
    play: (playerAction: PlayerAction) -> Unit,
) {
    val icon = when (isPlaying) {
        true -> Icons.Rounded.PauseCircleFilled
        else -> Icons.Rounded.PlayCircleFilled
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val buttonsModifier = Modifier
            .size(sideButtonSize)
            .semantics { role = Role.Button }

        Image(
            imageVector = Icons.Filled.SkipPrevious,
            contentDescription = stringResource(R.string.cd_skip_previous),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = buttonsModifier
        )
        Image(
            imageVector = Icons.Filled.Replay10,
            contentDescription = stringResource(R.string.cd_reply10),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = buttonsModifier.clickable {
                play(SeekBack)
            }
        )
        Image(
            imageVector = icon,
            contentDescription = stringResource(R.string.cd_play),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = Modifier
                .size(playerButtonSize)
                .semantics { role = Role.Button }
                .clickable {
                    play(Play)
                }
        )
        Image(
            imageVector = Icons.Filled.Forward30,
            contentDescription = stringResource(R.string.cd_forward30),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = buttonsModifier.clickable {
                play(SeekForward)
            }
        )
        Image(
            imageVector = Icons.Filled.SkipNext,
            contentDescription = stringResource(R.string.cd_skip_next),
            contentScale = ContentScale.Fit,
            colorFilter = ColorFilter.tint(LocalContentColor.current),
            modifier = buttonsModifier
        )
    }
}

/**
 * Theme that updates the colors dynamically depending on the podcast image URL
 */
@Composable
private fun PlayerDynamicTheme(
    podcastImageUrl: String,
    content: @Composable () -> Unit
) {
    val surfaceColor = MaterialTheme.colors.surface
    val dominantColorState = rememberDominantColorState(
        defaultColor = MaterialTheme.colors.surface
    ) { color ->
        // We want a color which has sufficient contrast against the surface color
        color.contrastAgainst(surfaceColor) >= MinContrastOfPrimaryVsSurface
    }
    DynamicThemePrimaryColorsFromImage(dominantColorState) {
        // Update the dominantColorState with colors coming from the podcast image URL
        LaunchedEffect(podcastImageUrl) {
            if (podcastImageUrl.isNotEmpty()) {
                dominantColorState.updateColorsFromImageUrl(podcastImageUrl)
            } else {
                dominantColorState.reset()
            }
        }
        content()
    }
}

/**
 * Full screen circular progress indicator
 */
@Composable
private fun FullScreenLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        CircularProgressIndicator()
    }
}

@Preview
@Composable
fun TopAppBarPreview() {
    JetcasterTheme {
        TopAppBar(onBackPress = { })
    }
}

//@Preview
//@Composable
//fun PlayerButtonsPreview() {
//    JetcasterTheme {
//        PlayerButtons()
//    }
//}
//
//@Preview
//@Composable
//fun PlayerScreenPreview() {
//    JetcasterTheme {
//        PlayerScreen(
//            PlayerUiState(
//                title = "Title",
//                duration = Duration.ofHours(2),
//                podcastName = "Podcast"
//            ),
//            devicePosture = DevicePosture.NormalPosture,
//            onBackPress = { },
//        )
//    }
//}
