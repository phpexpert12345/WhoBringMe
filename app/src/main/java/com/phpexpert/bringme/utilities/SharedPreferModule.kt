package com.phpexpert.bringme.utilities

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import javax.inject.Singleton


@Module
class SharedPreferModule(private var ctx: Context) {

    @Provides
    @Singleton
    fun provideSharedPreference():SharedPreferences{
        return ctx.getSharedPreferences("whobringsme", Context.MODE_PRIVATE)
    }
}
