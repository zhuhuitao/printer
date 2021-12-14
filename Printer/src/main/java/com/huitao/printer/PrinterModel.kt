package com.huitao.printer

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import com.huitao.printer.listener.BondListCallback
import com.huitao.printer.printerface.DeviceFoundCallback
import com.huitao.printer.printerface.IMyBinder
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printer.service.PrinterService
import com.huitao.printer.utils.PrinterDev


/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 */
class PrinterModel constructor(private val printer: Printer) {

    private var mIMyBinder: IMyBinder? = null
    private val mServiceConnect = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mIMyBinder = service as IMyBinder
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            startPrinterService()
        }
    }


    /**
     * @return return the bond devices
     */
    fun getBondDeviceList(deviceFoundCallback: DeviceFoundCallback): MutableList<String>? {
        mIMyBinder?.let {
            printer.getContext()?.let { ctx ->
                return it.onDiscovery(
                    ctx,
                    PrinterDev.PortType.Bluetooth,
                    deviceFoundCallback
                )
            }

        }
        return null
    }

    fun connectDeviceByMac(mac: String, taskCallback: TaskCallback) {
        mIMyBinder?.let {
            it.connectBtPort(mac, taskCallback)
            return
        }
        taskCallback.onFailed("mIMyBinder is not initialization,the lib was wrong")
    }

    fun disconnected(taskCallback: TaskCallback) {
        mIMyBinder?.let {
            it.disconnectCurrentPort(taskCallback)
            return
        }
        taskCallback.onFailed("mIMyBinder is not initialization,the lib was wrong")
    }

    fun writeData(taskCallback: TaskCallback, processData: ProcessData) {
        mIMyBinder?.let {
            it.writeSendData(taskCallback, processData)
            return
        }
        taskCallback.onFailed("mIMyBinder is not initialization,the lib was wrong")
    }

    fun writeData(byteArray: ByteArray, taskCallback: TaskCallback) {
        mIMyBinder?.let {
            it.write(byteArray, taskCallback)
            return
        }
        taskCallback.onFailed("mIMyBinder is not initialization,the lib was wrong")
    }


    fun startPrinterService(): PrinterModel {
        printer.getContext()?.let {
            val service = Intent(it, PrinterService::class.java)
            it.bindService(service, mServiceConnect, Service.BIND_AUTO_CREATE)
        }
        return this
    }


}