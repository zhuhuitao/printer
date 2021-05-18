package com.huitao.utils

import com.huitao.printer.printerface.IMyBinder
import com.huitao.printer.printerface.ProcessData
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printer.utils.DataForSendToPrinter
import com.huitao.printer.utils.strToBytes
import com.huitao.printerdemo.bean.OrderBean
import java.text.DecimalFormat

fun printOrderDetail(mMyBinder: IMyBinder, order: OrderBean, taskCallback: TaskCallback) {
    mMyBinder.writeSendData(taskCallback, object : ProcessData {
        override fun processDataBeforeSend(): MutableList<ByteArray> {
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
                list.add(DataForSendToPrinter.printThreeColumns("", "${it.buyCount}", "￥$price"))
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

fun numberFormat(doubleNumber: Double): String {
    return DecimalFormat("0.00").format(doubleNumber)
}