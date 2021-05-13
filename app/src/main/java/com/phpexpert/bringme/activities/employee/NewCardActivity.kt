package com.phpexpert.bringme.activities.employee

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutAddNewCardBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.PaymentConfigurationSingleton
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.models.JobPostModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.stripe.android.ApiResultCallback
import com.stripe.android.Stripe
import com.stripe.android.model.Card
import com.stripe.android.model.Token
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class NewCardActivity : BaseActivity() {
    private lateinit var cardActivityBinding: LayoutAddNewCardBinding
    private lateinit var servicePostValue: PostJobPostDto
    private lateinit var jobPostViewModel: JobPostModel
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var languageDtoData: LanguageDtoData

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        cardActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_add_new_card)
        cardActivityBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()
        jobPostViewModel = ViewModelProvider(this).get(JobPostModel::class.java)
        servicePostValue = intent.getSerializableExtra("postValues") as PostJobPostDto
        setPaymentAuthKeyObserver()
        setValues()
    }

    private fun setValues() {

        cardActivityBinding.cardName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                cardActivityBinding.cardHolderNameText.text = p0
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        cardActivityBinding.cvv.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                cardActivityBinding.cvvNumberText.text = p0
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
        cardActivityBinding.expiryDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p3 == 0) {
                    if (cardActivityBinding.expiryDate.text.toString().trim() != "") {
                        cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable("")
                        cardActivityBinding.expireDateText.text = cardActivityBinding.expiryDate.text.toString()
                    }
                } else {
                    if (p1 == 1 && p1 + p3 == 2 && p0?.contains('/') == false) {
                        cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable("$p0/")
                    } else if (p1 == 3 && p1 - p2 == 2 && p0?.contains('/') == true) {
                        cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable(p0.toString().replace("/", ""))
                    }
                    cardActivityBinding.expiryDate.setSelection(cardActivityBinding.expiryDate.text.toString().length)
                    cardActivityBinding.expireDateText.text = cardActivityBinding.expiryDate.text.toString()
                }

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        cardActivityBinding.cardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                cardActivityBinding.cardNumberText.text = p0
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    if (s!!.isNotEmpty() && s.length % 5 == 0) {
                        val c: Char = s[s.length - 1]
                        if (' ' == c) {
                            s.delete(s.length - 1, s.length)
                        }
                    }
                    // Insert char where needed.
                    // Insert char where needed.
                    if (s.isNotEmpty() && s.length % 5 == 0) {
                        val c: Char = s[s.length - 1]
                        // Only if its a digit where there should be a space we insert a space
                        if (Character.isDigit(c) && TextUtils.split(s.toString(), java.lang.String.valueOf(' ')).size <= 3) {
                            s.insert(s.length - 1, java.lang.String.valueOf(' '))
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })

        cardActivityBinding.payNowButton.setOnClickListener {
            if (cardActivityBinding.cardNumber.text.toString().length != 19) {
                bottomSheetDialogMessageText.text = languageDtoData.card_number_not_valid
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            } else {
                cardActivityBinding.payNowButton.startAnimation()
                try {
                    val cardData = Card.create(cardActivityBinding.cardNumber.text.toString().replace("\\s".toRegex(), ""),
                            cardActivityBinding.expiryDate.text.toString().split("/")[0].toInt(),
                            cardActivityBinding.expiryDate.text.toString().split("/")[1].toInt(),
                            cardActivityBinding.cvv.text.toString()
                    )
                    createPaymentCall(cardData)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        cardActivityBinding.backLayout.setOnClickListener {
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

    private fun createPaymentCall(card_details: Card) {
        try {
            val stripe = Stripe(this, PaymentConfigurationSingleton.paymentConfiguration.stripe_publishKey!!)
            stripe.createToken(card_details, object : ApiResultCallback<Token?> {
                override fun onError(error: Exception) {
                    Toast.makeText(this@NewCardActivity, "Please try again", Toast.LENGTH_SHORT).show()
                    //progressBar.setVisibility(View.GONE);
                }

                override fun onSuccess(token: Token) {
                    setPaymentTokenObserver(token.id)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setPaymentTokenObserver(stripToken: String) {
        if (isOnline()) {
            jobPostViewModel.getPaymentGenerateToken(getMapDataToken(stripToken)).observe(this, {
                if (it.status_code == "0") {
                    setJobPostDataObserver((TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())).toString())
                } else {
                    cardActivityBinding.payNowButton.revertAnimation()
                    bottomSheetDialogMessageText.text = languageDtoData.network_error
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
            })
        } else {
            cardActivityBinding.payNowButton.revertAnimation()
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setJobPostDataObserver(paymentTransactionId: String) {
        if (isOnline()) {
            if (isCheckPermissions(this, perission)) {
                val mLocationRequest = LocationRequest.create()
                mLocationRequest.interval = 60000
                mLocationRequest.fastestInterval = 5000
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                var mLocation: Location?
                mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        for (location in locationResult.locations) {
                            if (location != null) {
                                mLocation = location
                                jobPostViewModel.getPostJobData(getPostJobMap(paymentTransactionId, mLocation!!)).observe(this@NewCardActivity, {
                                    cardActivityBinding.payNowButton.revertAnimation()
                                    bottomSheetDialogMessageText.text = it.status_message
                                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                                    if (it.status_code == "0") {
                                        bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                                            bottomSheetDialog.dismiss()
                                            servicePostValue.jobId = it.data!!.job_order_id
                                            val intent = Intent(this@NewCardActivity, CongratulationScreen::class.java)
                                            intent.putExtra("postValue", servicePostValue)
                                            startActivity(intent)
                                            finishAffinity()
                                        }
                                    } else {
                                        bottomSheetDialogMessageOkButton.setOnClickListener {
                                            bottomSheetDialog.dismiss()
                                        }
                                    }
                                    bottomSheetDialog.show()
                                })
                                break
                            } else {
                                bottomSheetDialogMessageText.text = languageDtoData.location_not_found
                                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                                bottomSheetDialogMessageOkButton.setOnClickListener {
                                    bottomSheetDialog.dismiss()
                                }
                                bottomSheetDialog.show()
                            }
                        }
                        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                    }
                }
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
            } else {
                cardActivityBinding.payNowButton.revertAnimation()
                bottomSheetDialogMessageText.text = languageDtoData.please_provide_all_permission
                bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        } else {
            cardActivityBinding.payNowButton.revertAnimation()
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun getMapDataToken(stripToken: String): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["stripeToken"] = stripToken
        mapDataValue["amount"] = servicePostValue.grandTotal!!
        mapDataValue["currency"] = sharedPrefrenceManager.getAuthData().currency_code!!
        mapDataValue["description"] = servicePostValue.jobDescription!!
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        return mapDataValue
    }

    private fun getPostJobMap(paymentTransactionId: String, mLocation: Location): Map<String, String> {

        val mapDataValue = HashMap<String, String>()
        mapDataValue["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapDataValue["payment_transaction_id"] = paymentTransactionId
        mapDataValue["about_job"] = base64Encoded(servicePostValue.jobDescription!!)
        mapDataValue["job_offer_time"] = servicePostValue.jobTime!! + " minutes"
        mapDataValue["job_sub_total"] = servicePostValue.jobAmount!!
        mapDataValue["job_tax_amount"] = servicePostValue.job_tax_amount!!
        mapDataValue["Charge_for_Jobs"] = servicePostValue.Charge_for_Jobs!!
        mapDataValue["Charge_for_Jobs_percentage"] = servicePostValue.Charge_for_Jobs_percentage!!
        mapDataValue["Charge_for_Jobs_Admin_percentage"] = servicePostValue.Charge_for_Jobs_Admin_percentage!!
        mapDataValue["Charge_for_Jobs_Delivery_percentage"] = servicePostValue.Charge_for_Jobs_Delivery_percentage!!
        mapDataValue["admin_service_fees"] = servicePostValue.admin_service_fees!!
        mapDataValue["delivery_employee_fee"] = servicePostValue.delivery_employee_fee!!
        mapDataValue["job_total_amount"] = servicePostValue.grandTotal!!
        mapDataValue["payment_mode"] = servicePostValue.jobPaymentMode!!
        val geocoder = Geocoder(this@NewCardActivity, Locale.getDefault())
        val addresses = geocoder.getFromLocation(mLocation.latitude, mLocation.longitude, 1)
        mapDataValue["job_posted_country"] = base64Encoded(addresses[0]!!.countryName)
        mapDataValue["job_posted_state"] = base64Encoded(addresses[0]!!.adminArea)
        mapDataValue["job_posted_city"] = base64Encoded(addresses[0]!!.locality)
        mapDataValue["job_posted_lat"] = base64Encoded(mLocation.latitude.toString())
        mapDataValue["job_posted_lang"] = mLocation.longitude.toString()
        mapDataValue["job_posted_zipcode"] = addresses[0]!!.postalCode
        val stringBuilder = StringBuilder()
        for (i in 0..addresses[0]!!.maxAddressLineIndex)
            stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
        mapDataValue["job_posted_address"] = base64Encoded(stringBuilder.toString())
        mapDataValue["lang_code"] =sharedPrefrenceManager.getAuthData().lang_code!!
        mapDataValue["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        return mapDataValue
    }

    private fun setPaymentAuthKeyObserver() {
        if (isOnline()) {
            jobPostViewModel.getPaymentAuthKey(sharedPrefrenceManager.getAuthData().auth_key!!)
                    .observe(this, {
                        bottomSheetDialogMessageText.text = it.status_message
                        bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        if (it.status_code == "0") {
                            PaymentConfigurationSingleton.paymentConfiguration = it.data!!
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
}