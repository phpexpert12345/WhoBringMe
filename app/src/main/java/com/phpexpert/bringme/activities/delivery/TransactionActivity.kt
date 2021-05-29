@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.activities.delivery

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.adapters.Calender12AdapterTransaction
import com.phpexpert.bringme.adapters.Calender1AdapterTransaction
import com.phpexpert.bringme.adapters.TransactionHistoryAdapter
import com.phpexpert.bringme.databinding.LayoutTransactionHistoryActivityBinding
import com.phpexpert.bringme.databinding.TransactionFilterLayoutBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.TransactionDtoList
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.EarningViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class TransactionActivity : BaseActivity(), AuthInterface {
    private lateinit var transactionActivity: LayoutTransactionHistoryActivityBinding
    private lateinit var arrayList: ArrayList<TransactionDtoList>
    private lateinit var earningModel: EarningViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: TransactionFilterLayoutBinding
    private val month = ArrayList<String>()
    private var todayMonth = -1
    private var todayYear = -1
    private var currentMonth: Int = -1
    private var currentYear: Int = -1
    private lateinit var calenderInstance: Calendar
    private lateinit var commentDateAdapter: Calender1AdapterTransaction

    private val month1 = ArrayList<String>()
    private var todayMonth1 = -1
    private var todayYear1 = -1
    private var currentMonth1: Int = -1
    private var currentYear1: Int = -1
    private lateinit var calenderInstance1: Calendar
    private lateinit var commentDateAdapter1: Calender12AdapterTransaction


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionActivity = DataBindingUtil.setContentView(this, R.layout.layout_transaction_history_activity)
        transactionActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()
        earningModel = ViewModelProvider(this).get(EarningViewModel::class.java)

        jobViewBinding = transactionActivity.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        jobViewBinding.languageModel = languageDtoData

        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        setCalenderInitial1()
        setCalenderInitial2()

        setText1()
        setText2()

        transactionActivity.filterIcon.setOnClickListener {
            transactionActivity.blurView.visibility = View.VISIBLE
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
        }


        jobViewBinding.closeIcon.setOnClickListener {
            transactionActivity.blurView.visibility = View.GONE
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        jobViewBinding.btnSubmit.setOnClickListener {
            when {
                jobViewBinding.startDate.text!!.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().start_date_mandatory
                    bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                jobViewBinding.endDate.text!!.isEmpty() -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().end_date_mandatory
                    bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                !compareDates(jobViewBinding.startDate.text.toString(), jobViewBinding.endDate.text.toString()) -> {
                    bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().compare_date
                    bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    bottomSheetDialogMessageOkButton.setOnClickListener {
                        bottomSheetDialog.dismiss()
                    }
                    bottomSheetDialog.show()
                }
                else -> {
                    progressDialog.show()
                    setObserver()
                }
            }
        }

        transactionActivity.backArrow.setOnClickListener {
            finish()
        }

        transactionActivity.historyTransactionRV.layoutManager = LinearLayoutManager(this)
        transactionActivity.historyTransactionRV.isNestedScrollingEnabled = false
        arrayList = ArrayList()
        transactionActivity.historyTransactionRV.adapter = TransactionHistoryAdapter(this, arrayList)

        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(languageDtoData.please_wait)
        progressDialog.setCancelable(false)
        progressDialog.show()
        setObserver()
    }

    private fun setObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                earningModel.getTransactionData(mapData()).observe(this, {
                    when (it.status_code) {
                        "0" -> {
                            progressDialog.dismiss()
                            arrayList.clear()
                            arrayList.addAll(it.data.Transaction_History!!)
                            transactionActivity.blurView.visibility = View.GONE
                            mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                            transactionActivity.historyTransactionRV.adapter!!.notifyDataSetChanged()
                        }
                        "2" -> {
                            hitAuthApi(this@TransactionActivity)
                        }
                        else -> {
                            progressDialog.dismiss()
                            if (it.status_code == "11")
                                bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            else
                                bottomSheetDialogMessageText.text = it.status_message
                            bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
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
            progressDialog.dismiss()
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            bottomSheetDialogHeadingText.visibility = View.GONE
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun mapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapDataVal["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["start_date"] = changeDateTime2(jobViewBinding.startDate.text.toString())!!
        mapDataVal["end_date"] = changeDateTime2(jobViewBinding.endDate.text.toString())!!
        mapDataVal["lang_code"] = sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataVal
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            setObserver()
        } else {
            progressDialog.dismiss()
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

    private fun setCalenderInitial1() {
        month.add(languageDtoData.january)
        month.add(languageDtoData.february)
        month.add(languageDtoData.march)
        month.add(languageDtoData.april)
        month.add(languageDtoData.may)
        month.add(languageDtoData.june)
        month.add(languageDtoData.july)
        month.add(languageDtoData.august)
        month.add(languageDtoData.september)
        month.add(languageDtoData.october)
        month.add(languageDtoData.november)
        month.add(languageDtoData.december)

        jobViewBinding.startDateCalenderIcon.setOnClickListener {
            if (jobViewBinding.calenderLayout.visibility == View.VISIBLE) {
                jobViewBinding.calenderLayout.visibility = View.GONE
            } else {
                jobViewBinding.calenderLayout.visibility = View.VISIBLE
            }
        }

        commentDateAdapter = Calender1AdapterTransaction(this, jobViewBinding)
        jobViewBinding.calenderRV.layoutManager = GridLayoutManager(this, 7)
        jobViewBinding.calenderRV.isNestedScrollingEnabled = false
        jobViewBinding.calenderRV.adapter = commentDateAdapter

        jobViewBinding.previousMonth.setOnClickListener {
            setPreviousMonthYear1()
        }
        jobViewBinding.nextMonth.setOnClickListener {
            setNextMonthYear1()
        }
        calenderInstance = Calendar.getInstance()
        todayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        todayYear = Calendar.getInstance().get(Calendar.YEAR)
        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear, jobViewBinding.startDate.text.toString())
    }

    private fun setCalenderInitial2() {
        month1.add(languageDtoData.january)
        month1.add(languageDtoData.february)
        month1.add(languageDtoData.march)
        month1.add(languageDtoData.april)
        month1.add(languageDtoData.may)
        month1.add(languageDtoData.june)
        month1.add(languageDtoData.july)
        month1.add(languageDtoData.august)
        month1.add(languageDtoData.september)
        month1.add(languageDtoData.october)
        month1.add(languageDtoData.november)
        month1.add(languageDtoData.december)

        jobViewBinding.endDateCalenderIcon.setOnClickListener {
            if (jobViewBinding.calender2Layout.visibility == View.VISIBLE) {
                jobViewBinding.calender2Layout.visibility = View.GONE
            } else {
                jobViewBinding.calender2Layout.visibility = View.VISIBLE
            }
        }

        commentDateAdapter1 = Calender12AdapterTransaction(this, jobViewBinding)
        jobViewBinding.calenderRV2.layoutManager = GridLayoutManager(this, 7)
        jobViewBinding.calenderRV2.isNestedScrollingEnabled = false
        jobViewBinding.calenderRV2.adapter = commentDateAdapter1

        jobViewBinding.previousMonth2.setOnClickListener {
            setPreviousMonthYear2()
        }
        jobViewBinding.nextMonth2.setOnClickListener {
            setNextMonthYear2()
        }
        calenderInstance1 = Calendar.getInstance()
        todayMonth1 = Calendar.getInstance().get(Calendar.MONTH) + 1
        todayYear1 = Calendar.getInstance().get(Calendar.YEAR)
        currentMonth1 = calenderInstance1.get(Calendar.MONTH)
        currentYear1 = calenderInstance1.get(Calendar.YEAR)
        commentDateAdapter1.printDate(currentMonth1, currentYear1, jobViewBinding.endDate.text.toString())
    }

    private fun setPreviousMonthYear1() {
        if (currentMonth == 0) {
            calenderInstance.set(Calendar.MONTH, 11)
            calenderInstance.set(Calendar.YEAR, currentYear - 1)
        } else {
            calenderInstance.set(Calendar.MONTH, currentMonth - 1)
            calenderInstance.set(Calendar.YEAR, currentYear)
        }

        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear, jobViewBinding.startDate.text.toString())
        setText1()
    }

    private fun setNextMonthYear1() {
        if (currentMonth == 11) {
            calenderInstance.set(Calendar.MONTH, 0)
            calenderInstance.set(Calendar.YEAR, currentYear + 1)
        } else {
            calenderInstance.set(Calendar.MONTH, currentMonth + 1)
            calenderInstance.set(Calendar.YEAR, currentYear)
        }

        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear, jobViewBinding.startDate.text.toString())
        setText1()
    }

    @SuppressLint("SetTextI18n")
    private fun setText1() {
        if (currentMonth == todayMonth - 1 && currentYear == todayYear) {
            jobViewBinding.nextMonth.visibility = View.GONE
        } else jobViewBinding.nextMonth.visibility = View.VISIBLE
        jobViewBinding.currentMonth.text = "${month[currentMonth]}  $currentYear"
    }

    private fun setPreviousMonthYear2() {
        if (currentMonth1 == 0) {
            calenderInstance1.set(Calendar.MONTH, 11)
            calenderInstance1.set(Calendar.YEAR, currentYear1 - 1)
        } else {
            calenderInstance1.set(Calendar.MONTH, currentMonth1 - 1)
            calenderInstance1.set(Calendar.YEAR, currentYear1)
        }

        currentMonth1 = calenderInstance1.get(Calendar.MONTH)
        currentYear1 = calenderInstance1.get(Calendar.YEAR)
        commentDateAdapter1.printDate(currentMonth1, currentYear1, jobViewBinding.endDate.text.toString())
        setText2()
    }

    private fun setNextMonthYear2() {
        if (currentMonth1 == 11) {
            calenderInstance1.set(Calendar.MONTH, 0)
            calenderInstance1.set(Calendar.YEAR, currentYear1 + 1)
        } else {
            calenderInstance1.set(Calendar.MONTH, currentMonth1 + 1)
            calenderInstance1.set(Calendar.YEAR, currentYear1)
        }

        currentMonth1 = calenderInstance1.get(Calendar.MONTH)
        currentYear1 = calenderInstance1.get(Calendar.YEAR)
        commentDateAdapter1.printDate(currentMonth1, currentYear1, jobViewBinding.endDate.text.toString())
        setText2()
    }

    @SuppressLint("SetTextI18n")
    private fun setText2() {
        if (currentMonth1 == todayMonth1 - 1 && currentYear1 == todayYear1) {
            jobViewBinding.nextMonth2.visibility = View.GONE
        } else jobViewBinding.nextMonth2.visibility = View.VISIBLE
        jobViewBinding.currentMonth2.text = "${month1[currentMonth1]}  $currentYear1"
    }

    @SuppressLint("SimpleDateFormat")
    private fun compareDates(date1: String, date2: String): Boolean {
        val inputPattern = "dd MMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)

        val date: Date?
        val dateC2: Date?
        return try {
            date = inputFormat.parse(date1)
            dateC2 = inputFormat.parse(date2)
            date.time < dateC2.time
        } catch (e: ParseException) {
            e.printStackTrace()
            false
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun changeDateTime2(dateTime: String): String? {
        val inputPattern = "dd MMM yyyy"
        val outputPattern = "yyyy-MM-dd"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String?

        try {
            date = inputFormat.parse(dateTime)
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
            str = dateTime
        }

        return str
    }
}