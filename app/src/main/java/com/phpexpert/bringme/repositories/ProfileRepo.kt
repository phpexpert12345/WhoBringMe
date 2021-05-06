package com.phpexpert.bringme.repositories

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.facebook.internal.Mutable
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ProfileChangeNumberDtoMain
import com.phpexpert.bringme.dtos.ProfileResendDataMain
import com.phpexpert.bringme.dtos.ProfileVerifyOtpDtoMain
import com.phpexpert.bringme.retro.ProfileRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileRepo {

    private var changePassword: MutableLiveData<ForgotPasswordChangeDtoMain> = MutableLiveData()
    private var changeMobileNumber:MutableLiveData<ProfileChangeNumberDtoMain> = MutableLiveData()
    private var otpVerifyData:MutableLiveData<ProfileVerifyOtpDtoMain> = MutableLiveData()
    private var resendOtpData:MutableLiveData<ProfileResendDataMain> = MutableLiveData()

    fun changePassword(mapData: Map<String, String>): MutableLiveData<ForgotPasswordChangeDtoMain> {
        ServiceGenerator.createService(ProfileRetro::class.java).changePassword(mapData).enqueue(object : Callback<ForgotPasswordChangeDtoMain> {
            override fun onResponse(call: Call<ForgotPasswordChangeDtoMain>, response: Response<ForgotPasswordChangeDtoMain>) {
                if (response.isSuccessful) {
                    changePassword.postValue(response.body())
                } else {
                    val forgotPasswordChangeDtoMain = ForgotPasswordChangeDtoMain()
                    forgotPasswordChangeDtoMain.status_code = "1"
                    forgotPasswordChangeDtoMain.status_message = "Login error in api"
                    changePassword.postValue(forgotPasswordChangeDtoMain)
                }
            }

            override fun onFailure(call: Call<ForgotPasswordChangeDtoMain>, t: Throwable) {
                val forgotPasswordChangeDtoMain = ForgotPasswordChangeDtoMain()
                forgotPasswordChangeDtoMain.status_code = "1"
                forgotPasswordChangeDtoMain.status_message = "Login error in api"
                changePassword.postValue(forgotPasswordChangeDtoMain)
            }

        })
        return changePassword
    }

    fun getChangeMobileNumberData(mapData: Map<String, String>):MutableLiveData<ProfileChangeNumberDtoMain>{
        ServiceGenerator.createService(ProfileRetro::class.java).changePhoneNumber(mapData).enqueue(object :Callback<ProfileChangeNumberDtoMain>{
            override fun onResponse(call: Call<ProfileChangeNumberDtoMain>, response: Response<ProfileChangeNumberDtoMain>) {
                if (response.isSuccessful){
                    changeMobileNumber.postValue(response.body())
                }else{
                    val forgotPasswordChangeDtoMain = ProfileChangeNumberDtoMain()
                    forgotPasswordChangeDtoMain.status_code = "1"
                    forgotPasswordChangeDtoMain.status_message = "Number Change error in api"
                    changeMobileNumber.postValue(forgotPasswordChangeDtoMain)
                }
            }

            override fun onFailure(call: Call<ProfileChangeNumberDtoMain>, t: Throwable) {
                val forgotPasswordChangeDtoMain = ProfileChangeNumberDtoMain()
                forgotPasswordChangeDtoMain.status_code = "1"
                forgotPasswordChangeDtoMain.status_message = "Number Change error in api"
                changeMobileNumber.postValue(forgotPasswordChangeDtoMain)
            }

        })
        return changeMobileNumber
    }

    fun getOtpVerifyData(mapData: Map<String, String>):MutableLiveData<ProfileVerifyOtpDtoMain>{
        ServiceGenerator.createService(ProfileRetro::class.java).changePhoneNumberOtpVerify(mapData).enqueue(object :Callback<ProfileVerifyOtpDtoMain>{
            override fun onResponse(call: Call<ProfileVerifyOtpDtoMain>, response: Response<ProfileVerifyOtpDtoMain>) {
                if (response.isSuccessful){
                    otpVerifyData.postValue(response.body())
                }else{
                    val forgotPasswordChangeDtoMain = ProfileVerifyOtpDtoMain()
                    forgotPasswordChangeDtoMain.status_code = "1"
                    forgotPasswordChangeDtoMain.status_message = "Number Change error in api"
                    otpVerifyData.postValue(forgotPasswordChangeDtoMain)
                }
            }

            override fun onFailure(call: Call<ProfileVerifyOtpDtoMain>, t: Throwable) {
                val forgotPasswordChangeDtoMain = ProfileVerifyOtpDtoMain()
                forgotPasswordChangeDtoMain.status_code = "1"
                forgotPasswordChangeDtoMain.status_message = "Number Change error in api"
                otpVerifyData.postValue(forgotPasswordChangeDtoMain)
            }

        })
        return otpVerifyData
    }

    fun getResendOtpData(mapData: Map<String, String>):MutableLiveData<ProfileResendDataMain>{
        ServiceGenerator.createService(ProfileRetro::class.java).changePhoneNumberResendOtp(mapData).enqueue(object :Callback<ProfileResendDataMain>{
            override fun onResponse(call: Call<ProfileResendDataMain>, response: Response<ProfileResendDataMain>) {
                if (response.isSuccessful){
                    resendOtpData.postValue(response.body())
                }else{
                    val forgotPasswordChangeDtoMain = ProfileResendDataMain()
                    forgotPasswordChangeDtoMain.status_code = "1"
                    forgotPasswordChangeDtoMain.status_message = "Number Change error in api"
                    resendOtpData.postValue(forgotPasswordChangeDtoMain)
                }
            }

            override fun onFailure(call: Call<ProfileResendDataMain>, t: Throwable) {
                val forgotPasswordChangeDtoMain = ProfileResendDataMain()
                forgotPasswordChangeDtoMain.status_code = "1"
                forgotPasswordChangeDtoMain.status_message = "Number Change error in api"
                resendOtpData.postValue(forgotPasswordChangeDtoMain)
            }

        })
        return resendOtpData
    }

}