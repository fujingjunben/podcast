package com.example.jetcaster.play

import android.content.ComponentName
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.jetcaster.Graph
import com.example.jetcaster.data.Episode
import com.example.jetcaster.data.extension.*
import com.example.jetcaster.util.LogUtil
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.lang.Runnable

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

    fun updateMedia(episode: Episode): EpisodeState {
        return this.mediaId(episode.url).position(episode.playbackPosition)
    }
}

class PlayerControllerImpl(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default),
) : PlayerController() {
    private val TAG = "PlayerController"
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    private val mController: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private val handler: Handler = Handler(Looper.getMainLooper())

    private val episodeStore = Graph.episodeStore

    private var episodeState: EpisodeState = EpisodeState()


    override fun init(context: Context) {
        initializeController(context)
    }

    override fun release() {
        Log.d(TAG, "release")
        releaseController()
    }

    /**
     * 上一次播放的状态需要保存，不然每次都是ready
     */
    override fun play(episode: Episode) {
        Log.d(TAG, "$episode")
        when (val playerState = episode.playerAction) {
            is Play -> {
                when (mController?.isPlaying) {
                    true ->
                        if (episodeState.currentMediaId == episode.url) {
                            Log.d(TAG, "pause")
                            mController?.pause()
                            pauseEpisode()
                        } else {
                            Log.d(TAG, "switch")
                            pauseEpisode()
                            playingEpisode(episodeState.updateMedia(episode))
                            mController?.play(episode, true)
                        }
                    else ->
                        startPlayback(episode)
                }
            }
            is SeekTo -> {
                mController?.play(episode, true)
            }
            is SeekBack -> {
                mController?.seekBack()
            }
            is SeekForward -> {
                mController?.seekForward()
            }
            else -> playerState
        }
    }

    private fun startPlayback(episode: Episode){
        when(episode.url) {
            episodeState.currentMediaId -> {
                Log.d(TAG, "continue")
                playingEpisode(episodeState)
                mController?.continuePlayback()
            }
            else -> {
                Log.d(TAG, "start")
                resetPlaying()
                playingEpisode(episodeState.updateMedia(episode))
                positionState.update { episodeState.playbackPosition }
                mController?.play(episode, true)
            }
        }
    }

    private fun pauseEpisode(){
        updateEpisode(episodeState.pause(), false)
    }

    private fun playingEpisode(episodeState: EpisodeState) {
        updateEpisode(episodeState.playing(), true)
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

        // update playback progress state
//        togglePeriodicProgressUpdateRequest()

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
        pauseEpisode()
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
        positionState.update { position }
        if (mController?.hasMediaItems() == true) {
            updateEpisode(episodeState.position(position), true)
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
            val episode = episodeStore.episodeWithUri(episodeState.currentMediaId).first()
            episodeStore.updateEpisode(
                episode.copy(
                    playbackPosition = position,
                    isPlaying = isPlaying,
                    playState = episodeState.playState
                )
            )
        }
    }

    private fun resetPlaying(){
        scope.launch {
            episodeStore.episodeWhichIsPlaying().map { episodeToPodcasts ->
                episodeToPodcasts.forEach { (episode, podcast) ->
                    LogUtil.d("resetPlaying: $episode")
                    episodeStore.updateEpisode(
                        episode.copy(
                            isPlaying = false,
                            playState = PlayState.PREPARE
                        )
                    )
                }

            }
        }
    }


}