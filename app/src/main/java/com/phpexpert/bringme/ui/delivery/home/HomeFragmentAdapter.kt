package com.phpexpert.bringme.ui.delivery.home

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.DeliveryHomeCellBinding

@Suppress("DEPRECATION")
class HomeFragmentAdapter(var context: Context, private var arrayList: ArrayList<String>, private var onClickListener: OnClickView) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {

    private lateinit var homeFragmentCellBinding: DeliveryHomeCellBinding

    inner class HomeFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)
    interface OnClickView {
        fun onClick(textInput: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        return HomeFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.delivery_home_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        homeFragmentCellBinding = holder.viewBinding as DeliveryHomeCellBinding

        if (position == 2) {
            homeFragmentCellBinding.acceptViewLayout.text = "View"
            homeFragmentCellBinding.declineFinishedLayout.text = "Finished"
            homeFragmentCellBinding.declineFinishedLayout.setBackgroundColor(context.resources.getColor(R.color.colorLoginButton))
        }
        homeFragmentCellBinding.acceptViewLayout.setOnClickListener {
            if (position == 2) {
                onClickListener.onClick("viewData")
            } else {
                onClickListener.onClick("acceptData")
            }
        }

        homeFragmentCellBinding.declineFinishedLayout.setOnClickListener {
            if (position == 2) {
                onClickListener.onClick("finishedJob")
            } else {
                onClickListener.onClick("declineJob")
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}