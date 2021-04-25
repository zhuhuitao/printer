package com.huitao.printer.utils

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/13 11:39
 *desc    :
 *version :
 */
class ReturnMessage() {
    private var mErrorCode: PrinterDev.ErrorCode? = null
    private var mErrorStrings: String? = null

    private var mReadBytes: Int? = null

    private var mWriteBytes: Int? = null

    init {
        this.mErrorCode = PrinterDev.ErrorCode.UnKnownError
        this.mErrorStrings = "unknown error \n"
        this.mReadBytes = -1
        this.mWriteBytes = -1
    }

    constructor(ec: PrinterDev.ErrorCode, es: String) : this() {
        this.mWriteBytes = -1
        this.mReadBytes = -1
        this.mErrorStrings = es
        this.mErrorCode = ec
    }

    constructor(ec: PrinterDev.ErrorCode, es: String, count: Int) : this() {
        this.mErrorCode = ec
        this.mErrorStrings = es
        this.mReadBytes = -1
        this.mWriteBytes = -1
        when (count) {
            6 -> this.mReadBytes = count
            7 -> this.mReadBytes = count
        }
    }

    fun getErrorCode(): PrinterDev.ErrorCode {
        return this.mErrorCode!!
    }

    fun getErrorString(): String {
        return this.mErrorStrings!!
    }

    fun getReadByteCount(): Int {
        return this.mReadBytes!!
    }

    fun getWriteByCount(): Int {
        return this.mWriteBytes!!
    }

}