package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutRegistrationSelectionBinding
import com.phpexpert.bringme.utilities.BaseActivity


class RegistrationSelectionActivity : BaseActivity() {

    private lateinit var registrationSelectionBinding: LayoutRegistrationSelectionBinding
    private var selectionString: String = "client"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationSelectionBinding = DataBindingUtil.setContentView(this, R.layout.layout_registration_selection)
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
                Toast.makeText(this, "Please Select One", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onPause() {
        super.onPause()
        registrationSelectionBinding.nextButton.revertAnimation()
    }
}