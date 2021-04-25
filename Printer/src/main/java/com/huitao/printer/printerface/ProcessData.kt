package com.huitao.printer.printerface

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/14 10:15
 *desc    :
 *version :
 */
interface ProcessData {
    fun processDataBeforeSend(): MutableList<ByteArray>?
}