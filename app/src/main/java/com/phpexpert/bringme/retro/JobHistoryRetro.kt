package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.EmployeeJobHistoryDtoMain
import com.phpexpert.bringme.dtos.LatestJobDtoMain
import com.phpexpert.bringme.dtos.WriteReviewJobDtoMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface JobHistoryRetro {
    @FormUrlEncoded
    @POST("phpexpert_client_reciever_latest_order_list.php")
    fun latestJobHistory(@FieldMap mapData:Map<String, String>):Call<LatestJobDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_client_reciever_job_history.php")
    fun getJobHistory(@FieldMap mapData:Map<String, String>):Call<EmployeeJobHistoryDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_write_review.php")
    fun writeReviewData(@FieldMap mapData: Map<String, String>):Call<WriteReviewJobDtoMain>
}