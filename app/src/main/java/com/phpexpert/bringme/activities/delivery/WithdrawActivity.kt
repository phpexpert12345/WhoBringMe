package com.phpexpert.bringme.activities.delivery

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutWithdrawActivityBinding
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.EarningViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.HashMap

class WithdrawActivity : BaseActivity(), AuthInterface {

    private lateinit var layoutWithdrawActivity: LayoutWithdrawActivityBinding
    private lateinit var earningModel: EarningViewModel
    private var totalAvailableAmount: String? = ""
    private var withdrawType = "Bank Transfer"
    private var formatChange:Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutWithdrawActivity = DataBindingUtil.setContentView(this, R.layout.layout_withdraw_activity)
        layoutWithdrawActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        earningModel = ViewModelProvider(this).get(EarningViewModel::class.java)

        layoutWithdrawActivity.currencyCode.text = getCurrencySymbol()
        layoutWithdrawActivity.currencyCode1.text = getCurrencySymbol()
        Glide.with(this).load(sharedPrefrenceManager.getProfile().login_photo).circleCrop().placeholder(R.drawable.user_placeholder).into(layoutWithdrawActivity.userImage)

        totalAvailableAmount = intent.extras?.getString("totalAvailableAmount")!!
        layoutWithdrawActivity.availableAmount.text = totalAvailableAmount.formatChange()
        layoutWithdrawActivity.backArrow.setOnClickListener {
            finish()
        }

        layoutWithdrawActivity.withdrawData.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                if (p0!!.isNotEmpty() && formatChange) {
                    if (p0.contains(".") || p0.contains(",")) {
                        try {
                            if (p0.split(".")[1].isNotEmpty()) {
                                formatChange = false
                                layoutWithdrawActivity.withdrawData.text = Editable.Factory.getInstance().newEditable(p0.toString().formatChange())
                                layoutWithdrawActivity.withdrawData.setSelection(layoutWithdrawActivity.withdrawData.length())
                            } else {
                                formatChange = true
                            }
                        } catch (e: java.lang.Exception) {

                        }
                    } else {
                        formatChange = true
                    }
                } else {
                    formatChange = true
                }
            }

        })

        layoutWithdrawActivity.submitButton.setOnClickListener {
            if (checkValidation()) {
                layoutWithdrawActivity.submitButton.startAnimation()
                setObserver()
            }
        }
    }

    private fun checkValidation(): Boolean = when {
        layoutWithdrawActivity.withdrawData.text.isEmpty() -> {
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().withdraw_amount_is_mandatory
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
            false
        }
        totalAvailableAmount?.formatChangeCountry()?.toFloat()!! < layoutWithdrawActivity.withdrawData.text.toString().formatChangeCountry()!!.toFloat() -> {
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().insufficient_balance
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
            false
        }
        layoutWithdrawActivity.withdrawData.text.toString().formatChangeCountry()!!.toFloat() < 0 -> {
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().amount_greater_than_0
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
            false
        }

        else -> {
            true
        }
    }

    private fun setObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                earningModel.withdrawAmountData(getWithDrawData()).observe(this, {
                    when (it.status_code) {
                        "0" -> {
                            layoutWithdrawActivity.submitButton.revertAnimation()
                            if (it.status_code == "11")
                                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            else
                                bottomSheetDialogMessageText.text = it.status_message
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                                finish()
                            }
                            bottomSheetDialog.show()
                        }
                        "2" -> {
                            hitAuthApi(this@WithdrawActivity)
                        }
                        else -> {
                            layoutWithdrawActivity.submitButton.revertAnimation()
                            if (it.status_code == "11")
                                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            else
                                bottomSheetDialogMessageText.text = it.status_message
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    }
                })
            } else {
                hitAuthApi(this)
            }
        } else {
            layoutWithdrawActivity.submitButton.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun getWithDrawData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["available_amount"] = totalAvailableAmount!!.formatChangeCountry()!!
        mapDataVal["withdrawal_amount"] = layoutWithdrawActivity.withdrawData.text.toString().formatChangeCountry()!!
        mapDataVal["withdrawal_type"] = withdrawType
        mapDataVal["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataVal
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            setObserver()
        } else {
            layoutWithdrawActivity.submitButton.revertAnimation()
            bottomSheetDialogMessageText.text = message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogHeadingText.visibility = View.VISIBLE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun String?.formatChangeCountry() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale(sharedPrefrenceManager.getAuthData().lang_code!!, "DE"))
//            formatter.format(this?.toFloat())
            val new = this?.replace(",", ".")
//            val symbols = DecimalFormatSymbols(Locale("en", "IN"))
//            val formartter = (DecimalFormat("##.##", symbols))
//            formartter.format(new?.toFloat())
            return new
        } catch (e: Exception) {
            this
        }
    }
}