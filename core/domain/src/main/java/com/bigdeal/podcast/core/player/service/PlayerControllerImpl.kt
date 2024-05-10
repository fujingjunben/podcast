package com.bigdeal.podcast.core.player.service

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.lang.Runnable
import java.time.Duration

class PlayerControllerImpl(
) : PlayerController() {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val mController: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private val handler: Handler = Handler(Looper.getMainLooper())

    private var _currentEpisode: PlayerEpisode? = null


    override fun init(context: Context) {
        initializeController(context)
    }

    override fun release() {
        Timber.d("release")
        releaseController()
    }

    override fun play(episode: PlayerEpisode, duration: Duration) {
        Timber.d("play episode: $episode")
        _currentEpisode = episode
        mController?.play(episode, duration.toMillis())
    }

    override fun pause() {
        mController?.pause()
    }

    override fun seekTo(duration: Duration) {
        _currentEpisode?.let { mController?.play(it, duration.toMillis()) }
    }

    override fun seekBack() {
        mController?.seekBack()
    }

    override fun setRepeatMode(mode: Int) {
        mController?.repeatMode = mode
    }

    override fun addToQueue(episode: PlayerEpisode) {
        mController?.apply {
            addMediaItem(buildMediaItem(episode))
        }
        Timber.d("mediaItemCount: ${mController?.mediaItemCount}")
    }

    override fun removeAllFromQueue() {
        TODO("Not yet implemented")
    }

    override fun addPlayerListener(listener: Player.Listener) {
        Timber.d("addPlayerListener")
        mController?.addListener(listener)
    }

    override fun getMediaController(): MediaController? {
        return mController
    }

    override fun seekForward() {
        mController?.seekForward()
    }

    override fun continuePlay() {
        mController?.continuePlayback()
    }

    /* Initializes the MediaController - handles connection to PlayerService under the hood */
    private fun initializeController(context: Context) {
        controllerFuture = MediaController.Builder(
            context, SessionToken(
                context,
                ComponentName(context, PlaybackService::class.java)
            )
        ).buildAsync()
        controllerFuture.addListener({ setupController() }, MoreExecutors.directExecutor())
    }


    /* Sets up the MediaController  */
    private fun setupController() {
        val controller: MediaController = this.mController ?: return
        controller.addListener(playerListener)
    }

    /* Toggle periodic request of playback position from player service */
    private fun togglePeriodicProgressUpdateRequest() {
        when (mController?.isPlaying) {
            true -> {
                handler.removeCallbacks(periodicProgressUpdateRequestRunnable)
                handler.postDelayed(periodicProgressUpdateRequestRunnable, 0)
            }

            else -> {
                handler.removeCallbacks(periodicProgressUpdateRequestRunnable)
            }
        }
    }


    /* Releases MediaController */
    private fun releaseController() {
        Timber.d("releaseController")
        pause()
        MediaController.releaseFuture(controllerFuture)
    }


    /*
     * Runnable: Periodically requests playback position (and sleep timer if running)
     */
    private val periodicProgressUpdateRequestRunnable: Runnable = object : Runnable {
        override fun run() {
            // update progress bar
            updateProgressBar()
            // use the handler to start runnable again after specified delay
            handler.postDelayed(this, 1000)
        }
    }
    /*
     * End of declaration
     */

    /* Updates the progress bar */
    private fun updateProgressBar() {
        // update progress bar - only if controller is prepared with a media item
        val position = mController?.currentPosition ?: 0L
        if (mController?.hasMediaItems() == true) {
//            updateEpisode(episodeState.position(position), true)
        }
    }


    /*
     * Player.Listener: Called when one or more player states changed.
     */
    private var playerListener: Player.Listener = object : Player.Listener {

        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            super.onMediaItemTransition(mediaItem, reason)
        }

        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            playerState.value = PlayerEvent.IsPlayingChanged(isPlaying)
            if (isPlaying) {
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            playerState.value = PlayerEvent.PlaybackStateChanged(playbackState)
            when(playbackState) {
                Player.STATE_READY -> Timber.tag("mockplayer").d("ready")
                Player.STATE_IDLE -> Timber.tag("mockplayer").d("idle")
                Player.STATE_ENDED -> Timber.tag("mockplayer").d("end")
                else -> {}
            }
        }

        override fun onEvents(player: Player, events: Player.Events) {
            super.onEvents(player, events)
        }
    }
}