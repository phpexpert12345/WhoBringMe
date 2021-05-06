package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityLoginBinding
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

    private lateinit var forgotPasswordOneDialog: BottomSheetDialog
    private lateinit var forgotPasswordTwoDialog: BottomSheetDialog
//    private lateinit var forgotPasswordOneBehavior: BottomSheetBehavior<View>
//    private lateinit var forgotPasswordOneBinding: LayoutForgotPasswordOneBinding

//    private lateinit var forgotPasswordTwoBehavior: BottomSheetBehavior<View>
//    private lateinit var forgotPasswordTwoBinding: LayoutForgotPasswordTwoBinding

    private var passwordVisible: Boolean = false
    private var passwordNewVisible: Boolean = false
    private var passwordConfirmVisible: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        initValues() // method to init values

        loginBinding.countyCode.setTypeFace(Typeface.DEFAULT_BOLD)

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
        forgotPasswordOneDialog.setCancelable(false)
        forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.setTypeFace(Typeface.DEFAULT_BOLD)

        forgotPasswordTwoDialog = BottomSheetDialog(this, R.style.SheetDialog)
        forgotPasswordTwoDialog.setContentView(R.layout.layout_forgot_password_two)
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
            loginBinding.loginButton.startAnimation()
            observerDataLogin()
        }

        //forgot password button in login screen
        loginBinding.forgotPassword.setOnClickListener {
//            loginBinding.forPasswordOne.root.isClickable = true
//            forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            loginBinding.forgotPasswordView.visibility = View.VISIBLE
            forgotPasswordOneDialog.show()
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
                    Toast.makeText(this, "Enter Phone number first", Toast.LENGTH_LONG).show()
                }
                forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString().length != 10 -> {
                    Toast.makeText(this, "Please Enter Valid Phone Number", Toast.LENGTH_LONG).show()
                }
                else -> {
                    forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
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
                    Toast.makeText(this, "Otp not enter", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.isEmpty() -> {
                    Toast.makeText(this, "Enter new password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.length != 6 -> {
                    Toast.makeText(this, "Please enter 6 digit password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.isEmpty() -> {
                    Toast.makeText(this, "Enter Confirm password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.length != 6 -> {
                    Toast.makeText(this, "Please enter 6 digit confirm password", Toast.LENGTH_LONG).show()
                }
                forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.toString() != forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.confirmPasswordET)!!.text.toString() -> {
                    Toast.makeText(this, "Password not match", Toast.LENGTH_LONG).show()
                }
                else -> {
//                    forgotPasswordOneBinding.mobileNumber.text = Editable.Factory.getInstance().newEditable("")
                    forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.startAnimation()
                    observeForgotPasswordResetData()
                }
            }
        }
    }

    //method to observe login data
    @SuppressLint("SetTextI18n")
    private fun observerDataLogin() {
        if (isOnline()) {
            loginViewModel.getLoginData(mapDataLogin()).observe(this, {
                loginBinding.loginButton.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    sharedPrefrenceManager.savePrefrence(CONSTANTS.isLogin, "true")
                    sharedPrefrenceManager.saveProfile(it.data)
                    var intent: Intent?
                    if (sharedPrefrenceManager.getProfile().account_type == "1") {
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            intent = Intent(this, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                            bottomSheetDialog.dismiss()
                            startActivity(intent)
                            finish()
                        }

                    } else {
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            intent = Intent(this, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
                            bottomSheetDialog.dismiss()
                            startActivity(intent)
                            finish()
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
            bottomSheetDialogMessageText.text = getString(R.string.network_error)
            bottomSheetDialogMessageOkButton.text = "Ok"
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }


    //Observe data for forgot password send otp
    @SuppressLint("SetTextI18n")
    private fun observeForgotPasswordOtpSendData() {
        if (isOnline()) {
            loginViewModel.getOtpForgotPasswordSendData(mapDataOtpForgotSend()).observe(this, {
                forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        bottomSheetDialog.dismiss()
                        loginId = it.data!!.LoginId!!
                        forgotPasswordTwoDialog.findViewById<TextView>(R.id.mobileNUmberTV)!!.text = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString() + forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
                        forgotPasswordTwoDialog.dismiss()
                        forgotPasswordOneDialog.dismiss()
                    }
                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                }
                bottomSheetDialog.show()
            })
        } else {
            forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            bottomSheetDialogMessageText.text = getString(R.string.network_error)
            bottomSheetDialogMessageOkButton.text = "Ok"
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    //observer for reset password
    @SuppressLint("SetTextI18n")
    private fun observeForgotPasswordResetData() {
        if (isOnline()) {
            loginViewModel.getOtpForgotPasswordReset(mapDataResetPassword()).observe(this, {
                forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
                forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        forgotPasswordTwoDialog.dismiss()
                    }
                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                }
                bottomSheetDialog.dismiss()
            })
        } else {
            forgotPasswordTwoDialog.findViewById<CircularProgressButton>(R.id.continueButton)!!.revertAnimation()
            bottomSheetDialogMessageText.text = getString(R.string.network_error)
            bottomSheetDialogMessageOkButton.text = "Ok"
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
        mapDataValue["account_mobile"] = forgotPasswordOneDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataValue["account_phone_code"] = forgotPasswordOneDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        return mapDataValue
    }

    //map data for for forgot password send otp api
    private fun mapDataResetPassword(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["new_password"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.newPasswordET)!!.text.toString()
        mapDataValue["confirm_password"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.confirmPasswordET)!!.text.toString()
        mapDataValue["LoginId"] = loginId
        mapDataValue["Mobile_OTP"] = forgotPasswordTwoDialog.findViewById<EditText>(R.id.otpNumberET)!!.text.toString()
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        return mapDataValue
    }

    override fun onPause() {
        super.onPause()
        forgotPasswordOneDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
    }
}
