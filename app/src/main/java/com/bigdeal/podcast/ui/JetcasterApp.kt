package com.bigdeal.podcast.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.bigdeal.podcast.ui.theme.JetcasterTheme
import com.bigdeal.podcast.ui.v2.NavGraph
import com.bigdeal.podcast.ui.v2.Tabs
import com.bigdeal.podcast.ui.v2.playerBar.PlayerBar
import com.bigdeal.core.util.LogUtil

@Composable
fun PodcastApp() {
    val tabs = remember {
        Tabs.values()
    }
    val navController = rememberNavController()

    JetcasterTheme {
        Scaffold(
            bottomBar = {
                PodcastBottomBar(navController, tabs = tabs)
            }
        ) { paddingValues ->
            NavGraph(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                toPlay = { uri -> {} }
            )
        }
    }
}

@Composable
fun PodcastBottomBar(navController: NavController, tabs: Array<Tabs>) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
        ?: Tabs.EXPLORE.route

    val routes = remember { Tabs.values().map { it.route } }
    LogUtil.d("currentRoute: $currentRoute")
    if (currentRoute != Screen.Player.route) {
        Column {
//            PlayerBar(
//                navController = navController
//            )
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