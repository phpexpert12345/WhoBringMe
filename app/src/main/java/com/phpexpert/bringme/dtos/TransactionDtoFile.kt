package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class TransactionDtoMain : BaseResponseDto() {
    @SerializedName("data")
    var data: TransactionDtoData = TransactionDtoData()
}

class TransactionDtoData {
    @SerializedName("Transaction_History")
    var Transaction_History: ArrayList<TransactionDtoList>? = ArrayList()
}

class TransactionDtoList {
    @SerializedName("id")
    var id: String? = ""

    @SerializedName("transaction_id")
    var transaction_id: String? = ""

    @SerializedName("transaction_type")
    var transaction_type: String? = ""

    @SerializedName("transaction_amount")
    var transaction_amount: String? = ""

    @SerializedName("transaction_mode")
    var transaction_mode: String? = ""

    @SerializedName("transaction_date")
    var transaction_date: String? = ""

    @SerializedName("transaction_time")
    var transaction_time: String? = ""

    @SerializedName("transaction_status_msg")
    var transaction_status_msg: String? = ""

    @SerializedName("transaction_status_color_code")
    var transaction_status_color_code: String? = ""

    @SerializedName("transaction_status_text_color_code")
    var transaction_status_text_color_code: String? = ""
}