package com.phpexpert.bringme.activities.delivery

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutWithdrawActivityBinding
import com.phpexpert.bringme.utilities.BaseActivity

class WithdrawActivity : BaseActivity() {

    private lateinit var layoutWithdrawActivity: LayoutWithdrawActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutWithdrawActivity = DataBindingUtil.setContentView(this, R.layout.layout_withdraw_activity)
        layoutWithdrawActivity.languageModel = sharedPrefrenceManager.getLanguageData()

        layoutWithdrawActivity.currencyCode.text = getCurrencySymbol()
        layoutWithdrawActivity.currencyCode1.text = getCurrencySymbol()
        Glide.with(this).load(sharedPrefrenceManager.getProfile().login_photo).circleCrop().placeholder(R.drawable.user_placeholder).into(layoutWithdrawActivity.userImage)
        layoutWithdrawActivity.backArrow.setOnClickListener {
            finish()
        }
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