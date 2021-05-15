package com.phpexpert.bringme.ui.delivery.myjob

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutJobCellBinding
import com.phpexpert.bringme.dtos.MyJobDtoList
import com.phpexpert.bringme.utilities.BaseActivity
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MyJobAdapter(var context: Context, var arrayList: ArrayList<MyJobDtoList>, private var onClickListener: OnClickView) : RecyclerView.Adapter<MyJobAdapter.MyJobViewModel>() {

    private lateinit var jobCellBinding: LayoutJobCellBinding

    inner class MyJobViewModel(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    interface OnClickView {
        fun onClick(textInput: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobViewModel {
        return MyJobViewModel(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_job_cell, parent, false))
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyJobViewModel, position: Int) {
        jobCellBinding = holder.viewBinding as LayoutJobCellBinding
        jobCellBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
        arrayList[position].job_sub_total = String.format("%.2f", arrayList[position].job_sub_total?.toFloat())
        arrayList[position].Charge_for_Jobs = String.format("%.2f", arrayList[position].Charge_for_Jobs?.toFloat())
        arrayList[position].job_total_amount = String.format("%.2f", arrayList[position].job_total_amount?.toFloat())
        jobCellBinding.model = arrayList[position]
        Glide.with(context).load(arrayList[position].Client_photo).circleCrop().placeholder(R.drawable.user_placeholder).into(jobCellBinding.userImage)

        jobCellBinding.jobPostedDate.text = orderDateValue(arrayList[position].job_post_date!!)
        jobCellBinding.jobPostedTime.text = jobPostedTime(arrayList[position].job_posted_time!!)

        try {
            jobCellBinding.completeCancelStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
        } catch (e: Exception) {
            e.printStackTrace()
        }

        jobCellBinding.completeCancelText.text = if (arrayList[position].order_status_msg == "Accepted") {
            jobCellBinding.jobAcceptCancelTime.text = changeAcceptDateTime(arrayList[position].job_accept_date + " " + arrayList[position].job_accept_time)
            (context as BaseActivity).sharedPrefrenceManager.getLanguageData().accepted_time
        } else {
            jobCellBinding.jobAcceptCancelTime.text = changeAcceptDateTime(arrayList[position].job_completed_date + " " + arrayList[position].job_completed_time)
            (context as BaseActivity).sharedPrefrenceManager.getLanguageData().complete_time
        }

        try{
            jobCellBinding.ratingData.rating = arrayList[position].job_rating!!.toFloat()
        }catch (e:Exception){
            e.printStackTrace()
        }

        jobCellBinding.viewData.setOnClickListener {
            onClickListener.onClick("viewData", position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeAcceptDateTime(dateTime: String): String {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd MMM yyyy, EEEE, h:mm a"
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

        return str!!
    }

    @SuppressLint("SimpleDateFormat")
    private fun orderDateValue(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd"
        val outputPattern = "dd MMM yyyy"
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

    @SuppressLint("SimpleDateFormat")
    private fun jobPostedTime(dateTime: String): String? {
        val inputPattern = "hh:mm"
        val outputPattern = "hh:mm aa"
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