package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.PaymentLayoutBinding
import com.phpexpert.bringme.utilities.BaseActivity

class PaymentActivity : BaseActivity() {

    private lateinit var paymentActivityBinding: PaymentLayoutBinding
    private var selectionByString: String = "paypal"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        paymentActivityBinding = DataBindingUtil.setContentView(this, R.layout.payment_layout)
        setActions()
    }

    private fun setActions() {
        paymentActivityBinding.proceedButton.setOnClickListener {
            paymentActivityBinding.proceedButton.startAnimation()
            if (selectionByString == "paypal") {
                Handler().postDelayed({
                    startActivity(Intent(this, CongratulationScreen::class.java))
                }, 1000)
            } else {
                Handler().postDelayed({
                    startActivity(Intent(this, NewCardActivity::class.java))
                }, 1000)
            }
        }

        paymentActivityBinding.backIcon.setOnClickListener {
            finish()
        }

        paymentActivityBinding.editIcon.setOnClickListener {
            finish()
        }

        paymentActivityBinding.payPalSelection.setOnClickListener {
            if (selectionByString!="paypal"){
                selectionByString = "paypal"
                paymentActivityBinding.payPalSelection.setImageResource(R.drawable.dot_selected)
                paymentActivityBinding.creditCardSelection.setImageResource(R.drawable.dot_unselected)
            }
        }
        paymentActivityBinding.creditCardSelection.setOnClickListener {
            if (selectionByString=="paypal"){
                selectionByString = "credit_card"
                paymentActivityBinding.payPalSelection.setImageResource(R.drawable.dot_unselected)
                paymentActivityBinding.creditCardSelection.setImageResource(R.drawable.dot_selected)
            }
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
        paymentActivityBinding.proceedButton.revertAnimation()
    }
}