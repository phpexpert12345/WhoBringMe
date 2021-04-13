package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.databinding.ActivityCreateJobBinding

class CreateJobActivity:BaseActivity() {

    private lateinit var createJobBinding:ActivityCreateJobBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        createJobBinding = DataBindingUtil.setContentView(this, R.layout.activity_create_job)

        createJobBinding.submitButton.setOnClickListener {
            createJobBinding.submitButton.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this, PaymentActivity::class.java))
            }, 1000)
        }
    }

    override fun onPause() {
        super.onPause()
        createJobBinding.submitButton.revertAnimation()
    }
}