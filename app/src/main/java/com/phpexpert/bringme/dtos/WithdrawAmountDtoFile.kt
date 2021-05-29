package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class WithdrawAmountDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: ArrayList<WithdrawAmountDtoData>? = ArrayList()
}

class WithdrawAmountDtoData