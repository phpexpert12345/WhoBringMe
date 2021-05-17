package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ForgotPasswordDtoMain
import com.phpexpert.bringme.dtos.LoginDtoMain
import com.phpexpert.bringme.dtos.ResendOtpMain
import com.phpexpert.bringme.repositories.LoginRepo

class LoginViewModel : ViewModel() {

    private var loginData = MutableLiveData<LoginDtoMain>()
    private var otpForgotPasswordSend = MutableLiveData<ForgotPasswordDtoMain>()
    private var otpForgotPasswordReset = MutableLiveData<ForgotPasswordChangeDtoMain>()
    private var loginDetailsData = MutableLiveData<ResendOtpMain>()


    fun getLoginData(mapData: Map<String, String>): LiveData<LoginDtoMain> {
        loginData = LoginRepo().getLoginData(mapData)
        return loginData
    }

    fun getOtpForgotPasswordSendData(mapData: Map<String, String>): LiveData<ForgotPasswordDtoMain> {
        otpForgotPasswordSend = LoginRepo().getForgotPasswordOtpSend(mapData)
        return otpForgotPasswordSend
    }

    fun getOtpForgotPasswordReset(mapData: Map<String, String>): LiveData<ForgotPasswordChangeDtoMain> {
        otpForgotPasswordReset = LoginRepo().getForgotPasswordReset(mapData)
        return otpForgotPasswordReset
    }
    fun getLoginDetailsData(mapData: Map<String, String>): LiveData<ResendOtpMain> {
        loginDetailsData = LoginRepo().getLoginDetailsData(mapData)
        return loginDetailsData
    }

}