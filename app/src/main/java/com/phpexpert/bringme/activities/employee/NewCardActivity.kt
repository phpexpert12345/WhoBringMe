package com.phpexpert.bringme.activities.employee

import android.annotation.SuppressLint
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.LayoutAddNewCardBinding
import com.phpexpert.bringme.dtos.AuthSingleton
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


class NewCardActivity : BaseActivity() {
    private lateinit var cardActivityBinding: LayoutAddNewCardBinding
    private lateinit var servicePostValue: PostJobPostDto
    private lateinit var jobPostViewModel: JobPostModel
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        cardActivityBinding = DataBindingUtil.setContentView(this, R.layout.layout_add_new_card)
        jobPostViewModel = ViewModelProvider(this).get(JobPostModel::class.java)
        servicePostValue = intent.getSerializableExtra("postValues") as PostJobPostDto
        setValues()
    }

    private fun setValues() {
        cardActivityBinding.expiryDate.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p1 == 1 && p1 + p3 == 2 && p0?.contains('/') == false) {
                    cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable("$p0/")
                } else if (p1 == 3 && p1 - p2 == 2 && p0?.contains('/') == true) {
                    cardActivityBinding.expiryDate.text = Editable.Factory.getInstance().newEditable(p0.toString().replace("/", ""))
                }
                cardActivityBinding.expiryDate.setSelection(cardActivityBinding.expiryDate.text.toString().length)

            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })

        cardActivityBinding.cardNumber.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
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
            }
        })

        cardActivityBinding.payNowButton.setOnClickListener {
            val cardData = Card.create(cardActivityBinding.cardNumber.text.toString().replace("\\s".toRegex(), ""),
                    cardActivityBinding.expiryDate.text.toString().split("/")[0].toInt(),
                    cardActivityBinding.expiryDate.text.toString().split("/")[1].toInt(),
                    cardActivityBinding.cvv.text.toString()
            )
            createPaymentCall(cardData, 1)
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
//        cardActivityBinding.payNowButton.revertAnimation()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    private fun createPaymentCall(card_details: Card, type: Int) {
        val stripe = Stripe(this, PaymentConfigurationSingleton.paymentConfiguration.stripe_publishKey!!)
        stripe.createToken(card_details, object : ApiResultCallback<Token?> {
            override fun onError(error: Exception) {
                Toast.makeText(this@NewCardActivity, "Please try again", Toast.LENGTH_SHORT).show()
                //progressBar.setVisibility(View.GONE);
            }

            override fun onSuccess(token: Token) {
                val description = "Payment success"
                setPaymentTokenObserver(token.id)
            }
        })
    }

    private fun setPaymentTokenObserver(stripToken: String) {
        jobPostViewModel.getPaymentGenerateToken(this, getMapDataToken(stripToken)).observe(this, {
            if (it.status_code == "0") {
                setJobPostDataObserver((TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())).toString())
            } else {
                Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setJobPostDataObserver(paymentTransactionId: String) {
        jobPostViewModel.getPostJobData(this, getPostJobMap(paymentTransactionId)).observe(this, {
            if (it.status_code == "0") {

            } else {
                Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getMapDataToken(stripToken: String): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["stripeToken"] = stripToken
        mapDataValue["amount"] = servicePostValue.grandTotal!!
        mapDataValue["currency"] = AuthSingleton.authObject.currency_code!!
        mapDataValue["description"] = servicePostValue.jobDescription!!
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataValue
    }

    private fun getPostJobMap(paymentTransactionId: String): Map<String, String> {
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var mLocation: Location? = null
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        mLocation = location
                        break
                    } else {
                        Toast.makeText(this@NewCardActivity, "Location not found", Toast.LENGTH_LONG).show()
                    }
                }
                mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            }
        }
        val mapDataValue = HashMap<String, String>()
        mapDataValue["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapDataValue["payment_transaction_id"] = paymentTransactionId
        mapDataValue["about_job"] = servicePostValue.jobDescription!!
        mapDataValue["job_offer_time"] = servicePostValue.jobDescription!!
        mapDataValue["job_sub_total"] = servicePostValue.jobDescription!!
        mapDataValue["job_tax_amount"] = servicePostValue.jobDescription!!
        mapDataValue["Charge_for_Jobs"] = servicePostValue.jobDescription!!
        mapDataValue["Charge_for_Jobs_percentage"] = servicePostValue.jobDescription!!
        mapDataValue["Charge_for_Jobs_Admin_percentage"] = servicePostValue.jobDescription!!
        mapDataValue["Charge_for_Jobs_Delivery_percentage"] = servicePostValue.jobDescription!!
        mapDataValue["admin_service_fees"] = servicePostValue.jobDescription!!
        mapDataValue["delivery_employee_fee"] = servicePostValue.jobDescription!!
        mapDataValue["job_total_amount"] = servicePostValue.jobDescription!!
        mapDataValue["payment_mode"] = servicePostValue.jobDescription!!
        val geocoder = Geocoder(this@NewCardActivity, Locale.getDefault())
        val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)

        mapDataValue["job_posted_country"] = addresses[0]!!.countryName
        mapDataValue["job_posted_state"] = addresses[0]!!.adminArea
        mapDataValue["job_posted_city"] = addresses[0]!!.locality
        mapDataValue["job_posted_lat"] = mLocation!!.latitude.toString()
        mapDataValue["job_posted_lang"] = mLocation!!.longitude.toString()
        mapDataValue["job_posted_zipcode"] = addresses[0]!!.postalCode
        val stringBuilder = StringBuilder()
        for (i in 0..addresses[0]!!.maxAddressLineIndex)
            stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
        mapDataValue["job_posted_address"] = stringBuilder.toString()
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataValue
    }

}