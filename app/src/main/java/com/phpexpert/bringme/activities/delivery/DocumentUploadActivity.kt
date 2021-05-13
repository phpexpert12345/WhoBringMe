package com.phpexpert.bringme.activities.delivery

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.UploadDoucmentActivityBinding
import com.phpexpert.bringme.utilities.BaseActivity

class DocumentUploadActivity : BaseActivity() {

    private lateinit var uploadDocumentActivity: UploadDoucmentActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        uploadDocumentActivity = DataBindingUtil.setContentView(this, R.layout.upload_doucment_activity)

        uploadDocumentActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        uploadDocumentActivity.backArrow.setOnClickListener {
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