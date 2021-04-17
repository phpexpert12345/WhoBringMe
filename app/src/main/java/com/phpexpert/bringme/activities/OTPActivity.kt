package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityOTPBinding

@Suppress("DEPRECATION")
class OTPActivity : AppCompatActivity() {

    private lateinit var otpActivity: ActivityOTPBinding

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otpActivity = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        otpActivity.btnVerify.setOnClickListener {
            otpActivity.btnVerify.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this@OTPActivity, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java))
            }, 1000)
        }
        timerRestriction()
        otpActivity.headerText.text = "${resources.getString(R.string.please_wait_we_will_auto_verify_nthe_otp_sent_to)} ${intent.extras!!.getString("mobileNumber")}"

        otpActivity.backArrow.setOnClickListener {
            finish()
        }
        handleOtpET()
    }


    private fun handleOtpET() {
        otpActivity.otpPass1.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass1))
        otpActivity.otpPass2.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass2))
        otpActivity.otpPass3.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass3))
        otpActivity.otpPass4.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass4))
    }

    inner class GenericTextWatcher(var view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.otpPass1 -> if (text.length == 1) {
                    otpActivity.otpPass2.requestFocus()
                }
                R.id.otpPass2 -> if (text.length == 1) otpActivity.otpPass3.requestFocus() else if (text.isEmpty()) otpActivity.otpPass1.requestFocus()
                R.id.otpPass3 -> if (text.length == 1) otpActivity.otpPass4.requestFocus() else if (text.isEmpty()) otpActivity.otpPass2.requestFocus()
                R.id.otpPass4 -> if (text.isEmpty()) {
                    otpActivity.otpPass3.requestFocus()
                    otpActivity.btnVerify.text = "Verify"
                    otpActivity.btnVerify.setBackgroundResource(R.drawable.button_shape_gray)
                    otpActivity.btnVerify.isFocusableInTouchMode = false
                    otpActivity.btnVerify.isFocusable = false
                } else {
                    otpActivity.btnVerify.text = "Submit"
                    otpActivity.btnVerify.setBackgroundResource(R.drawable.button_rectangle_green)
                    otpActivity.btnVerify.isFocusableInTouchMode = true
                    otpActivity.btnVerify.isFocusable = true
                }
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.white)
    }

    private fun timerRestriction() {
        val counter = intArrayOf(30)
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                otpActivity.timeText.text = "${resources.getString(R.string.auto_verifying_your_otp_in_00_12)} in (00:${counter[0].toString()})"
                counter[0]--
            }

            override fun onFinish() { //                textView.setText("FINISH!!");
                otpActivity.timeText.visibility = View.GONE
                otpActivity.resendLayout.visibility = View.VISIBLE
            }
        }.start()
    }
}