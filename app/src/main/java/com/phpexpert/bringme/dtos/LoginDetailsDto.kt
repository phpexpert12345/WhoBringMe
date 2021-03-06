package com.phpexpert.bringme.dtos

import com.google.gson.annotations.SerializedName

open class LoginDetailsDto {

    @SerializedName("password")
    var password: String? = ""

    @SerializedName("LoginId")
    var LoginId: String? = ""

    @SerializedName("login_name")
    var login_name: String? = ""

    @SerializedName("login_first_name")
    var login_first_name: String? = ""

    @SerializedName("login_last_name")
    var login_last_name: String? = ""

    @SerializedName("login_email")
    var login_email: String? = ""

    @SerializedName("login_country")
    var login_country: String? = ""

    @SerializedName("login_state")
    var login_state: String? = ""

    @SerializedName("login_city")
    var login_city: String? = ""

    @SerializedName("login_address")
    var login_address: String? = ""

    @SerializedName("login_lat")
    var login_lat: String? = ""

    @SerializedName("login_long")
    var login_long: String? = ""

    @SerializedName("login_postcode")
    var login_postcode: String? = ""

    @SerializedName("account_type_name")
    var account_type_name: String? = ""

    @SerializedName("kyc_setup_status")
    var kyc_setup_status: String? = ""

    @SerializedName("account_type")
    var account_type: String? = ""

    @SerializedName("login_phone")
    var login_phone: String? = ""

    @SerializedName("login_phone_code")
    var login_phone_code: String? = ""

    @SerializedName("wallet_amount")
    var wallet_amount: String? = ""

    @SerializedName("Total_following")
    var Total_following: String? = ""

    @SerializedName("Total_followers")
    var Total_followers: String? = ""

    @SerializedName("Total_Inbox_Message")
    var Total_Inbox_Message: String? = ""

    @SerializedName("login_photo")
    var login_photo: String? = ""

    @SerializedName("login_document_front_photo")
    var login_document_front_photo: String? = ""

    @SerializedName("login_document_back_photo")
    var login_document_back_photo: String? = ""

    @SerializedName("document_country")
    var document_country: String? = ""

    @SerializedName("document_type")
    var document_type: String? = ""

    @SerializedName("document_status")
    var document_status: String? = ""

    @SerializedName("device_id_android")
    var device_id_android: String? = ""

}