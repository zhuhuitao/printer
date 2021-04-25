package com.huitao.printer.utils

import android.util.Log
import java.lang.Exception
import java.nio.charset.Charset

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/15 13:51
 *desc    :
 *version :
 */

fun charSetName(): Charset = Charset.forName("gbk")

fun strToBytes(str: String): ByteArray? {

    val data: ByteArray
    try {
        val b = str.toByteArray(charset = Charsets.UTF_8)
        data = String(b).toByteArray(Charset.forName("gbk"))
    } catch (e: Exception) {
        return null
    }
    return data
}


fun strToBytes(str: String, charset: String): ByteArray? {
    val data: ByteArray?
    try {
        val b = str.toByteArray(charset = Charsets.UTF_8)
        data = String(b).toByteArray(Charset.forName("gbk"))
    } catch (e: Exception) {
        return null
    }

    return data
}

