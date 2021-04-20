package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.repositories.ProfileRepo

class ProfileViewModel : ViewModel() {
    private var changePassword = MutableLiveData<ForgotPasswordChangeDtoMain>()

    fun changePassword(context: Context, mapData: Map<String, String>): LiveData<ForgotPasswordChangeDtoMain> {
        changePassword = ProfileRepo().changePassword(context, mapData)
        return changePassword
    }
}