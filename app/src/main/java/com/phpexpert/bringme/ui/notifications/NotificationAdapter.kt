package com.phpexpert.bringme.ui.notifications

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.HomeFragmentCellBinding

class NotificationAdapter(var context: Context, var arrayList: ArrayList<String>) : RecyclerView.Adapter<NotificationAdapter.NotificationFragmentViewHolder>() {

    private lateinit var notificationFragmentCellBinding: HomeFragmentCellBinding

    inner class NotificationFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationFragmentViewHolder {
        return NotificationFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_notification_cell, parent, false))
    }

    override fun onBindViewHolder(holder: NotificationFragmentViewHolder, position: Int) {
        notificationFragmentCellBinding = holder.viewBinding as HomeFragmentCellBinding
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}