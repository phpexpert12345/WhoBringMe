package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.MyJobDtoMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface MyJobRetro {
    @FormUrlEncoded
    @POST("phpexpert_delivery_employee_job_history.php")
    fun getMyJobData(@FieldMap map: Map<String, String>): Call<MyJobDtoMain>
}