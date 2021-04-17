package com.phpexpert.bringme.ui.delivery.earning

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutEarningCellBinding
import com.phpexpert.bringme.databinding.LayoutJobCellBinding

class EarningAdapter(var context: Context, var arrayList: ArrayList<String>) : RecyclerView.Adapter<EarningAdapter.MyJobViewModel>() {

    private lateinit var earningCellBinding: LayoutEarningCellBinding

    inner class MyJobViewModel(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobViewModel {
        return MyJobViewModel(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_earning_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyJobViewModel, position: Int) {
        earningCellBinding = holder.viewBinding as LayoutEarningCellBinding
        if (position == 1) {
            earningCellBinding.deliveryImageView.visibility = View.VISIBLE
            earningCellBinding.deliveryImageViewLayout.visibility = View.VISIBLE
        } else {
            earningCellBinding.deliveryImageView.visibility = View.GONE
            earningCellBinding.deliveryImageViewLayout.visibility = View.GONE
        }


    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}