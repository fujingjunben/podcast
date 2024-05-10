package com.bigdeal.podcast.ui.v2

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.bigdeal.podcast.R
import com.bigdeal.podcast.ui.JetcasterAppState
import com.bigdeal.podcast.ui.Screen
import com.bigdeal.podcast.ui.player.PlayerScreen
import com.bigdeal.podcast.ui.rememberJetcasterAppState
import com.bigdeal.podcast.ui.v2.episode.EpisodeScreen
import com.bigdeal.podcast.ui.v2.favourite.Favourite
import com.bigdeal.podcast.ui.v2.manage.Manage
import com.bigdeal.podcast.ui.v2.podcast.PodcastScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier,
    onPlay: (String) -> Unit,
    finishActivity: () -> Unit = {},
    appState: JetcasterAppState = rememberJetcasterAppState(navController = navController)
) {
    NavHost(navController = navController, startDestination = Destination.FAVOURITE_ROUTE) {
        composable(Destination.FAVOURITE_ROUTE) { backStackEntry ->
            Favourite(
                modifier = modifier,
                navigateToEpisode = {podcastUri, episodeUri ->
                    appState.navigateToEpisode(podcastUri, episodeUri, backStackEntry)
                },
                navigateToPodcast = { podcastUri ->
                    appState.navigateToPodcast(podcastUri, backStackEntry)
                },
                onPlay = onPlay
            )
        }

        composable(Screen.Episode.route) { backStackEntry ->
            EpisodeScreen(
                onBackPress = appState::navigateBack,
                navigateToPodcast = { podcastUri ->
                    appState.navigateToPodcast(podcastUri, backStackEntry)
                },

                onPlay = onPlay,
                modifier = Modifier
            )
        }
        composable(Screen.Podcast.route) { backStackEntry ->
            PodcastScreen(
                onBackPress = appState::navigateBack,
                navigateToEpisode = { podcastUri, episodeUri ->
                    appState.navigateToEpisode(podcastUri, episodeUri, backStackEntry)
                },
                onPlay = onPlay,
                modifier = Modifier
            )
        }

        composable(Screen.Player.route) { backStackEntry ->
            PlayerScreen(onBackPress = appState::navigateBack)
        }
        composable(Destination.MANAGE_ROUTE) {
            Manage()
        }
    }
}

enum class Tabs(
    @StringRes val title: Int,
    @DrawableRes val icon: Int,
    val route: String
) {
    FAVOURITE(R.string.favourite, R.drawable.ic_grain, Destination.FAVOURITE_ROUTE),
    EXPLORE(R.string.discover, R.drawable.ic_search, Destination.EXPLORE_ROUTE),
    MANAGE(R.string.library, R.drawable.ic_featured, Destination.MANAGE_ROUTE),
}

object Destination {
    const val FAVOURITE_ROUTE = "favourite"
    const val EXPLORE_ROUTE = "discover"
    const val MANAGE_ROUTE = "library"
    const val EPISODE = "episodeUri"
    const val PODCAST = "podcastUri"
}