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
import com.phpexpert.bringme.dtos.LatestJobDeliveryDataList

@Suppress("DEPRECATION")
class HomeFragmentAdapter(var context: Context, private var arrayList: ArrayList<LatestJobDeliveryDataList>, private var onClickListener: OnClickView) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {

    private lateinit var homeFragmentCellBinding: DeliveryHomeCellBinding

    inner class HomeFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)
    interface OnClickView {
        fun onClick(textInput: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        return HomeFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.delivery_home_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        homeFragmentCellBinding = holder.viewBinding as DeliveryHomeCellBinding
        homeFragmentCellBinding.model = arrayList[position]

        if (arrayList[position].job_accept_time != null) {
            homeFragmentCellBinding.acceptViewLayout.text = "View"
            homeFragmentCellBinding.declineFinishedLayout.text = "Finished"
            homeFragmentCellBinding.declineFinishedLayout.setBackgroundColor(context.resources.getColor(R.color.colorLoginButton))
        } else {
            homeFragmentCellBinding.acceptViewLayout.text = "Accept"
            homeFragmentCellBinding.declineFinishedLayout.text = "Decline"
            homeFragmentCellBinding.declineFinishedLayout.setBackgroundColor(context.resources.getColor(R.color.red))
        }

        homeFragmentCellBinding.acceptViewLayout.setOnClickListener {
            if (homeFragmentCellBinding.acceptViewLayout.text.toString() == "View") {
                onClickListener.onClick("viewData", position)
            } else {
                onClickListener.onClick("acceptData", position)
            }
        }

        homeFragmentCellBinding.declineFinishedLayout.setOnClickListener {
            if (homeFragmentCellBinding.declineFinishedLayout.text.toString() == "Finished") {
                onClickListener.onClick("finishedJob", position)
            } else {
                onClickListener.onClick("declineJob", position)
            }
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}