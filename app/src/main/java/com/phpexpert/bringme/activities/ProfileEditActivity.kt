package com.phpexpert.bringme.activities

import android.os.Bundle
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
}