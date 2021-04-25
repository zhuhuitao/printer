package com.huitao.printer.utils

import java.lang.StringBuilder
import java.nio.charset.Charset

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/4/15 9:23
 *desc    :
 *version :
 */
object DataForSendToPrinter {
    private const val horizontal_length = 32
    private const val left_length = 18
    private const val right_length = 14

    ///水平定位
    fun horizontalPositioning(): ByteArray {
        return byteArrayOfInts(0x09)
    }

    ///打印并换行
    fun printAndFeedLine(): ByteArray {
        return byteArrayOfInts(0x0A)
    }

    ///打印并换行
    fun printAddPaperWalking(n: Int = 0x01): ByteArray {
        return byteArrayOfInts(0x1B, 0x4A, n)
    }

    ///设置字符右间距
    fun setCharRightSpace(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x20, n)
    }

    ///选择打印模式
    fun selectPrintModel(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x21, n)
    }

    ///设置绝对打印位置
    fun setAbsolutePrintPosition(m: Int, n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x24, m, n)
    }

    ///选择/取消用户自定义字符
    fun selectOrCancelCustomerChar(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x25, n)
    }

    ///定义用户自定义字符
    fun defineUserDefinedCharacters(c1: Int, c2: Int, b: ByteArray): ByteArray {
        return byteMerge(byteArrayOf(0x1B.toByte(), 0x26.toByte(), c1.toByte(), c2.toByte()), b)
    }

    //选择位图模式
    fun selectBtmModel(m: Int, nL: Int, nH: Int, b: ByteArray): ByteArray {
        return byteMerge(byteArrayOfInts(0x1B, 0x2A, m, nL, nH), b)
    }

    ///选择取消下划线模式
    fun selectOrCancelUnderlineModel(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x2D, n)
    }

    ///设置默认行间距
    fun setDefaultLineSpace(): ByteArray {
        return byteArrayOfInts(0x1B, 0x32)
    }

    ///设置行间距
    fun setLineSpace(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x33, n)
    }

    ///取消用户自定义字符
    fun cancelUserDefineCharsets(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x3F, n)
    }

    ///初始化打印机
    fun initializePrinter(): ByteArray {
        return byteArrayOfInts(0x1B, 0x40)
    }

    ///设置横向跳格位置
    fun setHorizontalMovementPosition(b: ByteArray): ByteArray {
        var data = byteArrayOfInts(0x1B, 0x44)
        val nul = ByteArray(1)
        data = byteMerge(data, b)
        data = byteMerge(data, nul)
        return data
    }

    ///选择取消加粗模式
    fun selectOrCancelBoldModel(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x45, n)
    }

    ///选择/取消双重打印模式
    fun selectOrCancelDoublePrinterModel(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x47, n)
    }


    ///打印并走纸
    fun printAndFeed(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x4A, n)
    }

    ///选择字体
    fun selectFont(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x4D, n)
    }

    ///选择国际字符集
    fun selectInternationalCharsetSets(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x52, n)
    }

    ///选择/取消顺时针旋转90
    fun selectOrCancelCW90(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x56, n)
    }

    ///设置相对横向打印位置
    fun setRelativeHorizontalPrintPosition(nL: Int, nH: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x5C, nL, nH)
    }

    ///选择对齐方式
    fun selectAliment(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x61, n)
    }

    ///允许/禁止按键
    fun allowOrForbidPressButton(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x63, 0x35, n)
    }

    ///打印并向前走纸n行
    fun printAndFeedForward(n: Int): ByteArray {
        return byteArrayOfInts(0x1B, 0x64, n)
    }

    //设置字体大小
    fun selectFontSize(n: Int): ByteArray {
        return byteArrayOfInts(0x1D, 0x21, n)
    }


    fun printBothColumns(str1: String, str2: String): ByteArray {
        val byte1Length: Int = getByteLength(str1)
        val byte2Length = getByteLength(str2)
        val spaceLength = horizontal_length - byte1Length - byte2Length
        val sb = StringBuilder()
        sb.append(str1)
        for (i in 0 until spaceLength) {
            sb.append(" ")
        }
        sb.append(str2)
        return strToBytes(sb.toString())!!
    }

    fun printThreeColumns(str1: String, str2: String, str3: String): ByteArray {
        val sb = StringBuilder()
        //由于在打印的时候商品名称大度为一行，因此不做左边长度处理
        sb.append(str1)
        val leftSpaceLength = left_length - getByteLength(str1) - getByteLength(str2) / 2
        for (i in 0 until leftSpaceLength) {
            sb.append(" ")
        }
        sb.append(str2)
        val rightSpaceLength = right_length - getByteLength(str3) - getByteLength(str2) / 2
        for (i in 0 until rightSpaceLength) {
            sb.append(" ")
        }
        sb.append(str3)
        return strToBytes(sb.toString())!!
    }


}