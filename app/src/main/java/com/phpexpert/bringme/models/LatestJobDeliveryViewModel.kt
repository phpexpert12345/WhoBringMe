package com.phpexpert.bringme.models

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.phpexpert.bringme.dtos.DeliveryLatestJobDtoMain
import com.phpexpert.bringme.dtos.OrderAcceptMainDto
import com.phpexpert.bringme.dtos.OrderDeclineMainDto
import com.phpexpert.bringme.dtos.OrderFinishMainDto
import com.phpexpert.bringme.repositories.LatestJobDeliveryRepo

class LatestJobDeliveryViewModel:ViewModel() {

    private var latestJobData = MutableLiveData<DeliveryLatestJobDtoMain>()
    private var orderAcceptData = MutableLiveData<OrderAcceptMainDto>()
    private var orderDeclineData = MutableLiveData<OrderDeclineMainDto>()
    private var orderFinishData = MutableLiveData<OrderFinishMainDto>()

    fun getLatestJobDeliveryData(map: Map<String, String>): LiveData<DeliveryLatestJobDtoMain> {
        latestJobData = LatestJobDeliveryRepo().getLatestJobDeliveryData(map)
        return latestJobData
    }

    fun orderAcceptData(map: Map<String, String>):LiveData<OrderAcceptMainDto>{
        orderAcceptData = LatestJobDeliveryRepo().orderAcceptData(map)
        return orderAcceptData
    }

    fun orderDeclineData(map: Map<String, String>):LiveData<OrderDeclineMainDto>{
        orderDeclineData = LatestJobDeliveryRepo().orderDeclineData(map)
        return orderDeclineData
    }

    fun orderFinishData(map: Map<String, String>):LiveData<OrderFinishMainDto>{
        orderFinishData = LatestJobDeliveryRepo().orderFinishData(map)
        return orderFinishData
    }
}