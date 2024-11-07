package com.bigdeal.podcast.core.viewmodel

import androidx.lifecycle.ViewModel
import com.bigdeal.podcast.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.EpisodePlayer
import com.bigdeal.podcast.core.player.model.PlayerEpisode

abstract class BaseViewModel(
    private val episodePlayer: EpisodePlayer,
    private val podcastDownloader: PodcastDownloader

): ViewModel() {
    val episodePlayerState = episodePlayer.playerState
    fun play(playerEpisode: PlayerEpisode) {
        episodePlayer.play(playerEpisode)
    }

    fun pause() {
        episodePlayer.pause()
    }

    fun addToQueue(playerEpisode: PlayerEpisode) {
        episodePlayer.addToQueue(playerEpisode)
    }


    fun download(episode: PlayerEpisode) {
        podcastDownloader.downloadEpisode(episode)
    }

    fun cancelDownload(episode: PlayerEpisode) {
        podcastDownloader.cancelDownload(episode)
    }

    fun deleteDownload(episode: PlayerEpisode) {
        podcastDownloader.cancelDownload(episode)
    }

    fun saveAs(episode: PlayerEpisode) {
        podcastDownloader.saveAs(episode)
    }
}