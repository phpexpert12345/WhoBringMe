package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.MyJobDtoMain
import com.phpexpert.bringme.repositories.MyJobRepo

class MyJobDataModel:ViewModel() {

    private var myJobData: MutableLiveData<MyJobDtoMain> = MutableLiveData()
    fun getMyJobData(map: Map<String, String>):LiveData<MyJobDtoMain>{
        myJobData = MyJobRepo().getMyJobData(map)
        return myJobData
    }

}