package com.example.jetcaster.ui.v2.explore

import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.jetcaster.ui.home.Home

@Composable
fun Explore(
    modifier: Modifier,
    navigateToEpisode: (String, String) -> Unit,
    navigateToPodcast: (String) -> Unit,
) {
    Home(modifier, navigateToEpisode, navigateToPodcast)
}