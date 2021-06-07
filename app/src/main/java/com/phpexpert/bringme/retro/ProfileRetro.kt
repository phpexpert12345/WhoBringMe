package com.phpexpert.bringme.retro

import com.google.gson.JsonObject
import com.phpexpert.bringme.dtos.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileRetro {
    @FormUrlEncoded
    @POST("phpexpert_change_password.php")
    fun changePassword(@FieldMap mapData: Map<String, String>): Call<ForgotPasswordChangeDtoMain>

    @GET("json")
    fun getPlaces(@Query("query") query: String?, @Query("key") key: String?): Call<PlaceMainClass>

    @Multipart
    @POST("phpexpert_edit_login_profile.php")
    fun editPhotoData(@PartMap map: HashMap<String, RequestBody?>,
                      @Part account_photo: MultipartBody.Part?): Call<EditProfileDto>

    @FormUrlEncoded
    @POST("phpexpert_change_mobile_number_OTP.php")
    fun changePhoneNumber(@FieldMap mapData: Map<String, String>): Call<ProfileChangeNumberDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_change_mobile_number.php")
    fun changePhoneNumberOtpVerify(@FieldMap mapData: Map<String, String>): Call<ProfileVerifyOtpDtoMain>

    @FormUrlEncoded
    @POST("phpexpert_change_mobile_number_Resend_OTP.php")
    fun changePhoneNumberResendOtp(@FieldMap mapData: Map<String, String>): Call<ProfileResendDataMain>

    @Multipart
    @POST("phpexpert_delivery_emp_document_upload.php")
    fun uploadDocument(@PartMap map: HashMap<String, RequestBody?>,
                       @Part document_front: MultipartBody.Part?,
                       @Part document_back: MultipartBody.Part?): Call<JsonObject>

}