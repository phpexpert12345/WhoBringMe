@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.*
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.ActivityRegistrationBinding
import com.phpexpert.bringme.dtos.PostDataOtp
import com.phpexpert.bringme.models.RegistrationModel
import com.phpexpert.bringme.utilities.BaseActivity
import java.util.*
import kotlin.collections.HashMap


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
open class RegistrationActivity : BaseActivity(), GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private lateinit var registrationActivity: ActivityRegistrationBinding
    private lateinit var selectionString: String
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private var passwordVisible: Boolean = false

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registrationActivity = DataBindingUtil.setContentView(this, R.layout.activity_registration)
        registrationActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        if (mGoogleApiClient == null) {
            buildGoogleApiClient()
        }
        setActions()
        setValues()
    }

    private fun setActions() {
        registrationActivity.editData.setOnClickListener {
            finish()
        }

        registrationActivity.btnSubmit.setOnClickListener {
            if (checkValidations()) {
                if (isCheckPermissions(this, perission))
                    setObserver()
                else {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().please_enable_permissions_irst
                    bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
            }

        }

        registrationActivity.backArrow.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finishAffinity()
        }

        registrationActivity.mobileNumberEditText.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                registrationActivity.mobileNumberInputText.hint = ""
                registrationActivity.mobileNumberTextHint.visibility = View.VISIBLE
            } else {
                if (registrationActivity.mobileNumberEditText.text!!.isEmpty()) {
                    registrationActivity.mobileNumberInputText.hint = sharedPrefrenceManager.getLanguageData().mobile_number
                    registrationActivity.mobileNumberTextHint.visibility = View.GONE
                }

            }
        }
        registrationActivity.digitPin.onFocusChangeListener = View.OnFocusChangeListener { _, p1 ->
            if (p1) {
                registrationActivity.textData.hint = sharedPrefrenceManager.getLanguageData().password
            } else {
                if (registrationActivity.digitPin.text!!.isEmpty())
                    registrationActivity.textData.hint = sharedPrefrenceManager.getLanguageData()._6_digit_mpin_number
            }
        }



        registrationActivity.passwordEye.setOnClickListener {
            if (passwordVisible) {
                registrationActivity.passwordEye.setImageResource(R.drawable.eye_close)
                passwordVisible = false
                registrationActivity.digitPin.transformationMethod = PasswordTransformationMethod()
            } else {
                registrationActivity.passwordEye.setImageResource(R.drawable.eye_open)
                passwordVisible = true
                registrationActivity.digitPin.transformationMethod = null
            }
        }
    }

    private fun setValues() {
        selectionString = intent.extras!!.getString("selectionString")!!
        registrationActivity.selectionString.text = if (selectionString == "client") sharedPrefrenceManager.getLanguageData().client_receiver else sharedPrefrenceManager.getLanguageData().delivery_employee
    }

    @SuppressLint("MissingPermission")
    private fun setObserver() {
        if (isOnline()) {
            registrationActivity.btnSubmit.startAnimation()
            val otpSendViewModel = ViewModelProvider(this).get(RegistrationModel::class.java)
            otpSendViewModel.sendOtpModel(getMapData()).observe(this, {
                bottomSheetDialogMessageText.text = it.status_message
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                if (it.status_code == "0") {
                    bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        bottomSheetDialog.dismiss()
                        val v = Intent(this@RegistrationActivity, OTPActivity::class.java)
                        val postDataOtp = PostDataOtp()
                        postDataOtp.accountFirstName = registrationActivity.firstNameEt.text.toString()
                        postDataOtp.accountLasttName = registrationActivity.lastNameEt.text.toString()
                        postDataOtp.accountMobile = registrationActivity.mobileNumberEditText.text.toString()
                        postDataOtp.accountPhoneCode = registrationActivity.searchCountyCountry.textView_selectedCountry.text.toString()
                        postDataOtp.accountEmail = registrationActivity.emailEt.text.toString()
                        postDataOtp.accountType = selectionString
                        postDataOtp.mobilePinCode = registrationActivity.digitPin.text.toString()
                        postDataOtp.deviceTokenId = it.data!!.Token_ID
                        postDataOtp.devicePlatform = "Android"
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
                                        if (location != null) {
                                            mLocation = location
                                            val geocoder = Geocoder(this@RegistrationActivity, Locale.getDefault())
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
                                            break
                                        } else {
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
                                    v.putExtra("postDataModel", postDataOtp)
                                    startActivity(v)
                                }
                            }
                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

//                        val mLocation =
//                                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

                        } catch (e: Exception) {
                            e.printStackTrace()
                            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().something_is_wrong
                            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                            bottomSheetDialog.show()
                        }
                    }

                } else {
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }

                }
                bottomSheetDialog.show()
            })

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

    private fun getMapData(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["account_mobile"] = registrationActivity.mobileNumberEditText.text.toString()
        mapData["account_type"] = if (selectionString == "client") "1" else "2"
        mapData["account_phone_code"] = registrationActivity.searchCountyCountry.textView_selectedCountry.text.toString()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        mapData["lang_code"] = sharedPrefrenceManager.getAuthData().lang_code!!
        return mapData
    }


    private fun checkValidations(): Boolean {
        return when {
            registrationActivity.firstNameEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().first_name_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.lastNameEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().last_name_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.mobileNumberEditText.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().mobile_number_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.mobileNumberEditText.text.toString().length !in 10..14 -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.emailEt.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().email_id_is_required_field
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            !registrationActivity.emailEt.text.toString().isValidEmail() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enater_valid_email
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.digitPin.text.toString().isEmpty() -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().create_6_digit_password
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
                false
            }
            registrationActivity.digitPin.text.toString().length != 6 -> {
                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().must_contain_6_digit_password
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
    }

    @Synchronized
    protected fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
//        setAction()
    }

    override fun onConnected(p0: Bundle?) {
    }

    override fun onConnectionSuspended(p0: Int) {
    }

    override fun onConnectionFailed(p0: ConnectionResult) {
    }

}