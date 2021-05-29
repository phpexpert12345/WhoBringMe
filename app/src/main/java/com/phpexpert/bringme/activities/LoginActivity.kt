package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityLoginBinding
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.LoginViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.lang.Exception


@Suppress("DEPRECATION")
class LoginActivity : BaseActivity(), AuthInterface {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginId: String
    private lateinit var tokenId: String

    private lateinit var forgotPasswordOneDialog: BottomSheetDialog
    private lateinit var forgotPasswordTwoDialog: BottomSheetDialog

    private var passwordVisible: Boolean = false
    private var passwordNewVisible: Boolean = false
    private var passwordConfirmVisible: Boolean = false
    private var apiName: String = ""
    private lateinit var countDownTimer: CountDownTimer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        loginBinding.languageModel = sharedPrefrenceManager.getLanguageData()

        initValues() // method to init values
        setForgotPasswordOne()
        setForgotPasswordTwo()
        setAction()  // method to set all button actions
    }

    //method to init all values
    @SuppressLint("CutPasteId")
    private fun initValues() {
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
    }

    private fun setForgotPasswordTwo() {
        forgotPasswordTwoDialog = BottomSheetDialog(this, R.style.SheetDialog)
        forgotPasswordTwoDialog.setContentView(R.layout.layout_forgot_password_two)
        forgotPasswordTwoDialog.setCancelable(false)
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.otpVerificationText)?.text = sharedPrefrenceManager.getLanguageData().otp_verification
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.weSendOtp)?.text = sharedPrefrenceManager.getLanguageData().we_ve_sent_an_otp_to
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.weSendOtp)?.text = sharedPrefrenceManager.getLanguageData().we_ve_sent_an_otp_to
        forgotPasswordTwoDialog.findViewById<TextInputLayout>(R.id.otpData)?.hint = sharedPrefrenceManager.getLanguageData().enter_otp
        forgotPasswordTwoDialog.findViewById<TextInputLayout>(R.id.newPassword)?.hint = sharedPrefrenceManager.getLanguageData().enter_new_password
        forgotPasswordTwoDialog.findViewById<TextInputLayout>(R.id.confirmPassword)?.hint = sharedPrefrenceManager.getLanguageData().enter_confirm_password
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.didNotReceive)?.text = sharedPrefrenceManager.getLanguageData().did_not_receive_the_otp
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.resendText)?.text = sharedPrefrenceManager.getLanguageData().resend_otp
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.waitForOtp)?.text = sharedPrefrenceManager.getLanguageData().waiting_for_otp
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.oneTimePassword)?.text = sharedPrefrenceManager.getLanguageData().one_time_password
        forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)?.text = sharedPrefrenceManager.getLanguageData().submit

        forgotPasswordTwoDialog.findViewById<ImageView>(R.id.newPasswordEye)!!.setOnClickListener {
            if (passwordNewVisible) {
                forgotPasswordTwoDialog.findViewById<ImageView>(R.id.newPasswordEye)!!.setImageResource(R.drawable.eye_close)
                passwordNewVisible = false
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.transformationMethod = PasswordTransformationMethod()
            } else {
                forgotPasswordTwoDialog.findViewById<ImageView>(R.id.newPasswordEye)!!.setImageResource(R.drawable.eye_open)
                passwordNewVisible = true
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.transformationMethod = null
            }
            forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)?.setSelection(forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)?.text.toString().trim().length)
        }

        forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isEmpty()) {
                    forgotPasswordTwoDialog.findViewById<ImageView>(R.id.newPasswordEye)!!.visibility = View.GONE
                } else {
                    forgotPasswordTwoDialog.findViewById<ImageView>(R.id.newPasswordEye)!!.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isEmpty()) {
                    forgotPasswordTwoDialog.findViewById<ImageView>(R.id.confirmPasswordEye)!!.visibility = View.GONE
                } else {
                    forgotPasswordTwoDialog.findViewById<ImageView>(R.id.confirmPasswordEye)!!.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        forgotPasswordTwoDialog.findViewById<ImageView>(R.id.confirmPasswordEye)!!.setOnClickListener {
            if (passwordConfirmVisible) {
                forgotPasswordTwoDialog.findViewById<ImageView>(R.id.confirmPasswordEye)!!.setImageResource(R.drawable.eye_close)
                passwordConfirmVisible = false
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.transformationMethod = PasswordTransformationMethod()
            } else {
                forgotPasswordTwoDialog.findViewById<ImageView>(R.id.confirmPasswordEye)!!.setImageResource(R.drawable.eye_open)
                passwordConfirmVisible = true
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.transformationMethod = null
            }
            forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)?.setSelection(forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)?.text.toString().trim().length)
        }
    }

    @SuppressLint("CutPasteId")
    private fun setForgotPasswordOne() {
        forgotPasswordOneDialog = BottomSheetDialog(this, R.style.SheetDialog)
        forgotPasswordOneDialog.setContentView(R.layout.layout_forgot_password_one)
        forgotPasswordOneDialog.setCancelable(false)
        forgotPasswordOneDialog.findViewById<TextView>(R.id.forgotPasswordText)?.text = sharedPrefrenceManager.getLanguageData().forgot_password_heading
        forgotPasswordOneDialog.findViewById<TextView>(R.id.weWillSendText)?.text = sharedPrefrenceManager.getLanguageData().we_will_send_you_an
        forgotPasswordOneDialog.findViewById<TextView>(R.id.oneTimePassword)?.text = sharedPrefrenceManager.getLanguageData().one_time_password
        forgotPasswordOneDialog.findViewById<TextView>(R.id.onThisMobile)?.text = sharedPrefrenceManager.getLanguageData().on_this_mobile_number

        forgotPasswordOneDialog.findViewById<TextInputLayout>(R.id.mobileNumberInputText)?.hint = sharedPrefrenceManager.getLanguageData().enter_mobile_number
        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)?.text = sharedPrefrenceManager.getLanguageData().get_otp
        forgotPasswordOneDialog.findViewById<TextInputEditText>(R.id.mobileNumber)?.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._19sdp), 0, 0)
                forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)?.layoutParams = lp
            } else {
                if (forgotPasswordOneDialog.findViewById<TextInputEditText>(R.id.mobileNumber)?.text!!.isEmpty()) {
                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp), 0, 0)
                    forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)?.layoutParams = lp
                }
            }
        }

        forgotPasswordOneDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            try {
                forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)?.clearFocus()
            forgotPasswordOneDialog.dismiss()

            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.setOnClickListener {
            when {
                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_register_mobile_text
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString().length !in 10..14 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                else -> {
                    forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.startAnimation()
                    observeForgotPasswordOtpSendData()
                }
            }
        }
    }

    @SuppressLint("CutPasteId")
    @Suppress("DEPRECATION")
    private fun setAction() {
        //login button
        loginBinding.loginButton.setOnClickListener {
            when {
                loginBinding.mobileNumberEditText.text!!.length !in 10..14 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                loginBinding.digitPin.text!!.length < 6 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().password_must_contain_6_digits
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                else -> {
                    loginBinding.loginButton.startAnimation()
                    apiName = "login"
                    observerDataLogin()
                }
            }
        }

        //forgot password button in login screen
        loginBinding.forgotPassword.setOnClickListener {
            loginBinding.forgotPasswordView.visibility = View.VISIBLE
            this.hideKeyboard()
            loginBinding.digitPin.clearFocus()
            loginBinding.mobileNumberEditText.clearFocus()
            forgotPasswordOneDialog.show()
        }

        loginBinding.passwordEye.setOnClickListener {

            if (passwordVisible) {
                loginBinding.passwordEye.setImageResource(R.drawable.eye_close)
                passwordVisible = false
                loginBinding.digitPin.transformationMethod = PasswordTransformationMethod()
                loginBinding.digitPin.setSelection(loginBinding.digitPin.text.toString().length)
            } else {
                loginBinding.passwordEye.setImageResource(R.drawable.eye_open)
                passwordVisible = true
                loginBinding.digitPin.transformationMethod = null
                loginBinding.digitPin.setSelection(loginBinding.digitPin.text.toString().length)
            }
        }

        loginBinding.mobileNumberEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._19sdp), 0, 0)
                loginBinding.searchCountyCountry.layoutParams = lp
            } else {
                if (loginBinding.mobileNumberEditText.text!!.isEmpty()) {
                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp), 0, 0)
                    loginBinding.searchCountyCountry.layoutParams = lp
                }

            }
        }

        loginBinding.digitPin.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0!!.isEmpty()) {
                    loginBinding.passwordEye.visibility = View.GONE
                } else {
                    loginBinding.passwordEye.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        loginBinding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationSelectionActivity::class.java))
        }

        //forgot password screen two close button
        forgotPasswordTwoDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            try {
                forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
                this.hideKeyboard()

            } catch (e: Exception) {
                e.printStackTrace()
            }
            forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)?.clearFocus()
            forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.otpNumberET)?.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.otpNumberET)?.clearFocus()
            forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.newPasswordET)?.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.newPasswordET)?.clearFocus()
            forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.confirmPasswordET)?.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.confirmPasswordET)?.clearFocus()
            this.hideKeyboard()
            forgotPasswordTwoDialog.dismiss()
            try {
                countDownTimer.cancel()
            } catch (e: Exception) {
            }
            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        //continue button in forgot password two screen
        forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.setOnClickListener {
            when {
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.otpNumberET)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().otp_not_enter
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_new_password_error
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.length != 6 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_6_digit_password
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_confirm_password_error
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.length != 6 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData()._enter_6_digit_confirm_password
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.toString() != forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.toString() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().password_not_match
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                else -> {
//                    forgotPasswordOneBinding.mobileNumber.text = Editable.Factory.getInstance().newEditable("")
                    forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.startAnimation()
                    observeForgotPasswordResetData()
                }
            }
        }

        forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.resendOtpLayout)?.setOnClickListener {
            resendOtpObserver()
        }
    }

    //method to observe login data
    private fun observerDataLogin() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                loginViewModel.getLoginData(mapDataLogin()).observe(this, {

                    when (it.status_code) {
                        "0" -> {
                            loginBinding.loginButton.revertAnimation()
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogHeadingText.text = resources.getString(R.string.app_name)
                            sharedPrefrenceManager.savePrefrence(CONSTANTS.isLogin, "true")
                            sharedPrefrenceManager.saveProfile(it.data)
                            val intent: Intent?
                            if (sharedPrefrenceManager.getProfile().account_type == "1") {
//                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                intent = Intent(this, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                                bottomSheetDialog.dismiss()
                                startActivity(intent)
                                finishAffinity()
//                            }

                            } else {
//                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                intent = Intent(this, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
                                bottomSheetDialog.dismiss()
                                startActivity(intent)
                                finishAffinity()
//                            }
                            }
                        }
                        "2" -> {
                            apiName = "login"
                            hitAuthApi(this)
                        }
                        else -> {
                            loginBinding.loginButton.revertAnimation()
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    }
                })
            } else {
                apiName = "login"
                hitAuthApi(this)
            }
        } else {
            loginBinding.loginButton.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    //Observe data for forgot password send otp
    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun observeForgotPasswordOtpSendData() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "")
                loginViewModel.getOtpForgotPasswordSendData(mapDataOtpForgotSend()).observe(this, {

                    when (it.status_code) {
                        "0" -> {
                            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogHeadingText.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                                bottomSheetDialog.dismiss()
                                loginId = it.data!!.LoginId!!
                                tokenId = it.data!!.Token_ID!!
                                forgotPasswordTwoDialog.findViewById<TextView>(R.id.mobileNUmberTV)!!.text = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString() + forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
                                timerRestriction()
                                forgotPasswordTwoDialog.show()
                                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)?.clearFocus()
                                forgotPasswordOneDialog.dismiss()
                            }
                        }
                        "2" -> {
                            apiName = "forgotPasswordOne"
                            hitAuthApi(this)
                        }
                        else -> {
                            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
                                bottomSheetDialog.dismiss()
                            }
                        }
                    }
                    bottomSheetDialog.show()
                })
            else {
                apiName = "forgotPasswordOne"
                hitAuthApi(this)
            }
        } else {
            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    //observer for reset password
    private fun observeForgotPasswordResetData() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "")
                loginViewModel.getOtpForgotPasswordReset(mapDataResetPassword()).observe(this, {

                    when (it.status_code) {
                        "0" -> {
                            forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
                            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogHeadingText.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                                loginBinding.forgotPasswordView.visibility = View.GONE
                                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
                                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)?.clearFocus()
                                forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.otpNumberET)?.text = Editable.Factory.getInstance().newEditable("")
                                forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.otpNumberET)?.clearFocus()
                                forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.newPasswordET)?.text = Editable.Factory.getInstance().newEditable("")
                                forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.newPasswordET)?.clearFocus()
                                forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.confirmPasswordET)?.text = Editable.Factory.getInstance().newEditable("")
                                forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.confirmPasswordET)?.clearFocus()
                                this.hideKeyboard()
                                try {
                                    countDownTimer.cancel()
                                } catch (e: Exception) {
                                }
                                forgotPasswordTwoDialog.dismiss()
                            }
                        }
                        "2" -> {
                            apiName = "forgotPasswordTwo"
                            hitAuthApi(this)
                        }
                        else -> {
                            forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
                            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                        }
                    }
                    bottomSheetDialog.show()
                })
            else {
                apiName = "forgotPasswordTwo"
                hitAuthApi(this)
            }
        } else {
            forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    //map data for login api
    private fun mapDataLogin(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["account_phone_code"] = loginBinding.searchCountyCountry.textView_selectedCountry.text.toString()
        mapDataValue["account_mobile"] = loginBinding.mobileNumberEditText.text.toString()
        mapDataValue["account_password"] = loginBinding.digitPin.text.toString()
        mapDataValue["device_id"] = sharedPrefrenceManager.getPreference(CONSTANTS.fireBaseId)!!
        mapDataValue["device_platform"] = "Android"
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataValue
    }


    //map data for for forgot password send otp api
    private fun mapDataOtpForgotSend(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["account_mobile"] = forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataValue["account_phone_code"] = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataValue
    }

    //map data for for forgot password send otp api
    private fun mapDataResetPassword(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["new_password"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.toString()
        mapDataValue["confirm_password"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.toString()
        mapDataValue["LoginId"] = loginId
        mapDataValue["Mobile_OTP"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.otpNumberET)!!.text.toString()
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataValue
    }

    private fun resendData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        mapDataVal["account_mobile"] = forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataVal["account_phone_code"] = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()

        return mapDataVal
    }

    private fun resendOtpObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key == "") {
                loginViewModel.getLoginDetailsData(resendData()).observe(this, {
                    when (it.status_message) {
                        "2" -> {
                            apiName = "forgotPasswordResend"
                            hitAuthApi(this)
                        }
                        else -> {
                            when (it.status_code) {
                                "11" -> bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                                else -> bottomSheetDialogMessageText.text = it.status_message
                            }
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            if (it.status_code == "0") {
                                bottomSheetDialogHeadingText.visibility = View.GONE
                            } else {
                                bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            }
                            bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                                if (it.status_code == "0") {
                                    forgotPasswordTwoDialog.findViewById<TextView>(R.id.waitForOtp)?.visibility = View.VISIBLE
                                    forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.waitLayout)?.visibility = View.GONE
                                    forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.resendOtpLayout)?.visibility = View.GONE
                                    forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.otpNumberET)?.text = Editable.Factory.getInstance().newEditable("")
                                    forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.otpNumberET)?.clearFocus()
                                    forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.newPasswordET)?.text = Editable.Factory.getInstance().newEditable("")
                                    forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.newPasswordET)?.clearFocus()
                                    forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.confirmPasswordET)?.text = Editable.Factory.getInstance().newEditable("")
                                    forgotPasswordTwoDialog.findViewById<TextInputEditText>(R.id.confirmPasswordET)?.clearFocus()
                                    timerRestriction()
                                }
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    }
                })
            } else {
                apiName = "forgotPasswordResend"
                hitAuthApi(this)
            }
        } else {
            forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun timerRestriction() {
        forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.waitLayout)?.visibility = View.VISIBLE
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.waitForOtp)?.visibility = View.VISIBLE
        forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.resendOtpLayout)?.visibility = View.GONE
        val counter = intArrayOf(30)
        countDownTimer = object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                forgotPasswordTwoDialog.findViewById<TextView>(R.id.timeData)?.text = "(00:${counter[0]})"
                counter[0]--
            }

            override fun onFinish() { //                textView.setText("FINISH!!");
                forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.waitLayout)?.visibility = View.GONE
                forgotPasswordTwoDialog.findViewById<TextView>(R.id.waitForOtp)?.visibility = View.GONE
                forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.resendOtpLayout)?.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onPause() {
        super.onPause()
        this.hideKeyboard()
        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
    }

    private fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            when (apiName) {
                "login" -> {
                    observerDataLogin()
                }
                "forgotPasswordOne" -> observeForgotPasswordOtpSendData()
                "forgotPasswordTwo" -> observeForgotPasswordResetData()
                "forgotPasswordResend" -> resendOtpObserver()
            }
        } else {
            bottomSheetDialogMessageText.text = message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

}
