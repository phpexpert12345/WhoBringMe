package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutChangePasswordBinding
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.models.ProfileViewModel
import com.phpexpert.bringme.utilities.BaseActivity

class ChangePasswordActivity : BaseActivity() {

    private lateinit var changePasswordActivity: LayoutChangePasswordBinding
    private lateinit var changePasswordViewModel: ProfileViewModel
    private var oldPassword: Boolean = false
    private var passwordNewVisible: Boolean = false
    private var passwordConfirmVisible: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        changePasswordActivity = DataBindingUtil.setContentView(this, R.layout.layout_change_password)
        changePasswordViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)

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

    @SuppressLint("SetTextI18n")
    private fun validationData(): Boolean {
        return when {
            changePasswordActivity.oldPassword.text.isEmpty() -> {
                bottomSheetDialogMessageText.text = "Please Enter old password  first"
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.oldPassword.text.length != 6 -> {
                bottomSheetDialogMessageText.text = "Please Enter 6 digit old password"
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.newPasswordET.text.isEmpty() -> {
                bottomSheetDialogMessageText.text = "Please Enter new password first"
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.newPasswordET.text.length != 6 -> {
                bottomSheetDialogMessageText.text = "Please Enter 6 digit new password"
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.confirmPasswordET.text.isEmpty() -> {
                bottomSheetDialogMessageText.text = "Please Enter confirm password first"
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.confirmPasswordET.text.length != 6 -> {
                bottomSheetDialogMessageText.text = "Please Enter 6 digit confirm password"
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            changePasswordActivity.newPasswordET.text.toString() != changePasswordActivity.confirmPasswordET.text.toString() -> {
                bottomSheetDialogMessageText.text = "Password not match"
                bottomSheetDialogMessageOkButton.text = "Ok"
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

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        if (isOnline()) {
            changePasswordViewModel.changePassword(mapData()).observe(this, {
                changePasswordActivity.continueButton.revertAnimation()
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = "Ok"
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                    if (it.status_code == "0") {
                        finish()
                    }
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            })
        } else {
            changePasswordActivity.continueButton.revertAnimation()
            bottomSheetDialogMessageText.text = getString(R.string.network_error)
            bottomSheetDialogMessageOkButton.text = "Ok"
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
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        return mapDataValue
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    override fun onPause() {
        super.onPause()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

}