package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.NotificationDtoMain
import com.phpexpert.bringme.retro.NotificationRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NotificationRepo {

    private var notificationData = MutableLiveData<NotificationDtoMain>()

    fun getNotificationData(map: Map<String, String>): MutableLiveData<NotificationDtoMain> {
        ServiceGenerator.createService(NotificationRetro::class.java).getNotificationData(map).enqueue(object : Callback<NotificationDtoMain> {
            override fun onResponse(call: Call<NotificationDtoMain>, response: Response<NotificationDtoMain>) {
                if (response.isSuccessful) {
                    notificationData.postValue(response.body())
                } else {
                    val notificationDataMain = NotificationDtoMain()
                    notificationDataMain.status_message = "Notification Api Error"
                    notificationDataMain.status_code = "11"
                    notificationData.postValue(notificationDataMain)
                }
            }

            override fun onFailure(call: Call<NotificationDtoMain>, t: Throwable) {
                val notificationDataMain = NotificationDtoMain()
                notificationDataMain.status_message = "Notification Api Error"
                notificationDataMain.status_code = "11"
                notificationData.postValue(notificationDataMain)
            }

        })
        return notificationData
    }
}