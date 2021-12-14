# Printer 2.0

针对Android平台蓝牙打印相关功能封装的依赖库，支持扫描周围蓝牙设备，获取已绑定的蓝牙设备，连接蓝牙打印机，断开蓝牙打印机，发送蓝牙打印数据等功能。

## 集成使用

```groovy
repositories {
  google()
  mavenCentral()
}

dependencies {
  implementation 'com.github.zhuhuitao:printer:2.0'
}
```

### 权限问题  

在使用之前，需要打开位置权限，否则无法扫描到周围蓝牙设备，当然也需要相关蓝牙权限

###  扫描周围蓝牙设备，获取已绑定的蓝牙设备

```kotlin
//获取周围蓝牙设备，已绑定的蓝牙设备会直接通过集合返回，扫描发现的设备通过DeviceFoundCallback回调回来
val list = Printer.getInstance().getBondDevicesList(object : DeviceFoundCallback {
     override fun deviceFoundCallback(device: String) {
        //处理扫描发现的周围蓝牙设备
    }
 })
```

### 连接蓝牙打印机

```kotlin
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
```

###  断开蓝牙打印机

```kotlin
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
```

###  发送打印数据

```kotlin
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
```

### 测试结果

![](https://github.com/zhuhuitao/printer/blob/master/preview/lADPJwY7Q1dH-hTNC9DND8A_4032_3024.jpg)

