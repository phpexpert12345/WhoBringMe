package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class EditProfileDto : BaseResponseDto() {
    @SerializedName("data")
    var data: EditProfileDtoData = EditProfileDtoData()
}

class EditProfileDtoData {
    @SerializedName("LoginId")
    var LoginId: String? = ""

    @SerializedName("login_name")
    var login_name: String? = ""

    @SerializedName("login_email")
    var login_email: String? = ""

    @SerializedName("login_photo")
    var login_photo: String? = ""
}