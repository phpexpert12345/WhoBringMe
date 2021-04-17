package com.phpexpert.bringme.ui.employee.home

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.HomeFragmentCellBinding
import com.phpexpert.bringme.ui.employee.history.HistoryFragmentAdapter

class HomeFragmentAdapter(var context: Context, var arrayList: ArrayList<String>, var onClickListener: OnClickView) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {

    private lateinit var homeFragmentCellBinding: HomeFragmentCellBinding

    inner class HomeFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)
    interface OnClickView{
        fun onClick(textInput:String)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        return HomeFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.home_fragment_cell, parent, false))
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        homeFragmentCellBinding = holder.viewBinding as HomeFragmentCellBinding
        homeFragmentCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick("viewData")
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}