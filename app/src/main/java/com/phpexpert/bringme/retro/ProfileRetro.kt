package com.phpexpert.bringme.retro

import com.phpexpert.bringme.dtos.EditProfileDto
import com.phpexpert.bringme.dtos.ForgotPasswordChangeDtoMain
import com.phpexpert.bringme.dtos.PlaceMainClass
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProfileRetro {
    @FormUrlEncoded
    @POST("phpexpert_change_password.php")
    fun changePassword(@FieldMap mapData: Map<String, String>):Call<ForgotPasswordChangeDtoMain>

    @GET("json")
    fun getPlaces(@Query("query") query: String?, @Query("key") key: String?): Call<PlaceMainClass>

    @Multipart
    @POST("phpexpert_edit_login_profile.php")
    fun editPhotoData(@PartMap map: HashMap<String, RequestBody?>,
                      @Part account_photo: MultipartBody.Part?):Call<EditProfileDto>
}