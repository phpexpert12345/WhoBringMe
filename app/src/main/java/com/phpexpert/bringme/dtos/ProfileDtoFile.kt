package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class ProfileChangeNumberDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: ProfileChangeNumberDtoData? = ProfileChangeNumberDtoData()
}

class ProfileChangeNumberDtoData {

}

class ProfileVerifyOtpDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: ProfileVerifyOtpDtoData? = ProfileVerifyOtpDtoData()
}

class ProfileVerifyOtpDtoData {

}

class ProfileResendDataMain:BaseResponseDto(){
    @SerializedName("data")
    var data:ProfileResendDataDto?=ProfileResendDataDto()
}

class ProfileResendDataDto{

}