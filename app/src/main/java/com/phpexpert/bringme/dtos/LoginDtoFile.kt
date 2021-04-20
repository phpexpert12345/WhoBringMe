@file:Suppress("PropertyName")

package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class LoginDtoMain : BaseResponseDto() {

    @SerializedName("data")
    var data: LoginDetailsDto = LoginDetailsDto()
}