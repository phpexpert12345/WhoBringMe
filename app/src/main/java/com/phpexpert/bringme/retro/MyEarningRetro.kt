package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.EarningDtoMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyEarningRetro {
    @FormUrlEncoded
    @POST("phpexpert_delivery_employee_job_earning_history.php")
    fun getEarningData(@FieldMap map: Map<String, String>): Call<EarningDtoMain>
}