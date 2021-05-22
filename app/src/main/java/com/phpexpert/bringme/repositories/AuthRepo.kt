package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.retro.AuthRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepo {

    private var authData: MutableLiveData<AuthDtoMain> = MutableLiveData()
    fun getAuthData(): MutableLiveData<AuthDtoMain> {
        ServiceGenerator.createService(AuthRetro::class.java).getAuthApis().enqueue(object : Callback<AuthDtoMain> {
            override fun onResponse(call: Call<AuthDtoMain>, response: Response<AuthDtoMain>) {
                if (response.isSuccessful) {
                    authData.postValue(response.body())
                } else {
                    val authDtoMain = AuthDtoMain()
                    authDtoMain.status_message = "Auth Api Failure"
                    authDtoMain.status_code = "2"
                    authData.postValue(authDtoMain)
                }
            }

            override fun onFailure(call: Call<AuthDtoMain>, t: Throwable) {
                val authDtoMain = AuthDtoMain()
                authDtoMain.status_message = "Auth Api Failure"
                authDtoMain.status_code = "2"
                authData.postValue(authDtoMain)
            }

        })
        return authData
    }
}