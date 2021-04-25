package com.huitao.printer.utils

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/15 10:45
 *desc    :
 *version :
 */


fun byteArrayOfInts(vararg ints: Int) =
    ByteArray(ints.size) { pos -> ints[pos].toByte() }


fun byteMerge(b1: ByteArray, b2: ByteArray): ByteArray {
    val b3 = ByteArray(b1.size + b2.size)
    System.arraycopy(b1, 0, b3, 0, b1.size)
    System.arraycopy(b2, 0, b3, b1.size, b2.size)
    return b3
}

fun getByteLength(str: String): Int {
    return str.toByteArray(charSetName()).size
}