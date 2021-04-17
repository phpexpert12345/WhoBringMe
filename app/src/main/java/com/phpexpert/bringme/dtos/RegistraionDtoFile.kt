@file:Suppress("PropertyName", "unused")

package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName
import java.io.Serializable

class PostDataOtp : Serializable {
    var accountFirstName: String?=""
    var accountLasttName: String?=""
    var accountMobile: String?=""
    var accountType: String?=""
    var accountPhoneCode: String?=""
    var accountEmail: String?=""
    var mobilePinCode: String?=""
    var accountCountry: String?=""
    var accountState: String?=""
    var accountCity: String?=""
    var accountAddress: String?=""
    var addressPostCode: String?=""
    var accountLat: String?=""
    var accountLong: String?=""
    var accountReferralCode: String?=""
    var deviceTokenId: String?=""
    var devicePlatform: String?=""
    var otp: String?=""
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
    var data: RegistrationDataDto? = RegistrationDataDto()
}

@Suppress("PropertyName")
class RegistrationDataDto {
    @SerializedName("otp_number")
    var otp_number: String? = ""

    @SerializedName("account_first_name")
    var account_first_name: String? = ""

    @SerializedName("account_last_name")
    var account_last_name: String? = ""

    @SerializedName("account_email")
    var account_email: String? = ""

    @SerializedName("account_mobile")
    var account_mobile: String? = ""

    @SerializedName("account_mpin_number")
    var account_mpin_number: String? = ""

    @SerializedName("account_type")
    var account_type: String? = ""

    @SerializedName("account_country")
    var account_country: String? = ""

    @SerializedName("account_state")
    var account_state: String? = ""

    @SerializedName("account_city")
    var account_city: String? = ""

    @SerializedName("account_address")
    var account_address: String? = ""

    @SerializedName("address_postcode")
    var address_postcode: String? = ""

    @SerializedName("account_lat")
    var account_lat: String? = ""

    @SerializedName("account_long")
    var account_long: String? = ""

    @SerializedName("account_phone_code")
    var account_phone_code: String? = ""

    @SerializedName("referral_code")
    var referral_code: String? = ""

    @SerializedName("device_token_id")
    var device_token_id: String? = ""

    @SerializedName("device_platform")
    var device_platform: String? = ""

    @SerializedName("auth_key")
    var auth_key: String? = ""
}