package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.EmployeeJobHistoryDtoMain
import com.phpexpert.bringme.dtos.LatestJobDtoMain
import com.phpexpert.bringme.dtos.WriteReviewJobDtoMain
import com.phpexpert.bringme.retro.JobHistoryRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobHistoryRepo {
    private var latestJobData = MutableLiveData<LatestJobDtoMain>()
    private var writeReviewData = MutableLiveData<WriteReviewJobDtoMain>()
    private var getJobHistory = MutableLiveData<EmployeeJobHistoryDtoMain>()

    fun getLatestJobData(map: Map<String, String>): MutableLiveData<LatestJobDtoMain> {
        ServiceGenerator.createService(JobHistoryRetro::class.java).latestJobHistory(map).enqueue(object : Callback<LatestJobDtoMain> {
            override fun onResponse(call: Call<LatestJobDtoMain>, response: Response<LatestJobDtoMain>) {
                if (response.isSuccessful) {
                    latestJobData.postValue(response.body())
                } else {
                    val latestJobDtoMain = LatestJobDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Latest job history api error"
                    latestJobData.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<LatestJobDtoMain>, t: Throwable) {
                val latestJobDtoMain = LatestJobDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Latest job history api error"
                latestJobData.postValue(latestJobDtoMain)
            }

        })
        return latestJobData
    }

    fun getWriteJobData(map: Map<String, String>): MutableLiveData<WriteReviewJobDtoMain> {
        ServiceGenerator.createService(JobHistoryRetro::class.java).writeReviewData(map).enqueue(object : Callback<WriteReviewJobDtoMain> {
            override fun onResponse(call: Call<WriteReviewJobDtoMain>, response: Response<WriteReviewJobDtoMain>) {
                if (response.isSuccessful) {
                    writeReviewData.postValue(response.body())
                } else {
                    val latestJobDtoMain = WriteReviewJobDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Latest job history api error"
                    writeReviewData.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<WriteReviewJobDtoMain>, t: Throwable) {
                val latestJobDtoMain = WriteReviewJobDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Latest job history api error"
                writeReviewData.postValue(latestJobDtoMain)
            }

        })
        return writeReviewData
    }

    fun getJobHistoryData(map: Map<String, String>): MutableLiveData<EmployeeJobHistoryDtoMain> {
        ServiceGenerator.createService(JobHistoryRetro::class.java).getJobHistory(map).enqueue(object : Callback<EmployeeJobHistoryDtoMain> {
            override fun onResponse(call: Call<EmployeeJobHistoryDtoMain>, response: Response<EmployeeJobHistoryDtoMain>) {
                if (response.isSuccessful) {
                    getJobHistory.postValue(response.body())
                } else {
                    val latestJobDtoMain = EmployeeJobHistoryDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Job history api error"
                    getJobHistory.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<EmployeeJobHistoryDtoMain>, t: Throwable) {
                val latestJobDtoMain = EmployeeJobHistoryDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Job history api error"
                getJobHistory.postValue(latestJobDtoMain)
            }

        })
        return getJobHistory
    }
}