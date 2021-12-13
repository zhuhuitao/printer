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
import com.huitao.printer.printerface.TaskCallback
import com.huitao.printerdemo.R
import com.huitao.printerdemo.adapter.BlueAdapter
import com.huitao.printerdemo.bean.SimpleBlueDevice
import com.huitao.printerdemo.bean.getOrderData
import com.huitao.printerdemo.databinding.ActivityPrinterBinding
import com.huitao.printerdemo.printerface.AdapterClickListener
import com.huitao.printerdemo.service.MyService
import com.huitao.utils.SpaceItemDecoration
import kotlin.time.days

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 19:00
 *desc    :
 *version :
 */
class PrinterActivity : AppCompatActivity(), AdapterClickListener {
    private var mBinder: MyService.MyBinder? = null
    private lateinit var mMyConn: MyServiceConnect
    private lateinit var mList: ArrayList<SimpleBlueDevice>
    private lateinit var mAdapter: BlueAdapter
    private lateinit var mBinding: ActivityPrinterBinding
    private var mIsConnect = false

    private inner class MyServiceConnect : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder) {
            mBinder = p1 as MyService.MyBinder
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mBinder = null
            unbindService(mMyConn)
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_printer)
        bindMyService()
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

    private fun bindMyService() {
        val intent = Intent(this, MyService::class.java)
        mMyConn = MyServiceConnect()
        bindService(intent, mMyConn, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(mMyConn)
    }

    fun obtainBondDevices(view: View) {
        if (view.id == R.id.tv_bond_device) {
            val list = mBinder?.getService()?.getBondDeviceList()
            if (mList.isNotEmpty()) mList.clear()
            list?.forEach {
                mList.add(SimpleBlueDevice(name = it, isConnected = false))
            }
            mAdapter.notifyDataSetChanged()
        } else {
            if (mIsConnect) {
                mBinder?.getService()?.writeData(getOrderData(),object :TaskCallback{
                    override fun onSucceed() {

                    }

                    override fun onFailed() {
                    }

                })
            } else {
                Toast.makeText(this, getString(R.string.please_connect_first), Toast.LENGTH_SHORT)
                    .show()
            }
        }

    }

    override fun onClickViewListener(view: View, obj: Any) {
        when (mList[obj as Int].isConnected) {
            true -> {
                mBinder?.getService()?.disconnect(object : TaskCallback {
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

                    override fun onFailed() {
                        Toast.makeText(
                            this@PrinterActivity,
                            getString(R.string.disconnect_failure),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }

            else -> {
                mBinder?.getService()?.connectByMac(mList[obj].name.split("\n").last().trim(),
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

                        override fun onFailed() {
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