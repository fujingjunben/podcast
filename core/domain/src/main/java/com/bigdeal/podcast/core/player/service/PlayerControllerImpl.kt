package com.bigdeal.podcast.core.player.service

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.bigdeal.core.data.Episode
import com.bigdeal.core.data.EpisodeStateEntity
import com.bigdeal.core.data.EpisodeStore
import com.bigdeal.core.data.PlayState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.lang.Runnable
import java.time.Duration

private data class EpisodeState(
    val currentMediaId: String = "",
    val playState: PlayState = PlayState.PREPARE,
    val playbackPosition: Long = 0L
) {
    fun pause(): EpisodeState {
        return this.copy(playState = PlayState.PAUSE)
    }

    fun playing(): EpisodeState {
        return this.copy(playState = PlayState.PLAYING)
    }

    fun prepare(): EpisodeState {
        return this.copy(playState = PlayState.PREPARE)
    }

    fun position(position: Long): EpisodeState {
        return this.copy(playbackPosition = position)
    }

    fun mediaId(mediaId: String): EpisodeState {
        return this.copy(currentMediaId = mediaId)
    }
}

class PlayerControllerImpl(
    private val episodeStore: EpisodeStore,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : PlayerController() {
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val mController: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private val handler: Handler = Handler(Looper.getMainLooper())

    private var episodeState: EpisodeState = EpisodeState()

    private var _currentEpisode: PlayerEpisode? = null


    override fun init(context: Context) {
        initializeController(context)
    }

    override fun release() {
        Timber.d("release")
        releaseController()
    }

    /**
     * 上一次播放的状态需要保存，不然每次都是ready
     */
    override fun play(episode: PlayerEpisode) {
        Timber.d("play episode: $episode")
        _currentEpisode = episode
        mController?.play(episode)
    }

    override fun pause() {
        mController?.pause()
    }

    override fun seekTo(timeOffset: Long) {
        _currentEpisode?.let { mController?.play(it, timeOffset) }
    }

    override fun seekBack() {
        mController?.seekBack()
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
        positionState.update { Duration.ofSeconds(position)}
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
            if (isPlaying) {
                togglePeriodicProgressUpdateRequest()
            }
        }
    }

    private fun updateEpisode(episodeState: EpisodeState, isPlaying: Boolean = false) {
        this.episodeState = episodeState
        if (mController == null) {
            return
        }

        if (episodeState.currentMediaId.isEmpty()) {
            return
        }
        val isFinished = episodeState.playbackPosition >= mController!!.duration - 500
        val position =
            if (isFinished) 0L else episodeState.playbackPosition

        if (isFinished) {
            this.episodeState = episodeState.prepare()
        }

        scope.launch {
            Timber.d("update episode state record: $episodeState")
            episodeStore.updateEpisodeState(
                EpisodeStateEntity(
                    uri = episodeState.currentMediaId,
                    timeElapsed = position,
                    isPlaying = isPlaying,
                    playState = episodeState.playState
                )
            )
        }
    }
}