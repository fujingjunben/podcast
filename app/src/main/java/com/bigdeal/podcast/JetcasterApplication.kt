package com.bigdeal.podcast

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.bigdeal.podcast.sync.initializers.Sync
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

/**
 * Application which sets up our dependency [Graph] with a context.
 */
@HiltAndroidApp
class JetcasterApplication : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        Sync.initialize(this)
    }

    @Inject
    lateinit var imageLoader: ImageLoader

    override fun newImageLoader(): ImageLoader = imageLoader
}
