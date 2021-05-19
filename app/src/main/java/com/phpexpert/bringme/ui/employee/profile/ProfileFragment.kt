@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.employee.profile

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.PackageManager
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
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.ChangePasswordActivity
import com.phpexpert.bringme.activities.LoginActivity
import com.phpexpert.bringme.activities.employee.DashboardActivity
import com.phpexpert.bringme.activities.employee.ProfileEditActivity
import com.phpexpert.bringme.databinding.EmployeeProfileFragmentBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.utilities.BaseActivity

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var profileFragmentBinding: EmployeeProfileFragmentBinding
    private lateinit var profileViewMode: com.phpexpert.bringme.models.ProfileViewModel
    private lateinit var languageData: LanguageDtoData
    private lateinit var mobileNumberDialog: BottomSheetDialog
    private lateinit var otpDataDialog: BottomSheetDialog
    private lateinit var progressDialog: ProgressDialog

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        profileFragmentBinding = DataBindingUtil.inflate(layoutInflater, R.layout.employee_profile_fragment, container, false)
        profileFragmentBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        languageData = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        profileViewMode = ViewModelProvider(this).get(com.phpexpert.bringme.models.ProfileViewModel::class.java)
        initValues()
        setActions()
        profileFragmentBinding.phoneChangeLayout.setOnClickListener {
            mobileNumberDialog.show()
        }
        return profileFragmentBinding.root
    }

    @SuppressLint("CutPasteId")
    private fun initValues() {
        mobileNumberDialog = BottomSheetDialog(requireActivity(), R.style.SheetDialog)
        mobileNumberDialog.setContentView(R.layout.layout_change_phone_number)
        mobileNumberDialog.findViewById<TextView>(R.id.textHeading)?.text = languageData.change_phone_number
        mobileNumberDialog.findViewById<TextView>(R.id.weWillSendText)?.text = languageData.we_will_send_you_an
        mobileNumberDialog.findViewById<TextView>(R.id.oneTimePassword)?.text = languageData.one_time_password
        mobileNumberDialog.findViewById<TextView>(R.id.onThisMobile)?.text = languageData.on_this_mobile_number
        mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.hint = languageData.enter_your_phone_number
        mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)?.text = languageData.get_otp

        mobileNumberDialog.setCancelable(false)

        otpDataDialog = BottomSheetDialog(requireActivity(), R.style.SheetDialog)
        otpDataDialog.setContentView(R.layout.otp_verify_layout)
        otpDataDialog.findViewById<TextView>(R.id.headerText)?.text = languageData.please_wait_we_will_auto_verify_nthe_otp_sent_to
        otpDataDialog.findViewById<TextView>(R.id.timeText)?.text = languageData.auto_verifying_your_otp_in_00_12
        otpDataDialog.findViewById<TextView>(R.id.didNotReceive)?.text = languageData.don_t_receive_code_resend_code
        otpDataDialog.findViewById<TextView>(R.id.resendText)?.text = languageData.resend_code
        otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)?.text = languageData.verify

        otpDataDialog.setCancelable(false)

        mobileNumberDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            mobileNumberDialog.dismiss()
        }

        mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.setOnClickListener {
            when {
                mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString().trim() == "" -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_enter_mobile_number_first
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
                mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)?.text?.length !in 10..14 -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_enter_valid_phone_number
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
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

        otpDataDialog.findViewById<ImageView>(R.id.closeIcon)!!.setOnClickListener {
            mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text = Editable.Factory.getInstance().newEditable("")
            otpDataDialog.dismiss()
        }

        otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)!!.setOnClickListener {
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)!!.startAnimation()
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
            if ((activity as BaseActivity).isCheckPermissions(requireActivity(), perission)) {
                startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
            }
        }

        profileFragmentBinding.changeMPinLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), ChangePasswordActivity::class.java))
        }

        profileFragmentBinding.logoutLayout.setOnClickListener {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().are_you_sure_you_want_to_logout
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

    @SuppressLint("CutPasteId")
    private fun getOtpNumberObserver() {
        if ((activity as BaseActivity).isOnline()) {
            profileViewMode.changeMobileNumber(getOtpDataMap()).observe(viewLifecycleOwner, {
                mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                    if (it.status_code == "0") {
                        mobileNumberDialog.dismiss()
                        timerRestriction()
                        otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text = Editable.Factory.getInstance().newEditable("")
                        otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text = Editable.Factory.getInstance().newEditable("")
                        otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text = Editable.Factory.getInstance().newEditable("")
                        otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text = Editable.Factory.getInstance().newEditable("")
                        otpDataDialog.show()
                    }
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            })
        } else {
            mobileNumberDialog.findViewById<CircularProgressButton>(R.id.getOtpButton)!!.revertAnimation()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun verifyOtpObserver() {
        if ((activity as BaseActivity).isOnline()) {
            profileViewMode.otpVerifyData(getOtpVerify()).observe(viewLifecycleOwner, {
                otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.revertAnimation()
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                    if (it.status_code == "0") {
                        mobileNumberDialog.dismiss()
                        otpDataDialog.show()
                    }
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            })
        } else {
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.revertAnimation()
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
            profileViewMode.otpResendData(getResendOtp()).observe(viewLifecycleOwner, {
                progressDialog.dismiss()
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                    otpDataDialog.findViewById<LinearLayout>(R.id.resendLayout)!!.visibility = View.GONE
                    timerRestriction()
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            })
        } else {
            progressDialog.dismiss()
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
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataVal
    }

    private fun getOtpVerify(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["otp_number"] = mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataVal
    }

    private fun getResendOtp(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["account_mobile"] = mobileNumberDialog.findViewById<EditText>(R.id.mobileNumber)!!.text.toString()
        mapDataVal["account_phone_code"] = mobileNumberDialog.findViewById<com.hbb20.CountryCodePicker>(R.id.countyCode)!!.textView_selectedCountry.text.toString()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code!!
        return mapDataVal
    }

    private fun handleOtpET() {
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
                }
            }
            setSubButton()
        }

        override fun beforeTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }

        override fun onTextChanged(arg0: CharSequence, arg1: Int, arg2: Int, arg3: Int) {
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            100 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[3] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[4] == PackageManager.PERMISSION_GRANTED) {
                    startActivity(Intent(requireActivity(), ProfileEditActivity::class.java))
                } else {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_allow_all_permission
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as DashboardActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as DashboardActivity).bottomSheetDialog.show()
                }
            }
        }
    }

    @SuppressLint("CutPasteId")
    private fun setSubButton() {
        if (otpDataDialog.findViewById<EditText>(R.id.otpPass1)?.text.toString().trim() == "" && otpDataDialog.findViewById<EditText>(R.id.otpPass2)?.text.toString().trim() == "" || otpDataDialog.findViewById<EditText>(R.id.otpPass3)?.text.toString().trim() == "" || otpDataDialog.findViewById<EditText>(R.id.otpPass4)?.text.toString().trim() == "") {
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.visibility = View.GONE
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)!!.visibility = View.VISIBLE
        } else {
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_submit)!!.visibility = View.GONE
            otpDataDialog.findViewById<CircularProgressButton>(R.id.btn_verify)!!.visibility = View.VISIBLE
        }
    }
}