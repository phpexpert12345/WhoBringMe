package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.LatestJobDtoMain
import com.phpexpert.bringme.dtos.WriteReviewJobDtoMain
import com.phpexpert.bringme.repositories.JobHistoryRepo
import com.phpexpert.bringme.retro.JobHistoryRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobHistoryModel : ViewModel() {

    private var latestJobData = MutableLiveData<LatestJobDtoMain>()
    private var writeReviewData = MutableLiveData<WriteReviewJobDtoMain>()

    fun getLatestJobData(map: Map<String, String>):LiveData<LatestJobDtoMain>{
        latestJobData = JobHistoryRepo().getLatestJobData(map)
        return latestJobData
    }

    fun getWriteReviewJobData(map: Map<String, String>):LiveData<WriteReviewJobDtoMain>{
        writeReviewData = JobHistoryRepo().getWriteJobData(map)
        return writeReviewData
    }
}