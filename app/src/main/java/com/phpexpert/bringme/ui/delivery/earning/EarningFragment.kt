@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.delivery.earning

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.NotificationActivity
import com.phpexpert.bringme.activities.delivery.TransactionActivity
import com.phpexpert.bringme.activities.delivery.WithdrawActivity
import com.phpexpert.bringme.adapters.Calender12AdapterTransaction
import com.phpexpert.bringme.adapters.Calender1AdapterTransaction
import com.phpexpert.bringme.databinding.FragmentEarningBinding
import com.phpexpert.bringme.databinding.MyEarningViewLayoutDeliveryBinding
import com.phpexpert.bringme.databinding.TransactionFilterLayoutBinding
import com.phpexpert.bringme.dtos.EarningDtoList
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.EarningViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class EarningFragment : Fragment(), AuthInterface, EarningAdapter.OnClickView {

    private lateinit var fragmentEarningBinding: FragmentEarningBinding
    private lateinit var earningList: ArrayList<EarningDtoList>
    private lateinit var mainArrayList: ArrayList<EarningDtoList>
    private lateinit var earningModel: EarningViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private var searOrderString: String = ""
    private lateinit var jobViewBinding: MyEarningViewLayoutDeliveryBinding

    private lateinit var mBottomSheetFilterOriginal: BottomSheetBehavior<View>
    private lateinit var filterBinding: TransactionFilterLayoutBinding

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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentEarningBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_earning, container, false)
        fragmentEarningBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        languageDtoData = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        earningModel = ViewModelProvider(this).get(EarningViewModel::class.java)

        fragmentEarningBinding.earningRV.layoutManager = LinearLayoutManager(requireActivity())
        jobViewBinding = fragmentEarningBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        jobViewBinding.languageModel = languageDtoData

        fragmentEarningBinding.filterIcon.setOnClickListener {
            fragmentEarningBinding.blurView.visibility = View.VISIBLE
            mBottomSheetFilterOriginal.state = BottomSheetBehavior.STATE_EXPANDED
        }

        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        filterBinding = fragmentEarningBinding.filterLayout
        mBottomSheetFilterOriginal = BottomSheetBehavior.from(filterBinding.root)
        filterBinding.languageModel = languageDtoData

        mBottomSheetFilterOriginal.isDraggable = false
        mBottomSheetFilterOriginal.peekHeight = 0

        setCalenderInitial1()
        setCalenderInitial2()

        setText1()
        setText2()

        fragmentEarningBinding.blurView.setOnClickListener {
            fragmentEarningBinding.blurView.visibility = View.GONE
            mBottomSheetFilterOriginal.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        fragmentEarningBinding.notificationIcon.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }

        filterBinding.btnSubmit.setOnClickListener {
            when {
                filterBinding.startDate.text!!.isEmpty() -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().start_date_mandatory
                    (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
                filterBinding.endDate.text!!.isEmpty() -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().end_date_mandatory
                    (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
                !compareDates(filterBinding.startDate.text.toString(), filterBinding.endDate.text.toString()) -> {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().compare_date
                    (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
                else -> {
                    fragmentEarningBinding.blurView.visibility = View.GONE
                    mBottomSheetFilterOriginal.state = BottomSheetBehavior.STATE_COLLAPSED
                    progressDialog.show()
                    setObserver()
                }
            }
        }

        fragmentEarningBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()
        fragmentEarningBinding.currencyCode1.text = (context as BaseActivity).getCurrencySymbol()
        fragmentEarningBinding.earningRV.isNestedScrollingEnabled = false
        earningList = ArrayList()
        mainArrayList = ArrayList()
        fragmentEarningBinding.earningRV.adapter = EarningAdapter(requireActivity(), earningList, this)
        fragmentEarningBinding.transactionLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), TransactionActivity::class.java))
        }

        fragmentEarningBinding.searchIcon.setOnClickListener {
            fragmentEarningBinding.layoutSearchData.visibility = View.VISIBLE
            fragmentEarningBinding.searchIcon.visibility = View.GONE
        }

        fragmentEarningBinding.searchIconEdit.setOnClickListener {
            if (fragmentEarningBinding.searchET.text.toString().trim().isNotEmpty()) {
                searOrderString = fragmentEarningBinding.searchET.text.toString()
                progressDialog.show()
                setObserver()
            }
        }
        fragmentEarningBinding.closeIcon.setOnClickListener {
            fragmentEarningBinding.searchET.text = Editable.Factory.getInstance().newEditable("")
            fragmentEarningBinding.layoutSearchData.visibility = View.GONE
            fragmentEarningBinding.searchIcon.visibility = View.VISIBLE
            fragmentEarningBinding.noDataFoundLayout.visibility = View.GONE
            fragmentEarningBinding.myEarningScrollView.visibility = View.VISIBLE
            this.searOrderString = ""
            earningList.clear()
            earningList.addAll(mainArrayList)
            fragmentEarningBinding.earningRV.adapter?.notifyDataSetChanged()
        }

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage(languageDtoData.please_wait)
        progressDialog.setCancelable(false)
        progressDialog.show()
        setObserver()

        return fragmentEarningBinding.root
    }

    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if ((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != null && (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                earningModel.getLatestJobData(mapData()).observe(viewLifecycleOwner, {
                    when (it.status_code) {
                        "0" -> {
                            progressDialog.dismiss()

                            fragmentEarningBinding.withdrawLayout.setOnClickListener { _ ->
                                val intent = Intent(requireActivity(), WithdrawActivity::class.java)
                                intent.putExtra("totalAvailableAmount", it.Total_Earned_Order_Amount)
                                startActivity(intent)
                            }

                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                            fragmentEarningBinding.noDataFoundLayout.visibility = View.GONE
                            fragmentEarningBinding.myEarningScrollView.visibility = View.VISIBLE
                            fragmentEarningBinding.deliveredOrders.text = it.Total_Delivered_Orders
                            fragmentEarningBinding.canceledOrders.text = it.Total_Cancelled_Orders
                            fragmentEarningBinding.totalAmount.text = it.Total_Delivered_Order_Amount.formatChange()
                            fragmentEarningBinding.totalEarning.text = it.Total_Earned_Order_Amount.formatChange()
                            earningList.clear()
                            earningList.addAll(it.data!!.DeliveryEMPEarningList!!)
                            if (searOrderString == "") {
                                mainArrayList.clear()
                                mainArrayList.addAll(earningList)
                            }
                            fragmentEarningBinding.earningRV.adapter!!.notifyDataSetChanged()

                        }
                        "2" -> {
                            (activity as BaseActivity).hitAuthApi(this@EarningFragment)
                        }
                        "1" -> {
                            progressDialog.dismiss()
                            fragmentEarningBinding.noDataFoundLayout.visibility = View.VISIBLE
                            fragmentEarningBinding.myEarningScrollView.visibility = View.GONE
                        }
                        else -> {
                            progressDialog.dismiss()
                            if (it.status_code == "11")
                                (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            else
                                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                                (activity as BaseActivity).bottomSheetDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.show()
                        }
                    }
                })
            } else {
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = languageDtoData.network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }

    }

    private fun mapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["start_date"] = filterBinding.startDate.text.toString()
        mapDataVal["end_date"] = filterBinding.endDate.text.toString()
        mapDataVal["Order_Number"] = searOrderString
        return mapDataVal
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            setObserver()
        } else {
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = message
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(textInput: String, position: Int) {
        jobViewBinding.closeView.setOnClickListener {
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
            fragmentEarningBinding.blurView.visibility = View.GONE
        }

        jobViewBinding.currencyCode.text = (activity as BaseActivity).getCurrencySymbol()
        jobViewBinding.currencyCode1.text = (activity as BaseActivity).getCurrencySymbol()
        jobViewBinding.currencyCode2.text = (activity as BaseActivity).getCurrencySymbol()
        jobViewBinding.currencyCode4.text = (activity as BaseActivity).getCurrencySymbol()


        Glide.with(requireActivity()).load(earningList[position]).centerCrop().placeholder(R.drawable.user_placeholder).into(jobViewBinding.userImage)
        /* arrayList[position].job_sub_total = arrayList[position].job_sub_total.formatChange()
         arrayList[position].Charge_for_Jobs = arrayList[position].Charge_for_Jobs.formatChange()
         arrayList[position].job_total_amount = arrayList[position].job_total_amount.formatChange()*/
        jobViewBinding.data = earningList[position]
        jobViewBinding.jobPostedDate.text = orderDateValue(earningList[position].job_post_date!!)
        jobViewBinding.jobPostedTime.text = jobPostedTime(earningList[position].job_posted_time!!)
        jobViewBinding.jobSubTotal.text = earningList[position].job_sub_total.formatChange()
//                android:text='@{languageModel.admin_charges_for_job+" ("+data.charge_for_Jobs_Admin_percentage+"%)"}'
//                '@{data.job_tax_amount.equalsIgnoreCase("0") || data.job_tax_amount.equalsIgnoreCase("") ? View.GONE : View.VISIBLE}'
//                adminServiceFeesCharge
        jobViewBinding.jobSubTotal1.text = earningList[position].job_sub_total.formatChange()

        jobViewBinding.totalAmount.text = "${earningList[position].job_total_amount.formatChange()}/-"
        jobViewBinding.serviceCharges.text = earningList[position].Charge_for_Jobs.formatChange()
        if (earningList[position].job_tax_amount == "0" || earningList[position].job_tax_amount == ("")) {
            jobViewBinding.adminServiceFeesLayout.visibility = View.GONE
        } else {
            jobViewBinding.adminServiceFeesLayout.visibility = View.VISIBLE
            jobViewBinding.adminServiceFees.text = languageDtoData.admin_charges_for_job + " (" + earningList[position].Charge_for_Jobs_Admin_percentage + "%)"
            jobViewBinding.adminServiceFeesCharge.text = earningList[position].admin_service_fees.formatChange()
        }

        try {
            jobViewBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(earningList[position].order_status_color_code))
            jobViewBinding.orderStatus.setTextColor(Color.parseColor(earningList[position].order_status_text_color_code))
        } catch (e: Exception) {

        }
        mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
        fragmentEarningBinding.blurView.visibility = View.VISIBLE
    }

    @SuppressLint("SimpleDateFormat")
    private fun orderDateValue(dateTime: String): String? {
        val inputPattern = "yyyy-MM-dd"
        val outputPattern = "dd MMM yyyy"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String? = null

        try {
            date = inputFormat.parse(dateTime)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str
    }

    @SuppressLint("SimpleDateFormat")
    private fun jobPostedTime(dateTime: String): String? {
        val inputPattern = "hh:mm"
        val outputPattern = "hh:mm aa"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)

        val date: Date?
        var str: String? = null

        try {
            date = inputFormat.parse(dateTime)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return str
    }

    private fun String?.formatChange() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale((activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale((activity as BaseActivity).sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage), (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.country_code!!))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
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

        filterBinding.startDateCalenderIcon.setOnClickListener {
            if (filterBinding.calenderLayout.visibility == View.VISIBLE) {
                filterBinding.calenderLayout.visibility = View.GONE
            } else {
                filterBinding.calenderLayout.visibility = View.VISIBLE
            }
        }

        commentDateAdapter = Calender1AdapterTransaction(requireActivity(), filterBinding)
        filterBinding.calenderRV.layoutManager = GridLayoutManager(requireActivity(), 7)
        filterBinding.calenderRV.isNestedScrollingEnabled = false
        filterBinding.calenderRV.adapter = commentDateAdapter

        filterBinding.previousMonth.setOnClickListener {
            setPreviousMonthYear1()
        }
        filterBinding.nextMonth.setOnClickListener {
            setNextMonthYear1()
        }
        calenderInstance = Calendar.getInstance()
        todayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        todayYear = Calendar.getInstance().get(Calendar.YEAR)
        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear, filterBinding.startDate.text.toString())
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

        filterBinding.endDateCalenderIcon.setOnClickListener {
            if (filterBinding.calender2Layout.visibility == View.VISIBLE) {
                filterBinding.calender2Layout.visibility = View.GONE
            } else {
                filterBinding.calender2Layout.visibility = View.VISIBLE
            }
        }

        commentDateAdapter1 = Calender12AdapterTransaction(requireActivity(), filterBinding)
        filterBinding.calenderRV2.layoutManager = GridLayoutManager(requireActivity(), 7)
        filterBinding.calenderRV2.isNestedScrollingEnabled = false
        filterBinding.calenderRV2.adapter = commentDateAdapter1

        filterBinding.previousMonth2.setOnClickListener {
            setPreviousMonthYear2()
        }
        filterBinding.nextMonth2.setOnClickListener {
            setNextMonthYear2()
        }
        calenderInstance1 = Calendar.getInstance()
        todayMonth1 = Calendar.getInstance().get(Calendar.MONTH) + 1
        todayYear1 = Calendar.getInstance().get(Calendar.YEAR)
        currentMonth1 = calenderInstance1.get(Calendar.MONTH)
        currentYear1 = calenderInstance1.get(Calendar.YEAR)
        commentDateAdapter1.printDate(currentMonth1, currentYear1, filterBinding.endDate.text.toString())
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
        commentDateAdapter.printDate(currentMonth, currentYear, filterBinding.startDate.text.toString())
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
        commentDateAdapter.printDate(currentMonth, currentYear, filterBinding.startDate.text.toString())
        setText1()
    }

    @SuppressLint("SetTextI18n")
    private fun setText1() {
        if (currentMonth == todayMonth - 1 && currentYear == todayYear) {
            filterBinding.nextMonth.visibility = View.GONE
        } else filterBinding.nextMonth.visibility = View.VISIBLE
        filterBinding.currentMonth.text = "${month[currentMonth]}  $currentYear"
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
        commentDateAdapter1.printDate(currentMonth1, currentYear1, filterBinding.endDate.text.toString())
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
        commentDateAdapter1.printDate(currentMonth1, currentYear1, filterBinding.endDate.text.toString())
        setText2()
    }

    @SuppressLint("SetTextI18n")
    private fun setText2() {
        if (currentMonth1 == todayMonth1 - 1 && currentYear1 == todayYear1) {
            filterBinding.nextMonth2.visibility = View.GONE
        } else filterBinding.nextMonth2.visibility = View.VISIBLE
        filterBinding.currentMonth2.text = "${month1[currentMonth1]}  $currentYear1"
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