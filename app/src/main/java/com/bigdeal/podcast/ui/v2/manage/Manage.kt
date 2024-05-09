package com.bigdeal.podcast.ui.v2.manage

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun Manage() {
    Surface(
        modifier = Modifier
            .systemBarsPadding()
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
    ) {
        Text(text = "Manage")
    }
}