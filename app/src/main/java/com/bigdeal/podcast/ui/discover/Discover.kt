package com.bigdeal.podcast.ui.discover

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment


@Composable
fun Discover(modifier: Modifier = Modifier) {
    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
    ) {
        Box(modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center) {
            Text(text = "Work in progress")
        }
    }
}