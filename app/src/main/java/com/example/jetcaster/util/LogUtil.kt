package com.example.jetcaster.util

import timber.log.Timber

object LogUtil {
    private const val TAG = "PlayerBar"
    fun d(msg: String){
        Timber.d(TAG, msg)
    }
    fun d(tag: String, msg: String) {
        Timber.d(tag, msg)
    }

}