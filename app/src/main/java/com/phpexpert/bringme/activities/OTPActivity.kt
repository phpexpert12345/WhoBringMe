@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityOTPBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.PostDataOtp
import com.phpexpert.bringme.models.RegistrationModel
import com.phpexpert.bringme.utilities.BaseActivity
import java.util.*
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class OTPActivity : BaseActivity() {

    private lateinit var otpActivity: ActivityOTPBinding
    private lateinit var postDataOtp: PostDataOtp
    private lateinit var viewDataModel: RegistrationModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        otpActivity = DataBindingUtil.setContentView(this, R.layout.activity_o_t_p)
        otpActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()
        postDataOtp = intent.getSerializableExtra("postDataModel") as PostDataOtp

        otpActivity.btnSubmit.setOnClickListener {
            if (isOnline()) {
                otpActivity.btnSubmit.startAnimation()
                gettingLocation()
            } else {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        }

        viewDataModel = ViewModelProvider(this).get(RegistrationModel::class.java)

        timerRestriction()
        otpActivity.headerText.text = "${languageDtoData.please_wait_we_will_auto_verify_nthe_otp_sent_to} ${postDataOtp.accountPhoneCode + postDataOtp.accountMobile}"

        otpActivity.backArrow.setOnClickListener {
            finish()
        }

        otpActivity.resendLayout.setOnClickListener {
            progressDialog.show()
            resendOtpObserver()
        }

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(languageDtoData.please_wait)
        progressDialog.setCancelable(false)
        handleOtpET()
    }


    private fun handleOtpET() {
        otpActivity.otpPass1.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass1))
        otpActivity.otpPass2.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass2))
        otpActivity.otpPass3.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass3))
        otpActivity.otpPass4.addTextChangedListener(GenericTextWatcher(otpActivity.otpPass4))
    }

    inner class GenericTextWatcher(var view: View) : TextWatcher {
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.otpPass1 -> if (text.length == 1) {
                    otpActivity.otpPass2.requestFocus()
                }
                R.id.otpPass2 -> if (text.length == 1) {
                    otpActivity.otpPass3.requestFocus()
                } else if (text.isEmpty()) {
                    otpActivity.otpPass1.requestFocus()
                }
                R.id.otpPass3 -> if (text.length == 1) {
                    otpActivity.otpPass4.requestFocus()
                } else if (text.isEmpty()) {
                    otpActivity.otpPass2.requestFocus()
                }
                R.id.otpPass4 -> if (text.isEmpty()) {
                    otpActivity.otpPass3.requestFocus()
                    otpActivity.btnVerify.visibility = View.VISIBLE
                    otpActivity.btnSubmit.visibility = View.GONE
                } else {
                    otpActivity.btnVerify.visibility = View.GONE
                    otpActivity.btnSubmit.visibility = View.VISIBLE
                }
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.white)
    }

    private fun timerRestriction() {
        val counter = intArrayOf(30)
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                otpActivity.timeText.text = "${languageDtoData.auto_verifying_your_otp_in_00_12} (00:${counter[0]})"
                counter[0]--
            }

            override fun onFinish() { //                textView.setText("FINISH!!");
                otpActivity.timeText.visibility = View.GONE
                otpActivity.resendLayout.visibility = View.VISIBLE
            }
        }.start()
    }

    private fun setObserver() {
        viewDataModel.registerViewModel(mapData()).observe(this, {
            otpActivity.btnSubmit.revertAnimation()
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            if (it.status_code == "0") {
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    val intent = Intent(this, LoginActivity::class.java)
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
    }

    private fun resendOtpObserver() {
        viewDataModel.resendOtpModel(resendData()).observe(this, {
            progressDialog.dismiss()
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                if (it.status_code == "0") {
                    otpActivity.timeText.visibility = View.VISIBLE
                    otpActivity.resendLayout.visibility = View.GONE
                    timerRestriction()
                }
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        })
    }

    private fun mapData(): Map<String, String?> {
        val mapDataVal = HashMap<String, String?>()
        mapDataVal["otp_number"] = otpActivity.otpPass1.text.toString() + otpActivity.otpPass2.text.toString() + otpActivity.otpPass3.text.toString() + otpActivity.otpPass4.text.toString()
        mapDataVal["account_first_name"] = base64Encoded(postDataOtp.accountFirstName)
        mapDataVal["account_last_name"] = base64Encoded(postDataOtp.accountLasttName)
        mapDataVal["account_email"] = postDataOtp.accountEmail
        mapDataVal["account_mobile"] = postDataOtp.accountMobile
        mapDataVal["account_mpin_number"] = postDataOtp.mobilePinCode
        mapDataVal["account_type"] = if (postDataOtp.accountType == "client") "1" else "2"
        mapDataVal["account_country"] = base64Encoded(postDataOtp.accountCountry)
        mapDataVal["account_state"] = base64Encoded(postDataOtp.accountState)
        mapDataVal["account_city"] = base64Encoded(postDataOtp.accountCity)
        mapDataVal["account_address"] = base64Encoded(postDataOtp.accountAddress)
        mapDataVal["address_postcode"] = base64Encoded(postDataOtp.addressPostCode)
        mapDataVal["account_lat"] = postDataOtp.accountLat
        mapDataVal["account_long"] = postDataOtp.accountLong
        mapDataVal["account_phone_code"] = postDataOtp.accountPhoneCode
        mapDataVal["referral_code"] = postDataOtp.accountReferralCode
        mapDataVal["device_token_id"] = postDataOtp.deviceTokenId
        mapDataVal["device_platform"] = postDataOtp.devicePlatform
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataVal["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataVal
    }

    private fun resendData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataVal["Token_ID"] = postDataOtp.deviceTokenId!!
        mapDataVal["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataVal
    }

    @SuppressLint("MissingPermission")
    private fun gettingLocation() {
        try {
            val mLocationRequest = LocationRequest.create()
            mLocationRequest.interval = 60000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            var mLocation: Location?
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        try {
                            if (location != null) {
                                mLocation = location
                                val geocoder = Geocoder(this@OTPActivity, Locale.getDefault())
                                val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                                postDataOtp.accountLat = mLocation!!.latitude.toString()
                                postDataOtp.accountLong = mLocation!!.longitude.toString()
                                postDataOtp.accountCountry = addresses[0]!!.countryName
                                postDataOtp.accountState = addresses[0]!!.adminArea
                                postDataOtp.accountCity = addresses[0]!!.locality
                                val stringBuilder = StringBuilder()
                                for (i in 0..addresses[0]!!.maxAddressLineIndex)
                                    stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                                postDataOtp.accountAddress = stringBuilder.toString()
                                postDataOtp.addressPostCode = addresses[0]!!.postalCode
                                setObserver()
                            } else {
                                otpActivity.btnSubmit.revertAnimation()
                                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().location_not_found
                                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                                bottomSheetDialogMessageOkButton.setOnClickListener {
                                    bottomSheetDialog.dismiss()
                                }
                                bottomSheetDialog.show()

                            }
                            break
                        } catch (e: Exception) {
                            otpActivity.btnSubmit.revertAnimation()
                            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().location_not_found
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    }
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback)
//                    v.putExtra("postDataModel", postDataOtp)
//                    startActivity(v)
                }
            }
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

//                        val mLocation =
//                                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        } catch (e: Exception) {
            otpActivity.btnSubmit.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().something_is_wrong
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }
}