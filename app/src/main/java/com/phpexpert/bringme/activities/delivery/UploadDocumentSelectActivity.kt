package com.phpexpert.bringme.activities.delivery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutDocumentChoosenActivityBinding
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class UploadDocumentSelectActivity : BaseActivity() {

    private lateinit var documentSelectActivity: LayoutDocumentChoosenActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        documentSelectActivity = DataBindingUtil.setContentView(this, R.layout.layout_document_choosen_activity)
        documentSelectActivity.languageModel = sharedPrefrenceManager.getLanguageData()

        documentSelectActivity.countryCode.setArrowSize(70)
        documentSelectActivity.continueButton.setOnClickListener {
            documentSelectActivity.continueButton.startAnimation()
            Handler().postDelayed({
                startActivity(Intent(this, DocumentUploadActivity::class.java))
            }, 1000)
        }

        documentSelectActivity.backArrow.setOnClickListener {
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
        documentSelectActivity.continueButton.revertAnimation()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}