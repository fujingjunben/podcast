package com.bigdeal.podcast.ui.library

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text


@Composable
fun Library(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .systemBarsPadding()
    ) {
        Text(text = "Work In Progress")
    }

}