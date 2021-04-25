package com.huitao.printer.utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import java.io.InputStream
import java.io.OutputStream
import java.lang.Exception
import java.util.*

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/13 11:08
 *desc    :
 *version :
 */
class PrinterDev(portType: PortType, bluetoothId: String) {

    enum class ErrorCode private constructor() {
        OpenPortFailed,
        OpenPortSucceed,
        ClosePortFailed,
        ClosePortSucceed,
        WriteDataSucceed,
        WriteDataFailed,
        ReadDataSucceed,
        ReadDataFailed,
        UnKnownError;
    }

    enum class PortType private constructor() {
        UnKnown,
        USB,
        Bluetooth,
        Ethernet;
    }

    private var mPortInfo: PortInfo = PortInfo()
    private var mPrinterPort: PrinterPort? = null


    init {
        this.mPortInfo.portType = portType
        this.mPortInfo.bluetoothId = bluetoothId
    }

    private fun resetPar() {
        mPortInfo = PortInfo()
        this.mPrinterPort?.closePort()
        this.mPrinterPort = null
    }


    fun open(): ReturnMessage {
        return when (mPortInfo.portType) {
            PortType.Bluetooth -> {
                this.open(this.mPortInfo.portType!!, this.mPortInfo.bluetoothId)
            }
            else -> {
                ReturnMessage(ErrorCode.OpenPortFailed, "Only support bluetooth !\n")
            }
        }
    }

    private fun open(portType: PortType, bluetoothId: String): ReturnMessage {
        this.resetPar()
        return when {
            portType != PortType.Bluetooth -> ReturnMessage(
                ErrorCode.OpenPortFailed,
                "Port type wrong !\n"
            )
            !BluetoothAdapter.checkBluetoothAddress(bluetoothId) -> ReturnMessage(
                ErrorCode.OpenPortFailed,
                "BluetoothId wrong !/n"
            )
            else -> {
                this.mPortInfo.bluetoothId = bluetoothId
                this.mPortInfo.portType = PortType.Bluetooth
                this.mPrinterPort = BluetoothPort(this.mPortInfo)
                this.mPrinterPort!!.openPort()
            }
        }

    }

    @Synchronized
    fun close(): ReturnMessage {
        return if (this.mPrinterPort == null) ReturnMessage(
            ErrorCode.ClosePortFailed,
            "Not opened port !"
        ) else this.mPrinterPort!!.closePort()
    }

    fun write(data: ByteArray): ReturnMessage {
        return this.mPrinterPort!!.write(data)
    }

    fun getPortInfo(): PortInfo {
        this.mPortInfo.isOpened = this.mPrinterPort!!.portIsOpen()
        return this.mPortInfo
    }

    private inner class BluetoothPort(portInfo: PortInfo) : PrinterPort(portInfo) {
        private val SPP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        private var mBluetoothAdapter: BluetoothAdapter? = null
        private var mBluetoothDevice: BluetoothDevice? = null
        private var mBluetoothSocket: BluetoothSocket? = null
        private var mOutputStream: OutputStream? = null
        private var mInputStream: InputStream? = null

        init {
            when {
                portInfo.portType == PortType.Bluetooth && BluetoothAdapter.checkBluetoothAddress(
                    portInfo.bluetoothId
                ) -> {
                    this.mPortInfo?.parIsOk = true
                    this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
                }
                else -> this.mPortInfo?.parIsOk = false
            }
        }

        override fun openPort(): ReturnMessage {
            if (!this.mPortInfo!!.parIsOk) {
                return ReturnMessage(
                    ErrorCode.OpenPortFailed,
                    "PortInfo error !\n"
                )
            } else {
                try {
                    if (this.mBluetoothAdapter == null) {
                        return ReturnMessage(
                            ErrorCode.OpenPortFailed,
                            "Not Bluetooth adapter !\n"
                        )
                    }
                    if (!this.mBluetoothAdapter!!.isEnabled) {
                        return ReturnMessage(
                            ErrorCode.OpenPortFailed,
                            "Bluetooth adapter was closed !\n"
                        )
                    }
                    this.mBluetoothAdapter?.cancelDiscovery()
                    this.mBluetoothDevice =
                        this.mBluetoothAdapter?.getRemoteDevice(this.mPortInfo?.bluetoothId)
                    this.mBluetoothSocket =
                        this.mBluetoothDevice?.createRfcommSocketToServiceRecord(this.SPP_UUID)
                    this.mBluetoothSocket?.connect()
                    this.mOutputStream = null
                    this.mOutputStream = this.mBluetoothSocket?.outputStream
                    this.mInputStream = this.mBluetoothSocket?.inputStream
                    this.mIsOpen = true
                } catch (e: Exception) {
                    return ReturnMessage(ErrorCode.OpenPortFailed, e.toString())
                }
            }

            return ReturnMessage(ErrorCode.OpenPortSucceed, "Open bluetooth port success !\n")
        }

        override fun closePort(): ReturnMessage {
            try {
                if (this.mOutputStream != null) {
                    this.mOutputStream!!.flush()
                }
                if (this.mBluetoothSocket != null) {
                    this.mBluetoothSocket?.close()
                }
                this.mIsOpen = false
                this.mOutputStream = null
                this.mInputStream = null
            } catch (e: Exception) {
                return ReturnMessage(ErrorCode.ClosePortFailed, e.toString())
            }
            return ReturnMessage(ErrorCode.ClosePortSucceed, "Close bluetooth port success !\n")
        }

        override fun write(var1: Int): ReturnMessage {
            if (this.mIsOpen && this.mBluetoothSocket!!.isConnected && mOutputStream != null) {
                try {
                    this.mOutputStream?.write(var1)
                } catch (e: Exception) {
                    this.closePort()
                    return ReturnMessage(ErrorCode.WriteDataFailed, e.toString())
                }
                return ReturnMessage(ErrorCode.WriteDataSucceed, "Send 1 byte !\n", 1)
            } else {
                return ReturnMessage(ErrorCode.WriteDataFailed, "Bluetooth port was closed !\n")
            }
        }

        override fun write(var1: ByteArray): ReturnMessage {
            if (this.mIsOpen && this.mBluetoothSocket!!.isConnected && mOutputStream != null) {
                try {
                    this.mOutputStream?.write(var1)
                } catch (e: Exception) {
                    this.closePort()
                    return ReturnMessage(ErrorCode.WriteDataFailed, e.toString())
                }
                return ReturnMessage(ErrorCode.WriteDataSucceed, "Send ${var1.size} bytes !\n")
            } else {
                return ReturnMessage(ErrorCode.WriteDataFailed, "Bluetooth port was closed !\n")
            }
        }

        override fun write(var1: ByteArray, var2: Int, var3: Int): ReturnMessage {
            if (this.mIsOpen && this.mBluetoothSocket!!.isConnected && mOutputStream != null) {
                try {
                    this.mOutputStream?.write(var1, var2, var3)
                } catch (e: Exception) {
                    this.closePort()
                    return ReturnMessage(ErrorCode.WriteDataFailed, e.toString())
                }
                return ReturnMessage(ErrorCode.WriteDataSucceed, "Send $var3 bytes !\n ", var3)
            } else {
                return ReturnMessage(ErrorCode.WriteDataFailed, "Bluetooth port was closed")
            }
        }

        override fun read(): Int {
            return if (this.mIsOpen && this.mBluetoothSocket!!.isConnected && this.mInputStream != null) {
                try {
                    this.mInputStream!!.read()
                } catch (e: Exception) {
                    -1
                }
            } else {
                -1
            }
        }

        override fun read(var1: ByteArray): ReturnMessage {
            return this.read(var1, 0, var1.size)
        }

        override fun read(var1: ByteArray, var2: Int, var3: Int): ReturnMessage {
            if (this.mIsOpen && this.mBluetoothSocket!!.isConnected && this.mInputStream != null) {
                val readBytes: Int
                try {
                    readBytes = this.mInputStream!!.read(var1, var2, var3)
                } catch (e: Exception) {
                    return ReturnMessage(ErrorCode.ReadDataFailed, e.toString())
                }
                return ReturnMessage(ErrorCode.ReadDataSucceed, "Read $var3 bytes !\n", readBytes)
            } else {
                return ReturnMessage(ErrorCode.ReadDataFailed, "Bluetooth port was close !\n")
            }
        }

        override fun portIsOpen(): Boolean {
            val b = ByteArray(4)
            val msg: ReturnMessage = this.read(b)
            this.mIsOpen = msg.getReadByteCount() != -1
            return this.mIsOpen
        }

    }
}