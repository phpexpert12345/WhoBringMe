package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class NotificationDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: NotificationDtoData? = NotificationDtoData()
}

class NotificationDtoData {
    @SerializedName("NotificationList")
    var NotificationList: ArrayList<NotificationDtoList>? = ArrayList()
}

class NotificationDtoList {
    @SerializedName("notification_id")
    var notification_id: String? = ""

    @SerializedName("order_id")
    var order_id: String? = ""

    @SerializedName("notification_type")
    var notification_type: String? = ""

    @SerializedName("notification_subject")
    var notification_subject: String? = ""

    @SerializedName("notification_message")
    var notification_message: String? = ""

    @SerializedName("notification_date")
    var notification_date: String? = ""

    @SerializedName("notification_time")
    var notification_time: String? = ""
}

