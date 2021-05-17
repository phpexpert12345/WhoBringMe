package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ForgotPasswordDtoMain
import com.phpexpert.bringme.dtos.LoginDtoMain
import com.phpexpert.bringme.dtos.ResendOtpMain
import com.phpexpert.bringme.retro.LoginRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepo {

    private var loginData: MutableLiveData<LoginDtoMain> = MutableLiveData()
    private var forgotOtpSendData: MutableLiveData<ForgotPasswordDtoMain> = MutableLiveData()
    private var forgotOtpReset: MutableLiveData<ForgotPasswordChangeDtoMain> = MutableLiveData()
    private var loginDetailsData: MutableLiveData<ResendOtpMain> = MutableLiveData()

    fun getLoginData(mapData: Map<String, String>): MutableLiveData<LoginDtoMain> {
        ServiceGenerator.createService(LoginRetro::class.java).loginApi(mapData).enqueue(object : Callback<LoginDtoMain> {
            override fun onResponse(call: Call<LoginDtoMain>, response: Response<LoginDtoMain>) {
                if (response.isSuccessful) {
                    loginData.postValue(response.body())
                } else {
                    val loginDtoMain = LoginDtoMain()
                    loginDtoMain.status_message = "Login error in api"
                    loginDtoMain.status_code = "1"
                    loginData.postValue(loginDtoMain)
                }
            }

            override fun onFailure(call: Call<LoginDtoMain>, t: Throwable) {
                val loginDtoMain = LoginDtoMain()
                loginDtoMain.status_message = "Login error in api"
                loginDtoMain.status_code = "1"
                loginData.postValue(loginDtoMain)
            }

        })
        return loginData
    }

    fun getForgotPasswordOtpSend(mapData: Map<String, String>): MutableLiveData<ForgotPasswordDtoMain> {
        ServiceGenerator.createService(LoginRetro::class.java).getForgotPasswordOTP(mapData).enqueue(object : Callback<ForgotPasswordDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordDtoMain>, response: Response<ForgotPasswordDtoMain>) {
                if (response.isSuccessful) {
                    forgotOtpSendData.postValue(response.body())
                } else {
                    val forgotPasswordDtoMain = ForgotPasswordDtoMain()
                    forgotPasswordDtoMain.status_message="Login error in api"
                    forgotPasswordDtoMain.status_code="1"
                    forgotOtpSendData.postValue(forgotPasswordDtoMain)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordDtoMain>, t: Throwable) {
                val forgotPasswordDtoMain = ForgotPasswordDtoMain()
                forgotPasswordDtoMain.status_message="Login error in api"
                forgotPasswordDtoMain.status_code="1"
                forgotOtpSendData.postValue(forgotPasswordDtoMain)
            }

        })
        return forgotOtpSendData
    }

    fun getForgotPasswordReset(mapData: Map<String, String>): MutableLiveData<ForgotPasswordChangeDtoMain> {
        ServiceGenerator.createService(LoginRetro::class.java).getForgotPasswordReset(mapData).enqueue(object : Callback<ForgotPasswordChangeDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordChangeDtoMain>, response: Response<ForgotPasswordChangeDtoMain>) {
                if (response.isSuccessful) {
                    forgotOtpReset.postValue(response.body())
                } else {
                    val forgotPasswordChangeDtoMain = ForgotPasswordChangeDtoMain()
                    forgotPasswordChangeDtoMain.status_code="1"
                    forgotPasswordChangeDtoMain.status_message = "Login error in api"
                    forgotOtpReset.postValue(forgotPasswordChangeDtoMain)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordChangeDtoMain>, t: Throwable) {
                val forgotPasswordChangeDtoMain = ForgotPasswordChangeDtoMain()
                forgotPasswordChangeDtoMain.status_code="1"
                forgotPasswordChangeDtoMain.status_message = "Login error in api"
                forgotOtpReset.postValue(forgotPasswordChangeDtoMain)
            }

        })
        return forgotOtpReset
    }

    fun getLoginDetailsData(mapData: Map<String, String>): MutableLiveData<ResendOtpMain> {
        ServiceGenerator.createService(LoginRetro::class.java).getLoginDetails(mapData).enqueue(object : Callback<ResendOtpMain> {
            override fun onResponse(call: Call<ResendOtpMain>, response: Response<ResendOtpMain>) {
                if (response.isSuccessful) {
                    loginDetailsData.postValue(response.body())
                } else {
                    val loginDtoMain = ResendOtpMain()
                    loginDtoMain.status_message = "Login error in api"
                    loginDtoMain.status_code = "1"
                    loginDetailsData.postValue(loginDtoMain)
                }
            }

            override fun onFailure(call: Call<ResendOtpMain>, t: Throwable) {
                val loginDtoMain = ResendOtpMain()
                loginDtoMain.status_message = "Login error in api"
                loginDtoMain.status_code = "1"
                loginDetailsData.postValue(loginDtoMain)
            }

        })
        return loginDetailsData
    }

}