@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

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
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NotificationAdapter(var context: Context, var arrayList: ArrayList<NotificationDtoList>) : RecyclerView.Adapter<NotificationAdapter.NotificationFragmentViewHolder>() {

    private lateinit var notificationFragmentCellBinding: LayoutNotificationCellBinding

    inner class NotificationFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationFragmentViewHolder {
        return NotificationFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_notification_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NotificationFragmentViewHolder, position: Int) {
        notificationFragmentCellBinding = holder.viewBinding as LayoutNotificationCellBinding
        notificationFragmentCellBinding.jobTime.text = orderDateValue(arrayList[position].notification_date + " " + arrayList[position].notification_time)
        notificationFragmentCellBinding.jobId.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().order + arrayList[position].order_id
        notificationFragmentCellBinding.title.text = arrayList[position].notification_subject
        notificationFragmentCellBinding.message.text = arrayList[position].notification_message
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun orderDateValue(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd hh:mm"
        val outputPattern = "dd MMM yyyy hh:mm aa"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String? = null

        try {
            date = inputFormat.parse(dateTime)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str
    }
}