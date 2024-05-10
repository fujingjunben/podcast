package com.bigdeal.podcast.ui.v2.common

import androidx.lifecycle.ViewModel
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.core.download.PodcastDownloader
import com.bigdeal.podcast.core.player.service.PlayerController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EpisodeViewModel @Inject constructor(
    private val downloadEpisode: PodcastDownloader,
): ViewModel(){

    fun download(episodeOfPodcast: EpisodeOfPodcast) {
        downloadEpisode.downloadEpisode(episodeOfPodcast.episode)
    }

    fun cancelDownload(episodeOfPodcast: EpisodeOfPodcast) {
        downloadEpisode.cancelDownload(episodeOfPodcast.episode)
    }
}