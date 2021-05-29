package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.LanguageDtoMain
import com.phpexpert.bringme.repositories.AuthRepo

class AuthModel : ViewModel() {

    private lateinit var authDataModel: MutableLiveData<AuthDtoMain>
    private var languageDataData: MutableLiveData<LanguageDtoData> = MutableLiveData()
    private var languageList: MutableLiveData<LanguageDtoMain> = MutableLiveData()

    fun getAuthDataModel(): LiveData<AuthDtoMain> {
        authDataModel = AuthRepo().getAuthData()
        return authDataModel
    }

    fun getLanguageData(map: Map<String, String>): LiveData<LanguageDtoData> {
        languageDataData = AuthRepo().getLanguageData(map)
        return languageDataData
    }

    fun getLanguageList(map: Map<String, String>): LiveData<LanguageDtoMain> {
        languageList = AuthRepo().getLanguageList(map)
        return languageList
    }
}