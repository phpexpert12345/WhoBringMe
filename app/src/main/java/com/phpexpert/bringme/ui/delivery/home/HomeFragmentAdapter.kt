package com.phpexpert.bringme.ui.delivery.home

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.DeliveryHomeCellBinding
import com.phpexpert.bringme.dtos.LatestJobDeliveryDataList
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
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
        homeFragmentCellBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()
        homeFragmentCellBinding.jobTotalAmount.text = arrayList[position].job_total_amount.formatChange()

        Glide.with(context).load(arrayList[position].Client_photo).circleCrop().placeholder(R.drawable.user_placeholder).into(homeFragmentCellBinding.userImage)

        homeFragmentCellBinding.clientCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:" + arrayList[position].Client_phone_code + arrayList[position].Client_phone))
            context.startActivity(intent)
        }
        try {
            homeFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
            homeFragmentCellBinding.orderStatus.setTextColor(Color.parseColor(arrayList[position].order_status_text_color_code))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            homeFragmentCellBinding.orderDateValue.text = orderDateValue(arrayList[position].job_post_date!!)
            homeFragmentCellBinding.jobPostedTime.text = jobPostedTime(arrayList[position].job_posted_time!!)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        when (arrayList[position].order_status_msg) {
            "Accepted" -> {
                try {
                    homeFragmentCellBinding.acceptedDateTime.text = changeAcceptDateTime(arrayList[position].job_accept_date + " " + arrayList[position].job_accept_time)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                (holder.viewBinding as DeliveryHomeCellBinding).acceptViewLayout.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().view
                (holder.viewBinding as DeliveryHomeCellBinding).declineFinishedLayout.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().finished
                homeFragmentCellBinding.declineFinishedLayout.setBackgroundResource(R.drawable.button_blue_green)
            }
            "Completed" -> {
                try {
                    homeFragmentCellBinding.acceptedDateTime.text = changeAcceptDateTime(arrayList[position].job_accept_date + " " + arrayList[position].job_accept_time)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                (holder.viewBinding as DeliveryHomeCellBinding).acceptViewLayout.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().view
                (holder.viewBinding as DeliveryHomeCellBinding).declineFinishedLayout.visibility = View.GONE
                homeFragmentCellBinding.declineFinishedLayout.setBackgroundResource(R.drawable.button_blue_green)
            }
            "Decline" -> {
                homeFragmentCellBinding.declineFinishedLayout.visibility = View.GONE
                homeFragmentCellBinding.acceptedDateTime.visibility = View.GONE
                homeFragmentCellBinding.acceptViewLayout.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().view
            }
            else -> {
                homeFragmentCellBinding.acceptedDateTime.visibility = View.GONE
                (holder.viewBinding as DeliveryHomeCellBinding).acceptViewLayout.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().accept
                (holder.viewBinding as DeliveryHomeCellBinding).declineFinishedLayout.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().decline
                homeFragmentCellBinding.declineFinishedLayout.setBackgroundResource(R.drawable.button_rectangle_red)
            }
        }

        Glide.with(context).asGif().load(arrayList[position].order_status_icon).placeholder(R.drawable.cs).into(homeFragmentCellBinding.csImage)

        homeFragmentCellBinding.acceptViewLayout.setOnClickListener {
            if ((holder.viewBinding as DeliveryHomeCellBinding).acceptViewLayout.text.toString() == (context as BaseActivity).sharedPrefrenceManager.getLanguageData().view) {
                onClickListener.onClick("viewData", position)
            } else {
                onClickListener.onClick("acceptData", position)
            }
        }

        homeFragmentCellBinding.declineFinishedLayout.setOnClickListener {
            if ((holder.viewBinding as DeliveryHomeCellBinding).declineFinishedLayout.text.toString() == (context as BaseActivity).sharedPrefrenceManager.getLanguageData().finished) {
                onClickListener.onClick("finishedJob", position)
            } else {
                onClickListener.onClick("declineJob", position)
            }
        }
        (holder.viewBinding as DeliveryHomeCellBinding).languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeAcceptDateTime(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd MMM yyyy EEEE h:mm aa"
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

    private fun String?.formatChange() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code, "DE"))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }
}
