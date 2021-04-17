package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.GetOtpSendDataMain
import com.phpexpert.bringme.dtos.RegistrationMainDto
import com.phpexpert.bringme.repositories.RegistrationRepo

class RegistrationModel : ViewModel() {

    private lateinit var sendOtpModel: MutableLiveData<GetOtpSendDataMain>
    private lateinit var registerData: MutableLiveData<RegistrationMainDto>

    fun sendOtpModel(context: Context, mapData: Map<String, String>): LiveData<GetOtpSendDataMain> {
        sendOtpModel = RegistrationRepo().otpSendResponse(context, mapData)
        return sendOtpModel
    }

    fun registerViewModel(context: Context, mapData: Map<String, String?>): LiveData<RegistrationMainDto> {
        registerData = RegistrationRepo().registerData(context, mapData)
        return registerData
    }

}