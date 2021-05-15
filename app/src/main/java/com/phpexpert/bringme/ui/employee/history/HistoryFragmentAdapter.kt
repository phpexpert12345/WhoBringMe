package com.phpexpert.bringme.ui.employee.history

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
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
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HistoryFragmentAdapter(var context: Context, var arrayList: ArrayList<EmployeeJobHistoryDtoList>, private var onClickListener: OnClickView) : RecyclerView.Adapter<HistoryFragmentAdapter.HistoryFragmentViewHolder>() {

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
        historyFragmentCellBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
        arrayList[position].job_total_amount = String.format("%.2f", arrayList[position].job_total_amount?.toFloat())
        historyFragmentCellBinding.model = arrayList[position]

        try {
            historyFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (arrayList[position].review_status == "Not Done") {
            historyFragmentCellBinding.jobReviewLayout.visibility = View.GONE
        } else {
            historyFragmentCellBinding.jobReviewLayout.visibility = View.VISIBLE
            historyFragmentCellBinding.reviewRatingBar.rating = arrayList[position].job_rating!!.toFloat()
//            historyFragmentCellBinding.jobReviewTime.text = arrayList[position]
        }

        if (arrayList[position].order_status_msg == "Accepted") {
            historyFragmentCellBinding.jobAcceptLayout.visibility = View.VISIBLE
            historyFragmentCellBinding.csImage.setImageResource(R.drawable.cs1)
            try{
                historyFragmentCellBinding.acceptedDateTime.text = orderDateValue(arrayList[position].job_accept_date!!+" "+arrayList[position].job_accept_time)
            }catch (e:Exception){
                e.printStackTrace()
            }
        } else {
            historyFragmentCellBinding.jobAcceptLayout.visibility = View.GONE
            historyFragmentCellBinding.csImage.setImageResource(R.drawable.cs)
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

    @SuppressLint("SimpleDateFormat")
    private fun orderDateValue(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd MMM yyyy h:mm a"
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

    /*@SuppressLint("SimpleDateFormat")
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
    }*/
}