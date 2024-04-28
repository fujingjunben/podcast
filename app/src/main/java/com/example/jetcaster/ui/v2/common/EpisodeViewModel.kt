package com.example.jetcaster.ui.v2.common

import androidx.lifecycle.ViewModel
import com.example.jetcaster.Graph
import com.example.jetcaster.play.PlayerController

class EpisodeViewModel(
    private val playerController: PlayerController = Graph.playerController
): ViewModel(){
    fun play(episodeOfPodcast: EpisodeOfPodcast) {
        playerController.play(episodeOfPodcast.toEpisode())
    }
}