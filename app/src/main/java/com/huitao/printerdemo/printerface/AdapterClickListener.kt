package com.huitao.printerdemo.printerface

import android.view.View

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 20:39
 *desc    :
 *version :
 */
interface AdapterClickListener {
    fun onClickViewListener(view: View, obj: Any)
}