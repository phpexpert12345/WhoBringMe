package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.LanguageDtoMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AuthRetro {
    @POST("phpexpert_account_auth_key.php")
    fun getAuthApis(): Call<AuthDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_customer_app_langauge.php")
    fun getLanguageData(@FieldMap map: Map<String, String>): Call<LanguageDtoData>

    @FormUrlEncoded
    @POST("phpexpert_app_langauge_list.php")
    fun getLanguageList(@FieldMap map: Map<String, String>): Call<LanguageDtoMain>
}
