package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityOTPBinding

class OTPActivity : AppCompatActivity() {

    private lateinit var otpActivity: ActivityOTPBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otpActivity = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        otpActivity.btnVerify.setOnClickListener {
            otpActivity.btnVerify.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this@OTPActivity, DashboardActivity::class.java))
            }, 1000)
        }
    }
}