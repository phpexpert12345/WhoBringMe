package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.repositories.AuthRepo

class AuthModel : ViewModel() {

    private lateinit var authDataModel: MutableLiveData<AuthDtoMain>
    fun getAuthDataModel(): LiveData<AuthDtoMain> {
        authDataModel = AuthRepo().getAuthData()
        return authDataModel
    }
}