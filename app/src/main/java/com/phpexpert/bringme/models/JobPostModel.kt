package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.*
import com.phpexpert.bringme.repositories.JobPostRepo

class JobPostModel : ViewModel() {


    private var serviceChargesData = MutableLiveData<ServicesChargesDtoMain>()
    private var paymentAuthKey = MutableLiveData<PaymentConfigurationMain>()
    private var paymentGenerateToken = MutableLiveData<PaymentTokenMain>()
    private var postJobData = MutableLiveData<PostJobDataMain>()
    private var jobDetailsData = MutableLiveData<JobDetailsDtoMain>()
    private var cancelJobData = MutableLiveData<CancelJobDtoMain>()
    private var updateJobData = MutableLiveData<UpdateJobDtoMain>()




    fun getServiceCharges(subAmount: String, authKey: String): LiveData<ServicesChargesDtoMain> {
        serviceChargesData = JobPostRepo().jobPostRepo(subAmount, authKey)
        return serviceChargesData
    }

    fun getPaymentAuthKey(authKey: String): LiveData<PaymentConfigurationMain> {
        paymentAuthKey = JobPostRepo().getPaymentAuthKey(authKey)
        return paymentAuthKey
    }

    fun getPaymentGenerateToken(mapData: Map<String, String>): LiveData<PaymentTokenMain> {
        paymentGenerateToken = JobPostRepo().getPaymentGenerateToken(mapData)
        return paymentGenerateToken
    }

    fun getPostJobData(mapData: Map<String, String>): LiveData<PostJobDataMain> {
        postJobData = JobPostRepo().getPostJobData(mapData)
        return postJobData
    }

    fun getJobDetails(mapData: Map<String, String>): LiveData<JobDetailsDtoMain> {
        jobDetailsData = JobPostRepo().getJobDetailsData(mapData)
        return jobDetailsData
    }

    fun cancelJob(mapData:Map<String, String>):LiveData<CancelJobDtoMain>{
        cancelJobData = JobPostRepo().cancelJobData(mapData)
        return cancelJobData
    }

    fun updateJob(mapData:Map<String, String>):LiveData<UpdateJobDtoMain>{
        updateJobData = JobPostRepo().updateJobData(mapData)
        return updateJobData
    }
}