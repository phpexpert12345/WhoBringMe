package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityRegistrationBinding

@Suppress("DEPRECATION")
class RegistrationActivity : AppCompatActivity() {
    private lateinit var registrationActivity: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    private fun setValues() {
        val selectionString = intent.extras!!.getString("selectionString")

        registrationActivity.selectionString.text = if (selectionString == "client") "Client / Receiver" else "Delivery Employee"

    }
}