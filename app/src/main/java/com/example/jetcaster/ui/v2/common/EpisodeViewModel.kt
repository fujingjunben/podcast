package com.example.jetcaster.ui.v2.common

import androidx.lifecycle.ViewModel
import com.example.jetcaster.Graph
import com.example.jetcaster.download.PodcastDownloader
import com.example.jetcaster.play.PlayerController

class EpisodeViewModel(
    private val playerController: PlayerController = Graph.playerController,
    private val downloadEpisode: PodcastDownloader = Graph.downloadEpisode
): ViewModel(){
    fun play(episodeOfPodcast: EpisodeOfPodcast) {
        playerController.play(episodeOfPodcast.toEpisode())
    }

    fun download(episodeOfPodcast: EpisodeOfPodcast) {
        downloadEpisode.downloadEpisode(episodeOfPodcast.episode)
    }

    fun cancelDownload(episodeOfPodcast: EpisodeOfPodcast) {
        downloadEpisode.cancelDownload(episodeOfPodcast.episode)
    }
}