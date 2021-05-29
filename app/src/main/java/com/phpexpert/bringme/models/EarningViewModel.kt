package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.EarningDtoMain
import com.phpexpert.bringme.dtos.LatestJobDtoMain
import com.phpexpert.bringme.dtos.TransactionDtoMain
import com.phpexpert.bringme.dtos.WithdrawAmountDtoMain
import com.phpexpert.bringme.repositories.EarningRepo
import com.phpexpert.bringme.repositories.JobHistoryRepo

class EarningViewModel : ViewModel() {
    private var latestJobData = MutableLiveData<EarningDtoMain>()
    private var transactionHistory = MutableLiveData<TransactionDtoMain>()
    private var withdrawAmountData = MutableLiveData<WithdrawAmountDtoMain>()

    fun getLatestJobData(map: Map<String, String>): LiveData<EarningDtoMain> {
        latestJobData = EarningRepo().getEarningData(map)
        return latestJobData
    }

    fun getTransactionData(map: Map<String, String>): LiveData<TransactionDtoMain> {
        transactionHistory = EarningRepo().getTransactionData(map)
        return transactionHistory
    }

    fun withdrawAmountData(map: Map<String, String>): LiveData<WithdrawAmountDtoMain> {
        withdrawAmountData = EarningRepo().withdrawAmountRequest(map)
        return withdrawAmountData
    }

}