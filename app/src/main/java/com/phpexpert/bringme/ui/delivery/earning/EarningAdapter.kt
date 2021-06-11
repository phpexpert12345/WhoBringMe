@file:Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package com.phpexpert.bringme.ui.delivery.earning

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutEarningCellBinding
import com.phpexpert.bringme.dtos.EarningDtoList
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList

class EarningAdapter(var context: Context, var arrayList: ArrayList<EarningDtoList>, private var onClickListener: OnClickView) : RecyclerView.Adapter<EarningAdapter.MyJobViewModel>() {

    private lateinit var earningCellBinding: LayoutEarningCellBinding

    inner class MyJobViewModel(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyJobViewModel {
        return MyJobViewModel(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.layout_earning_cell, parent, false))
    }

    interface OnClickView {
        fun onClick(textInput: String, position: Int)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyJobViewModel, position: Int) {
        earningCellBinding = holder.viewBinding as LayoutEarningCellBinding
        earningCellBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
        earningCellBinding.model = arrayList[position]
        earningCellBinding.jobTotalAmount.text = arrayList[position].job_total_amount.formatChange()
        earningCellBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()
        Glide.with(context).asGif().load(arrayList[position].order_status_icon).placeholder(R.drawable.cs).into(earningCellBinding.csImage)
        earningCellBinding.statusText.setTextColor(Color.parseColor(arrayList[position].order_status_color_code))
        earningCellBinding.viewJobImageView.setOnClickListener {
            onClickListener.onClick("viewJob", position)
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    private fun String?.formatChange() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale((context as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale((context as BaseActivity).sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage), (context as BaseActivity).sharedPrefrenceManager.getAuthData()?.country_code!!))
            val formartter = (DecimalFormat("##.00", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }
}