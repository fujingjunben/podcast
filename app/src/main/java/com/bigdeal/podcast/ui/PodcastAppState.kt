package com.bigdeal.podcast.ui

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

/**
 * List of screens for podcastApp
 */
sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Player : Screen("player/{episodeId}") {
        fun createRoute(episodeId: String) = "player/$episodeId"
    }
    object Episode: Screen("episode/{podcastId}/{episodeId}"){
        fun createRoute(podcastId: String, episodeId: String) = "episode/$podcastId/$episodeId"
    }
    object Podcast: Screen("podcast/{podcastId}"){
        fun createRoute(podcastId: String) = "podcast/$podcastId"
    }
    companion object {
        val ARG_PODCAST_ID = "podcastId"
        val ARG_EPISODE_ID = "episodeId"
    }

}

@Composable
fun rememberPodcastAppState(
    navController: NavHostController = rememberNavController(),
    context: Context = LocalContext.current
) = remember(navController, context) {
    PodcastAppState(navController, context)
}

class PodcastAppState(
    val navController: NavHostController,
    private val context: Context
) {
    var isOnline by mutableStateOf(checkIfOnline())
        private set

    fun refreshOnline() {
        isOnline = checkIfOnline()
    }

    fun navigateToPlayer(episodeId: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            val encodedUri = Uri.encode(episodeId)
            navController.navigate(Screen.Player.createRoute(encodedUri))
        }
    }

    fun navigateToEpisode(podcastId: String, episodeId: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            val encodedEpisodeId = Uri.encode(episodeId)
            val encodedPodcastId = Uri.encode(podcastId)
            navController.navigate(Screen.Episode.createRoute(encodedPodcastId, encodedEpisodeId))
        }
    }

    fun navigateToPodcast(podcastId: String, from: NavBackStackEntry) {
        // In order to discard duplicated navigation events, we check the Lifecycle
        if (from.lifecycleIsResumed()) {
            val encodedId = Uri.encode(podcastId)
            navController.navigate(Screen.Podcast.createRoute(encodedId))
        }
    }

    fun navigateBack() {
        navController.popBackStack()
    }

    @Suppress("DEPRECATION")
    private fun checkIfOnline(): Boolean {
        return true
//        val cm = getSystemService(context, ConnectivityManager::class.java)
//
//        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            val capabilities = cm?.getNetworkCapabilities(cm.activeNetwork) ?: return false
//            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
//                capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
//        } else {
//            cm?.activeNetworkInfo?.isConnectedOrConnecting == true
//        }
    }
}

/**
 * If the lifecycle is not resumed it means this NavBackStackEntry already processed a nav event.
 *
 * This is used to de-duplicate navigation events.
 */
private fun NavBackStackEntry.lifecycleIsResumed() =
    this.lifecycle.currentState == Lifecycle.State.RESUMED
