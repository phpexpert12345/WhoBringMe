@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities.employee

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.PaymentLayoutBinding
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.dtos.PaymentConfigurationSingleton
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.models.JobPostModel
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class PaymentActivity : BaseActivity() {

    private lateinit var paymentActivityBinding: PaymentLayoutBinding
    private var selectionByString: String = "paypal"
    private lateinit var servicePostValue: PostJobPostDto
    private lateinit var jobPostViewModel: JobPostModel
    private var grandTotal: Float? = 0f
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        paymentActivityBinding = DataBindingUtil.setContentView(this, R.layout.payment_layout)

        jobPostViewModel = ViewModelProvider(this).get(JobPostModel::class.java)

        progressDialog = ProgressDialog(this)
        setValues()
        setActions()
    }

    private fun setActions() {
        paymentActivityBinding.proceedButton.setOnClickListener {
            paymentActivityBinding.proceedButton.startAnimation()
            if (selectionByString == "paypal") {
                /*Handler().postDelayed({
                    startActivity(Intent(this, CongratulationScreen::class.java))
                }, 1000)*/
            } else {

                setPaymentAuthKeyObserver()

            }
        }

        paymentActivityBinding.backIcon.setOnClickListener {
            finish()
        }

        paymentActivityBinding.editIcon.setOnClickListener {
            finish()
        }

        paymentActivityBinding.payPalSelection.setOnClickListener {
            if (selectionByString != "paypal") {
                selectionByString = "paypal"
                paymentActivityBinding.payPalSelection.setImageResource(R.drawable.dot_selected)
                paymentActivityBinding.creditCardSelection.setImageResource(R.drawable.dot_unselected)
            }
        }
        paymentActivityBinding.creditCardSelection.setOnClickListener {
            if (selectionByString == "paypal") {
                selectionByString = "credit_card"
                paymentActivityBinding.payPalSelection.setImageResource(R.drawable.dot_unselected)
                paymentActivityBinding.creditCardSelection.setImageResource(R.drawable.dot_selected)
            }
        }
    }

    private fun setValues() {
        servicePostValue = intent.getSerializableExtra("postValue") as PostJobPostDto
        paymentActivityBinding.postJobData = servicePostValue
        setObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        progressDialog.show()
        jobPostViewModel.getServiceCharges(this, servicePostValue.jobAmount!!, AuthSingleton.authObject.auth_key!!)
                .observe(this, {
                    progressDialog.dismiss()
                    if (it.status_code == "0") {

                        grandTotal = servicePostValue.jobAmount!!.toFloat()
                        paymentActivityBinding.serviceChargePercentage.text = "Charges for Job (${it.data!!.Charge_for_Jobs_percentage})"
                        paymentActivityBinding.serviceCharges.text = it.data!!.Charge_for_Jobs
                        grandTotal = grandTotal!! + it.data!!.Charge_for_Jobs!!.toFloat()
                        if (it.data!!.job_tax_amount == "" || it.data!!.job_tax_amount == "0") {
                            paymentActivityBinding.adminServiceFees.visibility = View.GONE
                            paymentActivityBinding.adminServiceFeesLayout.visibility = View.GONE
                        } else {
                            paymentActivityBinding.adminServiceFees.visibility = View.VISIBLE
                            paymentActivityBinding.adminServiceFeesLayout.visibility = View.VISIBLE
                            paymentActivityBinding.adminServiceFees.text = "Admin Charges (${it.data!!.Charge_for_Jobs_Admin_percentage})"
                            paymentActivityBinding.adminServiceFeesCharge.text = it.data!!.admin_service_fees
                            grandTotal = grandTotal!! + it.data!!.admin_service_fees!!.toFloat()
                        }
                        paymentActivityBinding.grandTotalAmount.text = grandTotal.toString()
                        servicePostValue.grandTotal = grandTotal.toString()
                        servicePostValue.Charge_for_Jobs = it.data!!.Charge_for_Jobs
                        servicePostValue.Charge_for_Jobs_Admin_percentage = it.data!!.Charge_for_Jobs_Admin_percentage
                        servicePostValue.jobPaymentMode = "Card"
                        servicePostValue.job_tax_amount = it.data!!.job_tax_amount
                        servicePostValue.Charge_for_Jobs_percentage = it.data!!.Charge_for_Jobs_percentage
                        servicePostValue.Charge_for_Jobs_Delivery_percentage = it.data!!.Charge_for_Jobs_Delivery_percentage
                        servicePostValue.admin_service_fees = it.data!!.admin_service_fees
                        servicePostValue.delivery_employee_fee = it.data!!.delivery_employee_fee

                    } else {
                        progressDialog.dismiss()
                        Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
                    }
                })
    }

    private fun setPaymentAuthKeyObserver() {
        jobPostViewModel.getPaymentAuthKey(this, AuthSingleton.authObject.auth_key!!)
                .observe(this, {
                    paymentActivityBinding.proceedButton.revertAnimation()
                    if (it.status_code == "0") {
                        PaymentConfigurationSingleton.paymentConfiguration = it.data!!
                        val intent = Intent(this, NewCardActivity::class.java)
                        intent.putExtra("postValues", servicePostValue)
                        startActivity(intent)
                    } else {
                        Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
                    }
                })
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