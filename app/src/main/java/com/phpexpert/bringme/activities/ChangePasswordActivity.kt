package com.phpexpert.bringme.activities

import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutChangePasswordBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.ProfileViewModel
import com.phpexpert.bringme.utilities.BaseActivity

class ChangePasswordActivity : BaseActivity(), AuthInterface {

    private lateinit var changePasswordActivity: LayoutChangePasswordBinding
    private lateinit var changePasswordViewModel: ProfileViewModel
    private var oldPassword: Boolean = false
    private var passwordNewVisible: Boolean = false
    private var passwordConfirmVisible: Boolean = false
    private lateinit var languageDtoData: LanguageDtoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordActivity = DataBindingUtil.setContentView(this, R.layout.layout_change_password)
        changePasswordActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        changePasswordViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        languageDtoData = sharedPrefrenceManager.getLanguageData()

        changePasswordActivity.continueButton.setOnClickListener {
            if (validationData()) {
                changePasswordActivity.continueButton.startAnimation()
                setObserver()
            }
        }

        changePasswordActivity.backArrow.setOnClickListener {
            finish()
        }

        changePasswordActivity.oldPasswordEye.setOnClickListener {
            if (oldPassword) {
                changePasswordActivity.oldPasswordEye.setImageResource(R.drawable.eye_close)
                oldPassword = false
                changePasswordActivity.oldPassword.transformationMethod = PasswordTransformationMethod()
            } else {
                changePasswordActivity.oldPasswordEye.setImageResource(R.drawable.eye_open)
                oldPassword = true
                changePasswordActivity.oldPassword.transformationMethod = null
            }
        }

        changePasswordActivity.newPasswordEye.setOnClickListener {
            if (passwordNewVisible) {
                changePasswordActivity.newPasswordEye.setImageResource(R.drawable.eye_close)
                passwordNewVisible = false
                changePasswordActivity.newPasswordET.transformationMethod = PasswordTransformationMethod()
            } else {
                changePasswordActivity.newPasswordEye.setImageResource(R.drawable.eye_open)
                passwordNewVisible = true
                changePasswordActivity.newPasswordET.transformationMethod = null
            }
        }
        changePasswordActivity.confirmPasswordEye.setOnClickListener {
            if (passwordConfirmVisible) {
                changePasswordActivity.confirmPasswordEye.setImageResource(R.drawable.eye_close)
                passwordConfirmVisible = false
                changePasswordActivity.confirmPasswordET.transformationMethod = PasswordTransformationMethod()
            } else {
                changePasswordActivity.confirmPasswordEye.setImageResource(R.drawable.eye_open)
                passwordConfirmVisible = true
                changePasswordActivity.confirmPasswordET.transformationMethod = null
            }
        }
    }

    private fun validationData(): Boolean {
        return when {
            changePasswordActivity.oldPassword.text!!.isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.please_enter_old_password_first
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.oldPassword.text?.length != 6 -> {
                bottomSheetDialogMessageText.text = languageDtoData.please_enter_6_digit_old_password
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.newPasswordET.text!!.isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.please_enter_new_password_first
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.newPasswordET.text?.length != 6 -> {
                bottomSheetDialogMessageText.text = languageDtoData.please_enter_6_digit_new_password
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.confirmPasswordET.text!!.isEmpty() -> {
                bottomSheetDialogMessageText.text = languageDtoData.please_enter_confirm_password_first
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.confirmPasswordET.text?.length != 6 -> {
                bottomSheetDialogMessageText.text = languageDtoData.please_enter_6_digit_confirm_password
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.newPasswordET.text.toString() != changePasswordActivity.confirmPasswordET.text.toString() -> {
                bottomSheetDialogMessageText.text = languageDtoData.password_not_match
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }

            else -> true
        }
    }

    private fun setObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key !=null && sharedPrefrenceManager.getAuthData()?.auth_key !="") {
                changePasswordActivity.continueButton.revertAnimation()
                changePasswordViewModel.changePassword(mapData()).observe(this, {
                    changePasswordActivity.continueButton.revertAnimation()
                    bottomSheetDialogMessageText.text = it.status_message
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    if (it.status_code == "0")
                        bottomSheetDialogHeadingText.visibility = View.GONE
                    else
                        bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        if (it.status_code == "0") {
                            finish()
                        }
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                })
            }else{
                hitAuthApi(this)
            }
        } else {
            changePasswordActivity.continueButton.revertAnimation()
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun mapData(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["new_password"] = changePasswordActivity.newPasswordET.text.toString()
        mapDataValue["confirm_password"] = changePasswordActivity.confirmPasswordET.text.toString()
        mapDataValue["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapDataValue["Old_Password"] = changePasswordActivity.oldPassword.text.toString()
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataValue["lang_code"] = sharedPrefrenceManager.getAuthData()?.lang_code!!
        return mapDataValue
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value){
            setObserver()
        }else{
            changePasswordActivity.continueButton.revertAnimation()
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