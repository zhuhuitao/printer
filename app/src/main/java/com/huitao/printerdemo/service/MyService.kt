package com.huitao.printerdemo.service

import android.app.Service
import android.content.*
import android.os.Binder
import android.os.Build
import android.os.IBinder
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.huitao.printer.printerface.DeviceFoundCallback
import com.huitao.printer.printerface.IMyBinder
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printer.service.PrinterService
import com.huitao.printer.utils.PrinterDev
import com.huitao.printerdemo.bean.OrderBean
import com.huitao.utils.printOrderDetail

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 18:07
 *desc    :
 *version :
 */
class MyService : Service() {
    private var mIMyBinder: IMyBinder? = null
    private var mIsConnected = false
    private lateinit var mLocalBroadcastManager: LocalBroadcastManager
    private val mServiceConnect = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            mIMyBinder = service as IMyBinder
            val mac = ""
            connectByMac(mac, object : TaskCallback {
                override fun onSucceed() {

                }

                override fun onFailed() {

                }
            })
        }

        override fun onServiceDisconnected(name: ComponentName?) {
        }
    }

    fun getBondDeviceList(): MutableList<String>? {
        return mIMyBinder?.onDiscovery(
            this,
            PrinterDev.PortType.Bluetooth,
            object : DeviceFoundCallback {
                override fun deviceFoundCallback(device: String) {
                }
            })
    }


    fun writeData(bean: OrderBean, task: TaskCallback) {
        printOrderDetail(mMyBinder = mIMyBinder!!, order = bean, taskCallback = task)
    }

    fun connectByMac(mac: String, task: TaskCallback) {
        if (mac.isNotEmpty()) {
            mIMyBinder?.connectBtPort(mac, task)
        }
    }

    fun disconnect(task: TaskCallback) {
        mIMyBinder?.disconnectCurrentPort(task)
    }

    override fun onCreate() {
        super.onCreate()
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this)
        val intent = Intent(this, AncillaryService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AncillaryService.startForeground(this)
            startForegroundService(intent)
        } else {
            startService(intent)
        }

        val service = Intent(this, PrinterService::class.java)
        bindService(service, mServiceConnect, BIND_AUTO_CREATE)
    }

    override fun onBind(p0: Intent?): IBinder {
        return MyBinder()
    }


    inner class MyBinder : Binder() {
        fun getService(): MyService {
            return this@MyService
        }
    }
}