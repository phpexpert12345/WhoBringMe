package com.phpexpert.bringme.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutNotificationCellBinding
import com.phpexpert.bringme.dtos.NotificationDtoList

class NotificationAdapter(var context: Context, var arrayList: ArrayList<NotificationDtoList>) : RecyclerView.Adapter<NotificationAdapter.NotificationFragmentViewHolder>() {

    private lateinit var notificationFragmentCellBinding: LayoutNotificationCellBinding

    inner class NotificationFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationFragmentViewHolder {
        return NotificationFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_notification_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationFragmentViewHolder, position: Int) {
        notificationFragmentCellBinding = holder.viewBinding as LayoutNotificationCellBinding
        notificationFragmentCellBinding.notificationTime.text = arrayList[position].notification_date + " " + arrayList[position].notification_time
        notificationFragmentCellBinding.orderId.text = arrayList[position].order_id
        notificationFragmentCellBinding.title.text = arrayList[position].notification_subject
        notificationFragmentCellBinding.notificationMessage.text = arrayList[position].notification_message
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}