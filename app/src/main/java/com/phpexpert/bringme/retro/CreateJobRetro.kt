package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.PaymentConfigurationMain
import com.phpexpert.bringme.dtos.PaymentTokenMain
import com.phpexpert.bringme.dtos.PostJobDataMain
import com.phpexpert.bringme.dtos.ServicesChargesDtoMain
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface CreateJobRetro {
    @FormUrlEncoded
    @POST("phpexpert_service_charget_get.php")
    fun getServiceChanges(@Field("subtotal_amount") subtotal_amount:String, @Field("auth_key") auth_key:String):Call<ServicesChargesDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_payment_key.php")
    fun getPaymentAuthKey(@Field("auth_key") auth_key:String):Call<PaymentConfigurationMain>

    @FormUrlEncoded
    @POST("phpexpert_payment_intent_generate.php")
    fun getPaymentToken(@FieldMap mapData:Map<String, String>):Call<PaymentTokenMain>

    @FormUrlEncoded
    @POST("phpexpert_employee_job_posting_submit.php")
    fun postJobData(@FieldMap map:Map<String, String>):Call<PostJobDataMain>
}