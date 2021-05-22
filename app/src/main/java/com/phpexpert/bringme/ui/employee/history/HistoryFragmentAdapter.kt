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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutHistroyCellBinding
import com.phpexpert.bringme.dtos.EmployeeJobHistoryDtoList
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS", "DEPRECATION")
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
//        arrayList[position].job_total_amount = arrayList[position].job_total_amount.formatChange()
        historyFragmentCellBinding.model = arrayList[position]

        historyFragmentCellBinding.jobTotalAmount.text = arrayList[position].job_total_amount.formatChange()
        historyFragmentCellBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()


        when (arrayList[position].order_status_msg) {
            "Accepted" -> {
                try {
                    historyFragmentCellBinding.timeData.text = orderDateValue("${arrayList[position].job_accept_date!!} ${arrayList[position].job_accept_time}")
                    historyFragmentCellBinding.acceptedDateTimeText.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().accepted_time_text
                    historyFragmentCellBinding.writeReview.visibility = View.GONE
                    historyFragmentCellBinding.reviewView.visibility = View.GONE
                    historyFragmentCellBinding.deliveryDataLayout.visibility = View.VISIBLE
                    historyFragmentCellBinding.declineMessage.visibility = View.GONE
                    historyFragmentCellBinding.declineView.visibility = View.GONE
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            "Completed" -> {
                try {
                    historyFragmentCellBinding.timeData.text = orderDateValue("${arrayList[position].job_completed_date!!} ${arrayList[position].job_completed_time}")
                    historyFragmentCellBinding.acceptedDateTimeText.text = (context as BaseActivity).sharedPrefrenceManager.getLanguageData().complete_time_text
                    if (arrayList[position].review_status == "Not Done") {
                        historyFragmentCellBinding.reviewView.visibility = View.GONE
                        historyFragmentCellBinding.writeReview.visibility = View.VISIBLE
                    } else {
                        historyFragmentCellBinding.reviewView.visibility = View.VISIBLE
                        historyFragmentCellBinding.writeReview.visibility = View.GONE
                    }
                    historyFragmentCellBinding.deliveryDataLayout.visibility = View.VISIBLE
                    historyFragmentCellBinding.declineMessage.visibility = View.GONE
                    historyFragmentCellBinding.declineView.visibility = View.GONE
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            "Cancelled" -> {
                try {
                    if (arrayList[position].review_status == "Not Done") {
                        historyFragmentCellBinding.reviewView.visibility = View.GONE
                        historyFragmentCellBinding.writeReview.visibility = View.VISIBLE
                    } else {
                        historyFragmentCellBinding.reviewView.visibility = View.VISIBLE
                        historyFragmentCellBinding.writeReview.visibility = View.GONE
                    }
                    historyFragmentCellBinding.deliveryDataLayout.visibility = View.GONE
                    historyFragmentCellBinding.declineMessage.text = arrayList[position].order_decline_reason
                    historyFragmentCellBinding.declineMessage.visibility = View.VISIBLE
                    historyFragmentCellBinding.declineView.visibility = View.VISIBLE
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
            "Pending" -> {
                try {
                    historyFragmentCellBinding.writeReview.visibility = View.GONE
                    historyFragmentCellBinding.deliveryDataLayout.visibility = View.GONE
                    historyFragmentCellBinding.reviewView.visibility = View.GONE
                    historyFragmentCellBinding.declineMessage.visibility = View.GONE
                    historyFragmentCellBinding.declineView.visibility = View.GONE
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        Glide.with(context).asGif().load(arrayList[position].order_status_icon).placeholder(R.drawable.cs).into(historyFragmentCellBinding.csImage)

        try {
            historyFragmentCellBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
            historyFragmentCellBinding.orderStatus.setTextColor(Color.parseColor(arrayList[position].order_status_text_color_code))
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        historyFragmentCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick("viewData", position)
        }

        historyFragmentCellBinding.writeReview.setOnClickListener {
            onClickListener.onClick("writeReview", position)
        }
        var requestOptions = RequestOptions()
        requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
        Glide.with(context).load(arrayList[position].Client_photo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .apply(requestOptions)
                .placeholder(R.drawable.user_placeholder)
                .into(historyFragmentCellBinding.userImage)

        Glide.with(context)
                .load(arrayList[position].Delivery_Employee_photo)
                .placeholder(R.drawable.user_placeholder)
                .apply(requestOptions)
                .placeholder(R.drawable.user_placeholder)
                .into(historyFragmentCellBinding.deliveryImageView)

        if (arrayList[position].job_rating!! != "") {
            historyFragmentCellBinding.ratingValue.rating = arrayList[position].job_rating!!.toFloat()
//            historyFragmentCellBinding.reviewRatingBar.rating = arrayList[position].job_rating!!.toFloat()
        }
        /*historyFragmentCellBinding = holder.viewBinding as LayoutHistroyCellBinding
        historyFragmentCellBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
        historyFragmentCellBinding.model = arrayList[position]

        historyFragmentCellBinding.jobTotalAmount.text = arrayList[position].job_total_amount.formatChange()
        historyFragmentCellBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()

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

        if (arrayList[position].order_status_msg == "Accepted" || arrayList[position].order_status_msg == "Completed") {
            historyFragmentCellBinding.jobAcceptLayout.visibility = View.VISIBLE
            historyFragmentCellBinding.csImage.setImageResource(R.drawable.cs1)
            if (arrayList[position].order_status_msg == "Accepted") {
                try {
                    historyFragmentCellBinding.acceptedDateTime.text = orderDateValue(arrayList[position].job_accept_date!! + " " + arrayList[position].job_accept_time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else if (arrayList[position].order_status_msg == "Completed") {
                try {
                    historyFragmentCellBinding.acceptedDateTime.text = orderDateValue(arrayList[position].job_completed_date!! + " " + arrayList[position].job_completed_time)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
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
        }*/
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

    private fun String?.formatChange() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code, (context as BaseActivity).sharedPrefrenceManager.getAuthData()?.country_code!!))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }
}