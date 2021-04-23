package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.GetOtpSendDataMain
import com.phpexpert.bringme.dtos.RegistrationMainDto
import com.phpexpert.bringme.dtos.ResendOtpMain
import com.phpexpert.bringme.repositories.RegistrationRepo

class RegistrationModel : ViewModel() {

    private lateinit var sendOtpModel: MutableLiveData<GetOtpSendDataMain>
    private lateinit var registerData: MutableLiveData<RegistrationMainDto>
    private lateinit var resendOtpMain: MutableLiveData<ResendOtpMain>

    fun sendOtpModel(mapData: Map<String, String>): LiveData<GetOtpSendDataMain> {
        sendOtpModel = RegistrationRepo().otpSendResponse(mapData)
        return sendOtpModel
    }

    fun registerViewModel(mapData: Map<String, String?>): LiveData<RegistrationMainDto> {
        registerData = RegistrationRepo().registerData(mapData)
        return registerData
    }

    fun resendOtpModel(mapData: Map<String, String>): LiveData<ResendOtpMain> {
        resendOtpMain = RegistrationRepo().resendOtp(mapData)
        return resendOtpMain
    }

}