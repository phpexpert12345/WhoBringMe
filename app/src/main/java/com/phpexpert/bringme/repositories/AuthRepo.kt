package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.LanguageDtoMain
import com.phpexpert.bringme.retro.AuthRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthRepo {

    private var authData: MutableLiveData<AuthDtoMain> = MutableLiveData()
    private var languageDataData: MutableLiveData<LanguageDtoData> = MutableLiveData()
    private var languageDataList: MutableLiveData<LanguageDtoMain> = MutableLiveData()

    fun getAuthData(): MutableLiveData<AuthDtoMain> {
        ServiceGenerator.createService(AuthRetro::class.java).getAuthApis().enqueue(object : Callback<AuthDtoMain> {
            override fun onResponse(call: Call<AuthDtoMain>, response: Response<AuthDtoMain>) {
                if (response.isSuccessful) {
                    authData.postValue(response.body())
                } else {
                    val authDtoMain = AuthDtoMain()
                    authDtoMain.status_message = "Auth Api Failure"
                    authDtoMain.status_code = "11"
                    authData.postValue(authDtoMain)
                }
            }

            override fun onFailure(call: Call<AuthDtoMain>, t: Throwable) {
                val authDtoMain = AuthDtoMain()
                authDtoMain.status_message = "Auth Api Failure"
                authDtoMain.status_code = "11"
                authData.postValue(authDtoMain)
            }

        })
        return authData
    }

    fun getLanguageData(map: Map<String, String>): MutableLiveData<LanguageDtoData> {
        ServiceGenerator.createService(AuthRetro::class.java).getLanguageData(map).enqueue(object : Callback<LanguageDtoData> {
            override fun onResponse(call: Call<LanguageDtoData>, response: Response<LanguageDtoData>) {
                if (response.isSuccessful) {
                    languageDataData.postValue(response.body())
                } else {
                    val authDtoMain = LanguageDtoData()
                    authDtoMain.success = "11"
                    languageDataData.postValue(authDtoMain)
                }
            }

            override fun onFailure(call: Call<LanguageDtoData>, t: Throwable) {
                val authDtoMain = LanguageDtoData()
                authDtoMain.success = "11"
                languageDataData.postValue(authDtoMain)
            }

        })
        return languageDataData
    }

    fun getLanguageList(map: Map<String, String>): MutableLiveData<LanguageDtoMain> {
        ServiceGenerator.createService(AuthRetro::class.java).getLanguageList(map).enqueue(object : Callback<LanguageDtoMain> {
            override fun onResponse(call: Call<LanguageDtoMain>, response: Response<LanguageDtoMain>) {
                if (response.isSuccessful) {
                    languageDataList.postValue(response.body())
                } else {
                    val authDtoMain = LanguageDtoMain()
                    authDtoMain.status_code = "11"
                    languageDataList.postValue(authDtoMain)
                }
            }

            override fun onFailure(call: Call<LanguageDtoMain>, t: Throwable) {
                val authDtoMain = LanguageDtoMain()
                authDtoMain.status_code = "11"
                languageDataList.postValue(authDtoMain)
            }

        })
        return languageDataList
    }
}