package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.EarningDtoMain
import com.phpexpert.bringme.retro.MyEarningRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EarningRepo {
    private var latestJobData = MutableLiveData<EarningDtoMain>()
    fun getEarningData(map: Map<String, String>): MutableLiveData<EarningDtoMain> {
        ServiceGenerator.createService(MyEarningRetro::class.java).getEarningData(map).enqueue(object : Callback<EarningDtoMain> {
            override fun onResponse(call: Call<EarningDtoMain>, response: Response<EarningDtoMain>) {
                if (response.isSuccessful) {
                    latestJobData.postValue(response.body())
                } else {
                    val latestJobDtoMain = EarningDtoMain()
                    latestJobDtoMain.status_code = "11"
                    latestJobDtoMain.status_message = "Latest job history api error"
                    latestJobData.postValue(latestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<EarningDtoMain>, t: Throwable) {
                val latestJobDtoMain = EarningDtoMain()
                latestJobDtoMain.status_code = "11"
                latestJobDtoMain.status_message = "Latest job history api error"
                latestJobData.postValue(latestJobDtoMain)
            }

        })
        return latestJobData
    }
}