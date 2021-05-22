package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.*
import com.phpexpert.bringme.retro.JobPostRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobPostRepo {

    private var serviceChargesData = MutableLiveData<ServicesChargesDtoMain>()
    private var paymentAuthKey = MutableLiveData<PaymentConfigurationMain>()
    private var paymentGenerateToken = MutableLiveData<PaymentTokenMain>()
    private var postJobData = MutableLiveData<PostJobDataMain>()
    private var jobDetailsData = MutableLiveData<JobDetailsDtoMain>()
    private var cancelJobData = MutableLiveData<CancelJobDtoMain>()
    private var updateJobData = MutableLiveData<UpdateJobDtoMain>()



    fun jobPostRepo(subAmount: String, authKey: String): MutableLiveData<ServicesChargesDtoMain> {
        ServiceGenerator.createService(JobPostRetro::class.java).getServiceChanges(subAmount, authKey)
                .enqueue(object : Callback<ServicesChargesDtoMain> {
                    override fun onResponse(call: Call<ServicesChargesDtoMain>, response: Response<ServicesChargesDtoMain>) {
                        if (response.isSuccessful) {
                            serviceChargesData.postValue(response.body())
                        } else {
                            val servicesChargesDtoMain = ServicesChargesDtoMain()
                            servicesChargesDtoMain.status_message = "Service Api Error"
                            servicesChargesDtoMain.status_code = "2"
                            serviceChargesData.postValue(servicesChargesDtoMain)
                        }
                    }

                    override fun onFailure(call: Call<ServicesChargesDtoMain>, t: Throwable) {
                        val servicesChargesDtoMain = ServicesChargesDtoMain()
                        servicesChargesDtoMain.status_message = "Service Api Error"
                        servicesChargesDtoMain.status_code = "2"
                        serviceChargesData.postValue(servicesChargesDtoMain)
                    }

                })
        return serviceChargesData
    }

    fun getPaymentAuthKey(authKey: String): MutableLiveData<PaymentConfigurationMain> {
        ServiceGenerator.createService(JobPostRetro::class.java).getPaymentAuthKey(authKey)
                .enqueue(object : Callback<PaymentConfigurationMain> {
                    override fun onResponse(call: Call<PaymentConfigurationMain>, response: Response<PaymentConfigurationMain>) {
                        if (response.isSuccessful) {
                            paymentAuthKey.postValue(response.body())
                        } else {
                            val paymentConfigurationMain = PaymentConfigurationMain()
                            paymentConfigurationMain.status_code = "2"
                            paymentConfigurationMain.status_message = "Payment Auth Api Error"
                            paymentAuthKey.postValue(paymentConfigurationMain)
                        }
                    }

                    override fun onFailure(call: Call<PaymentConfigurationMain>, t: Throwable) {
                        val paymentConfigurationMain = PaymentConfigurationMain()
                        paymentConfigurationMain.status_code = "2"
                        paymentConfigurationMain.status_message = "Payment Auth Api Error"
                        paymentAuthKey.postValue(paymentConfigurationMain)
                    }

                })
        return paymentAuthKey
    }

    fun getPaymentGenerateToken(mapData:Map<String, String>): MutableLiveData<PaymentTokenMain> {
        ServiceGenerator.createService(JobPostRetro::class.java).getPaymentToken(mapData)
                .enqueue(object : Callback<PaymentTokenMain> {
                    override fun onResponse(call: Call<PaymentTokenMain>, response: Response<PaymentTokenMain>) {
                        if (response.isSuccessful) {
                            paymentGenerateToken.postValue(response.body())
                        } else {
                            val paymentTokenMain = PaymentTokenMain()
                            paymentTokenMain.status_message = "Payment Token Api Error"
                            paymentTokenMain.status_code = "2"
                            paymentGenerateToken.postValue(paymentTokenMain)
                        }
                    }
                    override fun onFailure(call: Call<PaymentTokenMain>, t: Throwable) {
                        val paymentTokenMain = PaymentTokenMain()
                        paymentTokenMain.status_message = "Payment Token Api Error"
                        paymentTokenMain.status_code = "2"
                        paymentGenerateToken.postValue(paymentTokenMain)
                    }

                })
        return paymentGenerateToken
    }

    fun getPostJobData(mapData:Map<String, String>): MutableLiveData<PostJobDataMain> {
        ServiceGenerator.createService(JobPostRetro::class.java).postJobData(mapData)
                .enqueue(object : Callback<PostJobDataMain> {
                    override fun onResponse(call: Call<PostJobDataMain>, response: Response<PostJobDataMain>) {
                        if (response.isSuccessful) {
                            postJobData.postValue(response.body())
                        } else {
                            val postJobDataMain = PostJobDataMain()
                            postJobDataMain.status_message = "Payment Token Api Error"
                            postJobDataMain.status_code = "2"
                            postJobData.postValue(postJobDataMain)
                        }
                    }

                    override fun onFailure(call: Call<PostJobDataMain>, t: Throwable) {
                        val postJobDataMain = PostJobDataMain()
                        postJobDataMain.status_message = "Payment Token Api Error"
                        postJobDataMain.status_code = "2"
                        postJobData.postValue(postJobDataMain)
                    }

                })
        return postJobData
    }

    fun getJobDetailsData(mapData:Map<String, String>): MutableLiveData<JobDetailsDtoMain> {
        ServiceGenerator.createService(JobPostRetro::class.java).getJobDetails(mapData)
                .enqueue(object : Callback<JobDetailsDtoMain> {
                    override fun onResponse(call: Call<JobDetailsDtoMain>, response: Response<JobDetailsDtoMain>) {
                        if (response.isSuccessful) {
                            jobDetailsData.postValue(response.body())
                        } else {
                            val postJobDataMain = JobDetailsDtoMain()
                            postJobDataMain.status_message = "Job Details Api Error"
                            postJobDataMain.status_code = "2"
                            jobDetailsData.postValue(postJobDataMain)
                        }
                    }

                    override fun onFailure(call: Call<JobDetailsDtoMain>, t: Throwable) {
                        val postJobDataMain = JobDetailsDtoMain()
                        postJobDataMain.status_message = "Job Details Api Error"
                        postJobDataMain.status_code = "2"
                        jobDetailsData.postValue(postJobDataMain)
                    }

                })
        return jobDetailsData
    }

    fun cancelJobData(mapData: Map<String, String>):MutableLiveData<CancelJobDtoMain>{
        ServiceGenerator.createService(JobPostRetro::class.java).cancelJobData(mapData)
                .enqueue(object : Callback<CancelJobDtoMain> {
                    override fun onResponse(call: Call<CancelJobDtoMain>, response: Response<CancelJobDtoMain>) {
                        if (response.isSuccessful) {
                            cancelJobData.postValue(response.body())
                        } else {
                            val cancelJobDtoMain = CancelJobDtoMain()
                            cancelJobDtoMain.status_code="2"
                            cancelJobDtoMain.status_message = "Cancel Job Api Error"
                            cancelJobData.postValue(cancelJobDtoMain)
                        }
                    }

                    override fun onFailure(call: Call<CancelJobDtoMain>, t: Throwable) {
                        val cancelJobDtoMain = CancelJobDtoMain()
                        cancelJobDtoMain.status_code="2"
                        cancelJobDtoMain.status_message = "Cancel Job Api Error"
                        cancelJobData.postValue(cancelJobDtoMain)
                    }

                })
        return cancelJobData
    }

    fun updateJobData(mapData: Map<String, String>):MutableLiveData<UpdateJobDtoMain>{
        ServiceGenerator.createService(JobPostRetro::class.java).updateJobData(mapData)
                .enqueue(object : Callback<UpdateJobDtoMain> {
                    override fun onResponse(call: Call<UpdateJobDtoMain>, response: Response<UpdateJobDtoMain>) {
                        if (response.isSuccessful) {
                            updateJobData.postValue(response.body())
                        } else {
                            val cancelJobDtoMain = UpdateJobDtoMain()
                            cancelJobDtoMain.status_code="2"
                            cancelJobDtoMain.status_message = "Cancel Job Api Error"
                            updateJobData.postValue(cancelJobDtoMain)
                        }
                    }

                    override fun onFailure(call: Call<UpdateJobDtoMain>, t: Throwable) {
                        val cancelJobDtoMain = UpdateJobDtoMain()
                        cancelJobDtoMain.status_code="2"
                        cancelJobDtoMain.status_message = "Cancel Job Api Error"
                        updateJobData.postValue(cancelJobDtoMain)
                    }

                })
        return updateJobData
    }
}