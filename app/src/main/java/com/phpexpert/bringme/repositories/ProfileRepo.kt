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

    fun changePassword(context: Context, mapData: Map<String, String>): MutableLiveData<ForgotPasswordChangeDtoMain> {
        ServiceGenerator.createService(ProfileRetro::class.java).changePassword(mapData).enqueue(object : Callback<ForgotPasswordChangeDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordChangeDtoMain>, response: Response<ForgotPasswordChangeDtoMain>) {
                if (response.isSuccessful) {
                    changePassword.postValue(response.body())
                } else {
                    Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<ForgotPasswordChangeDtoMain>, t: Throwable) {
                Toast.makeText(context, "Login error in api", Toast.LENGTH_LONG).show()
            }

        })
        return changePassword
    }

}