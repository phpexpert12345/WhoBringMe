package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

class LanguageDtoMain:BaseResponseDto() {
    @SerializedName("LanguageListList")
    var LanguageListList: ArrayList<LanguageListList> = ArrayList()
}

class LanguageListList {
    @SerializedName("lang_code")
    var lang_code: String = ""

    @SerializedName("lang_name")
    var lang_name: String = ""

    @SerializedName("lang_icon")
    var lang_icon: String = ""

    @SerializedName("error")
    var error: String = ""
}