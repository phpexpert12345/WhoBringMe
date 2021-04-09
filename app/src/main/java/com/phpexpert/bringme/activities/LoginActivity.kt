package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import com.facebook.FacebookSdk
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityLoginBinding
import com.phpexpert.bringme.models.LoginViewModel

class LoginActivity : AppCompatActivity() {

    lateinit var loginBinding:ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        setAction()
    }


    private fun setAction(){
        loginBinding.loginButton.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }

        loginBinding.createAccount.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }
    }
}
