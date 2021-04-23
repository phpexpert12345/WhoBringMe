package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.GetOtpSendDataMain
import com.phpexpert.bringme.dtos.RegistrationMainDto
import com.phpexpert.bringme.dtos.ResendOtpMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface RegistrationRetro {
    @FormUrlEncoded
    @POST("phpexpert_account_otp_send.php")
    fun sendOtpApi(@FieldMap postResponse: Map<String, String>): Call<GetOtpSendDataMain>

    @FormUrlEncoded
    @POST("phpexpert_account_register_otp_verify.php")
    fun registerUser(@FieldMap postResponse: Map<String, String?>): Call<RegistrationMainDto>

    @FormUrlEncoded
    @POST("phpexpert_account_resned_otp.php")
    fun resendOtpData(@FieldMap map: Map<String, String>):Call<ResendOtpMain>
}