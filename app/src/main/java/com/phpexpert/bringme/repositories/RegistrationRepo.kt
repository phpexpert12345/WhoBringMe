package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.GetOtpSendDataMain
import com.phpexpert.bringme.dtos.RegistrationMainDto
import com.phpexpert.bringme.dtos.ResendOtpData
import com.phpexpert.bringme.dtos.ResendOtpMain
import com.phpexpert.bringme.retro.RegistrationRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegistrationRepo {
    private var otpSendDataMain: MutableLiveData<GetOtpSendDataMain> = MutableLiveData()
    private var registrationMainDto: MutableLiveData<RegistrationMainDto> = MutableLiveData()
    private var resendMainDto: MutableLiveData<ResendOtpMain> = MutableLiveData()

    fun otpSendResponse(mapField: Map<String, String>): MutableLiveData<GetOtpSendDataMain> {
        ServiceGenerator.createService(RegistrationRetro::class.java).sendOtpApi(mapField).enqueue(object : Callback<GetOtpSendDataMain> {
            override fun onResponse(call: Call<GetOtpSendDataMain>, response: Response<GetOtpSendDataMain>) {
                if (response.isSuccessful) {
                    otpSendDataMain.postValue(response.body())
                } else {
                    val getOtpSendDataMain = GetOtpSendDataMain()
                    getOtpSendDataMain.status_message = "Registration api failure"
                    getOtpSendDataMain.status_code = "11"
                    otpSendDataMain.postValue(getOtpSendDataMain)
                }
            }

            override fun onFailure(call: Call<GetOtpSendDataMain>, t: Throwable) {
                val getOtpSendDataMain = GetOtpSendDataMain()
                getOtpSendDataMain.status_message = "Registration api failure"
                getOtpSendDataMain.status_code = "11"
                otpSendDataMain.postValue(getOtpSendDataMain)
            }

        })

        return otpSendDataMain
    }

    fun registerData(mapField: Map<String, String?>): MutableLiveData<RegistrationMainDto> {
        ServiceGenerator.createService(RegistrationRetro::class.java).registerUser(mapField).enqueue(object : Callback<RegistrationMainDto> {
            override fun onResponse(call: Call<RegistrationMainDto>, response: Response<RegistrationMainDto>) {
                if (response.isSuccessful) {
                    registrationMainDto.postValue(response.body())
                } else {
                    val registrationMainDto  =RegistrationMainDto()
                    registrationMainDto.status_message = "Registration api failure"
                    registrationMainDto.status_code = "11"
                }
            }

            override fun onFailure(call: Call<RegistrationMainDto>, t: Throwable) {
                val registrationMainDto  =RegistrationMainDto()
                registrationMainDto.status_message = "Registration api failure"
                registrationMainDto.status_code = "11"
            }

        })

        return registrationMainDto
    }

    fun resendOtp(mapField: Map<String, String>): MutableLiveData<ResendOtpMain> {
        ServiceGenerator.createService(RegistrationRetro::class.java).resendOtpData(mapField).enqueue(object : Callback<ResendOtpMain> {
            override fun onResponse(call: Call<ResendOtpMain>, response: Response<ResendOtpMain>) {
                if (response.isSuccessful) {
                    resendMainDto.postValue(response.body())
                } else {
                    val resendOtpMain = ResendOtpMain()
                    resendOtpMain.status_code = "11"
                    resendOtpMain.status_message = "Resend Otp api error"
                    resendMainDto.postValue(resendOtpMain)
                }
            }

            override fun onFailure(call: Call<ResendOtpMain>, t: Throwable) {
                val resendOtpMain = ResendOtpMain()
                resendOtpMain.status_code = "11"
                resendOtpMain.status_message = "Resend Otp api error"
                resendMainDto.postValue(resendOtpMain)
            }

        })

        return resendMainDto
    }
}