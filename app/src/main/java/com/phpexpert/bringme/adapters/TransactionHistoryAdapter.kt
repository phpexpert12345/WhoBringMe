package com.phpexpert.bringme.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.TransactionHistoryCellBinding
import com.phpexpert.bringme.utilities.BaseActivity

class TransactionHistoryAdapter(var context: Context, var arrayList: ArrayList<String>) : RecyclerView.Adapter<TransactionHistoryAdapter.TransactionViewModel>() {
    private lateinit var transactionBinding: TransactionHistoryCellBinding

    inner class TransactionViewModel(var viewBinding: ViewDataBinding) : RecyclerView.ViewHolder(viewBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewModel {
        return TransactionViewModel(DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.transaction_history_cell, parent, false))
    }

    override fun onBindViewHolder(holder: TransactionViewModel, position: Int) {
        transactionBinding = holder.viewBinding as TransactionHistoryCellBinding
        transactionBinding.languageModel = (context as BaseActivity).sharedPrefrenceManager.getLanguageData()
        transactionBinding.currencyCode.text=(context as BaseActivity).getCurrencySymbol()
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}