@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities.employee

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
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
import com.phpexpert.bringme.models.JobPostModel
import com.phpexpert.bringme.utilities.BaseActivity
import java.util.concurrent.TimeUnit
import kotlin.Exception

@Suppress("DEPRECATION")
class CongratulationScreen : BaseActivity() {

    private lateinit var congratulationScreenBinding: PaymentSuccessfullPageBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding
    private lateinit var servicePostValue: PostJobPostDto
    private lateinit var jobViewModel: JobPostModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var countDownTimer: CountDownTimer
    private lateinit var languageDtoData: LanguageDtoData
    private var counting: Int = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
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
        congratulationScreenBinding.userName.text = "${languageDtoData.hello} ${sharedPrefrenceManager.getProfile().login_name},"
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
            startActivity(Intent(this, DashboardActivity::class.java))
            finishAffinity()
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
                    congratulationScreenBinding.hoursTV.text = counterHour[0].toString()
                    congratulationScreenBinding.minute1TV.text = (countMint[0] / 10).toString()
                    congratulationScreenBinding.minute2TV.text = (countMint[0] % 10).toString()
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

                    if (countMint[0] == 0) {
                        counterHour[0]--
                        countMint = intArrayOf(59)
                    }
                }

                @SuppressLint("CutPasteId")
                override fun onFinish() {
                    bottomSheetDialogMessageText.text = languageDtoData.we_did_not_find_any_delivery_employee_within
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

    private fun cancelJobObserver() {
        jobViewModel.cancelJob(getCancelMap()).observe(this, {
            try {
                congratulationScreenBinding.cancelButton.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            if (it.status_code == "0") {
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    countDownTimer.cancel()
                    bottomSheetDialog.dismiss()
                    startActivity(Intent(this, DashboardActivity::class.java))
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

    private fun updateJobObserver() {
        jobViewModel.updateJob(getUpdateMap()).observe(this, {
            progressDialog.dismiss()
            try {
                congratulationScreenBinding.cancelButton.revertAnimation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            if (it.status_code == "0") {
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                    countDownTimer.cancel()
                    setCountDownTimer()
                }
            } else {
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    bottomSheetDialog.dismiss()
                }
            }
            bottomSheetDialog.show()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun getJobDetailsObserver() {
        jobViewModel.getJobDetails(getJobDetailsMap()).observe(this, {
            if (it.status_code == "0") {
                if (it.data!!.OrderDetailList!![0].order_status_msg == "Accepted") {
                    congratulationScreenBinding.timingDataLayout.visibility = View.GONE
                    congratulationScreenBinding.userAcceptedData.visibility = View.VISIBLE
                    congratulationScreenBinding.homeButton.visibility = View.VISIBLE
                    countDownTimer.cancel()
                    Glide.with(this).load(it.data!!.OrderDetailList!![0].Delivery_Employee_photo).placeholder(R.drawable.user_placeholder).into(congratulationScreenBinding.deliveryImageView)
                    congratulationScreenBinding.userName2.text = it.data!!.OrderDetailList!![0].Delivery_Employee_name
                    congratulationScreenBinding.userMobileNo.text = it.data!!.OrderDetailList!![0].Delivery_Employee_phone_code + " " + it.data!!.OrderDetailList!![0].Delivery_Employee_phone
                }
            }
        })
    }

    private fun getJobDetailsMap(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["job_order_id"] = servicePostValue.jobId!!
        mapData["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        return mapData
    }

    private fun getCancelMap(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["job_order_id"] = servicePostValue.jobId!!
        mapData["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        return mapData
    }

    private fun getUpdateMap(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["job_order_id"] = servicePostValue.jobId!!
        mapData["job_offer_time"] = servicePostValue.jobTime!! + " minutes"
        mapData["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapData["auth_key"] = sharedPrefrenceManager.getAuthData().auth_key!!
        return mapData
    }
}