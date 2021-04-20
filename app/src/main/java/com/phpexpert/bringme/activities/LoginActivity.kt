package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityLoginBinding
import com.phpexpert.bringme.databinding.LayoutForgotPasswordOneBinding
import com.phpexpert.bringme.databinding.LayoutForgotPasswordTwoBinding
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.models.LoginViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.lang.Exception


@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginId: String

    private lateinit var forgotPasswordOneBehavior: BottomSheetBehavior<View>
    private lateinit var forgotPasswordOneBinding: LayoutForgotPasswordOneBinding

    private lateinit var forgotPasswordTwoBehavior: BottomSheetBehavior<View>
    private lateinit var forgotPasswordTwoBinding: LayoutForgotPasswordTwoBinding

    private var passwordVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.parseColor("#00000000")
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initValues() // method to init values

        loginBinding.countyCode.setTypeFace(Typeface.DEFAULT_BOLD)

        setAction()  // method to set all button actions
    }


    //method to init all values
    private fun initValues() {
        forgotPasswordOneBinding = loginBinding.forPasswordOne
        forgotPasswordOneBehavior = BottomSheetBehavior.from(forgotPasswordOneBinding.root)
        forgotPasswordOneBehavior.isDraggable = false
        forgotPasswordOneBehavior.peekHeight = 0


        forgotPasswordTwoBinding = loginBinding.forgotPasswordTwo
        forgotPasswordTwoBehavior = BottomSheetBehavior.from(forgotPasswordTwoBinding.root)
        forgotPasswordTwoBehavior.isDraggable = false
        forgotPasswordTwoBehavior.peekHeight = 0

        /*forgotPasswordThreeBinding = loginBinding.forgotPasswordThree
        forgotPasswordThreeBehavior = BottomSheetBehavior.from(forgotPasswordThreeBinding.root)
        forgotPasswordThreeBehavior.peekHeight = 0*/

        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }


    //method to set action of all buttons
    @Suppress("DEPRECATION")
    private fun setAction() {

        //login button
        loginBinding.loginButton.setOnClickListener {
            loginBinding.loginButton.startAnimation()
            observerDataLogin()
        }

        //forgot password button in login screen
        loginBinding.forgotPassword.setOnClickListener {
            loginBinding.forPasswordOne.root.isClickable = true
            forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            loginBinding.forgotPasswordView.visibility = View.VISIBLE
        }

        //login screen eye handling button
        loginBinding.eyeIcon.setOnClickListener {
            if (passwordVisible) {
                loginBinding.eyeIcon.setImageResource(R.drawable.eye_close)
                passwordVisible = false
                loginBinding.passwordET.transformationMethod = PasswordTransformationMethod()
            } else {
                loginBinding.eyeIcon.setImageResource(R.drawable.eye_open)
                passwordVisible = true
                loginBinding.passwordET.transformationMethod = null
            }
        }

        //create account button
        loginBinding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationSelectionActivity::class.java))
        }

        //forgot password screen one close button
        forgotPasswordOneBinding.closeIcon.setOnClickListener {
            try {
                forgotPasswordOneBinding.getOtpButton.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        // get otp button in forgot password button
        forgotPasswordOneBinding.getOtpButton.setOnClickListener {
            if (forgotPasswordOneBinding.mobileNumber.text.isEmpty()) {
                Toast.makeText(this, "Enter Mobile number first", Toast.LENGTH_LONG).show()
            } else {
                forgotPasswordOneBinding.getOtpButton.startAnimation()
                observeForgotPasswordOtpSendData()
            }
        }

        //forgot password screen two close button
        forgotPasswordTwoBinding.closeIcon.setOnClickListener {
            try {
                forgotPasswordTwoBinding.continueButton.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            forgotPasswordTwoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        //continue button in forgot password two screen
        forgotPasswordTwoBinding.continueButton.setOnClickListener {
            when {
                forgotPasswordTwoBinding.otpNumberET.text.isEmpty() -> {
                    Toast.makeText(this, "Otp not enter", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoBinding.newPasswordET.text.isEmpty() -> {
                    Toast.makeText(this, "Enter new password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoBinding.newPasswordET.text.length != 6 -> {
                    Toast.makeText(this, "Please enter 6 digit password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoBinding.confirmPasswordET.text.isEmpty() -> {
                    Toast.makeText(this, "Enter Confirm password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoBinding.confirmPasswordET.text.length != 6 -> {
                    Toast.makeText(this, "Please enter 6 digit confirm password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoBinding.newPasswordET.text.toString() == forgotPasswordTwoBinding.confirmPasswordET.text.toString() -> {
                    Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show()
                }
                else -> {
                    forgotPasswordTwoBinding.continueButton.startAnimation()
                    observeForgotPasswordResetData()
                }
            }
        }
    }

    //method to observe login data
    private fun observerDataLogin() {
        if (isOnline()) {
            loginViewModel.getLoginData(this, mapDataLogin()).observe(this, {
                loginBinding.loginButton.revertAnimation()
                if (it.status_code == "0") {
                    sharedPrefrenceManager.savePrefrence(CONSTANTS.isLogin, "true")
                    sharedPrefrenceManager.saveProfile(it.data)
                    if (sharedPrefrenceManager.getProfile().account_type == "1") {
                        val intent = Intent(this, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        val intent = Intent(this, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                } else {
                    Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
                }
            })
        } else {
            loginBinding.loginButton.revertAnimation()
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show()
        }
    }


    //Observe data for forgot password send otp
    @SuppressLint("SetTextI18n")
    private fun observeForgotPasswordOtpSendData() {
        if (isOnline()) {
            loginViewModel.getOtpForgotPasswordSendData(this, mapDataOtpForgotSend()).observe(this, {
                forgotPasswordOneBinding.getOtpButton.revertAnimation()
                if (it.status_code == "0") {
                    loginId = it.data!!.LoginId!!
                    forgotPasswordTwoBinding.mobileNUmberTV.text = forgotPasswordOneBinding.countyCode.textView_selectedCountry.text.toString() + forgotPasswordOneBinding.mobileNumber.text.toString()
                    forgotPasswordTwoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                    forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                } else {
                    Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
                }
            })
        } else {
            forgotPasswordOneBinding.getOtpButton.revertAnimation()
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show()
        }
    }

    //observer for reset password
    @SuppressLint("SetTextI18n")
    private fun observeForgotPasswordResetData() {
        if (isOnline()) {
            loginViewModel.getOtpForgotPasswordReset(this, mapDataResetPassword()).observe(this, {
                forgotPasswordTwoBinding.continueButton.revertAnimation()
                if (it.status_code == "0") {
                    forgotPasswordTwoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
                }
            })
        } else {
            forgotPasswordTwoBinding.continueButton.revertAnimation()
            Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show()
        }
    }

    //map data for login api
    private fun mapDataLogin(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["account_phone_code"] = loginBinding.countyCode.textView_selectedCountry.text.toString()
        mapDataValue["account_mobile"] = loginBinding.mobileNumber.text.toString()
        mapDataValue["account_password"] = loginBinding.passwordET.text.toString()
        mapDataValue["device_id"] = getIMEI(this)!!
        mapDataValue["device_platform"] = "Android"
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataValue["lang_code"] = "en"
        return mapDataValue
    }

    //map data for for forgot password send otp api
    private fun mapDataOtpForgotSend(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["account_mobile"] = forgotPasswordOneBinding.mobileNumber.text.toString()
        mapDataValue["account_phone_code"] = forgotPasswordOneBinding.countyCode.textView_selectedCountry.text.toString()
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        return mapDataValue
    }

    //map data for for forgot password send otp api
    private fun mapDataResetPassword(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["new_password"] = forgotPasswordTwoBinding.newPasswordET.text.toString()
        mapDataValue["confirm_password"] = forgotPasswordTwoBinding.confirmPasswordET.text.toString()
        mapDataValue["LoginId"] = loginId
        mapDataValue["Mobile_OTP"] = forgotPasswordTwoBinding.otpNumberET.text.toString()
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        return mapDataValue
    }

    override fun onResume() {
        super.onResume()
        val w: Window = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

    }

    override fun onPause() {
        super.onPause()
        forgotPasswordOneBinding.getOtpButton.revertAnimation()

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}
