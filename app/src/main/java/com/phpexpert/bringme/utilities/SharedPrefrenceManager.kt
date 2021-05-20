package com.phpexpert.bringme.utilities

import android.content.SharedPreferences
import com.google.gson.Gson
import com.phpexpert.bringme.dtos.AuthDtoData
import com.phpexpert.bringme.dtos.AuthDtoMain
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.LoginDetailsDto
import javax.inject.Inject

class SharedPrefrenceManager @Inject constructor(var sharedPreferences: SharedPreferences) {

    fun savePrefrence(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }


    fun getPreference(key: String): String? {
        return sharedPreferences.getString(key, "")
    }

    fun saveProfile(
            profile: LoginDetailsDto?
    ) {
        val gson = Gson()
        val regString = gson.toJson(profile)
        savePrefrence("USER_PROFILE", regString)
    }

    fun saveAuthData(
            profile: AuthDtoData?
    ) {
        val gson = Gson()
        val regString = gson.toJson(profile)
        savePrefrence("AUTH_DATA", regString)
    }

    fun saveLanguageData(
            profile: LanguageDtoData?
    ) {
        val gson = Gson()
        val regString = gson.toJson(profile)
        savePrefrence("LANGUAGE_DATA", regString)
    }

    fun deletePrefrence(key: String?) {
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.remove(key)
        editor.apply()
    }

    fun getProfile(): LoginDetailsDto {
        val g = Gson()
        val regStr: String = getPreference("USER_PROFILE")!!
        return g.fromJson(
                regStr,
                LoginDetailsDto::class.java
        )
    }

    fun getAuthData(): AuthDtoData? {
        val g = Gson()
        val regStr: String = getPreference("AUTH_DATA")!!
        return g.fromJson(
                regStr,
                AuthDtoData::class.java
        )
    }

    fun getLanguageData(): LanguageDtoData {
        val g = Gson()
        val regStr: String = getPreference("LANGUAGE_DATA")!!
        return g.fromJson(
                regStr,
                LanguageDtoData::class.java
        )
    }

    fun getLoginId(): String {
        val g = Gson()
        val regStr: String = getPreference("USER_PROFILE")!!
        return g.fromJson(
                regStr,
                LoginDetailsDto::class.java
        ).LoginId!!
    }

    /* public static void saveRegistration(Context ctx, Registration reg) {
        Gson gson = new Gson();
        String regString = gson.toJson(reg);
        savePrefrence(ctx, REGISTRATION_VALUES, regString);
    }

    public static Registration getRegistration(Context ctx) {
        Gson g = new Gson();
        String regStr = getPrefrence(ctx, REGISTRATION_VALUES);
        return g.fromJson(regStr, Registration.class);
    }

    public static void saveProfile(Context ctx, LoginVerifyResponse profile) {
        Gson gson = new Gson();
        String regString = gson.toJson(profile);
        savePrefrence(ctx, USER_PROFILE, regString);
    }

    public static LoginVerifyResponse getProfile(Context ctx) {
        Gson g = new Gson();
        String regStr = getPrefrence(ctx, USER_PROFILE);
        return g.fromJson(regStr, LoginVerifyResponse.class);
    }*/
    fun clearData() {
        val et: SharedPreferences.Editor = sharedPreferences.edit()
        et.clear()
        et.apply()
    }
}