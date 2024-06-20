package com.bigdeal.podcast.ui.library

import android.content.ContentResolver
import android.net.Uri
import androidx.core.net.toFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bigdeal.core.data.FeedRepository
import com.bigdeal.core.data.PodcastStore
import com.bigdeal.core.data.PodcastsRepository
import com.bigdeal.podcast.core.opml.OpmlImporter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val feedRepository: FeedRepository,
    private val podcastStore: PodcastStore,
    private val podcastsRepository: PodcastsRepository
): ViewModel() {
    fun importOpml(contentResolver: ContentResolver, uri: Uri) {
        Timber.d("uri: $uri")

        viewModelScope.launch {
            uri.path?.let {
                val inputStream = contentResolver.openInputStream(uri)
                async {
                    inputStream?.let {
                        val opmlImporter = OpmlImporter()
                        val feedEntries = opmlImporter.parse(it)
                        feedEntries.forEach {feedEntry ->
                            Timber.d("feedEntry: $feedEntries")
                            feedRepository.addFeed(feedEntry.url)
//                        awaitAll(
//                            async { podcastsRepository.fetchPodcasts(listOf(it.url)) },
//                            async { podcastStore.followPodcast(feedEntity.id) }
//                        )
                        }
                    }
                }.await()
                podcastsRepository.sync()
            }
        }
    }
}