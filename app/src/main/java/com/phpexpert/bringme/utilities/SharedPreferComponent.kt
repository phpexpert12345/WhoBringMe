package com.phpexpert.bringme.utilities

import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [SharedPreferModule::class])
interface SharedPreferComponent {
    fun inject(target: BaseActivity)
}