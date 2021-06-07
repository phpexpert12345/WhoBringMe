@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities.employee

import android.Manifest
import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.JobViewLayoutBinding
import com.phpexpert.bringme.databinding.PaymentSuccessfullPageBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.interfaces.PermissionInterface
import com.phpexpert.bringme.models.JobPostModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.util.concurrent.TimeUnit
import kotlin.Exception

@Suppress("DEPRECATION")
class CongratulationScreen : BaseActivity(), AuthInterface, PermissionInterface {

    private lateinit var congratulationScreenBinding: PaymentSuccessfullPageBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding
    private lateinit var servicePostValue: PostJobPostDto
    private lateinit var jobViewModel: JobPostModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var languageDtoData: LanguageDtoData
    private var counting: Int = 10
    private lateinit var apiName: String
    private lateinit var phoneNumber: String

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        permissionInterface = this
        congratulationScreenBinding = DataBindingUtil.setContentView(this, R.layout.payment_successfull_page)
        congratulationScreenBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()
        initValues()
        setActions()
        setCountDownTimer()
    }

    @SuppressLint("SetTextI18n")
    private fun initValues() {

        congratulationScreenBinding.currencyCode.text = getCurrencySymbol()
        congratulationScreenBinding.userName.text = "${sharedPrefrenceManager.getProfile().login_name},"
        congratulationScreenBinding.userName1.text = "${languageDtoData.hello} ${sharedPrefrenceManager.getProfile().login_name},"
        servicePostValue = intent.getSerializableExtra("postValue") as PostJobPostDto
        jobViewBinding = congratulationScreenBinding.jobViewLayout
        jobViewBinding.languageModel = languageDtoData
        servicePostValue.Charge_for_Jobs = servicePostValue.Charge_for_Jobs.formatChange()
        servicePostValue.admin_service_fees = servicePostValue.admin_service_fees.formatChange()
        servicePostValue.grandTotal = servicePostValue.grandTotal.formatChange()
        servicePostValue.jobAmount = servicePostValue.jobAmount.formatChange()
        jobViewBinding.jobDetails = servicePostValue

        jobViewBinding.currencyCode.text = getCurrencySymbol()
        jobViewBinding.currencyCode1.text = getCurrencySymbol()
        jobViewBinding.currencyCode2.text = getCurrencySymbol()
        jobViewBinding.currencyCode3.text = getCurrencySymbol()
        jobViewBinding.currencyCode4.text = getCurrencySymbol()
        congratulationScreenBinding.orderId.text = servicePostValue.jobId
        congratulationScreenBinding.orderId1.text = servicePostValue.jobId

        congratulationScreenBinding.grandTotalAmount.text = servicePostValue.grandTotal
        congratulationScreenBinding.grandTotalAmount1.text = servicePostValue.grandTotal
        congratulationScreenBinding.currencyCode.text = getCurrencySymbol()
        congratulationScreenBinding.currencyCode1.text = getCurrencySymbol()

        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        jobViewModel = ViewModelProvider(this).get(JobPostModel::class.java)
        mBottomSheetFilter.peekHeight = 0

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(languageDtoData.please_wait)
        progressDialog.setCancelable(false)

    }

    @SuppressLint("SetTextI18n")
    private fun setActions() {
        congratulationScreenBinding.viewJobIcon.setOnClickListener {
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
            congratulationScreenBinding.jobViewBlur.visibility = View.VISIBLE
            jobViewBinding.closeView.setOnClickListener {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                congratulationScreenBinding.jobViewBlur.visibility = View.GONE
            }
        }
        congratulationScreenBinding.viewJobIcon1.setOnClickListener {
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
            congratulationScreenBinding.jobViewBlur.visibility = View.VISIBLE
            jobViewBinding.closeView.setOnClickListener {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                congratulationScreenBinding.jobViewBlur.visibility = View.GONE
            }
        }

        congratulationScreenBinding.backArrow.setOnClickListener {
            finish()
        }

        congratulationScreenBinding.homeButton.setOnClickListener {
            try {
                startActivity(Intent(this, DashboardActivity::class.java))
                finishAffinity()
            } catch (e: Exception) {
                bottomSheetDialogMessageText.text = "Some issue from server side we are resolve it"
                bottomSheetDialogHeadingText.visibility = View.GONE
                bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
                bottomSheetDialogMessageCancelButton.visibility = View.GONE
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        }

        congratulationScreenBinding.cancelButton.setOnClickListener {
            bottomSheetDialogMessageText.text = languageDtoData.are_you_sure_you_want_to_cancel_this_job
            bottomSheetDialogMessageCancelButton.text = languageDtoData.no
            bottomSheetDialogMessageOkButton.text = languageDtoData.yes
            bottomSheetDialogMessageCancelButton.visibility = View.VISIBLE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                congratulationScreenBinding.cancelButton.startAnimation()
                cancelJobObserver()
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialogMessageCancelButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }

            bottomSheetDialog.show()
        }
    }

    private fun setCountDownTimer() {
        try {
            val counterHour: IntArray = if (servicePostValue.jobTime!!.toInt() % 60 == 0) {
                intArrayOf((servicePostValue.jobTime!!.toInt() / 60) - 1)
            } else {
                intArrayOf((servicePostValue.jobTime!!.toInt() / 60))
            }
            var countMint = intArrayOf((servicePostValue.jobTime!!.toInt() % 60) - 1)
            var countSecond = intArrayOf(59)
            countDownTimer = object : CountDownTimer(TimeUnit.MINUTES.toMillis(servicePostValue.jobTime!!.toLong()), 1000) {
                override fun onTick(p0: Long) {
                    if (counterHour[0] > 0)
                        congratulationScreenBinding.hoursTV.text = counterHour[0].toString()
                    else {
                        congratulationScreenBinding.hoursTV.text = "0"
                    }
                    if (countMint[0] > 0) {
                        congratulationScreenBinding.minute1TV.text = (countMint[0] / 10).toString()
                        congratulationScreenBinding.minute2TV.text = (countMint[0] % 10).toString()
                    } else {
                        congratulationScreenBinding.minute1TV.text = "0"
                        congratulationScreenBinding.minute2TV.text = "0"
                    }
                    congratulationScreenBinding.second1TV.text = (countSecond[0] / 10).toString()
                    congratulationScreenBinding.second2TV.text = (countSecond[0] % 10).toString()
                    if (countSecond[0] == 0) {
                        countMint[0]--
                        countSecond = intArrayOf(59)
                    } else {
                        countSecond[0]--
                    }

                    if (countSecond[0] % 3 == 0) {
                        getJobDetailsObserver()
                    }

                    if (countMint[0] == 0 && counterHour[0] != 0) {
                        counterHour[0]--
                        countMint = intArrayOf(59)
                    }
                }

                @SuppressLint("CutPasteId")
                override fun onFinish() {
                    bottomSheetDialogMessageText.text = languageDtoData.we_did_not_find_any_delivery_employee_within
                    bottomSheetDialogHeadingText.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.text = languageDtoData.yes
                    bottomSheetDialogMessageCancelButton.text = languageDtoData.no
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        val updateTimeDialog = BottomSheetDialog(this@CongratulationScreen)
                        updateTimeDialog.setContentView(R.layout.update_time_layout)
                        updateTimeDialog.findViewById<TextView>(R.id.minuteTv)?.text = languageDtoData.minute
                        updateTimeDialog.findViewById<TextView>(R.id.okText)?.text = languageDtoData.ok_text
                        val minusImageView = updateTimeDialog.findViewById<ImageView>(R.id.minusIcon)
                        val plusImageView = updateTimeDialog.findViewById<ImageView>(R.id.plusIcon)
                        val okTextView = updateTimeDialog.findViewById<TextView>(R.id.okText)
                        val mintsTextView = updateTimeDialog.findViewById<TextView>(R.id.mintsTextView)

                        plusImageView!!.setOnClickListener {
                            if (counting < 170) {
                                counting += 10
                                mintsTextView!!.text = counting.toString()
                            }
                        }

                        minusImageView!!.setOnClickListener {
                            if (counting != 10) {
                                counting -= 10
                                mintsTextView!!.text = counting.toString()
                            }
                        }

                        okTextView!!.setOnClickListener {
                            servicePostValue.jobTime = mintsTextView!!.text.toString()
                            progressDialog.show()
                            updateJobObserver()
                            updateTimeDialog.dismiss()
                        }

                        updateTimeDialog.show()
                    }
                    bottomSheetDialogMessageCancelButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                        progressDialog.show()
                        cancelJobObserver()
                    }
                    bottomSheetDialog.show()
                }

            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            countDownTimer.cancel()
        } catch (e: Exception) {
        }

    }


    private fun cancelJobObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                jobViewModel.cancelJob(getCancelMap()).observe(this, {
                    when (it.status_code) {
                        "0" -> {
                            try {
                                congratulationScreenBinding.cancelButton.revertAnimation()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageText.text = it.status_message
                            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                            bottomSheetDialogHeadingText.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                countDownTimer.cancel()
                                bottomSheetDialog.dismiss()
                                startActivity(Intent(this, DashboardActivity::class.java))
                                finishAffinity()
                            }
                        }
                        "2" -> {
                            apiName = "cancel"
                            hitAuthApi(this)
                        }
                        else -> {
                            try {
                                congratulationScreenBinding.cancelButton.revertAnimation()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                            if (it.status_code == "11")
                                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            else bottomSheetDialogMessageText.text = it.status_message

                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                        }
                    }
                    bottomSheetDialog.show()
                })
            } else {
                apiName = "cancel"
                hitAuthApi(this)
            }
        } else {
            congratulationScreenBinding.cancelButton.revertAnimation()
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun updateJobObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                jobViewModel.updateJob(getUpdateMap()).observe(this, {

                    when (it.status_code) {
                        "0" -> {
                            progressDialog.dismiss()
                            try {
                                congratulationScreenBinding.cancelButton.revertAnimation()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                            bottomSheetDialogHeadingText.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                                countDownTimer.cancel()
                                setCountDownTimer()
                            }
                        }
                        "2" -> {
                            apiName = "update"
                            hitAuthApi(this)
                        }
                        else -> {
                            progressDialog.dismiss()
                            try {
                                congratulationScreenBinding.cancelButton.revertAnimation()
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                            if (it.status_code == "11")
                                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            else bottomSheetDialogMessageText.text = it.status_message

                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.setOnClickListener {
                                bottomSheetDialog.dismiss()
                            }
                        }
                    }
                    bottomSheetDialog.show()
                })
            } else {
                apiName = "update"
                hitAuthApi(this)
            }
        } else {
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun getJobDetailsObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                jobViewModel.getJobDetails(getJobDetailsMap()).observe(this, {
                    if (it.status_code == "0") {
                        if (it.data!!.OrderDetailList!![0].job_status == "1") {
                            congratulationScreenBinding.timingDataLayout.visibility = View.GONE
                            congratulationScreenBinding.userAcceptedData.visibility = View.VISIBLE
                            congratulationScreenBinding.homeButton.visibility = View.VISIBLE
                            countDownTimer.cancel()
                            Glide.with(this).load(it.data!!.OrderDetailList!![0].Delivery_Employee_photo).placeholder(R.drawable.user_placeholder).into(congratulationScreenBinding.deliveryImageView)
                            congratulationScreenBinding.callNumber.setOnClickListener { _ ->
                                phoneNumber = it.data!!.OrderDetailList!![0].Delivery_Employee_phone_code + it.data!!.OrderDetailList!![0].Delivery_Employee_phone
                                if (isCheckPermissions(this, perission)) {
                                    val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
                                    startActivity(intent)
                                }
                            }
                            congratulationScreenBinding.userName2.text = it.data!!.OrderDetailList!![0].Delivery_Employee_name
                            congratulationScreenBinding.titleString.text = it.data!!.OrderDetailList!![0].thank_you_title
                            congratulationScreenBinding.messageString.text = it.data!!.OrderDetailList!![0].thank_you_content
                            congratulationScreenBinding.userMobileNo.text = it.data!!.OrderDetailList!![0].Delivery_Employee_phone_code + " " + it.data!!.OrderDetailList!![0].Delivery_Employee_phone
                        }
                    }
                })
            } else {
                apiName = "jobDetails"
                hitAuthApi(this)
            }
        } else {
            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().network_error
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun getJobDetailsMap(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["job_order_id"] = servicePostValue.jobId!!
        mapData["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapData["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapData
    }

    private fun getCancelMap(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["job_order_id"] = servicePostValue.jobId!!
        mapData["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapData["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapData
    }

    private fun getUpdateMap(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["job_order_id"] = servicePostValue.jobId!!
        mapData["job_offer_time"] = servicePostValue.jobTime!! + " minutes"
        mapData["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapData["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapData
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            when (apiName) {
                "cancel" -> cancelJobObserver()
                "update" -> updateJobObserver()
                "jobDetails" -> getJobDetailsObserver()
            }
        } else {
            try {
                congratulationScreenBinding.cancelButton.revertAnimation()
            } catch (e: Exception) {
            }
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

    override fun isPermission(value: Boolean) {
        if (value) {
            val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:$phoneNumber"))
            startActivity(intent)
        }
    }
}