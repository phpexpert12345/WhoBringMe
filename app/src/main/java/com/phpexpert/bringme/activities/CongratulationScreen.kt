package com.phpexpert.bringme.activities

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.PaymentLayoutBinding
import com.phpexpert.bringme.databinding.PaymentSuccessfullPageBinding
import com.phpexpert.bringme.utilities.BaseActivity

class CongratulationScreen:BaseActivity() {

    private lateinit var congratulationScreen: PaymentSuccessfullPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        congratulationScreen = DataBindingUtil.setContentView(this, R.layout.payment_successfull_page)
    }


}