@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities.employee

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.PaymentLayoutBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.models.JobPostModel
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class PaymentActivity : BaseActivity() {

    private lateinit var paymentActivityBinding: PaymentLayoutBinding
    private var selectionByString: String = ""
    private lateinit var servicePostValue: PostJobPostDto
    private lateinit var jobPostViewModel: JobPostModel
    private var grandTotal: Float? = 0f
    private lateinit var progressDialog: ProgressDialog
    private lateinit var languageDtoData: LanguageDtoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        paymentActivityBinding = DataBindingUtil.setContentView(this, R.layout.payment_layout)
        paymentActivityBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()
        jobPostViewModel = ViewModelProvider(this).get(JobPostModel::class.java)

        paymentActivityBinding.currencyCode.text = getCurrencySymbol()
        paymentActivityBinding.currencyCode1.text = getCurrencySymbol()
        paymentActivityBinding.currencyCode2.text = getCurrencySymbol()
        paymentActivityBinding.currencyCode3.text = getCurrencySymbol()
        paymentActivityBinding.currencyCode4.text = getCurrencySymbol()
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(languageDtoData.please_wait)
        progressDialog.setCancelable(false)
        setValues()
        setActions()
    }

    private fun setActions() {
        paymentActivityBinding.proceedButton.setOnClickListener {
            when (selectionByString) {
                "paypal" -> {
                    bottomSheetDialogMessageText.text = languageDtoData.coming_soon
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                "credit_card" -> {
                    paymentActivityBinding.proceedButton.startAnimation()
                    val intent = Intent(this, NewCardActivity::class.java)
                    intent.putExtra("postValues", servicePostValue)
                    startActivity(intent)

                }
                else -> {
                    bottomSheetDialogMessageText.text = languageDtoData.please_select_payment_method
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
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
                bottomSheetDialogMessageText.text = languageDtoData.coming_soon
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        }
        paymentActivityBinding.creditCardSelection.setOnClickListener {
            if (selectionByString != "credit_card") {
                selectionByString = "credit_card"
                paymentActivityBinding.payPalSelection.setImageResource(R.drawable.dot_unselected)
                paymentActivityBinding.creditCardSelection.setImageResource(R.drawable.dot_selected)
            }
        }
    }

    private fun setValues() {
        servicePostValue = intent.getSerializableExtra("postValue") as PostJobPostDto
        servicePostValue.jobAmount = servicePostValue.jobAmount.formatChange()
        paymentActivityBinding.postJobData = servicePostValue
        setObserver()
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        if (isOnline()) {
            progressDialog.show()
            jobPostViewModel.getServiceCharges(servicePostValue.jobAmount!!, sharedPrefrenceManager.getAuthData().auth_key!!)
                    .observe(this, {
                        progressDialog.dismiss()
                        bottomSheetDialogMessageText.text = it.status_message
                        bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        if (it.status_code == "0") {

                            grandTotal = servicePostValue.jobAmount!!.toFloat()
                            paymentActivityBinding.serviceChargePercentage.text = "${languageDtoData.charges_for_job} (${it.data!!.Charge_for_Jobs_percentage} %)"
                            paymentActivityBinding.serviceCharges.text = it.data!!.Charge_for_Jobs.formatChange()
                            grandTotal = grandTotal!! + it.data!!.Charge_for_Jobs!!.toFloat()
                            if (it.data!!.job_tax_amount == "" || it.data!!.job_tax_amount == "0") {
                                paymentActivityBinding.adminServiceFees.visibility = View.GONE
                                paymentActivityBinding.adminServiceFeesLayout.visibility = View.GONE
                            } else {
                                paymentActivityBinding.adminServiceFees.visibility = View.VISIBLE
                                paymentActivityBinding.adminServiceFeesLayout.visibility = View.VISIBLE
                                paymentActivityBinding.adminServiceFees.text = "${languageDtoData.admin_charges} (${it.data!!.Charge_for_Jobs_Admin_percentage} %)"
                                    paymentActivityBinding.adminServiceFeesCharge.text = it.data!!.admin_service_fees.formatChange()
                                grandTotal = grandTotal!! + it.data!!.admin_service_fees!!.toFloat()
                            }
                            paymentActivityBinding.grandTotalAmount.text = grandTotal.toString().formatChange()
                            servicePostValue.grandTotal = grandTotal.toString()
                            servicePostValue.Charge_for_Jobs = it.data!!.Charge_for_Jobs!!.toFloat().toString()
                            servicePostValue.Charge_for_Jobs_Admin_percentage = it.data!!.Charge_for_Jobs_Admin_percentage
                            servicePostValue.jobPaymentMode = "Card"
                            servicePostValue.job_tax_amount = it.data!!.job_tax_amount
                            servicePostValue.Charge_for_Jobs_percentage = it.data!!.Charge_for_Jobs_percentage
                            servicePostValue.Charge_for_Jobs_Delivery_percentage = it.data!!.Charge_for_Jobs_Delivery_percentage
                            servicePostValue.admin_service_fees = it.data!!.admin_service_fees
                            servicePostValue.delivery_employee_fee = it.data!!.delivery_employee_fee

                        } else {
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    })

        } else {
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    override fun onPause() {
        super.onPause()
        paymentActivityBinding.proceedButton.revertAnimation()
    }
}