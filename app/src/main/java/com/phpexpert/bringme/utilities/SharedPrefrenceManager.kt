@file:Suppress("SpellCheckingInspection", "unused")

package com.phpexpert.bringme.utilities

import android.content.SharedPreferences
import com.google.gson.Gson
import com.phpexpert.bringme.dtos.AuthDtoData
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


    fun clearData() {
        val et: SharedPreferences.Editor = sharedPreferences.edit()
        et.clear()
        et.apply()
    }
}