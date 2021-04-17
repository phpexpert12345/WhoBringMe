package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.AuthDtoMain
import retrofit2.Call
import retrofit2.http.POST

interface AuthRetro{
    @POST("phpexpert_account_auth_key.php")
    fun getAuthApis():Call<AuthDtoMain>
}
