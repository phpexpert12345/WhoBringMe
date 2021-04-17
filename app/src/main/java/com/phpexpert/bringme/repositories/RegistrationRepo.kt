package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.GetOtpSendDataMain
import com.phpexpert.bringme.dtos.RegistrationMainDto
import com.phpexpert.bringme.retro.RegistrationRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepo {
    private var otpSendDataMain: MutableLiveData<GetOtpSendDataMain> = MutableLiveData()
    private var registrationMainDto: MutableLiveData<RegistrationMainDto> = MutableLiveData()


    fun otpSendResponse(context: Context, mapField: Map<String, String>): MutableLiveData<GetOtpSendDataMain> {
        ServiceGenerator.createService(RegistrationRetro::class.java).sendOtpApi(mapField).enqueue(object : Callback<GetOtpSendDataMain> {
            override fun onResponse(call: Call<GetOtpSendDataMain>, response: Response<GetOtpSendDataMain>) {
                if (response.isSuccessful) {
                    otpSendDataMain.postValue(response.body())
                } else {
                    Toast.makeText(context, "Registration api failure", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<GetOtpSendDataMain>, t: Throwable) {
                Toast.makeText(context, "Registration api failure", Toast.LENGTH_LONG).show()
            }

        })

        return otpSendDataMain
    }

    fun registerData(context: Context, mapField: Map<String, String?>): MutableLiveData<RegistrationMainDto> {
        ServiceGenerator.createService(RegistrationRetro::class.java).registerUser(mapField).enqueue(object : Callback<RegistrationMainDto> {
            override fun onResponse(call: Call<RegistrationMainDto>, response: Response<RegistrationMainDto>) {
                if (response.isSuccessful) {
                    registrationMainDto.postValue(response.body())
                } else {
                    Toast.makeText(context, "Registration api failure", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<RegistrationMainDto>, t: Throwable) {
                Toast.makeText(context, "Registration api failure", Toast.LENGTH_LONG).show()
            }

        })

        return registrationMainDto
    }
}