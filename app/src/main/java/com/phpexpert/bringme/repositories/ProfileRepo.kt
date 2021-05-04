package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.retro.ProfileRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepo {

    private var changePassword: MutableLiveData<ForgotPasswordChangeDtoMain> = MutableLiveData()

    fun changePassword(mapData: Map<String, String>): MutableLiveData<ForgotPasswordChangeDtoMain> {
        ServiceGenerator.createService(ProfileRetro::class.java).changePassword(mapData).enqueue(object : Callback<ForgotPasswordChangeDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordChangeDtoMain>, response: Response<ForgotPasswordChangeDtoMain>) {
                if (response.isSuccessful) {
                    changePassword.postValue(response.body())
                } else {
                    val forgotPasswordChangeDtoMain = ForgotPasswordChangeDtoMain()
                    forgotPasswordChangeDtoMain.status_code = "1"
                    forgotPasswordChangeDtoMain.status_message = "Login error in api"
                    changePassword.postValue(forgotPasswordChangeDtoMain)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordChangeDtoMain>, t: Throwable) {
                val forgotPasswordChangeDtoMain = ForgotPasswordChangeDtoMain()
                forgotPasswordChangeDtoMain.status_code = "1"
                forgotPasswordChangeDtoMain.status_message = "Login error in api"
                changePassword.postValue(forgotPasswordChangeDtoMain)
            }

        })
        return changePassword
    }

}