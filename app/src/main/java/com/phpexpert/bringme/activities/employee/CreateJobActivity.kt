package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityCreateJobBinding
import com.phpexpert.bringme.utilities.BaseActivity

class CreateJobActivity : BaseActivity() {

    private var counting: Int = 10

    private lateinit var createJobBinding: ActivityCreateJobBinding
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

        createJobBinding.closeIcon.setOnClickListener {
            finish()
        }
        createJobBinding.plusIcon.setOnClickListener {
            counting += 10
            createJobBinding.mintsTextView.text = counting.toString()
        }

        createJobBinding.minusIcon.setOnClickListener {
            if (counting != 10) {
                counting -= 10
                createJobBinding.mintsTextView.text = counting.toString()
            }
        }

    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    override fun onPause() {
        super.onPause()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        createJobBinding.submitButton.revertAnimation()
    }
}