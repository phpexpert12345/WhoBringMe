package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ForgotPasswordDtoMain
import com.phpexpert.bringme.dtos.LoginDtoMain
import com.phpexpert.bringme.repositories.LoginRepo

class LoginViewModel : ViewModel() {

    private var loginData = MutableLiveData<LoginDtoMain>()
    private var otpForgotPasswordSend = MutableLiveData<ForgotPasswordDtoMain>()
    private var otpForgotPasswordReset = MutableLiveData<ForgotPasswordChangeDtoMain>()


    fun getLoginData(context: Context, mapData: Map<String, String>): LiveData<LoginDtoMain> {
        loginData = LoginRepo().getLoginData(context, mapData)
        return loginData
    }

    fun getOtpForgotPasswordSendData(context: Context, mapData: Map<String, String>): LiveData<ForgotPasswordDtoMain> {
        otpForgotPasswordSend = LoginRepo().getForgotPasswordOtpSend(context, mapData)
        return otpForgotPasswordSend
    }

    fun getOtpForgotPasswordReset(context: Context, mapData: Map<String, String>): LiveData<ForgotPasswordChangeDtoMain> {
        otpForgotPasswordReset = LoginRepo().getForgotPasswordReset(context, mapData)
        return otpForgotPasswordReset
    }
}