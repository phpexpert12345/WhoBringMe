@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Html
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.RelativeLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityRegistrationBinding
import com.phpexpert.bringme.dtos.PostDataOtp
import com.phpexpert.bringme.models.RegistrationModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.SoftInputAssist
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
open class RegistrationActivity : BaseActivity(){
    private lateinit var registrationActivity: ActivityRegistrationBinding
    private lateinit var selectionString: String

    private var passwordVisible: Boolean = false
    private lateinit var softInputAssist: SoftInputAssist

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationActivity = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        registrationActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        registrationActivity.continueMessage.text = Html.fromHtml(sharedPrefrenceManager.getLanguageData().by_continuing_you_agree_that_you_have_read_and_accept_our_t_amp_cs_and_privacy_policy)
        softInputAssist = SoftInputAssist(this)
        setActions()
        setValues()
    }

    private fun setActions() {
        registrationActivity.editData.setOnClickListener {
            finish()
        }

        registrationActivity.btnSubmit.setOnClickListener {
            if (checkValidations()) {
                if (isCheckPermissions(this, perission))
                    setObserver()
                else {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().please_enable_permissions_irst
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
            }

        }

        registrationActivity.backArrow.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        registrationActivity.mobileNumberEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                registrationActivity.mobileNumberInputText.hint = ""
                registrationActivity.mobileNumberTextHint.visibility = View.VISIBLE
                registrationActivity.mobileNumberTextHint.setTextColor(resources.getColor(R.color.colorLoginButton))
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._19sdp), 0, 0)
                registrationActivity.searchCountyCountry.layoutParams = lp
            } else {
                registrationActivity.mobileNumberTextHint.setTextColor(Color.parseColor("#808080"))
                if (registrationActivity.mobileNumberEditText.text!!.isEmpty()) {
                    registrationActivity.mobileNumberInputText.hint = sharedPrefrenceManager.getLanguageData().mobile_number
                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp), 0, 0)
                    registrationActivity.searchCountyCountry.layoutParams = lp
                    registrationActivity.mobileNumberTextHint.visibility = View.GONE
                }

            }
        }

        /*registrationActivity.firstNameEt.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                registrationActivity.firstNameLayout.hint = sharedPrefrenceManager.getLanguageData().first_name
            } else {
                if (registrationActivity.firstNameEt.text!!.isEmpty())
                    registrationActivity.firstNameLayout.hint = sharedPrefrenceManager.getLanguageData().first_name
            }
        }

        registrationActivity.lastNameEt.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                registrationActivity.lastNameLayout.hint = sharedPrefrenceManager.getLanguageData().last_name
            } else {
                if (registrationActivity.lastNameEt.text!!.isEmpty())
                    registrationActivity.lastNameLayout.hint = sharedPrefrenceManager.getLanguageData().last_name
            }
        }

        registrationActivity.emailEt.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                registrationActivity.emailLayout.hint = sharedPrefrenceManager.getLanguageData().email_id
            } else {
                if (registrationActivity.emailEt.text!!.isEmpty())
                    registrationActivity.emailLayout.hint = sharedPrefrenceManager.getLanguageData().email_id
            }
        }*/

        /*registrationActivity.emailEt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("DefaultLocale")
            override fun afterTextChanged(p0: Editable?) {
                var s: String = p0.toString()
                if (s != s.toUpperCase()) {
                    s = s.toUpperCase()
                    registrationActivity.emailEt.setText(s)
                    registrationActivity.emailEt.setSelection(registrationActivity.emailEt.length()) //fix reverse texting
                }
            }

        })*/

        /*registrationActivity.digitPin.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                registrationActivity.textData.hint = sharedPrefrenceManager.getLanguageData().password
            } else {
                if (registrationActivity.digitPin.text!!.isEmpty())
                    registrationActivity.textData.hint = sharedPrefrenceManager.getLanguageData()._6_digit_mpin_number
            }
        }*/



        registrationActivity.passwordEye.setOnClickListener {
            if (passwordVisible) {
                registrationActivity.passwordEye.setImageResource(R.drawable.eye_close)
                passwordVisible = false
                registrationActivity.digitPin.transformationMethod = PasswordTransformationMethod()
            } else {
                registrationActivity.passwordEye.setImageResource(R.drawable.eye_open)
                passwordVisible = true
                registrationActivity.digitPin.transformationMethod = null
            }
        }
    }

    private fun setValues() {
        selectionString = intent.extras!!.getString("selectionString")!!
        registrationActivity.selectionString.text = if (selectionString == "client") sharedPrefrenceManager.getLanguageData().client_receiver else sharedPrefrenceManager.getLanguageData().delivery_employee
    }

    @SuppressLint("MissingPermission")
    private fun setObserver() {
        if (isOnline()) {
            registrationActivity.btnSubmit.startAnimation()
            val otpSendViewModel = ViewModelProvider(this).get(RegistrationModel::class.java)
            otpSendViewModel.sendOtpModel(getMapData()).observe(this, {
                registrationActivity.btnSubmit.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        bottomSheetDialog.dismiss()
                        val v = Intent(this@RegistrationActivity, OTPActivity::class.java)
                        val postDataOtp = PostDataOtp()
                        postDataOtp.accountFirstName = registrationActivity.firstNameEt.text.toString()
                        postDataOtp.accountLasttName = registrationActivity.lastNameEt.text.toString()
                        postDataOtp.accountMobile = registrationActivity.mobileNumberEditText.text.toString()
                        postDataOtp.accountPhoneCode = registrationActivity.searchCountyCountry.textView_selectedCountry.text.toString()
                        postDataOtp.accountEmail = registrationActivity.emailEt.text.toString()
                        postDataOtp.accountType = selectionString
                        postDataOtp.mobilePinCode = registrationActivity.digitPin.text.toString()
                        postDataOtp.deviceTokenId = it.data!!.Token_ID
                        postDataOtp.devicePlatform = "Android"
                        v.putExtra("postDataModel", postDataOtp)
                        startActivity(v)
                    }

                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }

                }
                bottomSheetDialog.show()
            })

        } else {
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun getMapData(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["account_mobile"] = registrationActivity.mobileNumberEditText.text.toString()
        mapData["account_type"] = if (selectionString == "client") "1" else "2"
        mapData["account_phone_code"] = registrationActivity.searchCountyCountry.textView_selectedCountry.text.toString()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapData["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapData
    }


    private fun checkValidations(): Boolean {
        return when {
            registrationActivity.firstNameEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().first_name_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.lastNameEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().last_name_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.mobileNumberEditText.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().mobile_number_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.mobileNumberEditText.text.toString().length !in 10..14 -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.emailEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().email_id_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            !registrationActivity.emailEt.text.toString().isValidEmail() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enater_valid_email
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.digitPin.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().create_6_digit_password
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.digitPin.text.toString().length != 6 -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().must_contain_6_digit_password
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            else -> {
                true
            }
        }
    }

    override fun onPause() {
        softInputAssist.onPause()
        super.onPause()
        registrationActivity.btnSubmit.revertAnimation()
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