package com.bigdeal.core.util

import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import com.bigdeal.core.data.Episode

object MediaUtil {
    fun buildMediaItem(episode: Episode): MediaItem {
        // get the correct source for streaming / local playback
        // put uri in RequestMetadata - credit: https://stackoverflow.com/a/70103460
        val source = episode.url
        val requestMetadata = MediaItem.RequestMetadata.Builder().apply {
            setMediaUri(source.toUri())
        }.build()
        // build MediaItem and return it
        val mediaMetadata = MediaMetadata.Builder().apply {
            setAlbumTitle(episode.podcastName)
            setTitle(episode.title)
            setArtworkUri(episode.podcastImageUrl?.toUri())
        }.build()
        return MediaItem.Builder().apply {
            setMediaId(source)
            setRequestMetadata(requestMetadata)
            setMediaMetadata(mediaMetadata)
                .setMimeType("audio/mpeg")
            setUri(source.toUri())
        }.build()
    }
}