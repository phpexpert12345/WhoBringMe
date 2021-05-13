package com.phpexpert.bringme.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutRegistrationSelectionBinding
import com.phpexpert.bringme.utilities.BaseActivity


@Suppress("DEPRECATION")
class RegistrationSelectionActivity : BaseActivity() {

    private lateinit var registrationSelectionBinding: LayoutRegistrationSelectionBinding
    private var selectionString: String = "client"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationSelectionBinding = DataBindingUtil.setContentView(this, R.layout.layout_registration_selection)
        registrationSelectionBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        setActions()
    }

    private fun setActions() {
        registrationSelectionBinding.clientImage.setOnClickListener {
            selectionString = if (selectionString == "" || selectionString == "delivery") {
                registrationSelectionBinding.clientImage.setImageResource(R.drawable.client_select)
                registrationSelectionBinding.deliveryImage.setImageResource(R.drawable.delivery_employee_unselect)
                "client"
            } else {

                registrationSelectionBinding.deliveryImage.setImageResource(R.drawable.delivery_employee_unselect)
                "client"
            }
        }

        registrationSelectionBinding.backArrow.setOnClickListener {
            finish()
        }

        registrationSelectionBinding.deliveryImage.setOnClickListener {
            selectionString = if (selectionString == "" || selectionString == "client") {
                registrationSelectionBinding.deliveryImage.setImageResource(R.drawable.delivery_employee_select)
                registrationSelectionBinding.clientImage.setImageResource(R.drawable.client_unselect)
                "delivery"
            } else {
                registrationSelectionBinding.clientImage.setImageResource(R.drawable.client_unselect)
                "delivery"
            }
        }

        registrationSelectionBinding.nextButton.setOnClickListener {
            if (selectionString != ""){
                registrationSelectionBinding.nextButton.startAnimation()
                Handler().postDelayed({
                    val intent = Intent(this, RegistrationActivity::class.java)
                    intent.putExtra("selectionString", selectionString)
                    startActivity(intent)
                }, 1000)
            }else{
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().please_select_one
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        registrationSelectionBinding.nextButton.revertAnimation()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}