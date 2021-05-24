package com.phpexpert.bringme.activities.delivery

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutDocumentChoosenActivityBinding
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class UploadDocumentSelectActivity : BaseActivity() {

    private lateinit var documentSelectActivity: LayoutDocumentChoosenActivityBinding
    private var selectedDocumentType = "ID Card"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        documentSelectActivity = DataBindingUtil.setContentView(this, R.layout.layout_document_choosen_activity)
        documentSelectActivity.languageModel = sharedPrefrenceManager.getLanguageData()

        documentSelectActivity.countryCode.setArrowSize(70)
        documentSelectActivity.continueButton.setOnClickListener {
            documentSelectActivity.continueButton.startAnimation()
            Handler().postDelayed({
                val intent = Intent(this, DocumentUploadActivity::class.java)
                intent.putExtra("document_type", selectedDocumentType)
                intent.putExtra("document_country", documentSelectActivity.countryCode.textView_selectedCountry.text)
                startActivity(intent)
                finish()
            }, 1000)
        }

        documentSelectActivity.backArrow.setOnClickListener {
            finish()
        }
        setAction()
    }

    private fun setAction() {
        documentSelectActivity.idCardLayout.setOnClickListener {
            if (documentSelectActivity.idCheckedImage.visibility == View.GONE) {
                documentSelectActivity.idCheckedImage.visibility = View.VISIBLE
                documentSelectActivity.idCardLayout.setBackgroundResource(R.drawable.id_select_bg)
                documentSelectActivity.idCardImage.setImageResource(R.drawable.id_card_select)
                documentSelectActivity.idCardText.setTextColor(resources.getColor(R.color.colorGreen))
                documentSelectActivity.passportLayout.setBackgroundResource(R.drawable.id_unselct_bg)
                documentSelectActivity.passportCheckedImage.visibility = View.GONE
                documentSelectActivity.passportImage.setImageResource(R.drawable.passport_unselect)
                documentSelectActivity.passportText.setTextColor(resources.getColor(R.color.colorDarkGrey))
                documentSelectActivity.driverLicenseLayout.setBackgroundResource(R.drawable.id_unselct_bg)
                documentSelectActivity.driverLicenseCheckImage.visibility = View.GONE
                documentSelectActivity.driverLicenseImage.setImageResource(R.drawable.driver_license)
                documentSelectActivity.driverLicenseText.setTextColor(resources.getColor(R.color.colorDarkGrey))
                selectedDocumentType = "ID Card"
            }
        }

        documentSelectActivity.passportLayout.setOnClickListener {
            if (documentSelectActivity.passportCheckedImage.visibility == View.GONE) {
                documentSelectActivity.idCheckedImage.visibility = View.GONE
                documentSelectActivity.idCardLayout.setBackgroundResource(R.drawable.id_unselct_bg)
                documentSelectActivity.idCardImage.setImageResource(R.drawable.id_card_unselect)
                documentSelectActivity.idCardText.setTextColor(resources.getColor(R.color.colorDarkGrey))

                documentSelectActivity.passportLayout.setBackgroundResource(R.drawable.id_select_bg)
                documentSelectActivity.passportCheckedImage.visibility = View.VISIBLE
                documentSelectActivity.passportImage.setImageResource(R.drawable.passport_select)
                documentSelectActivity.passportText.setTextColor(resources.getColor(R.color.colorGreen))

                documentSelectActivity.driverLicenseLayout.setBackgroundResource(R.drawable.id_unselct_bg)
                documentSelectActivity.driverLicenseCheckImage.visibility = View.GONE
                documentSelectActivity.driverLicenseImage.setImageResource(R.drawable.driver_license)
                documentSelectActivity.driverLicenseText.setTextColor(resources.getColor(R.color.colorDarkGrey))
                selectedDocumentType = "Passport"

            }
        }

        documentSelectActivity.driverLicenseLayout.setOnClickListener {
            if (documentSelectActivity.driverLicenseCheckImage.visibility == View.GONE) {
                documentSelectActivity.idCheckedImage.visibility = View.GONE
                documentSelectActivity.idCardLayout.setBackgroundResource(R.drawable.id_unselct_bg)
                documentSelectActivity.idCardImage.setImageResource(R.drawable.id_card_unselect)
                documentSelectActivity.idCardText.setTextColor(resources.getColor(R.color.colorDarkGrey))

                documentSelectActivity.passportLayout.setBackgroundResource(R.drawable.id_unselct_bg)
                documentSelectActivity.passportCheckedImage.visibility = View.GONE
                documentSelectActivity.passportImage.setImageResource(R.drawable.passport_unselect)
                documentSelectActivity.passportText.setTextColor(resources.getColor(R.color.colorDarkGrey))

                documentSelectActivity.driverLicenseLayout.setBackgroundResource(R.drawable.id_select_bg)
                documentSelectActivity.driverLicenseCheckImage.visibility = View.VISIBLE
                documentSelectActivity.driverLicenseImage.setImageResource(R.drawable.driver_license_geen)
                documentSelectActivity.driverLicenseText.setTextColor(resources.getColor(R.color.colorGreen))
                selectedDocumentType = "Driving License"
            }
        }
    }
}