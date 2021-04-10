package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.PaymentLayoutBinding
import com.phpexpert.bringme.utilities.BaseActivity

class PaymentActivity:BaseActivity() {

    private lateinit var paymentActivityBinding:PaymentLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentActivityBinding = DataBindingUtil.setContentView(this, R.layout.payment_layout)
        setActions()
    }

    private fun setActions(){
        paymentActivityBinding.proceedButton.setOnClickListener {
            paymentActivityBinding.proceedButton.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this, CongratulationScreen::class.java))
            }, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        paymentActivityBinding.proceedButton.revertAnimation()
    }
}