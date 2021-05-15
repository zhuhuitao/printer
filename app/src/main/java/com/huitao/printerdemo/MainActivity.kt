package com.huitao.printerdemo

import android.bluetooth.BluetoothDevice
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.huitao.printer.printerface.DeviceFoundCallback
import com.huitao.printer.printerface.IMyBinder
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printer.service.PrinterService
import com.huitao.printer.utils.DataForSendToPrinter.initializePrinter
import com.huitao.printer.utils.DataForSendToPrinter.printAddPaperWalking
import com.huitao.printer.utils.DataForSendToPrinter.printAndFeedLine
import com.huitao.printer.utils.DataForSendToPrinter.printBothColumns
import com.huitao.printer.utils.DataForSendToPrinter.printThreeColumns
import com.huitao.printer.utils.DataForSendToPrinter.queryPrinterState
import com.huitao.printer.utils.DataForSendToPrinter.selectAliment
import com.huitao.printer.utils.DataForSendToPrinter.selectFont
import com.huitao.printer.utils.DataForSendToPrinter.selectFontSize
import com.huitao.printer.utils.DataForSendToPrinter.selectOrCancelBoldModel
import com.huitao.printer.utils.DataForSendToPrinter.selectOrCancelDoublePrinterModel
import com.huitao.printer.utils.DataForSendToPrinter.setAbsolutePrintPosition
import com.huitao.printer.utils.DataForSendToPrinter.setDefaultLineSpace
import com.huitao.printer.utils.DataForSendToPrinter.setHorizontalMovementPosition
import com.huitao.printer.utils.PrinterDev
import com.huitao.printer.utils.byteArrayOfInts
import com.huitao.printer.utils.strToBytes
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var mMyBinder: IMyBinder? = null
    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            mMyBinder = p1 as IMyBinder
            Log.d(TAG, "onServiceConnected: connected")

        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            Log.d(TAG, "onServiceDisconnected: disconnected")
        }

    }

    private val TAG = MainActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val intent = Intent(this, PrinterService::class.java)
        bindService(intent, mServiceConnection, BIND_AUTO_CREATE)
    }


    fun clickViews(view: View) {
        when (view.id) {
            R.id.tv_start_scan -> {
                mMyBinder?.onDiscovery(
                    this,
                    PrinterDev.PortType.Bluetooth,
                    object : DeviceFoundCallback {
                        override fun deviceFoundCallback(device: String) {
                            Log.d(TAG, "deviceFoundCallback: $device")
                        }
                    })
            }
            R.id.tv_bond -> {
                val list = mMyBinder?.getBtAvailableDevice()
                Log.d(TAG, "clickViews: $list")
                mMyBinder?.getBtAvailableDevice()
            }

            R.id.tv_start_connect -> {

            }
        }
    }

    fun test(view: View) {
        mMyBinder?.writeSendData(object : TaskCallback {
            override fun onSucceed() {
            }

            override fun onFailed() {
            }
        }, object : ProcessData {
            override fun processDataBeforeSend(): MutableList<ByteArray> {
                val list = mutableListOf<ByteArray>()
                list.add(initializePrinter())
                list.add(setAbsolutePrintPosition(0, 0))
                list.add(strToBytes("a")!!)
                list.add(setAbsolutePrintPosition(1, 0))
                list.add(strToBytes("b")!!)
                list.add(setAbsolutePrintPosition(2, 0))
                list.add(strToBytes("c")!!)
                list.add(setAbsolutePrintPosition(3, 0))
                list.add(strToBytes("d")!!)
                list.add(setAbsolutePrintPosition(0x0A, 0))
                list.add(strToBytes("e")!!)
                list.add(printAndFeedLine())
                list.add(printBothColumns("左边文字", "右边文字"))
                list.add(printAndFeedLine())


                list.add(initializePrinter())
                list.add(printThreeColumns("名称", "数量", "单价"))
                list.add(printAndFeedLine())
                /*  list.add(initializePrinter())
                  list.add(selectAliment(0x01))
                  list.add(selectFontSize(0x11))
                  list.add(strToBytes("#1 熊购到家")!!)
                  list.add(printAndFeedLine())
                  list.add(printAndFeedLine())


                  list.add(initializePrinter())
                  list.add(selectAliment(0x01))
                  list.add(selectFontSize(0x01))
                  list.add(strToBytes("*津品汤包*")!!)
                  list.add(printAndFeedLine())
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectFontSize(0x11))
                  list.add(selectAliment(0x01))
                  list.add(strToBytes("--已在线支付--")!!)
                  list.add(printAndFeedLine())

                  list.add(printAndFeedLine())
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(strToBytes("- - - - - - - - - - - - - - - - - -------------")!!)
                  list.add(printAndFeedLine())
                  list.add(printAndFeedLine())


                  list.add(initializePrinter())
                  list.add(selectAliment(0x00))
                  list.add(strToBytes("下单时间：")!!)
                  list.add(strToBytes("2021-04-09 16:02:18")!!)
                  list.add(strToBytes("订单编号：T5874555XG5D52")!!)
                  list.add(printAndFeedLine())
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectAliment(0x00))
                  list.add(strToBytes("客户留言：")!!)
                  list.add(printAndFeedLine())
                  list.add(selectAliment(0x00))
                  list.add(selectOrCancelBoldModel(0x01))
                  list.add(strToBytes("中国人民解放军国防科技大学（National University of Defense Technology），是直属中国共产党中央军事委员会领导的军队综合性大学")!!)
                  list.add(printAndFeedLine())
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectAliment(0x00))
                  list.add(strToBytes("商品明细:")!!)
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectAliment(0x00))
                  list.add(selectOrCancelBoldModel(0x01))
                  list.add(strToBytes("菜包不加辣")!!)
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectAliment(0x02))
                  list.add(selectOrCancelBoldModel(0x01))
                  list.add(strToBytes("X2        ")!!)
                  list.add(setHorizontalMovementPosition(byteArrayOfInts(0x04)))
                  list.add(strToBytes("12")!!)
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectAliment(0x00))
                  list.add(selectOrCancelBoldModel(0x01))
                  list.add(strToBytes("小鱼不加辣")!!)
                  list.add(printAndFeedLine())

                  list.add(initializePrinter())
                  list.add(selectAliment(0x02))
                  list.add(selectOrCancelBoldModel(0x01))
                  list.add(strToBytes("X2        ")!!)
                  list.add(setHorizontalMovementPosition(byteArrayOfInts(0x04)))
                  list.add(strToBytes("12")!!)
                  list.add(printAndFeedLine())


                  list.add(initializePrinter())
                  list.add(setAbsolutePrintPosition(0x00,0x00))
                  list.add(strToBytes("测试绝对位置")!!)
                  list.add(setAbsolutePrintPosition(0x300,0x00))
                  list.add()*/
                return list
            }
        })
    }


    fun writeData() {
        val handler = Handler()
        val run = Runnable {
            mMyBinder?.writeSendData(object : TaskCallback {
                override fun onSucceed() {
                }

                override fun onFailed() {
                }

            }, object : ProcessData {
                override fun processDataBeforeSend(): MutableList<ByteArray> {
                    return arrayListOf(queryPrinterState())
                }
            })

            while (true) {
                mMyBinder?.read(object : TaskCallback {
                    override fun onSucceed() {
                    }

                    override fun onFailed() {
                    }
                })
            }
        }
        viewModelStore.runCatching {
            kotlin.runCatching {
                handler.postDelayed(
                    run, 3000
                )
            }.onSuccess {
                Log.d(TAG, "writeData:成功 ")
                handler.postDelayed(run, 1000)
            }.onFailure {
                Log.d(TAG, "writeData: 失败")
            }

        }

    }


}