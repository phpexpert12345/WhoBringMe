package com.phpexpert.bringme.ui.employee.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.HomeFragmentCellBinding
import com.phpexpert.bringme.dtos.OrderListData
import com.phpexpert.bringme.utilities.BaseActivity
import java.lang.Exception
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragmentAdapter(var context: Context, var arrayList: ArrayList<OrderListData>, private var onClickListener: OnClickView) : RecyclerView.Adapter<HomeFragmentAdapter.HomeFragmentViewHolder>() {

    private lateinit var homeFragmentCellBinding: HomeFragmentCellBinding

    inner class HomeFragmentViewHolder(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)
    interface OnClickView {
        fun onClick(textInput: String, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeFragmentViewHolder {
        return HomeFragmentViewHolder(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.home_fragment_cell, parent, false))
    }

    override fun onBindViewHolder(holder: HomeFragmentViewHolder, position: Int) {
        homeFragmentCellBinding = holder.viewBinding as HomeFragmentCellBinding
        homeFragmentCellBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
//        arrayList[position].job_total_amount = arrayList[position].job_total_amount.formatChange()
        homeFragmentCellBinding.model = arrayList[position]

        homeFragmentCellBinding.jobTotalAmount.text = arrayList[position].job_total_amount.formatChange()
        homeFragmentCellBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()

        if (arrayList[position].order_status_msg == "Accepted" || arrayList[position].order_status_msg == "Completed") {
            homeFragmentCellBinding.csImage.setImageResource(R.drawable.cs1)
            try {
                homeFragmentCellBinding.timeData.text = orderDateValue("${arrayList[position].job_accept_date!!} ${arrayList[position].job_accept_time}")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            homeFragmentCellBinding.csImage.setImageResource(R.drawable.cs)
        }

        try {
            homeFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        homeFragmentCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick("viewData", position)
        }

        homeFragmentCellBinding.writeReview.setOnClickListener {
            onClickListener.onClick("writeReview", position)
        }
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(context).load(arrayList[position].Client_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .into(homeFragmentCellBinding.userImage)

        Glide.with(context)
                .load(arrayList[position].Delivery_Employee_photo)
                .placeholder(R.drawable.user_placeholder)
                .apply(requestOptions)
                .into(homeFragmentCellBinding.deliveryImageView)

        if (arrayList[position].job_rating!! != "") {
            homeFragmentCellBinding.ratingValue.rating = arrayList[position].job_rating!!.toFloat()
        }

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun orderDateValue(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd hh:mm:ss"
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

    private fun String?.formatChange() = run {
        try {
            val formatter = NumberFormat.getInstance(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
            formatter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }
}