package com.bigdeal.podcast.ui.v2.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bigdeal.core.data.Podcast
import com.bigdeal.podcast.core.player.model.PlayerEpisode

@Composable
fun PodcastTitleCard(playerEpisode: PlayerEpisode, onClick : () -> Unit = {}) {
    Row(horizontalArrangement = Arrangement.Start,
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        AsyncImage(
            model = playerEpisode.podcastImageUrl,
            contentDescription = playerEpisode.podcastName,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(100.dp)
                .clip(MaterialTheme.shapes.medium)
                .clickable { onClick() }
        )

        Text(text = playerEpisode.podcastName, modifier = Modifier.padding(12.dp))
    }
}