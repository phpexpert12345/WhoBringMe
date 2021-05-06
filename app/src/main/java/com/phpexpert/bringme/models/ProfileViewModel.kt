package com.phpexpert.bringme.models

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ProfileChangeNumberDtoMain
import com.phpexpert.bringme.dtos.ProfileResendDataMain
import com.phpexpert.bringme.dtos.ProfileVerifyOtpDtoMain
import com.phpexpert.bringme.repositories.ProfileRepo

class ProfileViewModel : ViewModel() {
    private var changePassword = MutableLiveData<ForgotPasswordChangeDtoMain>()
    private var changeMobileNumber = MutableLiveData<ProfileChangeNumberDtoMain>()
    private var otpVerifyData:MutableLiveData<ProfileVerifyOtpDtoMain> = MutableLiveData()
    private var resendOtpData:MutableLiveData<ProfileResendDataMain> = MutableLiveData()

    fun changePassword(mapData: Map<String, String>): LiveData<ForgotPasswordChangeDtoMain> {
        changePassword = ProfileRepo().changePassword(mapData)
        return changePassword
    }

    fun changeMobileNumber(mapData: Map<String, String>):LiveData<ProfileChangeNumberDtoMain>{
        changeMobileNumber = ProfileRepo().getChangeMobileNumberData(mapData)
        return changeMobileNumber
    }

    fun otpVerifyData(mapData: Map<String, String>):LiveData<ProfileVerifyOtpDtoMain>{
        otpVerifyData = ProfileRepo().getOtpVerifyData(mapData)
        return otpVerifyData
    }

    fun otpResendData(mapData: Map<String, String>):LiveData<ProfileResendDataMain>{
        resendOtpData = ProfileRepo().getResendOtpData(mapData)
        return resendOtpData
    }
}