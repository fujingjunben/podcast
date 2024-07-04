package com.bigdeal.podcast.core.player

import androidx.media3.common.Player
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.service.PlayerController
import com.bigdeal.podcast.core.player.service.PlayerEvent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Duration
import kotlin.reflect.KProperty

class MockEpisodePlayer(
    mainDispatcher: CoroutineDispatcher,
    private val playerController: PlayerController
) : EpisodePlayer {

    private val _playerState = MutableStateFlow(EpisodePlayerState())
    private val _currentEpisode = MutableStateFlow<PlayerEpisode?>(null)
    private val queue = MutableStateFlow<List<PlayerEpisode>>(emptyList())
    private val isPlaying = MutableStateFlow<PlayerAction>(PlayerAction.PLAY)
    private val timeElapsed = MutableStateFlow(Duration.ZERO)
    private val _playerSpeed = MutableStateFlow(DefaultPlaybackSpeed)
    private val coroutineScope = CoroutineScope(mainDispatcher)
    private var _playerRepeatMode = Player.REPEAT_MODE_OFF

    private var timerJob: Job? = null

    init {
        coroutineScope.launch {
            // Combine streams here
            combine(
                _currentEpisode,
                queue,
                isPlaying,
                timeElapsed,
                _playerSpeed,
            ) { currentEpisode, queue, isPlaying, timeElapsed, playerSpeed ->
                EpisodePlayerState(
                    currentEpisode = currentEpisode,
                    queue = queue,
                    isPlaying = isPlaying,
                    timeElapsed = timeElapsed,
                    playbackSpeed = playerSpeed
                )
            }.catch {
                // TODO handle error state
                throw it
            }.collect {
                _playerState.value = it
            }
        }

        coroutineScope.launch {
            playerController.playerState.collect { playerEvent ->
                Timber.d("mockplayer playerstate: $playerEvent")
                when (playerEvent) {
                    is PlayerEvent.IsPlayingChanged -> {
                        if (playerEvent.isPlaying) {
                            // playing
                            updatePlayerAction(PlayerAction.PLAYING)
                        } else {
                            // pause
                            updatePlayerAction(PlayerAction.PAUSE)
                        }
                    }
                    is PlayerEvent.InitState -> {Timber.d("mockplayer: init")
                    }
                    is PlayerEvent.PlaybackStateChanged -> {
                        when(playerEvent.state) {
                            Player.STATE_READY -> {
                                Timber.d("mockplayer: ready")
                                updatePlayerAction(PlayerAction.PLAYING)
                            }
                            Player.STATE_IDLE -> Timber.d("mockplayer: idle")
                            Player.STATE_ENDED -> {
                                Timber.d("mockplayer: end")
                                timeElapsed.value = Duration.ZERO
                                updatePlayerAction(PlayerAction.STOP)
                                if (_playerRepeatMode == Player.REPEAT_MODE_ONE) {
                                    play()
                                } else {
                                    if (hasNext()) {
                                        next()
                                    }
                                }

                            }
                            Player.STATE_BUFFERING -> {
                                Timber.d("mockplayer: buffering")
                                updatePlayerAction(PlayerAction.LOADING)
                            }

                        }
                    }

                    else -> {}
                }
            }
        }
    }

    override var playerSpeed: Duration = _playerSpeed.value

    override val playerState: StateFlow<EpisodePlayerState> = _playerState.asStateFlow()

    override var currentEpisode: PlayerEpisode? by _currentEpisode

    override fun addToQueue(episode: PlayerEpisode) {
        queue.update {
            it + episode
        }

    }

    override fun removeAllFromQueue() {
        queue.value = emptyList()
    }

    private fun play(isContinuePlaying: Boolean = false) {
        // Do nothing if already playing
        if (isPlaying.value.isPlaying()) {
            Timber.d("mockplayer isplaying")
            return
        }

        val episode = _currentEpisode.value ?: return

        if (isContinuePlaying) {
            playerController.continuePlay()
        } else {
            playerController.play(episode, timeElapsed.value)
        }

        timerJob = coroutineScope.launch {
            // Increment timer by a second
            while (isActive && timeElapsed.value < episode.duration.minus(EpisodeDurationThreshold)) {
                delay(playerSpeed.toMillis())
                playerController.getMediaController()?.let {controller ->
                    timeElapsed.update { Duration.ofMillis(controller.currentPosition) }
                }
            }
       }
    }

    override fun continuePlay() {
        play(true)
    }

    override fun play(playerEpisode: PlayerEpisode) {
        Timber.d("mockplayer: current episode: ${_currentEpisode.value?.title}")
        if (_currentEpisode.value?.id != playerEpisode.id) {
            Timber.d("mockplayer: play different episode: $playerEpisode")
            if (isPlaying.value.isPlaying()) {
                playerController.pause()
            }
            timerJob?.cancel()
            timerJob = null
            _currentEpisode.value = playerEpisode
            timeElapsed.value = Duration.ZERO
            updatePlayerAction(PlayerAction.LOADING)
            play()
        } else {
            continuePlay()
        }
    }

    override fun play(playerEpisodes: List<PlayerEpisode>) {
        if (isPlaying.value.isPlaying()) {
            pause()
        }

        // Keep the currently playing episode in the queue
        val playingEpisode = _currentEpisode.value
        var previousList: List<PlayerEpisode> = emptyList()
        queue.update { queue ->
            playerEpisodes.map { episode ->
                if (queue.contains(episode)) {
                    val mutableList = queue.toMutableList()
                    mutableList.remove(episode)
                    previousList = mutableList
                } else {
                    previousList = queue
                }
            }
            if (playingEpisode != null) {
                playerEpisodes + listOf(playingEpisode) + previousList
            } else {
                playerEpisodes + previousList
            }
        }

        next()
    }

    override fun pause() {
        if (isPlaying.value.isPlaying()) {
            updatePlayerAction(PlayerAction.PAUSE)
            playerController.pause()
        }

        timerJob?.cancel()
        timerJob = null
    }

    override fun stop() {
        timerJob?.cancel()
        timerJob = null
    }

    override fun advanceBy(duration: Duration) {
        val currentEpisodeDuration = _currentEpisode.value?.duration ?: return
        timeElapsed.update {
            (it + duration).coerceAtMost(currentEpisodeDuration)
        }
        playerController.seekForward()
    }

    override fun rewindBy(duration: Duration) {
        timeElapsed.update {
            (it - duration).coerceAtLeast(Duration.ZERO)
        }
        playerController.seekBack()
    }

    override fun onSeekingStarted() {
        // Need to pause the player so that it doesn't compete with timeline progression.
        pause()
        updatePlayerAction(PlayerAction.LOADING)
    }

    override fun onSeekingFinished(duration: Duration) {
        val currentEpisodeDuration = _currentEpisode.value?.duration ?: return
        val time = duration.coerceIn(Duration.ZERO, currentEpisodeDuration)
        timeElapsed.update { time }
        play()
    }

    override fun increaseSpeed(speed: Duration) {
        _playerSpeed.value += speed
    }

    override fun decreaseSpeed(speed: Duration) {
        _playerSpeed.value -= speed
    }

    override fun setRepeatMode(@Player.RepeatMode mode: Int) {
        Timber.d("mockplayer: setRepeatMode: $mode")
        _playerRepeatMode = mode
    }

    override fun next() {
        val q = queue.value
        if (q.isEmpty()) {
            return
        }

        timeElapsed.value = Duration.ZERO
        val nextEpisode = q[0]
        currentEpisode = nextEpisode
        queue.value = q - nextEpisode
        play()
    }

    override fun previous() {
        timeElapsed.value = Duration.ZERO
        updatePlayerAction(PlayerAction.STOP)
        timerJob?.cancel()
        timerJob = null
    }

    private fun hasNext(): Boolean {
        return queue.value.isNotEmpty()
    }

    private fun updatePlayerAction(nextAction: PlayerAction) {
        Timber.d("mockplayer update player action, ${isPlaying.value} to $nextAction")
        when(nextAction) {
            is PlayerAction.PAUSE -> {
                if (isPlaying.value is PlayerAction.PLAYING) {
                    isPlaying.value = nextAction
                }
            }
            else -> isPlaying.value = nextAction
        }
    }
}

// Used to enable property delegation
private operator fun <T> MutableStateFlow<T>.setValue(
    thisObj: Any?,
    property: KProperty<*>,
    value: T
) {
    this.value = value
}

private operator fun <T> MutableStateFlow<T>.getValue(thisObj: Any?, property: KProperty<*>): T =
    this.value
