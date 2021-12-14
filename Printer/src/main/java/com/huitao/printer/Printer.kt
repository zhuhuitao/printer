package com.huitao.printer

import android.content.Context
import com.huitao.printer.printerface.DeviceFoundCallback
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import java.lang.ref.WeakReference

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 */
class Printer private constructor() {
    private var context: WeakReference<Context>? = null
    private lateinit var printerModel: PrinterModel

    companion object {
        private var singleInstance: Printer? = null
            get() {
                if (null == field) {
                    field = Printer()
                }

                return field
            }

        @Synchronized
        fun getInstance() = singleInstance!!

    }

    fun initPrinter(context: Context) {
        this.context = WeakReference(context)
        printerModel = PrinterModel(this)
        printerModel.startPrinterService()
    }


    fun getContext(): Context? {
        return this.context?.get()
    }


    fun getBondDevicesList(foundCallback: DeviceFoundCallback): MutableList<String>? {
        return printerModel.getBondDeviceList(foundCallback)
    }


    /**
     * @param[mac] the printer mac
     * @param[taskCallback] the connect state by the taskCallback call
     */
    fun connectDeviceByMac(mac: String, taskCallback: TaskCallback) {
        printerModel.connectDeviceByMac(mac, taskCallback)
    }

    /**
     * @param[taskCallback] the disconnect state by taskCallback call
     */
    fun disconnected(taskCallback: TaskCallback) {
        printerModel.disconnected(taskCallback)
    }


    /**
     * @param[taskCallback] write state by the taskCallback call
     * @param[processData] the send data
     */
    fun writeData(taskCallback: TaskCallback, processData: ProcessData) {
        printerModel.writeData(taskCallback, processData)
    }

    /**
     * @param[byteArray] the send data
     * @param[taskCallback] write state by the taskCallback call
     */
    fun writeData(byteArray: ByteArray, taskCallback: TaskCallback) {
        printerModel.writeData(byteArray, taskCallback)
    }


}