package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.DeliveryLatestJobDtoMain
import com.phpexpert.bringme.dtos.OrderAcceptMainDto
import com.phpexpert.bringme.dtos.OrderDeclineMainDto
import com.phpexpert.bringme.dtos.OrderFinishMainDto
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface DeliveryLatestJob {
    @FormUrlEncoded
    @POST("phpexpert_delivery_employee_latest_order_list.php")
    fun getLatestJobDelivery(@FieldMap map: Map<String, String>): Call<DeliveryLatestJobDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_delivery_emp_order_accepted.php")
    fun orderAcceptData(@FieldMap map: Map<String, String>):Call<OrderAcceptMainDto>

    @FormUrlEncoded
    @POST("phpexpert_delivery_emp_order_decline.php")
    fun orderDeclineData(@FieldMap map: Map<String, String>):Call<OrderDeclineMainDto>

    @FormUrlEncoded
    @POST("phpexpert_delivery_emp_order_Completed.php")
    fun orderCompleteData(@FieldMap map:Map<String, String>):Call<OrderFinishMainDto>
}