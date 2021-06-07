package com.phpexpert.bringme.ui.employee.home

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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.HomeFragmentCellBinding
import com.phpexpert.bringme.dtos.OrderListData
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
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
/*
        if (arrayList[position].order_decline_reason == "") {
            homeFragmentCellBinding.declineMessage.visibility = View.GONE
            homeFragmentCellBinding.declineView.visibility = View.GONE
        } else {
            homeFragmentCellBinding.declineMessage.visibility = View.VISIBLE
            homeFragmentCellBinding.declineView.visibility = View.VISIBLE
            homeFragmentCellBinding.declineMessage.text = arrayList[position].order_decline_reason
            arrayList[position].order_status_msg = "Declined"
        }*/
        when (arrayList[position].job_status) {
            "1" -> {
                try {
                    homeFragmentCellBinding.timeData.text = orderDateValue("${arrayList[position].job_accept_date!!} ${arrayList[position].job_accept_time}")
                    homeFragmentCellBinding.acceptedDateTimeText.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().accepted_time_text
                    homeFragmentCellBinding.reviewView.visibility = View.GONE
                    homeFragmentCellBinding.writeReview.visibility = View.GONE
                    homeFragmentCellBinding.deliveryDataLayout.visibility = View.VISIBLE
                    homeFragmentCellBinding.declineMessage.visibility = View.GONE
                    homeFragmentCellBinding.declineView.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "3" -> {
                try {
                    homeFragmentCellBinding.timeData.text = orderDateValue("${arrayList[position].job_completed_date!!} ${arrayList[position].job_completed_time}")
                    homeFragmentCellBinding.acceptedDateTimeText.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().complete_time_text
                    if (arrayList[position].review_status == "Not Done") {
                        homeFragmentCellBinding.reviewView.visibility = View.GONE
                        homeFragmentCellBinding.writeReview.visibility = View.VISIBLE
                    } else {
                        homeFragmentCellBinding.reviewView.visibility = View.VISIBLE
                        homeFragmentCellBinding.writeReview.visibility = View.GONE
                    }
                    homeFragmentCellBinding.deliveryDataLayout.visibility = View.VISIBLE
                    homeFragmentCellBinding.declineMessage.visibility = View.GONE
                    homeFragmentCellBinding.declineView.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "4" -> {
                try {
                    if (arrayList[position].review_status == "Not Done") {
                        homeFragmentCellBinding.reviewView.visibility = View.GONE
                        homeFragmentCellBinding.writeReview.visibility = View.VISIBLE
                    } else {
                        homeFragmentCellBinding.reviewView.visibility = View.VISIBLE
                        homeFragmentCellBinding.writeReview.visibility = View.GONE
                    }
                    homeFragmentCellBinding.deliveryDataLayout.visibility = View.GONE
                    homeFragmentCellBinding.declineMessage.visibility = View.VISIBLE
                    homeFragmentCellBinding.declineView.visibility = View.VISIBLE
                    homeFragmentCellBinding.declineMessage.text = arrayList[position].order_decline_reason

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            "0" -> {
                try {
                    homeFragmentCellBinding.writeReview.visibility = View.GONE
                    homeFragmentCellBinding.deliveryDataLayout.visibility = View.GONE
                    homeFragmentCellBinding.reviewView.visibility = View.GONE
                    homeFragmentCellBinding.declineMessage.visibility = View.GONE
                    homeFragmentCellBinding.declineView.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        Glide.with(context).asGif().load(arrayList[position].order_status_icon).placeholder(R.drawable.cs).into(homeFragmentCellBinding.csImage)

        try {
            homeFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
            homeFragmentCellBinding.orderStatus.setTextColor(Color.parseColor(arrayList[position].order_status_text_color_code))
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
                .placeholder(R.drawable.user_placeholder)
                .into(homeFragmentCellBinding.userImage)

        Glide.with(context)
                .load(arrayList[position].Delivery_Employee_photo)
                .placeholder(R.drawable.user_placeholder)
                .apply(requestOptions)
                .placeholder(R.drawable.user_placeholder)
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
//            val formatter = NumberFormat.getInstance(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale((context as BaseActivity).sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage), (context as BaseActivity).sharedPrefrenceManager.getAuthData()?.country_code!!))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }
}