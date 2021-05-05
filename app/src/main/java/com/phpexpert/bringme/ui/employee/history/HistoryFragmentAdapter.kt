package com.phpexpert.bringme.ui.employee.history

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutHistroyCellBinding
import com.phpexpert.bringme.dtos.EmployeeJobHistoryDtoList
import java.text.ParseException
import java.util.*
import kotlin.collections.ArrayList

class HistoryFragmentAdapter(var context: Context, var arrayList: ArrayList<EmployeeJobHistoryDtoList>, var onClickListener: OnClickView) : RecyclerView.Adapter<HistoryFragmentAdapter.HistoryFragmentViewHolder>() {

    private lateinit var historyFragmentCellBinding: LayoutHistroyCellBinding

    interface OnClickView {
        fun onClick(textInput: String, position: Int)
    }

    inner class HistoryFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryFragmentViewHolder {
        return HistoryFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_histroy_cell, parent, false))
    }

    override fun onBindViewHolder(holder: HistoryFragmentViewHolder, position: Int) {
        historyFragmentCellBinding = holder.viewBinding as LayoutHistroyCellBinding
        historyFragmentCellBinding.model = arrayList[position]

        historyFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))

        if (arrayList[position].review_status == "Not Done") {
            historyFragmentCellBinding.jobReviewLayout.visibility = View.GONE
        } else {
            historyFragmentCellBinding.jobReviewLayout.visibility = View.VISIBLE
            historyFragmentCellBinding.reviewRatingBar.rating = arrayList[position].job_rating!!.toFloat()
        }

        if (arrayList[position].order_status_msg == "Accepted") {
            historyFragmentCellBinding.jobAcceptLayout.visibility = View.VISIBLE
        } else {
            historyFragmentCellBinding.jobAcceptLayout.visibility = View.GONE
        }
        Glide.with(context).load(arrayList[position].Client_photo).circleCrop().placeholder(R.drawable.user_placeholder).into(historyFragmentCellBinding.userImage)
        Glide.with(context).load(arrayList[position].Delivery_Employee_photo).circleCrop().placeholder(R.drawable.user_placeholder).into(historyFragmentCellBinding.deliveryImageView)

        historyFragmentCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick("viewData", position)
        }

        historyFragmentCellBinding.writeReview.setOnClickListener {
            onClickListener.onClick("writeReview", position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private fun changeReviewTime(dateInput: String) {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd-MMM-yyyy h:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        var date: Date? = null
        var str: String? = null

        try {
            date = inputFormat.parse(dateInput)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }
}