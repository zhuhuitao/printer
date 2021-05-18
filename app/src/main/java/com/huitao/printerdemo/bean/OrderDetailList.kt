package com.huitao.printerdemo.bean

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/18 9:25
 *desc    :
 *version :
 */
data class OrderDetailList(
    val isDiscount: Int,
    val amount: Double,
    val shopPrice: Double,
    val goodsName: String,
    val buyCount: Int,
)
