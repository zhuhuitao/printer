package com.huitao.printerdemo.bean

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 21:24
 *desc    :
 *version :
 */
data class OrderBean(
    val shopName: String,
    val shopAddress: String,
    val createTime: String,
    val orderCode: String,
    val shopMobile: String,
    val receiver: String?,
    val receiverMobile: String?,
    val address: String?,
    val pickUpCode: String,
    val deliveryTypeStr: String,
    val receiveTime: String?,
    val remarks: String?,
    val riderName: String?,
    val riderMobile: String?,
    val orderTypeStr: String,
    val orderDetailList: MutableList<OrderDetailList>,
    val money: Double,
    val freight: Double,
    val payMoney: Double,
    val appointmentTime: String
)


fun getOrderData(): OrderBean {
    return OrderBean(
        shopName = "一点点奶茶",
        shopAddress = "广东省深圳市深圳北站二楼一点点奶茶店",
        createTime = "2021-05-12 10:21:33",
        orderCode = "SP1258444BAD45",
        shopMobile = "0755-5335267",
        receiver = "张小蛋",
        receiverMobile = "13525853269",
        address = "广东省深圳市南山区科技园中芯国际大厦2021",
        pickUpCode = "25",
        deliveryTypeStr = "骑手专送",
        receiveTime = "2021-05-12 11:22:46",
        remarks = "多放点辣椒，帮我放到前台，谢谢",
        riderName = "光头强",
        riderMobile = "19926587561",
        orderTypeStr = "预约订单",
        money = 20.00,
        freight = 0.00,
        payMoney = 15.00,
        appointmentTime = "2021-05-12 20:52:20",
        orderDetailList = arrayListOf(
            OrderDetailList(
                isDiscount = 1,
                amount = 2.00,
                shopPrice = 3.00,
                goodsName = "波霸奶茶（大杯）",
                buyCount = 2,
            ), OrderDetailList(
                isDiscount = 1,
                amount = 2.00,
                shopPrice = 3.00,
                goodsName = "手抓饼（加辣）",
                buyCount = 2,
            ), OrderDetailList(
                isDiscount = 1,
                amount = 2.00,
                shopPrice = 3.00,
                goodsName = "珍珠奶茶（小杯0杯）",
                buyCount = 2,
            )
        )
    )
}
