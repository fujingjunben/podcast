package com.bigdeal.core.data.extension

import java.security.MessageDigest

fun String.toSHA256(): String {
    val digest = MessageDigest.getInstance("SHA-256")
    val hashBytes = digest.digest(toByteArray())
    val hexString = StringBuilder()

    for (byte in hashBytes) {
        val hex = String.format("%02x", byte)
        hexString.append(hex)
    }

    return hexString.toString()
}