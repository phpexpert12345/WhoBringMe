package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.*


class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    private lateinit var forgotPasswordOneBehavior: BottomSheetBehavior<View>
    private lateinit var forgotPasswordOneBinding: LayoutForgotPasswordOneBinding

    private lateinit var forgotPasswordTwoBehavior: BottomSheetBehavior<View>
    private lateinit var forgotPasswordTwoBinding: LayoutForgotPasswordTwoBinding

    private lateinit var forgotPasswordThreeBehavior: BottomSheetBehavior<View>
    private lateinit var forgotPasswordThreeBinding: LayoutForgotPasswordThreeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val w: Window = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)


        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        forgotPasswordOneBinding = loginBinding.forPasswordOne
        forgotPasswordOneBehavior = BottomSheetBehavior.from(forgotPasswordOneBinding.root)
        forgotPasswordOneBehavior.peekHeight = 0

        forgotPasswordTwoBinding = loginBinding.forgotPasswordTwo
        forgotPasswordTwoBehavior = BottomSheetBehavior.from(forgotPasswordTwoBinding.root)
        forgotPasswordTwoBehavior.peekHeight = 0

        forgotPasswordThreeBinding = loginBinding.forgotPasswordThree
        forgotPasswordThreeBehavior = BottomSheetBehavior.from(forgotPasswordThreeBinding.root)
        forgotPasswordThreeBehavior.peekHeight = 0
        setAction()
    }


    @Suppress("DEPRECATION")
    private fun setAction() {
        loginBinding.loginButton.setOnClickListener {
            loginBinding.loginButton.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this, DashboardActivity::class.java))
            }, 1000)
        }

        loginBinding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationSelectionActivity::class.java))
        }
        forgotPasswordOneBinding.closeIcon.setOnClickListener {
            forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            loginBinding.forgotPasswordView.visibility = View.GONE
        }

        loginBinding.forgotPassword.setOnClickListener {
            forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            loginBinding.forgotPasswordView.visibility = View.VISIBLE
        }

        forgotPasswordOneBinding.getOtpButton.setOnClickListener {
            forgotPasswordOneBinding.getOtpButton.startAnimation()

            Handler().postDelayed({
                forgotPasswordTwoBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                forgotPasswordOneBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            }, 1000)
        }

        forgotPasswordTwoBinding.closeIcon.setOnClickListener {
            forgotPasswordTwoBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            loginBinding.forgotPasswordView.visibility = View.GONE
        }
    }


    override fun onPause() {
        super.onPause()
        forgotPasswordOneBinding.getOtpButton.revertAnimation()

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}
