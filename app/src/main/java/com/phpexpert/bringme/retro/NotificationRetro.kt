package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.NotificationDtoMain
import retrofit2.Call
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NotificationRetro {

    @FormUrlEncoded
    @POST("phpexpert_login_notification_list.php")
    fun getNotificationData(@FieldMap map:Map<String, String>):Call<NotificationDtoMain>
}