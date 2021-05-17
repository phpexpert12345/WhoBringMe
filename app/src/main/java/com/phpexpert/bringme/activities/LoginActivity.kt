package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityLoginBinding
import com.phpexpert.bringme.models.LoginViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import com.phpexpert.bringme.utilities.SoftInputAssist
import java.lang.Exception


@Suppress("DEPRECATION")
class LoginActivity : BaseActivity() {

    private lateinit var loginBinding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginId: String
    private lateinit var tokenId: String

    private lateinit var forgotPasswordOneDialog: BottomSheetDialog
    private lateinit var forgotPasswordTwoDialog: BottomSheetDialog

    private var passwordVisible: Boolean = false
    private var passwordNewVisible: Boolean = false
    private var passwordConfirmVisible: Boolean = false
    private lateinit var softInputAssist: SoftInputAssist


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        loginBinding.languageModel = sharedPrefrenceManager.getLanguageData()

        initValues() // method to init values
        softInputAssist = SoftInputAssist(this)

        loginBinding.searchCountyCountry.setTypeFace(Typeface.DEFAULT_BOLD)

        setAction()  // method to set all button actions
    }


    //method to init all values
    private fun initValues() {
//        forgotPasswordOneBinding = loginBinding.forPasswordOne
//        forgotPasswordOneBehavior = BottomSheetBehavior.from(forgotPasswordOneBinding.root)
//        forgotPasswordOneBehavior.isDraggable = false
//        forgotPasswordOneBehavior.peekHeight = 0

        forgotPasswordOneDialog = BottomSheetDialog(this, R.style.SheetDialog)
        forgotPasswordOneDialog.setContentView(R.layout.layout_forgot_password_one)
        forgotPasswordOneDialog.findViewById<TextView>(R.id.forgotPasswordText)?.text = sharedPrefrenceManager.getLanguageData().forgot_password
        forgotPasswordOneDialog.findViewById<TextView>(R.id.weWillSendText)?.text = sharedPrefrenceManager.getLanguageData().we_will_send_you_an
        forgotPasswordOneDialog.findViewById<TextView>(R.id.oneTimePassword)?.text = sharedPrefrenceManager.getLanguageData().one_time_password
        forgotPasswordOneDialog.findViewById<TextView>(R.id.onThisMobile)?.text = sharedPrefrenceManager.getLanguageData().on_this_mobile_number
        forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)?.hint = sharedPrefrenceManager.getLanguageData().enter_mobile_number
        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)?.text = sharedPrefrenceManager.getLanguageData().get_otp

        forgotPasswordOneDialog.setCancelable(false)
        forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.setTypeFace(Typeface.DEFAULT_BOLD)

        forgotPasswordTwoDialog = BottomSheetDialog(this, R.style.SheetDialog)
        forgotPasswordTwoDialog.setContentView(R.layout.layout_forgot_password_two)
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.otpVerificationText)?.text = sharedPrefrenceManager.getLanguageData().otp_verification
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.weSendOtp)?.text = sharedPrefrenceManager.getLanguageData().we_ve_sent_an_otp_to
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.weSendOtp)?.text = sharedPrefrenceManager.getLanguageData().we_ve_sent_an_otp_to
        forgotPasswordTwoDialog.findViewById<EditText>(R.id.otpNumberET)?.hint = sharedPrefrenceManager.getLanguageData().enter_otp
        forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)?.hint = sharedPrefrenceManager.getLanguageData().new_password
        forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)?.hint = sharedPrefrenceManager.getLanguageData().confirm_password
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.didNotReceive)?.text = sharedPrefrenceManager.getLanguageData().did_not_receive_the_otp
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.resendText)?.text = sharedPrefrenceManager.getLanguageData().resend_otp
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.waitForOtp)?.text = sharedPrefrenceManager.getLanguageData().waiting_for_otp
        forgotPasswordTwoDialog.findViewById<TextView>(R.id.oneTimePassword)?.text = sharedPrefrenceManager.getLanguageData().one_time_password
        forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)?.text = sharedPrefrenceManager.getLanguageData().save_amp_continue

        forgotPasswordTwoDialog.setCancelable(false)
//        forgotPasswordTwoBinding = loginBinding.forgotPasswordTwo
//        forgotPasswordTwoBehavior = BottomSheetBehavior.from(forgotPasswordTwoBinding.root)
//        forgotPasswordTwoBehavior.isDraggable = false
//        forgotPasswordTwoBehavior.peekHeight = 0

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
            when {
                loginBinding.mobileNumberEditText.text!!.length !in 10..14 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                loginBinding.digitPin.text!!.length < 6 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().password_must_contain_6_digits
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                else -> {
                    loginBinding.loginButton.startAnimation()
                    observerDataLogin()
                }
            }
        }

        //forgot password button in login screen
        loginBinding.forgotPassword.setOnClickListener {
//            loginBinding.forPasswordOne.root.isClickable = true
//            forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            loginBinding.forgotPasswordView.visibility = View.VISIBLE
            forgotPasswordOneDialog.show()
        }

        //login screen eye handling button
        loginBinding.passwordEye.setOnClickListener {
            if (passwordVisible) {
                loginBinding.passwordEye.setImageResource(R.drawable.eye_close)
                passwordVisible = false
                loginBinding.digitPin.transformationMethod = PasswordTransformationMethod()
            } else {
                loginBinding.passwordEye.setImageResource(R.drawable.eye_open)
                passwordVisible = true
                loginBinding.digitPin.transformationMethod = null
            }
        }

        loginBinding.digitPin.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                loginBinding.textData.hint = sharedPrefrenceManager.getLanguageData().password
            } else {
                if (loginBinding.digitPin.text!!.isEmpty())
                    loginBinding.textData.hint = sharedPrefrenceManager.getLanguageData().enter_password
            }
        }

        loginBinding.mobileNumberEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                loginBinding.mobileNumberInputText.hint = ""
                loginBinding.mobileNumberTextHint.visibility = View.VISIBLE
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._19sdp), 0, 0)
                loginBinding.searchCountyCountry.layoutParams = lp
            } else {
                if (loginBinding.mobileNumberEditText.text!!.isEmpty()) {
                    loginBinding.mobileNumberInputText.hint = sharedPrefrenceManager.getLanguageData().enter_mobile_number
                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp), 0, 0)
                    loginBinding.searchCountyCountry.layoutParams = lp
                    loginBinding.mobileNumberTextHint.visibility = View.GONE
                }

            }
        }

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
        }

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
        }


        //create account button
        loginBinding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationSelectionActivity::class.java))
        }

        //forgot password screen one close button
        forgotPasswordOneDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            try {
                forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordOneDialog.dismiss()
            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        // get otp button in forgot password button
        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.setOnClickListener {
            when {
                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_register_mobile_text
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString().length !in 10..14 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
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

        //forgot password screen two close button
        forgotPasswordTwoDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            try {
                forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            forgotPasswordTwoDialog.dismiss()
            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        //continue button in forgot password two screen
        forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.setOnClickListener {
            when {
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.otpNumberET)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().otp_not_enter
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_new_password
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.length != 6 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_6_digit_password
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_confirm_password
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.length != 6 -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData()._enter_6_digit_confirm_password
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.toString() != forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.toString() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().password_not_match
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
            loginViewModel.getLoginData(mapDataLogin()).observe(this, {
                loginBinding.loginButton.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialog.findViewById<ImageView>(R.id.companyLogo)!!.visibility = View.VISIBLE
                    bottomSheetDialog.findViewById<TextView>(R.id.textHeading)!!.text = resources.getString(R.string.app_name)
                    sharedPrefrenceManager.savePrefrence(CONSTANTS.isLogin, "true")
                    sharedPrefrenceManager.saveProfile(it.data)
                    var intent: Intent?
                    if (sharedPrefrenceManager.getProfile().account_type == "1") {
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            bottomSheetDialog.findViewById<ImageView>(R.id.companyLogo)!!.visibility = View.GONE
                            bottomSheetDialog.findViewById<TextView>(R.id.textHeading)!!.text = sharedPrefrenceManager.getLanguageData().alert_text
                            intent = Intent(this, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                            bottomSheetDialog.dismiss()
                            startActivity(intent)
                            finishAffinity()
                        }

                    } else {
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            intent = Intent(this, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
                            bottomSheetDialog.dismiss()
                            startActivity(intent)
                            finishAffinity()
                        }
                    }

                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                }
                bottomSheetDialog.show()
            })
        } else {
            loginBinding.loginButton.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
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
            loginViewModel.getOtpForgotPasswordSendData(mapDataOtpForgotSend()).observe(this, {
                forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        bottomSheetDialog.dismiss()
                        loginId = it.data!!.LoginId!!
                        tokenId = it.data!!.Token_ID!!
                        forgotPasswordTwoDialog.findViewById<TextView>(R.id.mobileNUmberTV)!!.text = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString() + forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
                        forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
                        timerRestriction()
                        forgotPasswordTwoDialog.show()
                        forgotPasswordOneDialog.dismiss()
                    }
                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
                        bottomSheetDialog.dismiss()
                    }
                }
                bottomSheetDialog.show()
            })
        } else {
            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
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
            loginViewModel.getOtpForgotPasswordReset(mapDataResetPassword()).observe(this, {
                forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
                forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        loginBinding.forgotPasswordView.visibility = View.GONE
                        forgotPasswordTwoDialog.dismiss()
                    }
                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                }
                bottomSheetDialog.show()
            })
        } else {
            forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
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
        mapDataValue["device_id"] = getIMEI(this)!!
        mapDataValue["device_platform"] = "Android"
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataValue
    }

    //map data for for forgot password send otp api
    private fun mapDataOtpForgotSend(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["account_mobile"] = forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataValue["account_phone_code"] = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataValue
    }

    //map data for for forgot password send otp api
    private fun mapDataResetPassword(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["new_password"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.toString()
        mapDataValue["confirm_password"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.toString()
        mapDataValue["LoginId"] = loginId
        mapDataValue["Mobile_OTP"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.otpNumberET)!!.text.toString()
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataValue
    }

    private fun resendData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataVal["Token_ID"] = tokenId
        mapDataVal["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataVal
    }

    private fun resendOtpObserver() {
        loginViewModel.getLoginDetailsData(resendData()).observe(this, {
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                if (it.status_code == "0") {
                    forgotPasswordTwoDialog.findViewById<TextView>(R.id.waitForOtp)?.visibility = View.VISIBLE
                    forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.waitLayout)?.visibility = View.GONE
                    forgotPasswordTwoDialog.findViewById<LinearLayout>(R.id.resendOtpLayout)?.visibility = View.GONE
                    timerRestriction()
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        })
    }

    private fun timerRestriction() {
        val counter = intArrayOf(30)
        object : CountDownTimer(30000, 1000) {
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
        softInputAssist.onPause()
        super.onPause()
        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
    }

    override fun onResume() {
        softInputAssist.onResume()
        super.onResume()
    }

    override fun onDestroy() {
        softInputAssist.onDestroy()
        super.onDestroy()

    }
}
