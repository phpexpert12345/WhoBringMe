package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

open class BaseResponseDto {
    @SerializedName("status_code")
    var status_code: String? = ""

    @SerializedName("status")
    var status: String? = ""

    @SerializedName("status_message")
    var status_message: String? = ""

    @SerializedName("status_message_heading")
    var status_message_heading: String? = ""
}