package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.EarningDtoMain
import com.phpexpert.bringme.dtos.TransactionDtoMain
import com.phpexpert.bringme.dtos.WithdrawAmountDtoMain
import com.phpexpert.bringme.retro.MyEarningRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarningRepo {
    private var latestJobData = MutableLiveData<EarningDtoMain>()
    private var transactionHistory = MutableLiveData<TransactionDtoMain>()
    private var withdrawAmountData = MutableLiveData<WithdrawAmountDtoMain>()

    fun getEarningData(map: Map<String, String>): MutableLiveData<EarningDtoMain> {
        ServiceGenerator.createService(MyEarningRetro::class.java).getEarningData(map).enqueue(object : Callback<EarningDtoMain> {
            override fun onResponse(call: Call<EarningDtoMain>, response: Response<EarningDtoMain>) {
                if (response.isSuccessful) {
                    latestJobData.postValue(response.body())
                } else {
                    val latestJobDtoMain = EarningDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Latest job history api error"
                    latestJobData.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<EarningDtoMain>, t: Throwable) {
                val latestJobDtoMain = EarningDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Latest job history api error"
                latestJobData.postValue(latestJobDtoMain)
            }

        })
        return latestJobData
    }

    fun getTransactionData(map: Map<String, String>): MutableLiveData<TransactionDtoMain> {
        ServiceGenerator.createService(MyEarningRetro::class.java).getTransactionHistoryData(map).enqueue(object : Callback<TransactionDtoMain> {
            override fun onResponse(call: Call<TransactionDtoMain>, response: Response<TransactionDtoMain>) {
                if (response.isSuccessful) {
                    transactionHistory.postValue(response.body())
                } else {
                    val latestJobDtoMain = TransactionDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Latest job history api error"
                    transactionHistory.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<TransactionDtoMain>, t: Throwable) {
                val latestJobDtoMain = TransactionDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Latest job history api error"
                transactionHistory.postValue(latestJobDtoMain)
            }

        })
        return transactionHistory
    }

    fun withdrawAmountRequest(map: Map<String, String>): MutableLiveData<WithdrawAmountDtoMain> {
        ServiceGenerator.createService(MyEarningRetro::class.java).withDrawAmountRequest(map).enqueue(object : Callback<WithdrawAmountDtoMain> {
            override fun onResponse(call: Call<WithdrawAmountDtoMain>, response: Response<WithdrawAmountDtoMain>) {
                if (response.isSuccessful) {
                    withdrawAmountData.postValue(response.body())
                } else {
                    val latestJobDtoMain = WithdrawAmountDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Latest job history api error"
                    withdrawAmountData.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<WithdrawAmountDtoMain>, t: Throwable) {
                val latestJobDtoMain = WithdrawAmountDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Latest job history api error"
                withdrawAmountData.postValue(latestJobDtoMain)
            }

        })
        return withdrawAmountData
    }
}