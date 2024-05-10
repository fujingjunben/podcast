package com.bigdeal.podcast.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bigdeal.podcast.ui.theme.JetcasterTheme
import com.bigdeal.podcast.ui.v2.NavGraph
import com.bigdeal.podcast.ui.v2.Tabs
import com.bigdeal.podcast.ui.v2.playerBar.PlayerBar
import com.bigdeal.core.util.LogUtil
import com.bigdeal.podcast.ui.v2.playerBar.PlayerBarViewModel

@Composable
fun PodcastApp() {
    val tabs = remember {
        Tabs.entries.toTypedArray()
    }
    val navController = rememberNavController()

    JetcasterTheme {
        val episodeUri = remember {
            mutableStateOf("")
        }
        val showPlayerBar = remember(episodeUri) { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                PodcastBottomBar(navController, tabs = tabs,
                    showPlayerBar.value, episodeUri.value)
            }
        ) { paddingValues ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onPlay = { uri ->
                    run {
                        showPlayerBar.value = true
                        episodeUri.value = uri
                    }
                }
            )
        }
    }
}

@Composable
fun PodcastBottomBar(navController: NavController,
                     tabs: Array<Tabs>,
                     showPlayerBar: Boolean,
                     episodeUri: String) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: Tabs.EXPLORE.route

    LogUtil.d("currentRoute: $currentRoute")
    if (currentRoute != Screen.Player.route) {
        Column {
            if (showPlayerBar) {
                val podcastDetailsViewModel =
                    hiltViewModel<PlayerBarViewModel, PlayerBarViewModel.Factory>(
                        key = episodeUri
                    ) {
                        it.create(episodeUri)
                    }
                PlayerBar(
                    navController = navController,
                    viewModel = podcastDetailsViewModel
                )
            }
            NavigationBar(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.navigationBars.add(WindowInsets(bottom = 56.dp))
                )
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        icon = { Icon(painterResource(tab.icon), contentDescription = null) },
                        label = { Text(stringResource(tab.title)) },
                        selected = currentRoute == tab.route,
                        onClick = {
                            if (tab.route != currentRoute) {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        alwaysShowLabel = true,
                        modifier = Modifier.navigationBarsPadding()
                    )
                }
            }
        }
    }
}