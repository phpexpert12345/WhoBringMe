@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.employee.profile

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.ChangePasswordActivity
import com.phpexpert.bringme.activities.LoginActivity
import com.phpexpert.bringme.activities.employee.DashboardActivity
import com.phpexpert.bringme.activities.employee.ProfileEditActivity
import com.phpexpert.bringme.databinding.EmployeeProfileFragmentBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.SharedPrefrenceManager

@Suppress("DEPRECATION")
class ProfileFragment : Fragment(), AuthInterface {
    private lateinit var profileFragmentBinding: EmployeeProfileFragmentBinding
    private lateinit var profileViewMode: com.phpexpert.bringme.models.ProfileViewModel
    private lateinit var languageData: LanguageDtoData
    private lateinit var mobileNumberDialog: BottomSheetDialog
    private lateinit var otpDataDialog: BottomSheetDialog
    private lateinit var progressDialog: ProgressDialog
    private lateinit var sharedPrefrenceManager: SharedPrefrenceManager
    private lateinit var apiName: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        profileFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.employee_profile_fragment, container, false)
        profileFragmentBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        languageData = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        sharedPrefrenceManager = (activity as BaseActivity).sharedPrefrenceManager
        profileViewMode = ViewModelProvider(this).get(com.phpexpert.bringme.models.ProfileViewModel::class.java)
        setMobileNumberDialog()
        setOtpDialog()
        setActions()
        profileFragmentBinding.phoneChangeLayout.setOnClickListener {
            mobileNumberDialog.show()
        }
        return profileFragmentBinding.root
    }

    @SuppressLint("CutPasteId")
    private fun setMobileNumberDialog() {
        mobileNumberDialog = BottomSheetDialog(requireActivity(), R.style.SheetDialog)
        mobileNumberDialog.setContentView(R.layout.layout_change_phone_number)
        mobileNumberDialog.setCancelable(false)
        mobileNumberDialog.findViewById<TextView>(R.id.textHeading)?.text = sharedPrefrenceManager.getLanguageData().change_phone_number
        mobileNumberDialog.findViewById<TextView>(R.id.weWillSendText)?.text = sharedPrefrenceManager.getLanguageData().we_will_send_you_an
        mobileNumberDialog.findViewById<TextView>(R.id.oneTimePassword)?.text = sharedPrefrenceManager.getLanguageData().one_time_password
        mobileNumberDialog.findViewById<TextView>(R.id.onThisMobile)?.text = sharedPrefrenceManager.getLanguageData().on_this_mobile_number
        mobileNumberDialog.findViewById<TextInputLayout>(R.id.mobileNumberInputText)?.hint = sharedPrefrenceManager.getLanguageData().enter_mobile_number

        mobileNumberDialog.findViewById<TextInputLayout>(R.id.mobileNumberInputText)?.hint = sharedPrefrenceManager.getLanguageData().enter_mobile_number
        mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)?.text = sharedPrefrenceManager.getLanguageData().get_otp
        mobileNumberDialog.findViewById<TextInputEditText>(R.id.mobileNumber)?.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            if (b) {
                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._19sdp), 0, 0)
                mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)?.layoutParams = lp
            } else {
                if (mobileNumberDialog.findViewById<TextInputEditText>(R.id.mobileNumber)?.text!!.isEmpty()) {
                    val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT)
                    lp.setMargins(resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._minus6sdp), resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp), 0, 0)
                    mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)?.layoutParams = lp
                }
            }
        }

        mobileNumberDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            try {
                mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.clearFocus()
            mobileNumberDialog.dismiss()
        }

        mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.setOnClickListener {
            when {
                mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.isEmpty() -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_register_mobile_text
                    (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
                mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString().length !in 10..14 -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().enter_valid_mobile_number
                    (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
                else -> {
                    mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.startAnimation()
                    getOtpNumberObserver()
                }
            }
        }
    }

    private fun setOtpDialog() {
        otpDataDialog = BottomSheetDialog(requireActivity(), R.style.SheetDialog)
        otpDataDialog.setContentView(R.layout.otp_verify_layout)
        otpDataDialog.findViewById<TextView>(R.id.timeText)?.text = languageData.auto_verifying_your_otp_in_00_12
        otpDataDialog.findViewById<TextView>(R.id.didNotReceive)?.text = languageData.don_t_receive_code_resend_code
        otpDataDialog.findViewById<TextView>(R.id.resendText)?.text = languageData.resend_code
        otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)?.text = languageData.verify
        otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)?.text = languageData.submit

        otpDataDialog.setCancelable(false)

        otpDataDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text = Editable.Factory.getInstance().newEditable("")
            otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text = Editable.Factory.getInstance().newEditable("")
            otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text = Editable.Factory.getInstance().newEditable("")
            otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text = Editable.Factory.getInstance().newEditable("")
            otpDataDialog.dismiss()
        }

        otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.setOnClickListener {
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.startAnimation()
            verifyOtpObserver()
        }

        otpDataDialog.findViewById<LinearLayout>(R.id.resendLayout)!!.setOnClickListener {
            progressDialog.show()
            resendOtpObserver()
        }

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage((activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_wait)
        progressDialog.setCancelable(false)

        handleOtpET()
    }

    private fun timerRestriction() {
        val counter = intArrayOf(30)
        object : CountDownTimer(30000, 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                try {
                    otpDataDialog.findViewById<TextView>(R.id.timeText)!!.text = "${(activity as BaseActivity).sharedPrefrenceManager.getLanguageData().auto_verifying_your_otp_in_00_12} (00:${counter[0]})"
                    counter[0]--
                } catch (e: Exception) {
                }
            }

            override fun onFinish() { //                textView.setText("FINISH!!");
                try {
                    otpDataDialog.findViewById<TextView>(R.id.timeText)!!.visibility = View.GONE
                    otpDataDialog.findViewById<LinearLayout>(R.id.resendLayout)!!.visibility = View.VISIBLE
                } catch (e: Exception) {
                }
            }
        }.start()
    }

    private fun setActions() {
        profileFragmentBinding.editImage.setOnClickListener {
            startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
        }

        profileFragmentBinding.changeMPinLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }

        profileFragmentBinding.logoutLayout.setOnClickListener {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().are_you_sure_you_want_to_logout
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().yes
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().no
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.VISIBLE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as DashboardActivity).bottomSheetDialog.dismiss()
                val languageDtoData = (activity as DashboardActivity).sharedPrefrenceManager.getLanguageData()
                val authData = (activity as DashboardActivity).sharedPrefrenceManager.getAuthData()
                (activity as DashboardActivity).sharedPrefrenceManager.clearData()
                (activity as DashboardActivity).sharedPrefrenceManager.saveLanguageData(languageDtoData)
                (activity as DashboardActivity).sharedPrefrenceManager.saveAuthData(authData)
                startActivity(Intent(requireActivity(), LoginActivity::class.java))
                requireActivity().finishAffinity()
            }
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.setOnClickListener {
                (activity as DashboardActivity).bottomSheetDialog.dismiss()
            }
            (activity as DashboardActivity).bottomSheetDialog.show()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setObserver()
        ProfileViewModel.changeModel.postValue(false)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
//        if ((activity as BaseActivity).isOnline()) {
//            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
        ProfileViewModel.getChangeModel().observe(viewLifecycleOwner, {
            var requestOptions = RequestOptions()
            requestOptions = requestOptions.transforms(CenterCrop(), RoundedCorners(16))
            Glide.with(this).load((activity as BaseActivity).sharedPrefrenceManager.getProfile().login_photo)
                    .apply(requestOptions)
                    .placeholder(R.drawable.user_placeholder)
                    .into(profileFragmentBinding.userImage)

            profileFragmentBinding.userName.text = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_name
            profileFragmentBinding.userEmailTV.text = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_email
            profileFragmentBinding.userMobileNo.text = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_phone_code + (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_phone
        })
    }

    @SuppressLint("SetTextI18n")
    private fun getOtpNumberObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                profileViewMode.changeMobileNumber(getOtpDataMap()).observe(viewLifecycleOwner, {
                    if (it.status_code == "2") {
                        apiName = "sendOtp"
                        (activity as BaseActivity).hitAuthApi(this)
                    } else {
                        mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                        if (it.status_code == "11")
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        else
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        if (it.status_code == "0") {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                        } else {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        }
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                            if (it.status_code == "0") {
                                mobileNumberDialog.dismiss()
                                timerRestriction()
                                otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<TextView>(R.id.headerText)?.text = languageData.please_wait_we_will_auto_verify_nthe_otp_sent_to + " " + mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString() + mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.text.toString()
                                otpDataDialog.show()
                            }
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                })
            } else {
                apiName = "sendOtp"
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    @SuppressLint("CutPasteId", "SetTextI18n")
    private fun verifyOtpObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                profileViewMode.otpVerifyData(getOtpVerify()).observe(viewLifecycleOwner, {
                    if (it.status_code == "2") {
                        apiName = "verifyOtp"
                        (activity as BaseActivity).hitAuthApi(this)
                    } else {
                        otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.revertAnimation()
                        if (it.status_code == "11")
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        else
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        if (it.status_code == "0") {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                        } else {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        }
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                            if (it.status_code == "0") {
                                mobileNumberDialog.dismiss()
                                sharedPrefrenceManager.getProfile().login_phone = mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.text.toString()
                                sharedPrefrenceManager.getProfile().login_phone_code = mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)?.textView_selectedCountry?.text.toString()
                                sharedPrefrenceManager.saveProfile(sharedPrefrenceManager.getProfile())
                                profileFragmentBinding.userMobileNo.text = mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)?.textView_selectedCountry?.text.toString() + mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.text.toString()
                                otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text = Editable.Factory.getInstance().newEditable("")
                                mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.text = Editable.Factory.getInstance().newEditable("")
                                otpDataDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                })
            } else {
                apiName = "verifyOtp"
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.revertAnimation()
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun resendOtpObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                profileViewMode.otpResendData(getResendOtp()).observe(viewLifecycleOwner, {
                    if (it.status_code == "2"){
                        apiName = "resendOtp"
                        (activity as BaseActivity).hitAuthApi(this)
                    }else {
                        progressDialog.dismiss()
                        if (it.status_code == "11")
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        else
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        if (it.status_code == "0") {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                        } else {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        }
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            otpDataDialog.findViewById<LinearLayout>(R.id.resendLayout)!!.visibility = View.GONE
                            otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text = Editable.Factory.getInstance().newEditable("")
                            otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text = Editable.Factory.getInstance().newEditable("")
                            otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text = Editable.Factory.getInstance().newEditable("")
                            otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text = Editable.Factory.getInstance().newEditable("")
                            timerRestriction()
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                })
            } else {
                apiName = "resendOtp"
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }


    private fun getOtpDataMap(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["account_mobile"] = mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataVal["account_phone_code"] = mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        mapDataVal["LoginId"] = sharedPrefrenceManager.getLoginId()
        return mapDataVal
    }

    private fun getOtpVerify(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["otp_number"] = otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text.toString() + otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text.toString() + otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text.toString() + otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text.toString()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        return mapDataVal
    }

    private fun getResendOtp(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["account_mobile"] = mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataVal["account_phone_code"] = mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        mapDataVal["LoginId"] = sharedPrefrenceManager.getLoginId()
        return mapDataVal
    }

    private fun handleOtpET() {
        otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.setSelection(otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.text.length)
            if (b) {
                if (otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.text.isNotEmpty() && otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.text.isNotEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.requestFocus()
                }
            }
        }

        otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.setSelection(otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.text.length)
            if (b) {
                if (otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.text.isNotEmpty() && otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.text.isNotEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.requestFocus()
                } else if (otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.text.isEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.requestFocus()
                }
            }
        }

        otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.setSelection(otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.text.length)
            if (b) {
                if (otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.text.isNotEmpty() && otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.text.isNotEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.requestFocus()
                } else if (otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.text.isEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.requestFocus()
                }
            }
        }

        otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.onFocusChangeListener = View.OnFocusChangeListener { _, b ->
            otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.setSelection(otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.text.length)
            if (b) {
                if (otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.text.isEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.requestFocus()
                }
            }
        }

        otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.addTextChangedListener(GenericTextWatcher(otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!))
        otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.addTextChangedListener(GenericTextWatcher(otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!))
        otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.addTextChangedListener(GenericTextWatcher(otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!))
        otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.addTextChangedListener(GenericTextWatcher(otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!))

    }

    inner class GenericTextWatcher(var view: View) : TextWatcher {
        @SuppressLint("CutPasteId")
        override fun afterTextChanged(editable: Editable) {
            val text = editable.toString()
            when (view.id) {
                R.id.otpPass1 -> if (text.length == 1) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.requestFocus()
                }
                R.id.otpPass2 -> if (text.length == 1) otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.requestFocus() else if (text.isEmpty()) otpDataDialog.findViewById<EditText>(R.id.otpPass1)!!.requestFocus()
                R.id.otpPass3 -> if (text.length == 1) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass4)!!.requestFocus()
                } else if (text.isEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass2)!!.requestFocus()
                }
                R.id.otpPass4 -> if (text.isEmpty()) {
                    otpDataDialog.findViewById<EditText>(R.id.otpPass3)!!.requestFocus()
                    otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.visibility = View.GONE
                    otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)!!.visibility = View.VISIBLE
//                    otpActivity.btnVerify.visibility = View.VISIBLE
//                    otpActivity.btnSubmit.visibility = View.GONE
                } else {
                    otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.visibility = View.VISIBLE
                    otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)!!.visibility = View.GONE
                }
            }
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            when (apiName) {
                "sendOtp" -> getOtpNumberObserver()
                "verifyOtp" -> verifyOtpObserver()
                "resendOtp" -> resendOtpObserver()
            }
        } else {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = message
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }
}