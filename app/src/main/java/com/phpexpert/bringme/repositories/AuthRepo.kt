package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.retro.AuthRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepo {

    private var authData: MutableLiveData<AuthDtoMain> = MutableLiveData()
    fun getAuthData(context: Context): MutableLiveData<AuthDtoMain> {
        ServiceGenerator.createService(AuthRetro::class.java).getAuthApis().enqueue(object : Callback<AuthDtoMain> {
            override fun onResponse(call: Call<AuthDtoMain>, response: Response<AuthDtoMain>) {
                if (response.isSuccessful) {
                    authData.postValue(response.body())
                } else {
                    Toast.makeText(context, "Auth Api Failure", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<AuthDtoMain>, t: Throwable) {
                Toast.makeText(context, "Auth Api Failure", Toast.LENGTH_SHORT).show()
            }

        })
        return authData
    }
}