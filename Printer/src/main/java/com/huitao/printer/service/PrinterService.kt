package com.huitao.printer.service

import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Binder
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.huitao.printer.printerface.DeviceFoundCallback
import com.huitao.printer.printerface.IMyBinder
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printer.utils.PrinterDev
import com.huitao.printer.utils.ReturnMessage
import com.huitao.printer.utils.RoundQueue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/13 10:39
 *desc    :
 *version :
 */
class PrinterService : Service() {
    private val mMyBinder = MyBinder()
    private lateinit var mPrinterDev: PrinterDev
    private lateinit var mReturnMsg: ReturnMessage
    private var mIsConnected = false
    private var mQueue: RoundQueue<ByteArray>? = null
    private var mDeviceFoundCallback: DeviceFoundCallback? = null
    private fun getInstanceRoundQueue(): RoundQueue<ByteArray> {
        if (this.mQueue == null) {
            mQueue = RoundQueue(500)
        }
        return this.mQueue!!
    }

    override fun onCreate() {
        super.onCreate()
        this.mQueue = this.getInstanceRoundQueue()
    }


    private val mViewModelScope = CoroutineScope(Dispatchers.IO)
    override fun onBind(p0: Intent?): IBinder {
        return this.mMyBinder
    }


    inner class MyBinder : Binder(), IMyBinder {
        private var mBluetoothAdapter: BluetoothAdapter? = null
        private var mFond: MutableList<String>? = null
        private var mBond: MutableList<String>? = null
        private var mPortType: PrinterDev.PortType? = null
        private val mReceiver = object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                p1?.let {
                    if (it.action == "android.bluetooth.device.action.FOUND") {
                        val device =
                            it.getParcelableExtra<BluetoothDevice>("android.bluetooth.device.extra.DEVICE")
                                ?: return
                        if (!device.name.isNullOrEmpty()) {
                            mFond?.forEach { found ->
                                if (found.split("\n").last() == device.address) {
                                    return
                                }
                            }
                            mFond?.add("${device.name}\n${device.address}")
                            mDeviceFoundCallback?.deviceFoundCallback("${device.name} \n ${device.address}")
                        }
                    }
                }
            }
        }

        override fun connectBtPort(var1: String, var2: TaskCallback) {
            mViewModelScope.launch {
                mPrinterDev = PrinterDev(PrinterDev.PortType.Bluetooth, var1)
                mReturnMsg = mPrinterDev.open()
                mPortType = PrinterDev.PortType.Bluetooth
                mViewModelScope.launch(Dispatchers.Main) {
                    when (mReturnMsg.getErrorCode()) {
                        PrinterDev.ErrorCode.OpenPortSucceed -> {
                            mIsConnected = true
                            var2.onSucceed()
                        }
                        else -> {
                            var2.onFailed("Bluetooth connected Failed")
                        }
                    }
                }
            }
        }

        override fun disconnectCurrentPort(var1: TaskCallback) {
            mViewModelScope.launch {
                mReturnMsg = mPrinterDev.close()
                mViewModelScope.launch(Dispatchers.Main) {
                    when (mReturnMsg.getErrorCode()) {
                        PrinterDev.ErrorCode.ClosePortSucceed -> {
                            mIsConnected = false
                            if (mQueue != null) {
                                mQueue?.clear()
                            }
                            var1.onSucceed()
                        }
                        else -> {
                            var1.onFailed("Bluetooth disconnected failed")
                        }
                    }
                }
            }.start()
        }

        override fun clearBuffer() {
            mQueue?.clear()
        }

        override fun checkLinkedState(var1: TaskCallback) {
            mViewModelScope.launch {
                if (mPrinterDev.getPortInfo().isOpened) var1.onSucceed() else var1.onFailed("")
            }.start()
        }

        override fun onDiscovery(
            var1: Context,
            portType: PrinterDev.PortType,
            callback: DeviceFoundCallback
        ): MutableList<String>? {
            this.mFond = mutableListOf()
            this.mBond = mutableListOf()
            mDeviceFoundCallback = callback
            if (portType == PrinterDev.PortType.Bluetooth) {
                this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                if (mBluetoothAdapter == null) {
                    Toast.makeText(
                        this@PrinterService,
                        "Device didn't support bluetooth !\n",
                        Toast.LENGTH_SHORT
                    ).show()
                    return null
                }
                if (mBluetoothAdapter!!.isEnabled) {
                    if (mBluetoothAdapter!!.enable()) {
                        if (!mBluetoothAdapter!!.isDiscovering) {
                            mBluetoothAdapter!!.startDiscovery()
                        }
                        val filter = IntentFilter("android.bluetooth.device.action.FOUND")
                        registerReceiver(mReceiver, filter)
                        val pairedDevice = mBluetoothAdapter!!.bondedDevices
                        if (!pairedDevice.isNullOrEmpty()) {
                            val it = pairedDevice.iterator()
                            while (it.hasNext()) {
                                val device = it.next()
                                mBond?.add("${device.name}\n${device.address}")
                            }
                        } else {
                            Looper.prepare()
                            Toast.makeText(
                                this@PrinterService,
                                "no paired device",
                                Toast.LENGTH_SHORT
                            ).show()
                            Looper.loop()
                        }
                    } else {
                        Toast.makeText(
                            this@PrinterService,
                            "Bluetooth is not enable !\n",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@PrinterService,
                        "Bluetooth adapter is not enabled !\n",
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
            return this.mBond
        }

        override fun getBtAvailableDevice(): MutableList<String> {
            this.mBluetoothAdapter?.cancelDiscovery()
            return this.mFond!!
        }

        override fun write(var1: ByteArray?, var2: TaskCallback) {
            if (var1 != null) {
                mViewModelScope.launch {
                    mReturnMsg = mPrinterDev.write(var1)
                    mViewModelScope.launch(Dispatchers.Main) {
                        when (mReturnMsg.getErrorCode()) {
                            PrinterDev.ErrorCode.WriteDataSucceed -> {
                                mIsConnected = true
                                var2.onSucceed()
                            }
                            else -> {
                                mIsConnected = false
                                var2.onFailed("Write data failed ,please check the device connected")
                            }
                        }
                    }
                }
            }
        }

        override fun writeSendData(var1: TaskCallback, var2: ProcessData) {
            val list = var2.processDataBeforeSend()
            if (list == null) {
                var1.onFailed("Write data is null")
            } else {
                mViewModelScope.launch {
                    list.forEach {
                        mReturnMsg = mPrinterDev.write(it)
                    }
                    when (mReturnMsg.getErrorCode()) {
                        PrinterDev.ErrorCode.WriteDataSucceed -> {
                            mIsConnected = true
                            var1.onSucceed()
                        }
                        else -> {
                            mIsConnected = false
                            var1.onFailed("Bluetooth is no connected,please connect first")
                        }
                    }
                }.start()
            }
        }

        override fun acceptDataFromPrinter(var1: TaskCallback?, var2: Int) {
            val buffer = ByteArray(var2)
            mViewModelScope.launch {
                kotlin.runCatching {
                    mQueue = getInstanceRoundQueue()
                    mQueue?.clear()
                    mQueue?.addLast(buffer)
                    Log.i("frank", "acceptDataFromPrinter: " + Arrays.toString(mQueue!!.last))
                }.onSuccess {

                }.onFailure {

                }
            }
        }

        override fun readBuffer(): RoundQueue<ByteArray?>? {
            return null
        }

        override fun read(var1: TaskCallback) {
            mViewModelScope.launch {
                val msg = mPrinterDev.read()
                Log.d("frank", "read: $msg")
            }
        }

    }
}