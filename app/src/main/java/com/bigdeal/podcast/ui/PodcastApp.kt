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
import com.bigdeal.podcast.ui.theme.PodcastTheme
import com.bigdeal.podcast.ui.playerBar.PlayerBar
import com.bigdeal.core.util.LogUtil
import com.bigdeal.podcast.ui.playerBar.PlayerBarViewModel

@Composable
fun PodcastApp() {
    val tabs = remember {
        Tabs.entries.toTypedArray()
    }
    val navController = rememberNavController()

    PodcastTheme {
        val episodeId = remember {
            mutableStateOf("")
        }
        val showPlayerBar = remember { mutableStateOf(false) }

        Scaffold(
            bottomBar = {
                PodcastBottomBar(navController, tabs = tabs,
                    showPlayerBar.value, episodeId.value)
            }
        ) { paddingValues ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onPlay = { id ->
                    run {
                        showPlayerBar.value = true
                        episodeId.value = id
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
                     episodeId: String) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: Tabs.EXPLORE.route

    LogUtil.d("currentRoute: $currentRoute")
    if (currentRoute != Screen.Player.route) {
        Column {
            if (showPlayerBar) {
                val podcastDetailsViewModel =
                    hiltViewModel<PlayerBarViewModel, PlayerBarViewModel.Factory>(
                        key = episodeId
                    ) {
                        it.create(episodeId)
                    }
                PlayerBar(
                    navController = navController,
                    viewModel = podcastDetailsViewModel
                )
            }
            NavigationBar(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.navigationBars.add(WindowInsets(bottom = 60.dp))
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
                            .padding(vertical = 3.dp)
                    )
                }
            }
        }
    }
}