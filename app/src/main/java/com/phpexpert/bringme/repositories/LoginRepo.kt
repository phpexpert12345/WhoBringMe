package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ForgotPasswordDtoMain
import com.phpexpert.bringme.dtos.LoginDtoMain
import com.phpexpert.bringme.retro.LoginRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginRepo {

    private var loginData: MutableLiveData<LoginDtoMain> = MutableLiveData()
    private var forgotOtpSendData: MutableLiveData<ForgotPasswordDtoMain> = MutableLiveData()
    private var forgotOtpReset: MutableLiveData<ForgotPasswordChangeDtoMain> = MutableLiveData()


    fun getLoginData(context: Context, mapData: Map<String, String>): MutableLiveData<LoginDtoMain> {
        ServiceGenerator.createService(LoginRetro::class.java).loginApi(mapData).enqueue(object : Callback<LoginDtoMain> {
            override fun onResponse(call: Call<LoginDtoMain>, response: Response<LoginDtoMain>) {
                if (response.isSuccessful) {
                    loginData.postValue(response.body())
                } else {
                    Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<LoginDtoMain>, t: Throwable) {
                loginData.postValue(null)
            }

        })
        return loginData
    }

    fun getForgotPasswordOtpSend(context: Context, mapData: Map<String, String>): MutableLiveData<ForgotPasswordDtoMain> {
        ServiceGenerator.createService(LoginRetro::class.java).getForgotPasswordOTP(mapData).enqueue(object : Callback<ForgotPasswordDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordDtoMain>, response: Response<ForgotPasswordDtoMain>) {
                if (response.isSuccessful) {
                    forgotOtpSendData.postValue(response.body())
                } else {
                    Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordDtoMain>, t: Throwable) {
                Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
            }

        })
        return forgotOtpSendData
    }

    fun getForgotPasswordReset(context: Context, mapData: Map<String, String>): MutableLiveData<ForgotPasswordChangeDtoMain> {
        ServiceGenerator.createService(LoginRetro::class.java).getForgotPasswordReset(mapData).enqueue(object : Callback<ForgotPasswordChangeDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordChangeDtoMain>, response: Response<ForgotPasswordChangeDtoMain>) {
                if (response.isSuccessful) {
                    forgotOtpReset.postValue(response.body())
                } else {
                    Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordChangeDtoMain>, t: Throwable) {
                Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
            }

        })
        return forgotOtpReset
    }

}