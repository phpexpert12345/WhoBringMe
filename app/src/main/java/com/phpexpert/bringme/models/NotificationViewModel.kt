package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.NotificationDtoMain
import com.phpexpert.bringme.repositories.NotificationRepo

class NotificationViewModel:ViewModel() {
    private var notificationData = MutableLiveData<NotificationDtoMain>()

    fun getNotificationData(map: Map<String, String>):LiveData<NotificationDtoMain>{
        notificationData = NotificationRepo().getNotificationData(map)
        return notificationData
    }
}