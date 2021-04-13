package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityOTPBinding

@Suppress("DEPRECATION")
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
        handleOtpET()
    }


    private fun handleOtpET() {
        otpActivity.otpPass1.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass1))
        otpActivity.otpPass1.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass2))
        otpActivity.otpPass1.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass3))
        otpActivity.otpPass1.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass4))
    }

    inner class GenericTextWatcher(var view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.otpPass1 -> if (text.length == 1) otpActivity.otpPass2.requestFocus()
                R.id.otpPass2 -> if (text.length == 1) otpActivity.otpPass3.requestFocus() else if (text.isEmpty()) otpActivity.otpPass1.requestFocus()
                R.id.otpPass3 -> if (text.length == 1)otpActivity.otpPass4.requestFocus() else if (text.isEmpty()) otpActivity.otpPass2.requestFocus()
                R.id.otpPass4 -> if (text.isEmpty()) otpActivity.otpPass3.requestFocus()
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

    }
}