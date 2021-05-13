@file:Suppress("PropertyName", "unused")

package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PostDataOtp : Serializable {
    var accountFirstName: String? = ""
    var accountLasttName: String? = ""
    var accountMobile: String? = ""
    var accountType: String? = ""
    var accountPhoneCode: String? = ""
    var accountEmail: String? = ""
    var mobilePinCode: String? = ""
    var accountCountry: String? = ""
    var accountState: String? = ""
    var accountCity: String? = ""
    var accountAddress: String? = ""
    var addressPostCode: String? = ""
    var accountLat: String? = ""
    var accountLong: String? = ""
    var accountReferralCode: String? = ""
    var deviceTokenId: String? = ""
    var devicePlatform: String? = ""
    var otp: String? = ""
}

class GetOtpSendDataMain {
    @SerializedName("status_code")
    var status_code: String? = null

    @SerializedName("status")
    var status: String? = null

    @SerializedName("status_message")
    var status_message: String? = null

    @SerializedName("data")
    var data: OtpSendData? = OtpSendData()
}

class OtpSendData {
    @SerializedName("account_mobile")
    var account_mobile: String? = ""

    @SerializedName("account_type")
    var account_type: String? = ""

    @SerializedName("account_phone_code")
    var account_phone_code: String? = ""

    @SerializedName("auth_key")
    var auth_key: String? = ""

    @SerializedName("Token_ID")
    var Token_ID: String? = ""
}

@Suppress("unused", "PropertyName")
class RegistrationMainDto {
    @SerializedName("status_code")
    var status_code: String? = ""

    @SerializedName("status")
    var status: String? = ""

    @SerializedName("status_message")
    var status_message: String? = ""

    @SerializedName("data")
    var data: LoginDetailsDto? = LoginDetailsDto()
}

class ResendOtpMain : BaseResponseDto() {
//    @SerializedName("data")
//    var data: ArrayList<ResendOtpData>? = ArrayList()
}

class ResendOtpData {

}