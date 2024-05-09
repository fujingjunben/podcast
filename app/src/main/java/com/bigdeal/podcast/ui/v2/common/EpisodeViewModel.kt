package com.bigdeal.podcast.ui.v2.common

import androidx.lifecycle.ViewModel
import com.bigdeal.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.service.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val playerController: com.bigdeal.podcast.core.player.service.PlayerController,
    private val downloadEpisode: PodcastDownloader,
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