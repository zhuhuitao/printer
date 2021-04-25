package com.huitao.printer.utils

import java.util.*

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/13 11:36
 *desc    :
 *version :
 */
abstract class PrinterPort() {
    protected var mPortInfo: PortInfo? = null
    protected var mIsOpen = false
    protected var mRxdQueue: Queue<Byte>? = null

    private var mTxdQueue: Queue<Byte>? = null


    abstract fun openPort(): ReturnMessage

    abstract fun closePort(): ReturnMessage

    abstract fun write(var1: Int): ReturnMessage

    abstract fun write(var1: ByteArray): ReturnMessage

    abstract fun write(var1: ByteArray, var2: Int, var3: Int): ReturnMessage

    abstract fun read(): Int

    abstract fun read(var1: ByteArray): ReturnMessage

    abstract fun read(var1: ByteArray, var2: Int, var3: Int): ReturnMessage

    abstract fun portIsOpen(): Boolean

    constructor(portInfo: PortInfo) : this() {
        this.mPortInfo = portInfo
    }

    fun getRxdCount(): Int {
        return if (mRxdQueue != null) mRxdQueue!!.size else 0
    }

    fun getTxdCount(): Int {
        return if (mTxdQueue != null) mTxdQueue!!.size else 0
    }

}