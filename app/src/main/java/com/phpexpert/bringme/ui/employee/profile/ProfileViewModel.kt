package com.phpexpert.bringme.ui.employee.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

object ProfileViewModel : ViewModel(){

    var changeModel = MutableLiveData<Boolean>()

    fun getChangeModel():LiveData<Boolean>{
        return changeModel
    }
}