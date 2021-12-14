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
            //获取周围蓝牙设备，已绑定的蓝牙设备会直接通过集合返回，扫描发现的设备通过DeviceFoundCallback回调回来
            val list = Printer.getInstance().getBondDevicesList(object : DeviceFoundCallback {
                override fun deviceFoundCallback(device: String) {
                    //处理扫描发现的周围蓝牙设备
                }
            })
            if (mList.isNotEmpty()) mList.clear()
            list?.forEach {
                mList.add(SimpleBlueDevice(name = it, isConnected = false))
            }
            mAdapter.notifyDataSetChanged()
        } else {
            //如果设备已连接，则执行写入数据
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
                //打印成功
            }

            override fun onFailed(error: String) {
                //打印失败
            }
        }, object : ProcessData {
            //组装数据
            override fun processDataBeforeSend(): MutableList<ByteArray>? {
                val list = ArrayList<ByteArray>()
                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.selectAliment(0x01))
                list.add(DataForSendToPrinter.selectFontSize(0x11))
                list.add(strToBytes("#${order.pickUpCode} 测试外卖")!!)
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
                list.add(strToBytes("--已在线支付--")!!)
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.printBothColumns("配送方式：", order.deliveryTypeStr))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.printBothColumns("下单时间：", order.createTime))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())

                val onTime = order.receiveTime
                if (onTime != null) {
                    list.add(DataForSendToPrinter.initializePrinter())
                    list.add(DataForSendToPrinter.printBothColumns("预计送达时间：", onTime))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(DataForSendToPrinter.selectOrCancelBoldModel(0x01))
                list.add(strToBytes("客户留言：")!!)
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
                            "收货人：",
                            "${order.receiver.substring(0, 1)}**"
                        )
                    )
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.receiverMobile != null) {
                    list.add(DataForSendToPrinter.printBothColumns("电话：", order.receiverMobile))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.riderName != null) {
                    list.add(DataForSendToPrinter.printBothColumns("骑手：", order.riderName))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.riderMobile != null) {
                    list.add(DataForSendToPrinter.printBothColumns("电话：", order.riderMobile))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                if (order.address != null) {
                    list.add(strToBytes("收货地址：${order.address}")!!)
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                list.add(DataForSendToPrinter.printThreeColumns("名称", "数量", "售价"))
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
                            "￥$price"
                        )
                    )
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }

                list.add(DataForSendToPrinter.initializePrinter())
                list.add(
                    DataForSendToPrinter.printBothColumns(
                        "订单原价：",
                        "￥${numberFormat(order.money)}"
                    )
                )
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(
                    DataForSendToPrinter.printBothColumns(
                        "配送费：",
                        "￥${numberFormat(order.freight)}"
                    )
                )
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(
                    DataForSendToPrinter.printBothColumns(
                        "实付金额：",
                        "￥${numberFormat(order.payMoney)}"
                    )
                )
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printBothColumns("订单类型：", order.orderTypeStr))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                if (order.orderTypeStr.contains("预约")) {
                    list.add(DataForSendToPrinter.printBothColumns("预约时间：", order.appointmentTime))
                    list.add(DataForSendToPrinter.printAndFeedLine())
                    list.add(DataForSendToPrinter.printAndFeedLine())
                }
                list.add(DataForSendToPrinter.printBothColumns("订单号：", order.orderCode))
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                list.add(DataForSendToPrinter.printAndFeedLine())
                return list
            }
        })
    }

    override fun onClickViewListener(view: View, obj: Any) {
        //获取当前设备的连接状态，如果已连接，则断开，未连接则连接
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