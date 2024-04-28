package com.example.jetcaster.ui.v2.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.jetcaster.data.Podcast

@Composable
fun PodcastTitleCard(podcast: Podcast) {
    Row(horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        if (podcast.imageUrl != null) {
            AsyncImage(
                model = podcast.imageUrl,
                contentDescription = podcast.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { }
            )
        }

        Text(text = podcast.title, modifier = Modifier.padding(12.dp))
    }
}