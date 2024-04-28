package com.example.jetcaster.ui.v2

import androidx.activity.compose.BackHandler
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.jetcaster.R
import com.example.jetcaster.ui.JetcasterAppState
import com.example.jetcaster.ui.Screen
import com.example.jetcaster.ui.player.PlayerScreen
import com.example.jetcaster.ui.player.PlayerViewModel
import com.example.jetcaster.ui.rememberJetcasterAppState
import com.example.jetcaster.ui.v2.episode.EpisodeScreen
import com.example.jetcaster.ui.v2.episode.EpisodeScreenViewModel
import com.example.jetcaster.ui.v2.explore.Explore
import com.example.jetcaster.ui.v2.favourite.Favourite
import com.example.jetcaster.ui.v2.manage.Manage
import com.example.jetcaster.ui.v2.podcast.PodcastScreen
import com.example.jetcaster.ui.v2.podcast.PodcastViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier,
    toPlay: (String) -> Unit,
    finishActivity: () -> Unit = {},
    appState: JetcasterAppState = rememberJetcasterAppState(navController = navController)
) {
    NavHost(navController = navController, startDestination = Destination.EXPLORE_ROUTE) {
        composable(Destination.FAVOURITE_ROUTE) { backStackEntry ->
            Favourite(
                modifier = modifier,
                navigateToEpisode = {podcastUri, episodeUri ->
                    appState.navigateToEpisode(podcastUri, episodeUri, backStackEntry)
                },
                navigateToPodcast = { podcastUri ->
                    appState.navigateToPodcast(podcastUri, backStackEntry)
                }
            )
        }
        composable(Destination.EXPLORE_ROUTE) { backStackEntry ->
            BackHandler {
                finishActivity()
            }

            Explore(
                modifier = modifier,
                navigateToEpisode = { podcastUri, episodeUri ->
                    appState.navigateToEpisode(podcastUri, episodeUri, backStackEntry)
                },
                navigateToPodcast = { podcastUri ->
                    appState.navigateToPodcast(podcastUri, backStackEntry)
                }
            )
        }

        composable(Screen.Episode.route) { backStackEntry ->
            val viewModel: EpisodeScreenViewModel = viewModel(
                factory = EpisodeScreenViewModel.provideFactory(
                    owner = backStackEntry,
                    defaultArgs = backStackEntry.arguments
                )
            )
            EpisodeScreen(
                onBackPress = appState::navigateBack,
                navigateToPodcast = { podcastUri ->
                    appState.navigateToPodcast(podcastUri, backStackEntry)
                },
                modifier = Modifier,
                episodeScreenViewModel = viewModel
            )
        }
        composable(Screen.Podcast.route) { backStackEntry ->
            val viewModel: PodcastViewModel = viewModel(
                factory = PodcastViewModel.provideFactory(
                    owner = backStackEntry,
                    defaultArgs = backStackEntry.arguments
                )
            )
            PodcastScreen(
                onBackPress = appState::navigateBack,
                navigateToEpisode = { podcastUri, episodeUri ->
                    appState.navigateToEpisode(podcastUri, episodeUri, backStackEntry)
                },
                modifier = Modifier,
                viewModel = viewModel
            )
        }

        composable(Screen.Player.route) { backStackEntry ->
            val playerViewModel: PlayerViewModel = viewModel(
                factory = PlayerViewModel.provideFactory(
                    owner = backStackEntry,
                    defaultArgs = backStackEntry.arguments
                )
            )
            PlayerScreen(playerViewModel, onBackPress = appState::navigateBack)
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
    EXPLORE(R.string.explore, R.drawable.ic_search, Destination.EXPLORE_ROUTE),
    MANAGE(R.string.manage, R.drawable.ic_featured, Destination.MANAGE_ROUTE),
}

object Destination {
    const val FAVOURITE_ROUTE = "favourite"
    const val EXPLORE_ROUTE = "explore"
    const val MANAGE_ROUTE = "manage"
    const val EPISODE = "episodeUri"
    const val PODCAST = "podcastUri"
}