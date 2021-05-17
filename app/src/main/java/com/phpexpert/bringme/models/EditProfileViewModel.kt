package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.LoginDtoMain
import com.phpexpert.bringme.repositories.LoginRepo

class EditProfileViewModel:ViewModel() {

    private var loginDetailsData = MutableLiveData<LoginDtoMain>()

    fun getLoginDetailsData(mapData: Map<String, String>): LiveData<LoginDtoMain> {
        loginDetailsData = LoginRepo().getLoginData(mapData)
        return loginDetailsData
    }
}