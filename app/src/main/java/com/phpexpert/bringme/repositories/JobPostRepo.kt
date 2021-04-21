package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.PaymentConfigurationMain
import com.phpexpert.bringme.dtos.PaymentTokenMain
import com.phpexpert.bringme.dtos.PostJobDataMain
import com.phpexpert.bringme.dtos.ServicesChargesDtoMain
import com.phpexpert.bringme.retro.CreateJobRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobPostRepo {

    private var serviceChargesData = MutableLiveData<ServicesChargesDtoMain>()
    private var paymentAuthKey = MutableLiveData<PaymentConfigurationMain>()
    private var paymentGenerateToken = MutableLiveData<PaymentTokenMain>()
    private var postJobData = MutableLiveData<PostJobDataMain>()


    fun jobPostRepo(context: Context, subAmount: String, authKey: String): MutableLiveData<ServicesChargesDtoMain> {
        ServiceGenerator.createService(CreateJobRetro::class.java).getServiceChanges(subAmount, authKey)
                .enqueue(object : Callback<ServicesChargesDtoMain> {
                    override fun onResponse(call: Call<ServicesChargesDtoMain>, response: Response<ServicesChargesDtoMain>) {
                        if (response.isSuccessful) {
                            serviceChargesData.postValue(response.body())
                        } else {
                            Toast.makeText(context, "Service Api Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<ServicesChargesDtoMain>, t: Throwable) {
                        Toast.makeText(context, "Service Api Error", Toast.LENGTH_LONG).show()
                    }

                })
        return serviceChargesData
    }

    fun getPaymentAuthKey(context: Context, authKey: String): MutableLiveData<PaymentConfigurationMain> {
        ServiceGenerator.createService(CreateJobRetro::class.java).getPaymentAuthKey(authKey)
                .enqueue(object : Callback<PaymentConfigurationMain> {
                    override fun onResponse(call: Call<PaymentConfigurationMain>, response: Response<PaymentConfigurationMain>) {
                        if (response.isSuccessful) {
                            paymentAuthKey.postValue(response.body())
                        } else {
                            Toast.makeText(context, "Payment Auth Api Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<PaymentConfigurationMain>, t: Throwable) {
                        Toast.makeText(context, "Payment Auth Api Error", Toast.LENGTH_LONG).show()
                    }

                })
        return paymentAuthKey
    }

    fun getPaymentGenerateToken(context: Context, mapData:Map<String, String>): MutableLiveData<PaymentTokenMain> {
        ServiceGenerator.createService(CreateJobRetro::class.java).getPaymentToken(mapData)
                .enqueue(object : Callback<PaymentTokenMain> {
                    override fun onResponse(call: Call<PaymentTokenMain>, response: Response<PaymentTokenMain>) {
                        if (response.isSuccessful) {
                            paymentGenerateToken.postValue(response.body())
                        } else {
                            Toast.makeText(context, "Payment Token Api Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<PaymentTokenMain>, t: Throwable) {
                        Toast.makeText(context, "Payment Token Api Error", Toast.LENGTH_LONG).show()
                    }

                })
        return paymentGenerateToken
    }

    fun getPostJobData(context: Context, mapData:Map<String, String>): MutableLiveData<PostJobDataMain> {
        ServiceGenerator.createService(CreateJobRetro::class.java).postJobData(mapData)
                .enqueue(object : Callback<PostJobDataMain> {
                    override fun onResponse(call: Call<PostJobDataMain>, response: Response<PostJobDataMain>) {
                        if (response.isSuccessful) {
                            postJobData.postValue(response.body())
                        } else {
                            Toast.makeText(context, "Payment Token Api Error", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: Call<PostJobDataMain>, t: Throwable) {
                        Toast.makeText(context, "Payment Token Api Error", Toast.LENGTH_LONG).show()
                    }

                })
        return postJobData
    }
}