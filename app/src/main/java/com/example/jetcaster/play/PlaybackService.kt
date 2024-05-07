package com.example.jetcaster.play

import android.content.Context
import androidx.annotation.OptIn
import androidx.core.net.toUri
import androidx.media3.common.AudioAttributes
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.common.util.Util
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultDataSourceFactory
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.datasource.HttpDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ConcatenatingMediaSource
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.example.jetcaster.Graph
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.io.File


class PlaybackService : MediaSessionService() {
    private val maxBytes = 1000 * 1000 * 1000L
    private var mediaSession: MediaSession? = null
    private lateinit var player: Player

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val exoPlayer: ExoPlayer = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .setHandleAudioBecomingNoisy(true)
            .setSeekBackIncrementMs(Keys.SKIP_BACK_TIME_SPAN)
            .setSeekForwardIncrementMs(Keys.SKIP_FORWARD_TIME_SPAN)
            .build()
        exoPlayer.addListener(playerListener)

        // manually add seek to next and seek to previous since headphones issue them and they are translated to skip 30 sec forward / 10 sec back
        player = object : ForwardingPlayer(exoPlayer) {
            override fun getAvailableCommands(): Player.Commands {
                return super.getAvailableCommands().buildUpon().add(Player.COMMAND_SEEK_TO_NEXT).add(Player.COMMAND_SEEK_TO_PREVIOUS).build()
            }
        }

        mediaSession = MediaSession.Builder(this, player)
            .setCallback(CustomSessionCallback())
            .build()
    }

    @OptIn(UnstableApi::class)
    private fun buildOnlineMediaSource(): CacheDataSource.Factory {
        val cacheDirectory = File(this.cacheDir, "media_cache")
        // An on-the-fly cache should evict media when reaching a maximum disk space limit.
        val cache =
            SimpleCache(
                cacheDirectory, LeastRecentlyUsedCacheEvictor(maxBytes), Graph.databaseProvider)
        val httpDataSourceFactory: HttpDataSource.Factory = DefaultHttpDataSource.Factory()
            .setUserAgent(Util.getUserAgent(this, this.applicationInfo.packageName))
        // Configure the DataSource.Factory with the cache and factory for the desired HTTP stack.
        val cacheDataSourceFactory =
            CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(httpDataSourceFactory)
        return cacheDataSourceFactory
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? {
        return mediaSession
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    /*
     * Player.Listener: Called when one or more player states changed.
     */
    private val playerListener: Player.Listener = object : Player.Listener {

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            val mediaId: String = player.currentMediaItem?.mediaId ?: String()
            val currentPosition: Long = player.currentPosition
            // store state of playback

            if (isPlaying) {
                // playback is active - start to periodically save the playback position to database
            } else {
                // playback is not active - stop periodically saving the playback position to database

                // Not playing because playback is paused, ended, suppressed, or the player
                // is buffering, stopped or failed. Check player.getPlayWhenReady,
                // player.getPlaybackState, player.getPlaybackSuppressionReason and
                // player.getPlaybackError for details.
                when (player.playbackState) {
                    // player is able to immediately play from its current position
                    Player.STATE_READY -> {
                        // todo
                    }
                    // buffering - data needs to be loaded
                    Player.STATE_BUFFERING -> {
                        // todo
                    }
                    // player finished playing all media
                    Player.STATE_ENDED -> {
                    }
                    // initial state or player is stopped or playback failed
                    Player.STATE_IDLE -> {
                        // todo
                    }
                }
            }
        }
    }

    private inner class CustomSessionCallback : MediaSession.Callback {

        override fun onAddMediaItems(
            mediaSession: MediaSession,
            controller: MediaSession.ControllerInfo,
            mediaItems: MutableList<MediaItem>
        ): ListenableFuture<List<MediaItem>> {
            val updatedMediaItems = mediaItems.map { mediaItem ->
                mediaItem.buildUpon().apply {
                    setUri(mediaItem.requestMetadata.mediaUri)
                }.build()
            }
            return Futures.immediateFuture(updatedMediaItems)
        }

    }
}