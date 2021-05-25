@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.delivery.earning

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.delivery.TransactionActivity
import com.phpexpert.bringme.activities.delivery.WithdrawActivity
import com.phpexpert.bringme.databinding.FragmentEarningBinding
import com.phpexpert.bringme.databinding.MyEarningViewLayoutDeliveryBinding
import com.phpexpert.bringme.dtos.EarningDtoList
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.EarningViewModel
import com.phpexpert.bringme.utilities.BaseActivity
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
    private lateinit var earningModel: EarningViewModel
    private lateinit var progressDialog: ProgressDialog
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: MyEarningViewLayoutDeliveryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentEarningBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_earning, container, false)
        fragmentEarningBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        languageDtoData = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        earningModel = ViewModelProvider(this).get(EarningViewModel::class.java)

        fragmentEarningBinding.earningRV.layoutManager = LinearLayoutManager(requireActivity())
        jobViewBinding = fragmentEarningBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        jobViewBinding.languageModel = languageDtoData

        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        fragmentEarningBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()
        fragmentEarningBinding.currencyCode1.text = (context as BaseActivity).getCurrencySymbol()
        fragmentEarningBinding.earningRV.isNestedScrollingEnabled = false
        earningList = ArrayList()
        fragmentEarningBinding.earningRV.adapter = EarningAdapter(requireActivity(), earningList, this)
        fragmentEarningBinding.transactionLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), TransactionActivity::class.java))
        }
        fragmentEarningBinding.withdrawLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), WithdrawActivity::class.java))
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
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                            fragmentEarningBinding.noDataFoundLayout.visibility = View.GONE
                            fragmentEarningBinding.myEarningScrollView.visibility = View.VISIBLE
                            fragmentEarningBinding.deliveredOrders.text = it.Total_Delivered_Orders
                            fragmentEarningBinding.canceledOrders.text = it.Total_Cancelled_Orders
                            fragmentEarningBinding.totalAmount.text = it.Total_Delivered_Order_Amount.formatChange()
                            fragmentEarningBinding.totalEarning.text = it.Total_Earned_Order_Amount.formatChange()
                            earningList.clear()
                            earningList.addAll(it.data!!.DeliveryEMPEarningList!!)
                            fragmentEarningBinding.earningRV.adapter!!.notifyDataSetChanged()

                        }
                        "2" -> {
                            (activity as BaseActivity).hitAuthApi(this@EarningFragment)
                        }
                        "3" -> {
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

        earningModel.getLatestJobData(mapData())
    }

    private fun mapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
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
            val symbols = DecimalFormatSymbols(Locale((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code, (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.country_code!!))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }
}