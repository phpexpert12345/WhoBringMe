package com.phpexpert.bringme.ui.employee.history

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutHistroyCellBinding

class HistoryFragmentAdapter(var context: Context, var arrayList: ArrayList<String>, var onClickListener:OnClickView) : RecyclerView.Adapter<HistoryFragmentAdapter.HistoryFragmentViewHolder>() {

    private lateinit var historyFragmentCellBinding: LayoutHistroyCellBinding

    interface OnClickView{
        fun onClick()
    }

    inner class HistoryFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryFragmentViewHolder {
        return HistoryFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_histroy_cell, parent, false))
    }

    override fun onBindViewHolder(holder: HistoryFragmentViewHolder, position: Int) {
        historyFragmentCellBinding = holder.viewBinding as LayoutHistroyCellBinding
        historyFragmentCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick()
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}