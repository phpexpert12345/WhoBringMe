package com.phpexpert.bringme.activities.employee

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ProfileEditLayoutBinding
import com.phpexpert.bringme.utilities.BaseActivity

class ProfileEditActivity : BaseActivity() {
    private lateinit var profileEditLayoutBinding: ProfileEditLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        profileEditLayoutBinding = DataBindingUtil.setContentView(this, R.layout.profile_edit_layout)

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