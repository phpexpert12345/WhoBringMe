package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityRegistrationBinding
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class RegistrationActivity : BaseActivity() {
    private lateinit var registrationActivity: ActivityRegistrationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        registrationActivity = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        setActions()
        setValues()
    }

    private fun setActions() {
        registrationActivity.editData.setOnClickListener {
            finish()
        }

        registrationActivity.btnSubmit.setOnClickListener {
            if (registrationActivity.mobileNumberEditText.text!!.toString() != "") {
                registrationActivity.btnSubmit.startAnimation()
                Handler().postDelayed({
                    val v = Intent(this@RegistrationActivity, OTPActivity::class.java)
                    val mobileNumber = registrationActivity.searchCountyCountry.textView_selectedCountry.text.toString() + registrationActivity.mobileNumberEditText.text.toString()
                    v.putExtra("mobileNumber", mobileNumber)
                    startActivity(v)
                }, 1000)
            } else {
                Toast.makeText(this, "Enter mobile number first", Toast.LENGTH_LONG).show()
            }
        }

        registrationActivity.backArrow.setOnClickListener {
            finish()
        }

        registrationActivity.textData.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(p0: View?, p1: Boolean) {
                if (p1) {
                    registrationActivity.textData.hint = "Password"
                } else {
                    if (registrationActivity.digitPin.text!!.isEmpty())
                        registrationActivity.textData.hint = "6 digit mPin Number"
                }
            }

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