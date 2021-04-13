package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityRegistrationBinding

@Suppress("DEPRECATION")
class RegistrationActivity : AppCompatActivity() {
    private lateinit var registrationActivity: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.white)
        registrationActivity = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        setActions()
        setValues()
    }

    private fun setActions() {
        registrationActivity.editData.setOnClickListener {
            finish()
        }

        registrationActivity.btnSubmit.setOnClickListener {
            registrationActivity.btnSubmit.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this@RegistrationActivity, OTPActivity::class.java))
            }, 1000)

        }
    }

    override fun onPause() {
        super.onPause()
        registrationActivity.btnSubmit.revertAnimation()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)

    }

    private fun setValues() {
        val selectionString = intent.extras!!.getString("selectionString")

        registrationActivity.selectionString.text = if (selectionString == "client") "Client / Receiver" else "Delivery Employee"



    }
}