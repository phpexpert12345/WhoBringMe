package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.ForgotPasswordDtoMain
import com.phpexpert.bringme.dtos.LoginDtoMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface LoginRetro {
    @FormUrlEncoded
    @POST("phpexpert_login.php")
    fun loginApi(@FieldMap mapData: Map<String, String>): Call<LoginDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_account_forgot_password.php")
    fun getForgotPasswordOTP(@FieldMap mapData:Map<String, String>):Call<ForgotPasswordDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_account_reset_password.php")
    fun getForgotPasswordReset(@FieldMap mapData:Map<String, String>):Call<ForgotPasswordChangeDtoMain>
}