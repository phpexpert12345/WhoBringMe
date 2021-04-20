package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class ForgotPasswordDtoMain:BaseResponseDto() {
    @SerializedName("data")
    var data: ForgotPasswordData? = ForgotPasswordData()
}

class ForgotPasswordData {
    @SerializedName("Token_ID")
    var Token_ID: String? = ""

    @SerializedName("LoginId")
    var LoginId: String? = ""

    @SerializedName("login_name")
    var login_name: String? = ""

    @SerializedName("Mobile_OTP")
    var Mobile_OTP: String? = ""

    @SerializedName("login_phone")
    var login_phone: String? = ""

    @SerializedName("login_phone_code")
    var login_phone_code: String? = ""
}

class ForgotPasswordChangeDtoMain : BaseResponseDto() {

    @SerializedName("data")
    var data: ForgotPasswordVerifyData? = ForgotPasswordVerifyData()
}

class ForgotPasswordVerifyData {
    @SerializedName("LoginId")
    var LoginId: String? = ""

    @SerializedName("login_name")
    var login_name: String? = ""
}