package com.phpexpert.bringme.repositories

import androidx.lifecycle.MutableLiveData
import com.phpexpert.bringme.dtos.DeliveryLatestJobDtoMain
import com.phpexpert.bringme.dtos.OrderAcceptMainDto
import com.phpexpert.bringme.dtos.OrderDeclineMainDto
import com.phpexpert.bringme.dtos.OrderFinishMainDto
import com.phpexpert.bringme.retro.DeliveryLatestJob
import com.phpexpert.bringme.retro.ServiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LatestJobDeliveryRepo {

    private var latestJobData = MutableLiveData<DeliveryLatestJobDtoMain>()
    private var orderAcceptData = MutableLiveData<OrderAcceptMainDto>()
    private var orderDeclineData = MutableLiveData<OrderDeclineMainDto>()
    private var orderFinishData = MutableLiveData<OrderFinishMainDto>()

    fun getLatestJobDeliveryData(map: Map<String, String>): MutableLiveData<DeliveryLatestJobDtoMain> {
        ServiceGenerator.createService(DeliveryLatestJob::class.java).getLatestJobDelivery(map).enqueue(object : Callback<DeliveryLatestJobDtoMain> {
            override fun onResponse(call: Call<DeliveryLatestJobDtoMain>, response: Response<DeliveryLatestJobDtoMain>) {
                if (response.isSuccessful) {
                    latestJobData.postValue(response.body())
                } else {
                    val deliveryLatestJobDtoMain = DeliveryLatestJobDtoMain()
                    deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                    deliveryLatestJobDtoMain.status_code = "1"
                    latestJobData.postValue(deliveryLatestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<DeliveryLatestJobDtoMain>, t: Throwable) {
                val deliveryLatestJobDtoMain = DeliveryLatestJobDtoMain()
                deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                deliveryLatestJobDtoMain.status_code = "1"
                latestJobData.postValue(deliveryLatestJobDtoMain)
            }

        })
        return latestJobData
    }

    fun orderAcceptData(map:Map<String, String>):MutableLiveData<OrderAcceptMainDto>{
        ServiceGenerator.createService(DeliveryLatestJob::class.java).orderAcceptData(map).enqueue(object :Callback<OrderAcceptMainDto>{
            override fun onResponse(call: Call<OrderAcceptMainDto>, response: Response<OrderAcceptMainDto>) {
                if (response.isSuccessful){
                    orderAcceptData.postValue(response.body())
                }else{
                    val deliveryLatestJobDtoMain = OrderAcceptMainDto()
                    deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                    deliveryLatestJobDtoMain.status_code = "1"
                    orderAcceptData.postValue(deliveryLatestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<OrderAcceptMainDto>, t: Throwable) {
                val deliveryLatestJobDtoMain = OrderAcceptMainDto()
                deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                deliveryLatestJobDtoMain.status_code = "1"
                orderAcceptData.postValue(deliveryLatestJobDtoMain)
            }

        })
        return orderAcceptData
    }

    fun orderDeclineData(map:Map<String, String>):MutableLiveData<OrderDeclineMainDto>{
        ServiceGenerator.createService(DeliveryLatestJob::class.java).orderDeclineData(map).enqueue(object :Callback<OrderDeclineMainDto>{
            override fun onResponse(call: Call<OrderDeclineMainDto>, response: Response<OrderDeclineMainDto>) {
                if (response.isSuccessful){
                    orderDeclineData.postValue(response.body())
                }else{
                    val deliveryLatestJobDtoMain = OrderDeclineMainDto()
                    deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                    deliveryLatestJobDtoMain.status_code = "1"
                    orderDeclineData.postValue(deliveryLatestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<OrderDeclineMainDto>, t: Throwable) {
                val deliveryLatestJobDtoMain = OrderDeclineMainDto()
                deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                deliveryLatestJobDtoMain.status_code = "1"
                orderDeclineData.postValue(deliveryLatestJobDtoMain)
            }

        })
        return orderDeclineData
    }

    fun orderFinishData(map:Map<String, String>):MutableLiveData<OrderFinishMainDto>{
        ServiceGenerator.createService(DeliveryLatestJob::class.java).orderCompleteData(map).enqueue(object :Callback<OrderFinishMainDto>{
            override fun onResponse(call: Call<OrderFinishMainDto>, response: Response<OrderFinishMainDto>) {
                if (response.isSuccessful){
                    orderFinishData.postValue(response.body())
                }else{
                    val deliveryLatestJobDtoMain = OrderFinishMainDto()
                    deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                    deliveryLatestJobDtoMain.status_code = "1"
                    orderFinishData.postValue(deliveryLatestJobDtoMain)
                }
            }

            override fun onFailure(call: Call<OrderFinishMainDto>, t: Throwable) {
                val deliveryLatestJobDtoMain = OrderFinishMainDto()
                deliveryLatestJobDtoMain.status_message = "Latest Job Api Error"
                deliveryLatestJobDtoMain.status_code = "1"
                orderFinishData.postValue(deliveryLatestJobDtoMain)
            }

        })
        return orderFinishData
    }
}