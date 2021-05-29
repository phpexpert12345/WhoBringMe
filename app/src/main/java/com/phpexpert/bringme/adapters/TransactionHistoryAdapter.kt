package com.phpexpert.bringme.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.TransactionHistoryCellBinding
import com.phpexpert.bringme.dtos.TransactionDtoList
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TransactionHistoryAdapter(var context: Context, var arrayList: ArrayList<TransactionDtoList>) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewModel>() {
    private lateinit var transactionBinding: TransactionHistoryCellBinding

    inner class TransactionViewModel(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewModel {
        return TransactionViewModel(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.transaction_history_cell, parent, false))
    }

    override fun onBindViewHolder(holder: TransactionViewModel, position: Int) {
        transactionBinding = holder.viewBinding as TransactionHistoryCellBinding
        transactionBinding.model = arrayList[position]
        transactionBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
        transactionBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()
        transactionBinding.transactionDate.text = changeAcceptDateTime(arrayList[position].transaction_date + " " + arrayList[position].transaction_time)
        try {
            transactionBinding.transactionStatus.setTextColor(Color.parseColor(arrayList[position].transaction_status_color_code))
        } catch (e: Exception) {
        }
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeAcceptDateTime(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd HH:mm:ss"
        val outputPattern = "dd MMM yyyy h:mm aa"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String?

        try {
            date = inputFormat.parse(dateTime)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            str = dateTime
        }

        return str
    }
}