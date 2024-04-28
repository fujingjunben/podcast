package com.example.jetcaster.util

fun Long.toHHMMSS(): String {
    var minutes = 0L
    var hour = 0L
    var second = 0L

    if (this < 60) {
        return "${this}s"
    }

    var result = ""
    minutes = this / 60
    if (minutes >= 60) {
        hour = minutes / 60
        result = "${hour}h"
    }
    val m = minutes % 60
    if (m > 0) {
        result = "${result}${m}m"
    }

    second = this % 60
    if (second > 0) {
        result = "${result}${second}s"
    }
    return result
}