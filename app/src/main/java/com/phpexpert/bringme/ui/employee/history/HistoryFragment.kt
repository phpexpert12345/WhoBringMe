@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.employee.history

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.EmployeeFragmentHistoryBinding
import com.phpexpert.bringme.databinding.JobViewLayoutBinding
import com.phpexpert.bringme.databinding.WriteReviewLayoutBinding
import com.phpexpert.bringme.dtos.EmployeeJobHistoryDtoList
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.JobHistoryModel
import com.phpexpert.bringme.utilities.BaseActivity
import java.lang.Exception
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION")
class HistoryFragment : Fragment(), HistoryFragmentAdapter.OnClickView, AuthInterface {
    private var jobHistoryViewModel: JobHistoryModel? = null
    private lateinit var historyBinding: EmployeeFragmentHistoryBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding

    private lateinit var mBottomSheetReview: BottomSheetBehavior<View>
    private lateinit var reviewBinding: WriteReviewLayoutBinding
    private lateinit var arrayList: ArrayList<EmployeeJobHistoryDtoList>
    private var searOrderString: String = ""
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mainArrayList: ArrayList<EmployeeJobHistoryDtoList>
    private lateinit var apiName: String
    private lateinit var reviewJobId: String
    private lateinit var reviewJobRating: String
    private lateinit var reviewJobText: String
    private var selectedPosition: Int = -1

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {

        historyBinding = DataBindingUtil.inflate(inflater, R.layout.employee_fragment_history, container, false)
        historyBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        jobHistoryViewModel = ViewModelProvider(this).get(JobHistoryModel::class.java)
        historyBinding.textHeading.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().history

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage((activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_wait)
        progressDialog.setCancelable(false)

        setList()
        jobViewBinding = historyBinding.bottomHistryLayout
        jobViewBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        reviewBinding = historyBinding.reviewLayout
        reviewBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        mBottomSheetReview = BottomSheetBehavior.from(reviewBinding.root)
        mBottomSheetReview.isDraggable = false
        mBottomSheetReview.peekHeight = 0
        setAction()
        setObserver()
        return historyBinding.root
    }

    private fun setAction() {
        historyBinding.searchIconEdit.setOnClickListener {
            if (historyBinding.layoutSearchData.visibility != View.VISIBLE) {
                historyBinding.layoutSearchData.visibility = View.VISIBLE
                historyBinding.searchIconEdit.visibility = View.GONE
            }
        }

        historyBinding.searchIcon.setOnClickListener {
            if (historyBinding.searchET.text.isNotEmpty()) {
                searOrderString = historyBinding.searchET.text.toString().trim()
                setObserver()
            }
        }

        historyBinding.closeIcon.setOnClickListener {
            historyBinding.searchET.text = Editable.Factory.getInstance().newEditable("")
            this.searOrderString = ""
            historyBinding.searchIconEdit.visibility = View.VISIBLE
            historyBinding.layoutSearchData.visibility = View.GONE
            this.hideKeyboard()
            if (mainArrayList.isNotEmpty()) {
                arrayList.clear()
                arrayList.addAll(mainArrayList)
                historyBinding.historyRV.adapter?.notifyDataSetChanged()
            } else {
                setObserver()
            }
        }
    }

    private fun setList() {
        historyBinding.historyRV.layoutManager = LinearLayoutManager(requireActivity())
        historyBinding.historyRV.isNestedScrollingEnabled = false
        mainArrayList = ArrayList()
        arrayList = ArrayList()
        historyBinding.historyRV.adapter = HistoryFragmentAdapter(requireActivity(), arrayList, this)
    }

    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if ((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "" && (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                progressDialog.show()
                jobHistoryViewModel!!.getJobHistoryData(jobHistoryMapData()).observe(viewLifecycleOwner, {
                    progressDialog.dismiss()
                    if (it.status_code == "0") {
                        historyBinding.noJobHistroy.visibility = View.GONE
                        historyBinding.nestedScrollView.visibility = View.VISIBLE
                        arrayList.clear()
                        arrayList.addAll(it.data!!.OrderList)
                        if (searOrderString == "") {
                            mainArrayList.addAll(arrayList)
                        }
                        historyBinding.historyRV.adapter!!.notifyDataSetChanged()
                    } else {
                        if (it.status != "") {
                            historyBinding.noJobHistroy.visibility = View.VISIBLE
                            historyBinding.nestedScrollView.visibility = View.GONE
                        } else {
                            if (it.status_code == "1")
                                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                            else
                                (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                                (activity as BaseActivity).bottomSheetDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.show()
                        }
                    }
                })
            } else {
                apiName = "listData"
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun jobHistoryMapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["Order_Number"] = searOrderString
        return mapDataVal
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(textInput: String, position: Int) {
        when (textInput) {
            "viewData" -> {

                jobViewBinding.closeView.setOnClickListener {
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    historyBinding.blurView.visibility = View.GONE
                }

                val postDataValue = PostJobPostDto()
                postDataValue.jobDescription = arrayList[position].about_job
                postDataValue.jobTime = arrayList[position].job_offer_time!!.split(" ")[0]
                postDataValue.jobAmount = arrayList[position].job_sub_total.formatChange()
                postDataValue.grandTotal = arrayList[position].job_total_amount.formatChange()
                postDataValue.jobPaymentMode = arrayList[position].payment_mode
                try {
                    postDataValue.job_tax_amount = arrayList[position].job_tax_amount.formatChange()
                } catch (e: Exception) {

                }
                postDataValue.Charge_for_Jobs = arrayList[position].Charge_for_Jobs.formatChange()
                postDataValue.Charge_for_Jobs_percentage = arrayList[position].Charge_for_Jobs_percentage
                postDataValue.Charge_for_Jobs_Admin_percentage = arrayList[position].Charge_for_Jobs_Admin_percentage
                postDataValue.Charge_for_Jobs_Delivery_percentage = arrayList[position].Charge_for_Jobs_Delivery_percentage
                postDataValue.admin_service_fees = arrayList[position].admin_service_fees.formatChange()
                postDataValue.delivery_employee_fee = arrayList[position].delivery_employee_fee.formatChange()
                postDataValue.jobId = arrayList[position].job_order_id

                jobViewBinding.jobDetails = postDataValue

                jobViewBinding.currencyCode.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode1.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode2.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode3.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode4.text = (activity as BaseActivity).getCurrencySymbol()

                mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
                historyBinding.blurView.visibility = View.VISIBLE
            }
            else -> {

                reviewBinding.closeIcon.setOnClickListener {
                    this.hideKeyboard()
                }
                reviewBinding.maxCharacters.text = "${(context as BaseActivity).sharedPrefrenceManager.getLanguageData().maximum_characters_250} ${250}"
                reviewBinding.writeReviewET.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    @SuppressLint("SetTextI18n")
                    override fun afterTextChanged(p0: Editable?) {
                        reviewBinding.maxCharacters.text = "${(context as BaseActivity).sharedPrefrenceManager.getLanguageData().maximum_characters_250} ${250 - (p0.toString().length)}"
                    }

                })
                reviewBinding.submitButton.setOnClickListener {
                    when {
                        reviewBinding.ratingData.rating == 0f -> {
                            (activity as BaseActivity).bottomSheetDialog.show()
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_provide_rating
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                                (activity as BaseActivity).bottomSheetDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.show()
                        }
                        reviewBinding.writeReviewET.text.toString() == "" -> {
                            (activity as BaseActivity).bottomSheetDialog.show()
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_enter_review
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                                (activity as BaseActivity).bottomSheetDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.show()
                        }
                        else -> {
                            reviewBinding.submitButton.startAnimation()
                            reviewBinding.closeIcon.isClickable = false
                            reviewJobId = arrayList[position].job_order_id!!
                            reviewJobRating = reviewBinding.ratingData.rating.toString()
                            reviewJobText = reviewBinding.writeReviewET.text.toString()
                            writeReviewData(arrayList[position].job_order_id!!, reviewBinding.ratingData.rating.toString(), reviewBinding.writeReviewET.text.toString(), position)
                            reviewBinding.ratingData.rating = 0f
                            reviewBinding.writeReviewET.text = Editable.Factory.getInstance().newEditable("")
                        }
                    }
                }

                mBottomSheetReview.state = BottomSheetBehavior.STATE_EXPANDED
                historyBinding.blurView.visibility = View.VISIBLE
            }
        }
    }

    private fun writeReviewData(jobOrderId: String, totalRating: String, reviewContent: String, position: Int) {
        if ((activity as BaseActivity).isOnline()) {
            if ((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "" && (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                jobHistoryViewModel!!.getWriteReviewJobData(reviewDataMap(jobOrderId, totalRating, reviewContent)).observe(viewLifecycleOwner, {
                    reviewBinding.submitButton.revertAnimation()
                    reviewBinding.closeIcon.isClickable = true
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    historyBinding.blurView.visibility = View.GONE
                    if (it.status_code == "0") {
                        (activity as BaseActivity).bottomSheetDialog.show()
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message!!
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.VISIBLE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                            this.hideKeyboard()

                            arrayList[position].review_status = "Done"
                            arrayList[position].job_review_description = reviewContent
                            arrayList[position].job_rating = totalRating
                            historyBinding.historyRV.adapter!!.notifyItemChanged(position)
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    } else {
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        if (it.status_code == "2") {
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        } else
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message!!
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                })
            } else {
                apiName = "writeReview"
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            reviewBinding.submitButton.startAnimation()
            reviewBinding.closeIcon.isClickable = true
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun reviewDataMap(jobOrderId: String, totalRating: String, reviewContent: String): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataValue["job_order_id"] = jobOrderId
        mapDataValue["total_tating"] = totalRating
        mapDataValue["review_content"] = (activity as BaseActivity).base64Encoded(reviewContent)
        mapDataValue["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        return mapDataValue
    }

    private fun String?.formatChange() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale((activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code!!, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!, (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.country_code!!))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        Handler().postDelayed({
            try {
                reviewBinding.writeReviewET.text = Editable.Factory.getInstance().newEditable("")
                mBottomSheetReview.state = BottomSheetBehavior.STATE_COLLAPSED
                historyBinding.blurView.visibility = View.GONE
            } catch (e: Exception) {
            }
        }, 100)
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            when (apiName) {
                "listData" -> setObserver()
                "writeReview" -> writeReviewData(reviewJobId, reviewJobRating, reviewJobText, selectedPosition)
            }
        } else {
            try {
                progressDialog.dismiss()
                reviewBinding.submitButton.startAnimation()
                reviewBinding.closeIcon.isClickable = true
            } catch (e: Exception) {
            }
            (activity as BaseActivity).bottomSheetDialogMessageText.text = message
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }
}