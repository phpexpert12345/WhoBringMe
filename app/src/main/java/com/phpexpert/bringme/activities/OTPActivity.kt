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
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityOTPBinding
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.dtos.PostDataOtp
import com.phpexpert.bringme.models.RegistrationModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS

@Suppress("DEPRECATION")
class OTPActivity : BaseActivity() {

    private lateinit var otpActivity: ActivityOTPBinding
    private lateinit var postDataOtp: PostDataOtp

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otpActivity = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)

        postDataOtp = intent.getSerializableExtra("postDataModel") as PostDataOtp

        otpActivity.btnVerify.setOnClickListener {
            if (isOnline()) {
                otpActivity.btnVerify.startAnimation()
                setObserver()
            } else {
                Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show()
            }
        }
        timerRestriction()
        otpActivity.headerText.text = "${resources.getString(R.string.please_wait_we_will_auto_verify_nthe_otp_sent_to)} ${postDataOtp.accountPhoneCode + postDataOtp.accountMobile}"

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
        @SuppressLint("SetTextI18n")
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
                otpActivity.timeText.text = "${resources.getString(R.string.auto_verifying_your_otp_in_00_12)} in (00:${counter[0]})"
                counter[0]--
            }

            override fun onFinish() { //                textView.setText("FINISH!!");
                otpActivity.timeText.visibility = View.GONE
                otpActivity.resendLayout.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun setObserver() {
        val viewDataModel = ViewModelProvider(this).get(RegistrationModel::class.java)

        viewDataModel.registerViewModel(this, mapData()).observe(this, {
            if (it.status_code == "0") {
                sharedPrefrenceManager.savePrefrence(CONSTANTS.isLogin, "true")
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finishAffinity()
                /*if (sharedPrefrenceManager.getProfile().account_type == "1") {
                    val intent = Intent(this, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val intent = Intent(this, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
                    startActivity(intent)
                    finish()
                }*/
            } else {
                Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun mapData(): Map<String, String?> {
        val mapDataVal = HashMap<String, String?>()
        mapDataVal["otp_number"] = otpActivity.otpPass1.text.toString() + otpActivity.otpPass2.text.toString() + otpActivity.otpPass3.text.toString() + otpActivity.otpPass4.text.toString()
        mapDataVal["account_first_name"] = base64Encoded(postDataOtp.accountFirstName)
        mapDataVal["account_last_name"] = base64Encoded(postDataOtp.accountLasttName)
        mapDataVal["account_email"] = postDataOtp.accountEmail
        mapDataVal["account_mobile"] = postDataOtp.accountMobile
        mapDataVal["account_mpin_number"] = postDataOtp.mobilePinCode
        mapDataVal["account_type"] = if (postDataOtp.accountType == "client") "1" else "2"
        mapDataVal["account_country"] = base64Encoded(postDataOtp.accountCountry)
        mapDataVal["account_state"] = base64Encoded(postDataOtp.accountState)
        mapDataVal["account_city"] = base64Encoded(postDataOtp.accountCity)
        mapDataVal["account_address"] = base64Encoded(postDataOtp.accountAddress)
        mapDataVal["address_postcode"] = base64Encoded(postDataOtp.addressPostCode)
        mapDataVal["account_lat"] = postDataOtp.accountLat
        mapDataVal["account_long"] = postDataOtp.accountLong
        mapDataVal["account_phone_code"] = postDataOtp.accountPhoneCode
        mapDataVal["referral_code"] = postDataOtp.accountReferralCode
        mapDataVal["device_token_id"] = postDataOtp.deviceTokenId
        mapDataVal["device_platform"] = postDataOtp.devicePlatform
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataVal["lang_code"] = AuthSingleton.authObject.lang_code
        return mapDataVal
    }
}