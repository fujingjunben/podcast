package com.bigdeal.podcast.ui.v2.playerBar

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.PauseCircleFilled
import androidx.compose.material.icons.rounded.PlayCircleFilled
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.bigdeal.podcast.R
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.PlayState
import com.bigdeal.podcast.ui.Screen

@Composable
fun PlayerBar(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: PlayerBarViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    when (uiState) {
        is PlayerBarUiState.Loading -> {
        }

        is PlayerBarUiState.Success -> {
            PlayerBarContent(
                modifier,
                (uiState as PlayerBarUiState.Success).episodeToPodcast,
                viewModel::play
            ) { episodeUri -> navigateToEpisode(episodeUri, navController, navBackStackEntry) }
        }
    }
}

@Composable
fun PlayerBarContent(
    modifier: Modifier,
    episodeToPodcast: EpisodeToPodcast,
    play: () -> Unit,
    navigateToPlayer: (String) -> Unit
) {

    val (episode, podcast) = episodeToPodcast
    val icon = Icons.Rounded.PlayCircleFilled

    Surface {
        Row(
            modifier = modifier
                .height(50.dp)
                .padding(end = 10.dp)
                .clickable {
                    navigateToPlayer(episode.uri)
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            // If we have an image Url, we can show it using Coil
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(podcast.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                contentScale = ContentScale.FillHeight,
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .padding(5.dp)
            )

            Text(
                text = episode.title,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleSmall,
                modifier = Modifier.weight(1f)
            )

            Image(
                imageVector = icon,
                contentDescription = stringResource(R.string.cd_play),
                contentScale = ContentScale.FillHeight,
                colorFilter = ColorFilter.tint(LocalContentColor.current),
                modifier = Modifier
                    .fillMaxHeight()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberRipple(bounded = false, radius = 24.dp)
                    ) { play() }
            )

        }
    }
}

private fun navigateToEpisode(episodeUri: String, navController: NavController, from: NavBackStackEntry?) {
    // In order to discard duplicated navigation events, we check the Lifecycle
    if (from?.lifecycleIsResumed() == true) {
        val encodedUri = Uri.encode(episodeUri)
        navController.navigate(Screen.Player.createRoute(encodedUri))
    }
}

private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED

