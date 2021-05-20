@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.delivery.myjob

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
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
import com.phpexpert.bringme.adapters.CalenderAdapter
import com.phpexpert.bringme.databinding.FragmentMyJobBinding
import com.phpexpert.bringme.databinding.MyJobViewLayoutDeliveryBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.MyJobDtoList
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.MyJobDataModel
import com.phpexpert.bringme.utilities.BaseActivity
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class MyJobFragment : Fragment(), MyJobAdapter.OnClickView, AuthInterface {

    private lateinit var myJobBinding: FragmentMyJobBinding
    private lateinit var arrayList: ArrayList<MyJobDtoList>
    private lateinit var myJobModel: MyJobDataModel
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: MyJobViewLayoutDeliveryBinding
    private lateinit var progressDialog: ProgressDialog
    private lateinit var languageDtoData: LanguageDtoData
    private val month = ArrayList<String>()
    private var todayMonth = -1
    private var todayYear = -1
    private var currentMonth: Int = -1
    private var currentYear: Int = -1
    private lateinit var calenderInstance: Calendar
    private lateinit var commentDateAdapter: CalenderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        myJobBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_job, container, false)
        myJobModel = ViewModelProvider(this).get(MyJobDataModel::class.java)
        myJobBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        languageDtoData = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()

        jobViewBinding = myJobBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        jobViewBinding.languageModel = languageDtoData

        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0
        myJobBinding.jobRV.layoutManager = LinearLayoutManager(requireActivity())
        myJobBinding.jobRV.isNestedScrollingEnabled = false
        arrayList = ArrayList()
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

        myJobBinding.calenderTextLayout.setOnClickListener {
            if (myJobBinding.calenderLayout.visibility == View.VISIBLE) {
                myJobBinding.calenderLayout.visibility = View.GONE
            } else {
                myJobBinding.calenderLayout.visibility = View.VISIBLE
            }
        }
        commentDateAdapter = CalenderAdapter(requireActivity(), this)
        myJobBinding.calenderRV.layoutManager = GridLayoutManager(activity, 7)
        myJobBinding.calenderRV.isNestedScrollingEnabled = false
        myJobBinding.calenderRV.adapter = commentDateAdapter

        myJobBinding.previousMonth.setOnClickListener {
            setPreviousMonthYear()
        }
        myJobBinding.nextMonth.setOnClickListener {
            setNextMonthYear()
        }
        calenderInstance = Calendar.getInstance()
        todayMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        todayYear = Calendar.getInstance().get(Calendar.YEAR)
        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear)
        setText()
        myJobBinding.jobRV.adapter = MyJobAdapter(requireActivity(), arrayList, this)
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage(languageDtoData.please_wait)
        progressDialog.setCancelable(false)
        progressDialog.show()
        setObserver()

        return myJobBinding.root
    }

    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if ((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != null && (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                myJobModel.getMyJobData(getMapData()).observe(viewLifecycleOwner, {
                    progressDialog.dismiss()
                    if (it.status_code == "0") {
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                        myJobBinding.noDataFoundLayout.visibility = View.GONE
                        myJobBinding.nestedScrollView.visibility = View.VISIBLE
                        myJobBinding.runningOrders.text = it.Total_Orders
                        myJobBinding.totalAmount.text = it.Total_Order_Amount.formatChange()
                        arrayList.clear()
                        arrayList.addAll(it.data!!.OrderList!!)
                        myJobBinding.jobRV.adapter!!.notifyDataSetChanged()

                    } else {
                        myJobBinding.noDataFoundLayout.visibility = View.VISIBLE
                        myJobBinding.nestedScrollView.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
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

    private fun getMapData(): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataValue["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        mapDataValue["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        return mapDataValue
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(textInput: String, position: Int) {
        jobViewBinding.closeView.setOnClickListener {
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
            myJobBinding.blurView.visibility = View.GONE
        }

        jobViewBinding.currencyCode.text = (activity as BaseActivity).getCurrencySymbol()
        jobViewBinding.currencyCode1.text = (activity as BaseActivity).getCurrencySymbol()
        jobViewBinding.currencyCode2.text = (activity as BaseActivity).getCurrencySymbol()
        jobViewBinding.currencyCode4.text = (activity as BaseActivity).getCurrencySymbol()


        Glide.with(requireActivity()).load(arrayList[position]).centerCrop().placeholder(R.drawable.user_placeholder).into(jobViewBinding.userImage)
        /* arrayList[position].job_sub_total = arrayList[position].job_sub_total.formatChange()
         arrayList[position].Charge_for_Jobs = arrayList[position].Charge_for_Jobs.formatChange()
         arrayList[position].job_total_amount = arrayList[position].job_total_amount.formatChange()*/
        jobViewBinding.data = arrayList[position]
        jobViewBinding.jobPostedDate.text = orderDateValue(arrayList[position].job_post_date!!)
        jobViewBinding.jobPostedTime.text = jobPostedTime(arrayList[position].job_posted_time!!)
        jobViewBinding.jobSubTotal.text = arrayList[position].job_sub_total.formatChange()
//                android:text='@{languageModel.admin_charges_for_job+" ("+data.charge_for_Jobs_Admin_percentage+"%)"}'
//                '@{data.job_tax_amount.equalsIgnoreCase("0") || data.job_tax_amount.equalsIgnoreCase("") ? View.GONE : View.VISIBLE}'
//                adminServiceFeesCharge
        jobViewBinding.jobSubTotal1.text = arrayList[position].job_sub_total.formatChange()

        jobViewBinding.totalAmount.text = "${arrayList[position].job_total_amount.formatChange()}/-"
        jobViewBinding.serviceCharges.text = arrayList[position].Charge_for_Jobs.formatChange()
        if (arrayList[position].job_tax_amount == "0" || arrayList[position].job_tax_amount == ("")) {
            jobViewBinding.adminServiceFeesLayout.visibility = View.GONE
        } else {
            jobViewBinding.adminServiceFeesLayout.visibility = View.VISIBLE
            jobViewBinding.adminServiceFees.text = languageDtoData.admin_charges_for_job + " (" + arrayList[position].Charge_for_Jobs_Admin_percentage + "%)"
            jobViewBinding.adminServiceFeesCharge.text = arrayList[position].admin_service_fees.formatChange()
        }

        try {
            jobViewBinding.orderStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor(arrayList[position].order_status_color_code))
            jobViewBinding.orderStatus.setTextColor(Color.parseColor(arrayList[position].order_status_text_color_code))
        } catch (e: Exception) {

        }
        mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
        myJobBinding.blurView.visibility = View.VISIBLE
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
            val symbols = DecimalFormatSymbols(Locale((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code, "DE"))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }

    private fun setPreviousMonthYear() {
        if (currentMonth == 0) {
            calenderInstance.set(Calendar.MONTH, 11)
            calenderInstance.set(Calendar.YEAR, currentYear - 1)
        } else {
            calenderInstance.set(Calendar.MONTH, currentMonth - 1)
            calenderInstance.set(Calendar.YEAR, currentYear)
        }

        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear)
        setText()
    }

    private fun setNextMonthYear() {
        if (currentMonth == 11) {
            calenderInstance.set(Calendar.MONTH, 0)
            calenderInstance.set(Calendar.YEAR, currentYear + 1)
        } else {
            calenderInstance.set(Calendar.MONTH, currentMonth + 1)
            calenderInstance.set(Calendar.YEAR, currentYear)
        }

        currentMonth = calenderInstance.get(Calendar.MONTH)
        currentYear = calenderInstance.get(Calendar.YEAR)
        commentDateAdapter.printDate(currentMonth, currentYear)
        setText()
    }

    @SuppressLint("SetTextI18n")
    private fun setText() {
        if (currentMonth == todayMonth - 1 && currentYear == todayYear) {
            myJobBinding.nextMonth.visibility = View.GONE
        } else myJobBinding.nextMonth.visibility = View.VISIBLE
        myJobBinding.currentMonth.text = "${month[currentMonth]}  $currentYear"
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

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            progressDialog.dismiss()
        } catch (e: Exception) {

        }
    }
}