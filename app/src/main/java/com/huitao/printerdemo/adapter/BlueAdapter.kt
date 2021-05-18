package com.huitao.printerdemo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.huitao.printerdemo.R
import com.huitao.printerdemo.bean.SimpleBlueDevice
import com.huitao.printerdemo.databinding.AdapterBondListBinding
import com.huitao.printerdemo.printerface.AdapterClickListener

/**
 *author  : huitao
 *e-mail  : pig.huitao@gmail.com
 *date    : 2021/5/17 19:43
 *desc    :
 *version :
 */
class BlueAdapter(list: List<SimpleBlueDevice>, context: Context) :
    RecyclerView.Adapter<BlueAdapter.MyHolder>() {
    private var mList = list
    private var mCtx = context
    private var mOnClickListener: AdapterClickListener? = null
    fun setOnClickListener(clickListener: AdapterClickListener) {
        this.mOnClickListener = clickListener
    }

    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mBinding = DataBindingUtil.bind<AdapterBondListBinding>(view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val view = LayoutInflater.from(mCtx).inflate(R.layout.adapter_bond_list, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.mBinding?.tvBlueName?.text = mList[position].name
        holder.mBinding?.tvBlueStatus?.text = when (mList[position].isConnected) {
            true -> "已连接"
            else -> "连接"
        }
        holder.mBinding?.root?.setOnClickListener {
            mOnClickListener?.onClickViewListener(it, holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }


}