@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.employee.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.icu.text.NumberFormat
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.CreateJobActivity
import com.phpexpert.bringme.databinding.EmployeeFragmentHomeBinding
import com.phpexpert.bringme.databinding.JobViewLayoutBinding
import com.phpexpert.bringme.databinding.WriteReviewLayoutBinding
import com.phpexpert.bringme.dtos.OrderListData
import com.phpexpert.bringme.dtos.PostJobPostDto
import com.phpexpert.bringme.models.JobHistoryModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.SharedPrefrenceManager
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment(), HomeFragmentAdapter.OnClickView {
    private lateinit var jobHistoryModel: JobHistoryModel
    private lateinit var homeFragmentBinding: EmployeeFragmentHomeBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding
    private lateinit var writeReviewBinding: WriteReviewLayoutBinding
    private lateinit var writeReviewBehaviour: BottomSheetBehavior<View>
    private lateinit var progressDialog: ProgressDialog
    private lateinit var orderListData: ArrayList<OrderListData>
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var sharedPreference: SharedPrefrenceManager

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.employee_fragment_home, container, false)
        homeFragmentBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        (activity as BaseActivity).isCheckPermissions(requireActivity(), perission)
        sharedPreference = (activity as BaseActivity).sharedPrefrenceManager
        initValues()
        setActions()
        setList()
        setObserver()

        return homeFragmentBinding.root
    }

    @SuppressLint("MissingPermission")
    private fun initValues() {
        homeFragmentBinding.clientCurrentLocation.text = sharedPreference.getLanguageData().fetching_location
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var mLocation: Location?
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                for (location in locationResult.locations) {
                    if (location != null) {
                        try {
                            mLocation = location
                            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                            val stringBuilder = StringBuilder()
                            for (i in 0..addresses[0]!!.maxAddressLineIndex)
                                stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                            homeFragmentBinding.clientCurrentLocation.text = stringBuilder.toString()
                        } catch (e: Exception) {
                            homeFragmentBinding.clientCurrentLocation.text = sharedPreference.getProfile().login_address
                        }
                        break
                    } else {
                        homeFragmentBinding.clientCurrentLocation.text = sharedPreference.getProfile().login_address
                    }
                }
                mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            }
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)


        jobViewBinding = homeFragmentBinding.bottomHistryLayout
        jobViewBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        writeReviewBinding = homeFragmentBinding.writeReviewLayout
        writeReviewBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        writeReviewBehaviour = BottomSheetBehavior.from(writeReviewBinding.root)
        writeReviewBehaviour.isDraggable = false
        writeReviewBehaviour.peekHeight = 0

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setCancelable(false)
        progressDialog.setMessage((activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_wait)
        jobHistoryModel = ViewModelProvider(this).get(JobHistoryModel::class.java)
    }

    private fun setActions() {
        homeFragmentBinding.addJobButton.setOnClickListener {
            startActivity(Intent(requireActivity(), CreateJobActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    private fun setList() {
        homeFragmentBinding.homeRv.layoutManager = LinearLayoutManager(requireActivity())
        homeFragmentBinding.homeRv.isNestedScrollingEnabled = false
        orderListData = ArrayList()
        homeFragmentBinding.homeRv.adapter = HomeFragmentAdapter(requireActivity(), orderListData, this)
    }

    private fun setObserver() {
        progressDialog.show()
        jobHistoryModel.getLatestJobData(getMapData()).observe(viewLifecycleOwner, {
            progressDialog.dismiss()
            if (it.status_code == "0") {
                homeFragmentBinding.noDataFoundLayout.visibility = View.GONE
                homeFragmentBinding.messageNoData.visibility = View.GONE
                homeFragmentBinding.scrollableBar.visibility = View.VISIBLE
                orderListData.clear()
                orderListData.addAll(it.data!!.OrderList!!)
                homeFragmentBinding.homeRv.adapter!!.notifyDataSetChanged()
            } else {
                if (it.status == "") {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message!!
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                } else {
                    homeFragmentBinding.noDataFoundLayout.visibility = View.VISIBLE
                    homeFragmentBinding.messageNoData.visibility = View.VISIBLE
                    homeFragmentBinding.scrollableBar.visibility = View.GONE
                }
            }
        })
    }

    private fun getMapData(): Map<String, String> {
        val mapData = HashMap<String, String>()
        mapData["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapData["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code!!
        mapData["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().auth_key!!
        return mapData
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(textInput: String, position: Int) {
        when (textInput) {
            "viewData" -> {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
                jobViewBinding.root.isClickable = true
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                jobViewBinding.closeView.setOnClickListener {
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }

                val postDataValue = PostJobPostDto()
                postDataValue.jobDescription = orderListData[position].about_job
                postDataValue.jobTime = orderListData[position].job_offer_time!!.split(" ")[0]
                postDataValue.jobAmount = orderListData[position].job_sub_total.formatChange()
                postDataValue.grandTotal = orderListData[position].job_total_amount.formatChange()
                postDataValue.jobPaymentMode = orderListData[position].payment_mode
                postDataValue.job_tax_amount = orderListData[position].job_tax_amount.formatChange()
                postDataValue.Charge_for_Jobs = orderListData[position].Charge_for_Jobs.formatChange()
                postDataValue.Charge_for_Jobs_percentage = orderListData[position].Charge_for_Jobs_percentage
                postDataValue.Charge_for_Jobs_Admin_percentage = orderListData[position].Charge_for_Jobs_Admin_percentage
                postDataValue.Charge_for_Jobs_Delivery_percentage = orderListData[position].Charge_for_Jobs_Delivery_percentage
                postDataValue.admin_service_fees = orderListData[position].admin_service_fees.formatChange()
                postDataValue.delivery_employee_fee = orderListData[position].delivery_employee_fee.formatChange()
                postDataValue.jobId = orderListData[position].job_order_id

                jobViewBinding.jobDetails = postDataValue

                jobViewBinding.currencyCode.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode1.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode2.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode3.text = (activity as BaseActivity).getCurrencySymbol()
                jobViewBinding.currencyCode4.text = (activity as BaseActivity).getCurrencySymbol()
            }
            "writeReview" -> {
                writeReviewBehaviour.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE

                writeReviewBinding.closeIcon.setOnClickListener {
//                    writeReviewBinding.writeReviewET.clearFocus()
                    this.hideKeyboard()
                }
                writeReviewBinding.maxCharacters.text = "${(context as BaseActivity).sharedPrefrenceManager.getLanguageData().maximum_characters_250} ${250}"
                writeReviewBinding.writeReviewET.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    @SuppressLint("SetTextI18n")
                    override fun afterTextChanged(p0: Editable?) {
                        writeReviewBinding.maxCharacters.text = "${(context as BaseActivity).sharedPrefrenceManager.getLanguageData().maximum_characters_250} ${250 - (p0.toString().length)}"
                    }

                })
                writeReviewBinding.submitButton.setOnClickListener {
                    writeReviewBinding.submitButton.startAnimation()
                    when {
                        writeReviewBinding.ratingData.rating == 0f -> {
                            (activity as BaseActivity).bottomSheetDialog.show()
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_provide_rating
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                                (activity as BaseActivity).bottomSheetDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.show()
                        }
                        writeReviewBinding.writeReviewET.text.toString() == "" -> {
                            (activity as BaseActivity).bottomSheetDialog.show()
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_enter_review
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                                (activity as BaseActivity).bottomSheetDialog.dismiss()
                            }
                            (activity as BaseActivity).bottomSheetDialog.show()
                        }
                        else -> {
                            writeReviewData(orderListData[position].job_order_id!!, writeReviewBinding.ratingData.rating.toString(), writeReviewBinding.writeReviewET.text.toString(), position)
                            writeReviewBinding.ratingData.rating = 0f
                            writeReviewBinding.writeReviewET.text = Editable.Factory.getInstance().newEditable("")
                        }
                    }
                }
            }
        }
    }

    private fun writeReviewData(jobOrderId: String, totalRating: String, reviewContent: String, position: Int) {
        jobHistoryModel.getWriteReviewJobData(reviewDataMap(jobOrderId, totalRating, reviewContent)).observe(viewLifecycleOwner, {
            writeReviewBinding.submitButton.revertAnimation()
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
            homeFragmentBinding.blurView.visibility = View.GONE
            if (it.status_code == "0") {
                (activity as BaseActivity).bottomSheetDialog.show()
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message!!
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.VISIBLE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                    this.hideKeyboard()
                    orderListData[position].review_status = "Done"
                    orderListData[position].job_review_description = reviewContent
                    orderListData[position].job_rating = totalRating
                    homeFragmentBinding.homeRv.adapter!!.notifyItemChanged(position)
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            } else {
                (activity as BaseActivity).bottomSheetDialog.show()
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message!!
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            }
        })
    }

    private fun reviewDataMap(jobOrderId: String, totalRating: String, reviewContent: String): Map<String, String> {
        val mapDataValue = HashMap<String, String>()
        mapDataValue["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataValue["job_order_id"] = jobOrderId
        mapDataValue["total_tating"] = totalRating
        mapDataValue["review_content"] = (activity as BaseActivity).base64Encoded(reviewContent)
        mapDataValue["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData().auth_key!!
        return mapDataValue
    }

    private fun String?.formatChange() = run {
        try {
            val formatter = NumberFormat.getInstance(Locale((activity as BaseActivity).sharedPrefrenceManager.getAuthData().lang_code, "DE"))
            formatter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hideKeyboard()
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        Handler().postDelayed({
            writeReviewBehaviour.state = BottomSheetBehavior.STATE_COLLAPSED
            homeFragmentBinding.blurView.visibility = View.GONE
        }, 100)
    }

}