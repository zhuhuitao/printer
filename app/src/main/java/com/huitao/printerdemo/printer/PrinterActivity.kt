package com.huitao.printerdemo.printer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.huitao.printer.Printer
import com.huitao.printer.printerface.DeviceFoundCallback
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printer.utils.DataForSendToPrinter
import com.huitao.printer.utils.strToBytes
import com.huitao.printerdemo.R
import com.huitao.printerdemo.adapter.BlueAdapter
import com.huitao.printerdemo.bean.OrderBean
import com.huitao.printerdemo.bean.SimpleBlueDevice
import com.huitao.printerdemo.bean.getOrderData
import com.huitao.printerdemo.databinding.ActivityPrinterBinding
import com.huitao.printerdemo.printerface.AdapterClickListener
import com.huitao.printerdemo.service.MyService
import com.huitao.utils.SpaceItemDecoration
import com.huitao.utils.numberFormat

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 19:00
 *desc    :
 *version :
 */
class PrinterActivity : AppCompatActivity(), AdapterClickListener {
    private lateinit var mList: ArrayList<SimpleBlueDevice>
    private lateinit var mAdapter: BlueAdapter
    private lateinit var mBinding: ActivityPrinterBinding
    private var mIsConnect = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_printer)
        initViews()
    }

    private fun initViews() {
        mList = ArrayList()
        mAdapter = BlueAdapter(mList, this).apply {
            setOnClickListener(this@PrinterActivity)
        }
        mBinding.rv.apply {
            addItemDecoration(SpaceItemDecoration(0, 12, true))
            adapter = mAdapter
        }

    }


    fun obtainBondDevices(view: View) {
        if (view.id == R.id.tv_bond_device) {
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????DeviceFoundCallback????????????
            val list = Printer.getInstance().getBondDevicesList(object : DeviceFoundCallback {
                override fun deviceFoundCallback(device: String) {
                    //???????????????????????????????????????
                }
            })
            if (mList.isNotEmpty()) mList.clear()
            list?.forEach {
                mList.add(SimpleBlueDevice(name = it, isConnected = false))
            }
            mAdapter.notifyDataSetChanged()
        } else {
            //?????????????????????????????????????????????
            if (mIsConnect) {
                writeData(getOrderData())
            } else {
                Toast.makeText(this, getString(R.string.please_connect_first), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }


    private fun writeData(order: OrderBean) {
        Printer.getInstance().writeData(object : TaskCallback {
            override fun onSucceed() {
                //????????????
            }

            override fun onFailed(error: String) {
                //????????????
            }
        }, object : ProcessData {
            //????????????
            override fun processDataBeforeSend(): MutableList<ByteArray>? {
                val list = ArrayList<ByteArray>()
                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.selectAliment(0x01))
                list.add(DataForSendToPrinter.selectFontSize(0x11))
                list.add(strToBytes("#${order.pickUpCode} ????????????")!!)
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.selectAliment(0x01))
                list.add(DataForSendToPrinter.selectFontSize(0x01))
                list.add(strToBytes("*${order.shopName}*")!!)
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.selectFontSize(0x11))
                list.add(DataForSendToPrinter.selectAliment(0x01))
                list.add(strToBytes("--???????????????--")!!)
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.printBothColumns("???????????????", order.deliveryTypeStr))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.printBothColumns("???????????????", order.createTime))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                val onTime = order.receiveTime
                if (onTime != null) {
                    list.add(DataForSendToPrinter.initializePrinter())
                    list.add(DataForSendToPrinter.printBothColumns("?????????????????????", onTime))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.selectOrCancelBoldModel(0x01))
                list.add(strToBytes("???????????????")!!)
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                if (order.remarks == null) {
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                } else {
                    list.add(strToBytes(order.remarks)!!)
                }
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                if (order.receiver != null && order.receiver.isNotEmpty()) {
                    list.add(
                        DataForSendToPrinter.printBothColumns(
                            "????????????",
                            "${order.receiver.substring(0, 1)}**"
                        )
                    )
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.receiverMobile != null) {
                    list.add(DataForSendToPrinter.printBothColumns("?????????", order.receiverMobile))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.riderName != null) {
                    list.add(DataForSendToPrinter.printBothColumns("?????????", order.riderName))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.riderMobile != null) {
                    list.add(DataForSendToPrinter.printBothColumns("?????????", order.riderMobile))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.address != null) {
                    list.add(strToBytes("???????????????${order.address}")!!)
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                list.add(DataForSendToPrinter.printThreeColumns("??????", "??????", "??????"))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                order.orderDetailList.forEach {
                    val price = when (it.isDiscount) {
                        1 -> numberFormat(it.amount)
                        else -> numberFormat(it.shopPrice)
                    }
                    list.add(DataForSendToPrinter.initializePrinter())
                    list.add(DataForSendToPrinter.selectOrCancelBoldModel(0x01))
                    list.add(strToBytes(it.goodsName)!!)
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(
                        DataForSendToPrinter.printThreeColumns(
                            "",
                            "${it.buyCount}",
                            "???$price"
                        )
                    )
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(
                    DataForSendToPrinter.printBothColumns(
                        "???????????????",
                        "???${numberFormat(order.money)}"
                    )
                )
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(
                    DataForSendToPrinter.printBothColumns(
                        "????????????",
                        "???${numberFormat(order.freight)}"
                    )
                )
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(
                    DataForSendToPrinter.printBothColumns(
                        "???????????????",
                        "???${numberFormat(order.payMoney)}"
                    )
                )
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printBothColumns("???????????????", order.orderTypeStr))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                if (order.orderTypeStr.contains("??????")) {
                    list.add(DataForSendToPrinter.printBothColumns("???????????????", order.appointmentTime))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                list.add(DataForSendToPrinter.printBothColumns("????????????", order.orderCode))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                return list
            }
        })
    }

    override fun onClickViewListener(view: View, obj: Any) {
        //????????????????????????????????????????????????????????????????????????????????????
        when (mList[obj as Int].isConnected) {
            true -> {
                Printer.getInstance().disconnected(object : TaskCallback {
                    override fun onSucceed() {
                        Toast.makeText(
                            this@PrinterActivity,
                            getString(R.string.disconnect_success),
                            Toast.LENGTH_SHORT
                        ).show()
                        mList[obj].isConnected = false
                        mAdapter.notifyItemChanged(obj)
                        mIsConnect = false
                    }

                    override fun onFailed(error: String) {
                        Toast.makeText(
                            this@PrinterActivity,
                            getString(R.string.disconnect_failure),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                })
            }

            else -> {
                Printer.getInstance().connectDeviceByMac(mList[obj].name.split("\n").last().trim(),
                    object : TaskCallback {
                        override fun onSucceed() {
                            Toast.makeText(
                                this@PrinterActivity,
                                getString(R.string.connect_success),
                                Toast.LENGTH_SHORT
                            ).show()
                            mIsConnect = true
                            mList[obj].isConnected = true
                            mAdapter.notifyItemChanged(obj)
                        }

                        override fun onFailed(error: String) {
                            Toast.makeText(
                                this@PrinterActivity,
                                getString(R.string.connect_failure),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }

    }


}