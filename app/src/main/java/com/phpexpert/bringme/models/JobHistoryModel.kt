package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.EmployeeJobHistoryDtoMain
import com.phpexpert.bringme.dtos.LatestJobDtoMain
import com.phpexpert.bringme.dtos.WriteReviewJobDtoMain
import com.phpexpert.bringme.repositories.JobHistoryRepo

class JobHistoryModel : ViewModel() {

    private var latestJobData = MutableLiveData<LatestJobDtoMain>()
    private var writeReviewData = MutableLiveData<WriteReviewJobDtoMain>()
    private var getJobHistoryData = MutableLiveData<EmployeeJobHistoryDtoMain>()

    fun getLatestJobData(map: Map<String, String>):LiveData<LatestJobDtoMain>{
        latestJobData = JobHistoryRepo().getLatestJobData(map)
        return latestJobData
    }

    fun getWriteReviewJobData(map: Map<String, String>):LiveData<WriteReviewJobDtoMain>{
        writeReviewData = JobHistoryRepo().getWriteJobData(map)
        return writeReviewData
    }

    fun getJobHistoryData(map: Map<String, String>):LiveData<EmployeeJobHistoryDtoMain>{
        getJobHistoryData = JobHistoryRepo().getJobHistoryData(map)
        return getJobHistoryData
    }
}