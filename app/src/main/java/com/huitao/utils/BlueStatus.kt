package com.huitao.utils

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 18:45
 *desc    :
 *version :
 */
object BlueStatus {
    //蓝牙连接状态本地广播action
    const val ACTION_CONNECT_STATUS = "action_connect_status"

    //用于intent 携带数据key
    const val CONNECT_STATUS = "connect_status"

    //蓝牙连接状态成功 value
    const val STATUS_SUCCESS = "status_success"

    //蓝牙连接状态失败 value
    const val STATUS_FAIL = "status_fail"

    //开始连接蓝牙action
    const val ACTION_CONNECT_START = "action_connect_start"

    const val ACTION_DISCONNECT = "action_disconnect"

    //已绑定的蓝牙设备
    const val BOND_DEVICE_LIST = "bond_device_list"


    const val BOND_DEVICE_LIST_RESULT = "bond_device_list_result"

    const val DISCONNECT_SUCCESS = "disconnect_success"

    const val DISCONNECT_FAILURE = "disconnect_failure"
}