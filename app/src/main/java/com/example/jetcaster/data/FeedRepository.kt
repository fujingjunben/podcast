package com.example.jetcaster.data

import com.example.jetcaster.data.room.FeedDao
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

class FeedRepository (
    private val feedDao: FeedDao,
    ioDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(ioDispatcher)

    val feedFlow: MutableStateFlow<List<FeedEntity>> = MutableStateFlow(emptyList())

    init {
        scope.launch {
            feedDao.queryAll().collect{
                feedFlow.value = it
            }
        }
    }

    fun addFeed(url: String) {
        scope.launch {
            Timber.d("feeddao insert: $url")
            feedDao.insert(FeedEntity(url = url))
        }
    }
}