package com.huitao.printer.utils

import android.content.Context

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/13 11:22
 *desc    :
 *version :
 */
data class PortInfo(
    var bluetoothId: String = "",
    var context: Context? = null,
    var parIsOk: Boolean = false,
    var isOpened: Boolean = false,
    var portType: PrinterDev.PortType? = null
)
