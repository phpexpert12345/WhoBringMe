package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.EarningDtoMain
import com.phpexpert.bringme.dtos.LatestJobDtoMain
import com.phpexpert.bringme.repositories.EarningRepo
import com.phpexpert.bringme.repositories.JobHistoryRepo

class EarningViewModel:ViewModel() {
    private var latestJobData = MutableLiveData<EarningDtoMain>()
    fun getLatestJobData(map: Map<String, String>): LiveData<EarningDtoMain> {
        latestJobData = EarningRepo().getEarningData(map)
        return latestJobData
    }
}