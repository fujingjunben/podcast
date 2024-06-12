package com.bigdeal.podcast.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bigdeal.podcast.R
import com.bigdeal.podcast.ui.player.PlayerScreen
import com.bigdeal.podcast.ui.episode.EpisodeScreen
import com.bigdeal.podcast.ui.favourite.Favourite
import com.bigdeal.podcast.ui.discover.Discover
import com.bigdeal.podcast.ui.library.Library
import com.bigdeal.podcast.ui.podcast.PodcastScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier,
    onPlay: (String) -> Unit,
    appState: PodcastAppState = rememberPodcastAppState(navController = navController)
) {
    NavHost(navController = navController, startDestination = Destination.FAVOURITE_ROUTE) {
        composable(Destination.FAVOURITE_ROUTE) { backStackEntry ->
            Favourite(
                modifier = modifier,
                navigateToEpisode = { podcastId, episodeId ->
                    appState.navigateToEpisode(podcastId, episodeId, backStackEntry)
                },
                navigateToPodcast = { podcastId ->
                    appState.navigateToPodcast(podcastId, backStackEntry)
                },
                onPlay = onPlay
            )
        }

        composable(Screen.Episode.route) { backStackEntry ->
            EpisodeScreen(
                onBackPress = appState::navigateBack,
                navigateToPodcast = { podcastId ->
                    appState.navigateToPodcast(podcastId, backStackEntry)
                },

                onPlay = onPlay,
                modifier = modifier
            )
        }
        composable(Screen.Podcast.route) { backStackEntry ->
            PodcastScreen(
                onBackPress = appState::navigateBack,
                navigateToEpisode = { podcastId, episodeId ->
                    appState.navigateToEpisode(podcastId, episodeId, backStackEntry)
                },
                onPlay = onPlay,
                modifier = modifier
            )
        }

        composable(Screen.Player.route) { backStackEntry ->
            PlayerScreen(onBackPress = appState::navigateBack)
        }
        composable(Destination.DISCOVER_ROUTE) { backStackEntry ->
            Discover(
                onPlay = onPlay,
                modifier = modifier,
                navigateToPodcast = { podcastId ->
                    appState.navigateToPodcast(podcastId, backStackEntry)
                },
                navigateToEpisode = { podcastId, episodeId ->
                    appState.navigateToEpisode(podcastId, episodeId, backStackEntry)
                },
            )
        }
        composable(Destination.LIBRARY_ROUTE) {
            Library()
        }
    }
}

enum class Tabs(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
) {
    FAVOURITE(R.string.favourite, R.drawable.ic_grain, Destination.FAVOURITE_ROUTE),
    EXPLORE(R.string.discover, R.drawable.ic_search, Destination.DISCOVER_ROUTE),
    MANAGE(R.string.library, R.drawable.ic_featured, Destination.LIBRARY_ROUTE),
}

object Destination {
    const val FAVOURITE_ROUTE = "favourite"
    const val DISCOVER_ROUTE = "discover"
    const val LIBRARY_ROUTE = "library"
    const val EPISODE = "episodeId"
    const val PODCAST = "podcastId"
}