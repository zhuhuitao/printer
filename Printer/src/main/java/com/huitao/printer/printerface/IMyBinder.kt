package com.huitao.printer.printerface

import android.content.Context

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/13 10:42
 *desc    :
 *version :
 */
interface IMyBinder {
    fun connectBtPort(var1: String, var2: TaskCallback)

    fun disconnectCurrentPort(var1: TaskCallback)

    fun clearBuffer()

    fun checkLinkedState(var1: TaskCallback)

    fun onDiscovery(var1: Context): MutableList<String>?

    fun getBtAvailableDevice(): MutableList<String>

    fun write(var1:ByteArray?,var2:TaskCallback)

    fun writeSendData(var1:TaskCallback,var2:ProcessData)
}