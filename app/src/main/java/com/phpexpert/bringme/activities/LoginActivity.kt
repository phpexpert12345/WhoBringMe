package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityLoginBinding


class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setAction()
    }


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
    }
}
