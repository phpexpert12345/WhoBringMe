package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.PaymentConfigurationMain
import com.phpexpert.bringme.dtos.PaymentTokenMain
import com.phpexpert.bringme.dtos.PostJobDataMain
import com.phpexpert.bringme.dtos.ServicesChargesDtoMain
import com.phpexpert.bringme.repositories.JobPostRepo

class JobPostModel:ViewModel() {


    private var serviceChargesData = MutableLiveData<ServicesChargesDtoMain>()
    private var paymentAuthKey = MutableLiveData<PaymentConfigurationMain>()
    private var paymentGenerateToken = MutableLiveData<PaymentTokenMain>()
    private var postJobData = MutableLiveData<PostJobDataMain>()

    fun getServiceCharges(context: Context, subAmount:String, authKey:String):LiveData<ServicesChargesDtoMain>{
        serviceChargesData = JobPostRepo().jobPostRepo(context, subAmount, authKey)
        return serviceChargesData
    }

    fun getPaymentAuthKey(context: Context, authKey:String):LiveData<PaymentConfigurationMain>{
        paymentAuthKey = JobPostRepo().getPaymentAuthKey(context, authKey)
        return paymentAuthKey
    }

    fun getPaymentGenerateToken(context: Context, mapData:Map<String, String>):LiveData<PaymentTokenMain>{
        paymentGenerateToken = JobPostRepo().getPaymentGenerateToken(context, mapData)
        return paymentGenerateToken
    }

    fun getPostJobData(context: Context, mapData:Map<String, String>):LiveData<PostJobDataMain>{
        postJobData = JobPostRepo().getPostJobData(context, mapData)
        return postJobData
    }
}

