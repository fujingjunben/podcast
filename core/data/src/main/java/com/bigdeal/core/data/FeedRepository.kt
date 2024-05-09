package com.bigdeal.core.data

import com.bigdeal.core.Dispatcher
import com.bigdeal.core.JetcasterDispatchers
import com.bigdeal.core.data.room.FeedDao
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class FeedRepository (
    private val feedDao: FeedDao,
    @Dispatcher(JetcasterDispatchers.IO) ioDispatcher: CoroutineDispatcher
) {
    private val scope = CoroutineScope(ioDispatcher)

    val feedFlow: MutableStateFlow<List<FeedEntity>> = MutableStateFlow(emptyList())

    init {
        scope.launch {
            feedDao.queryAll().collect{
                Timber.d("feed query alll, $it")
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