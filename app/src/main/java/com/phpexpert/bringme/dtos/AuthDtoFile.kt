package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class AuthDtoMain {
    @SerializedName("status_code")
    var status_code: String? = ""

    @SerializedName("status_message")
    var status_message: String? = ""

    @SerializedName("data")
    var data: ArrayList<AuthDtoData>? = ArrayList()

}

class AuthDtoData {
    @SerializedName("auth_key")
    var auth_key: String? = ""

    @SerializedName("currency_code")
    var currency_code: String? = ""

    @SerializedName("GOOGLE_MAP_KEY")
    var GOOGLE_MAP_KEY: String? = ""

    @SerializedName("lang_code")
    var lang_code: String? = ""
}

object AuthSingleton {
    lateinit var authObject: AuthDtoData
}