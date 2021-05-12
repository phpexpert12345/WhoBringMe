package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.MyJobDtoMain
import com.phpexpert.bringme.retro.MyJobRetro
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyJobRepo {

    private var myJobData:MutableLiveData<MyJobDtoMain> = MutableLiveData()

    fun getMyJobData(map: Map<String, String>):MutableLiveData<MyJobDtoMain>{
        ServiceGenerator.createService(MyJobRetro::class.java).getMyJobData(map).enqueue(object : Callback<MyJobDtoMain>{
            override fun onResponse(call: Call<MyJobDtoMain>, response: Response<MyJobDtoMain>) {
                if (response.isSuccessful){

                }else{
                    val postJobDataMain = MyJobDtoMain()
                    postJobDataMain.status_message = "My Job Api Error"
                    postJobDataMain.status_code = "1"
                    myJobData.postValue(postJobDataMain)
                }
            }

            override fun onFailure(call: Call<MyJobDtoMain>, t: Throwable) {
                val postJobDataMain = MyJobDtoMain()
                postJobDataMain.status_message = "My Job Api Error"
                postJobDataMain.status_code = "1"
                myJobData.postValue(postJobDataMain)
            }

        })
        return myJobData
    }
}