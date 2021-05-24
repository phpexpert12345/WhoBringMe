@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.delivery.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.location.Address
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
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.NotificationActivity
import com.phpexpert.bringme.databinding.*
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.LatestJobDeliveryDataList
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.interfaces.PermissionInterface
import com.phpexpert.bringme.models.LatestJobDeliveryViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS
import com.phpexpert.bringme.utilities.SharedPrefrenceManager
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment(), HomeFragmentAdapter.OnClickView, AuthInterface, PermissionInterface {
    private var latestJobViewModel: LatestJobDeliveryViewModel? = null
    private lateinit var homeFragmentBinding: DeliveryFragmentHomeBinding

    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutDeliveryBinding

    private lateinit var orderAcceptBehavior: BottomSheetBehavior<View>
    private lateinit var orderAcceptBinding: LayoutOrderAcceptBottomSheetBinding

    private lateinit var orderFinishedBehavior: BottomSheetBehavior<View>
    private lateinit var orderFinishedBinding: OrderFinishedBottomSheetBinding

    private lateinit var orderDeclineBehavior: BottomSheetBehavior<View>
    private lateinit var orderDeclineBinding: OrderDeclineBottomSheetBinding

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var currentLocation: Location
    private lateinit var address: Address
    private lateinit var arrayList: ArrayList<LatestJobDeliveryDataList>
    private var selectedPosition: Int = -1
    private lateinit var orderDelcineString: String

    private lateinit var mainArrayList: ArrayList<LatestJobDeliveryDataList>

    private lateinit var progressDialog: ProgressDialog
    private var searOrderString: String = ""
    private lateinit var languageDtoData: LanguageDtoData
    private lateinit var apiName: String
    private lateinit var sharedPrefrenceManager: SharedPrefrenceManager

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE
    )

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_fragment_home, container, false)
        homeFragmentBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        languageDtoData = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        latestJobViewModel = ViewModelProvider(this).get(LatestJobDeliveryViewModel::class.java)
        sharedPrefrenceManager = (activity as BaseActivity).sharedPrefrenceManager
        (activity as BaseActivity).permissionInterface = this

        jobViewBinding = homeFragmentBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        jobViewBinding.languageModel = languageDtoData
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        orderAcceptBinding = homeFragmentBinding.orderAcceptLayout
        orderAcceptBehavior = BottomSheetBehavior.from(orderAcceptBinding.root)
        orderAcceptBinding.languageModel = languageDtoData
        orderAcceptBehavior.isDraggable = false
        orderAcceptBehavior.peekHeight = 0

        orderFinishedBinding = homeFragmentBinding.finishedOrderLayout
        orderFinishedBehavior = BottomSheetBehavior.from(orderFinishedBinding.root)
        orderFinishedBinding.languageModel = languageDtoData
        orderFinishedBehavior.isDraggable = false
        orderFinishedBehavior.peekHeight = 0

        orderDeclineBinding = homeFragmentBinding.declineOrderLayout
        orderDeclineBehavior = BottomSheetBehavior.from(orderDeclineBinding.root)
        orderDeclineBinding.languageModel = languageDtoData
        orderDeclineBehavior.isDraggable = false
        orderDeclineBehavior.peekHeight = 0

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setCancelable(false)

        setActions()
        setList()

        if (sharedPrefrenceManager.getPreference(CONSTANTS.isLocation) == "true") {
            val locationData = Location("")
            locationData.latitude = sharedPrefrenceManager.getPreference(CONSTANTS.currentLatitue)!!.toDouble()
            locationData.longitude = sharedPrefrenceManager.getPreference(CONSTANTS.currentLongitude)!!.toDouble()

            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
            val addresses = geocoder.getFromLocation(locationData.latitude, locationData.longitude, 1)
            val stringBuilder = StringBuilder()
            for (i in 0..addresses[0]!!.maxAddressLineIndex)
                stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
            currentLocation = locationData
            address = addresses[0]
            setObserver()
        } else {
            if ((activity as BaseActivity).isCheckPermissions(requireActivity(), perission))
                if ((activity as BaseActivity).isLocationEnabled()) {
                    getLocation()
                } else {
                    (activity as BaseActivity).bottomSheetDialogMessageText.text = languageDtoData.location_enable_message
                    (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                }
        }

        return homeFragmentBinding.root
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        progressDialog.setMessage(languageDtoData.fetching_location)
        progressDialog.show()
        val mLocationRequest = LocationRequest.create()
        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        var mLocation: Location?
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                try {
                    for (location in locationResult.locations) {
                        try {
                            if (location != null) {
                                mLocation = location
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.isLocation, "true")
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.currentLongitude, mLocation?.longitude.toString())
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.currentLatitue, mLocation?.latitude.toString())

                                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                                val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                                val stringBuilder = StringBuilder()
                                for (i in 0..addresses[0]!!.maxAddressLineIndex)
                                    stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                                currentLocation = mLocation!!
                                address = addresses[0]
                                progressDialog.dismiss()
                                setObserver()
                            } else {
                                val locationData = Location("")
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.isLocation, "false")
                                locationData.latitude = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_lat!!.toDouble()
                                locationData.longitude = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_long!!.toDouble()
                                mLocation = locationData
                                val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                                val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                                val stringBuilder = StringBuilder()
                                for (i in 0..addresses[0]!!.maxAddressLineIndex)
                                    stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                                currentLocation = mLocation!!
                                address = addresses[0]
                                progressDialog.dismiss()
                                setObserver()
                            }
                        } catch (e: Exception) {
                            val locationData = Location("")
                            locationData.latitude = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_lat!!.toDouble()
                            locationData.longitude = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_long!!.toDouble()
                            mLocation = locationData
                            val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                            val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                            val stringBuilder = StringBuilder()
                            for (i in 0..addresses[0]!!.maxAddressLineIndex)
                                stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                            currentLocation = mLocation!!
                            address = addresses[0]
                            progressDialog.dismiss()
                            setObserver()
                        }
                        break
                    }
                } catch (e: Exception) {
                    val locationData = Location("")
                    locationData.latitude = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_lat!!.toDouble()
                    locationData.longitude = (activity as BaseActivity).sharedPrefrenceManager.getProfile().login_long!!.toDouble()
                    mLocation = locationData
                    val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                    val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                    val stringBuilder = StringBuilder()
                    for (i in 0..addresses[0]!!.maxAddressLineIndex)
                        stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                    currentLocation = mLocation!!
                    address = addresses[0]
                    progressDialog.dismiss()
                    setObserver()
                }
                mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            }
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun setActions() {

        homeFragmentBinding.notificationIcon.setOnClickListener {
            startActivity(Intent(requireActivity(), NotificationActivity::class.java))
        }

        homeFragmentBinding.searchIcon.setOnClickListener {
            homeFragmentBinding.layoutSearchData.visibility = View.VISIBLE
            homeFragmentBinding.searchIcon.visibility = View.GONE
        }

        homeFragmentBinding.searchIconEdit.setOnClickListener {
            if (homeFragmentBinding.searchET.text.toString().trim().isNotEmpty()) {
                searOrderString = homeFragmentBinding.searchET.text.toString()
                setObserver()
            }
        }
        homeFragmentBinding.closeIcon.setOnClickListener {
            homeFragmentBinding.searchET.text = Editable.Factory.getInstance().newEditable("")
            homeFragmentBinding.layoutSearchData.visibility = View.GONE
            homeFragmentBinding.searchIcon.visibility = View.VISIBLE
            this.searOrderString = ""
            arrayList.clear()
            arrayList.addAll(mainArrayList)
            homeFragmentBinding.homeRecyclerView.adapter?.notifyDataSetChanged()
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
        homeFragmentBinding.homeRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        homeFragmentBinding.homeRecyclerView.isNestedScrollingEnabled = false
        arrayList = ArrayList()
        mainArrayList = ArrayList()
        homeFragmentBinding.homeRecyclerView.adapter = HomeFragmentAdapter(requireActivity(), arrayList, this)
    }

    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            progressDialog.setMessage(languageDtoData.please_wait)
            progressDialog.show()
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                latestJobViewModel!!.getLatestJobDeliveryData(mapData()).observe(viewLifecycleOwner, {
                    progressDialog.dismiss()
                    if (it.status_code == "0") {
                        homeFragmentBinding.noDataFoundLayout.visibility = View.GONE
                        homeFragmentBinding.nestedScrollView.visibility = View.VISIBLE
                        arrayList.clear()
                        arrayList.addAll(it.data!!.OrderList)
                        if (searOrderString == "") {
                            mainArrayList.clear()
                            mainArrayList.addAll(arrayList)
                        }
                        homeFragmentBinding.homeRecyclerView.adapter!!.notifyDataSetChanged()
                        homeFragmentBinding.runningOrders.text = it.Total_Running_Orders
                        homeFragmentBinding.totalAmount.text = it.Total_Running_Order_Amount.formatChange()
                    } else {
                        homeFragmentBinding.noDataFoundLayout.visibility = View.VISIBLE
                        homeFragmentBinding.nestedScrollView.visibility = View.GONE
                        if (it.status_code == "2")
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
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
                })
            } else {
                apiName = "homeApi"
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = languageDtoData.network_error
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun orderAcceptObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                latestJobViewModel!!.orderAcceptData(orderMapData()).observe(viewLifecycleOwner, {
//                    progressDialog.dismiss()
                    if (it.status_code == "2")
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                    else
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    if (it.status_code == "0") {
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                    } else {
                        progressDialog.dismiss()
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    }
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        if (it.status_code == "0") {
                            setObserver()
                        }
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                })
            } else {
                apiName = "acceptOrderApi"
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

    private fun orderDeclineObserver() {
        progressDialog.show()
        if ((activity as BaseActivity).isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                latestJobViewModel!!.orderDeclineData(orderDeclineData()).observe(viewLifecycleOwner, {
                    if (it.status_code == "2")
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                    else
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                    (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                    if (it.status_code == "0") {
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                    } else {
                        progressDialog.dismiss()
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                    }
                    (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                        if (it.status_code == "0") {
                            setObserver()
                        }
                        (activity as BaseActivity).bottomSheetDialog.dismiss()
                    }
                    (activity as BaseActivity).bottomSheetDialog.show()
                })
            } else {
                apiName = "orderDeclineApi"
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

    private fun orderFinishObserver() {
//        progressDialog.show()
        if ((activity as BaseActivity).isOnline()) {
            if (orderFinishedBinding.jobCode.text.toString().trim() != "") {
                if (sharedPrefrenceManager.getAuthData()?.auth_key != "" && sharedPrefrenceManager.getAuthData()?.auth_key != null) {
                    latestJobViewModel!!.orderFinishData(orderFinishData()).observe(viewLifecycleOwner, {
//                        progressDialog.dismiss()
                        if (it.status_code == "2")
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        else
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        if (it.status_code == "0") {
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                        } else {
                            progressDialog.dismiss()
                            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        }
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                            if (it.status_code == "0") {
                                setObserver()
                            }
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    })
                } else {
                    apiName = "finishOrderApi"
                    (activity as BaseActivity).hitAuthApi(this)
                }
            } else {
                progressDialog.dismiss()
                (activity as BaseActivity).bottomSheetDialogMessageText.text = languageDtoData.enter_job_code_first
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            }
        } else {
            this.hideKeyboard()
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = languageDtoData.network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun mapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["current_lat"] = currentLocation.latitude.toString()
        mapDataVal["current_long"] = currentLocation.longitude.toString()
        mapDataVal["current_city"] = (activity as BaseActivity).base64Encoded(address.adminArea)
        mapDataVal["current_locality"] = (activity as BaseActivity).base64Encoded(address.locality)
        mapDataVal["current_zipcode"] = address.postalCode
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.lang_code!!
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["Order_Number"] = searOrderString
        return mapDataVal
    }

    private fun orderMapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["job_order_id"] = arrayList[selectedPosition].job_order_id!!
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        return mapDataVal
    }

    private fun orderDeclineData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["job_order_id"] = arrayList[selectedPosition].job_order_id!!
        mapDataVal["order_decline_reason"] = (activity as BaseActivity).base64Encoded(orderDelcineString)
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        return mapDataVal
    }

    private fun orderFinishData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["job_order_id"] = arrayList[selectedPosition].job_order_id!!
        mapDataVal["job_OTP_code"] = orderFinishedBinding.jobCode.text.toString()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        return mapDataVal
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(textInput: String, position: Int) {
        selectedPosition = position
        when (textInput) {
            "viewData" -> {

                jobViewBinding.closeView.setOnClickListener {
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
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
                homeFragmentBinding.blurView.visibility = View.VISIBLE
            }
            "acceptData" -> {

                orderAcceptBinding.orderId.text = arrayList[position].job_order_id!!
                orderAcceptBinding.noLayout.setOnClickListener {
                    orderAcceptBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderAcceptBinding.yesLayout.setOnClickListener {
                    orderAcceptBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                    progressDialog.show()
                    orderAcceptObserver()
                }
                orderAcceptBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
            }
            "finishedJob" -> {
                orderFinishedBinding.orderId.text = arrayList[position].job_order_id!!
                orderFinishedBinding.orderFinishData.text = "${languageDtoData.enter_job_code_which_is_provide_by_raj_nkaushal_to_finish_your_job}\n${arrayList[position].Client_name} ${languageDtoData.to_finish_your_job}"
                orderFinishedBinding.noLayout.setOnClickListener {
                    this.hideKeyboard()
                }
                orderFinishedBinding.yesLayout.setOnClickListener {
//                    if (orderFinishedBinding.otp1.text.toString().trim() != "" && orderFinishedBinding.otp2.text.toString().trim() != "" && orderFinishedBinding.otp3.text.toString().trim() != "" && orderFinishedBinding.otp4.text.toString().trim() != "") {
                    this.hideKeyboard()
                    progressDialog.show()
                    orderFinishObserver()
//                    } else {
//                        (activity as BaseActivity).bottomSheetDialogMessageText.text = "Please enter order otp first"
//                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
//                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
//                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
//                            (activity as BaseActivity).bottomSheetDialog.dismiss()
//                        }
//                        (activity as BaseActivity).bottomSheetDialog.show()
//                    }
                }
                orderFinishedBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
            }
            "declineJob" -> {

                orderDeclineBinding.orderId.text = arrayList[position].job_order_id!!
                orderDeclineBinding.noLayout.setOnClickListener {
                    orderDeclineBinding.orderReason.text = Editable.Factory.getInstance().newEditable("")
                    this.hideKeyboard()
                }
                orderDeclineBinding.yesLayout.setOnClickListener {
                    if (orderDeclineBinding.orderReason.text.toString().trim() != "") {
                        this.hideKeyboard()
                        orderDelcineString = orderDeclineBinding.orderReason.text.toString()
                        progressDialog.show()
                        orderDeclineObserver()
                    } else {
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = languageDtoData.please_enter_order_decline_reason
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                }
                orderDeclineBinding.maxCharacters.text = "${(context as BaseActivity).sharedPrefrenceManager.getLanguageData().maximum_characters_250} ${1000}"
                orderDeclineBinding.orderReason.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    }

                    @SuppressLint("SetTextI18n")
                    override fun afterTextChanged(p0: Editable?) {
                        orderDeclineBinding.maxCharacters.text = "${sharedPrefrenceManager.getLanguageData().maximum_characters_250} ${1000 - (p0.toString().length)}"
                    }

                })

                orderDeclineBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
            }
        }
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

    private fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    private fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)

        Handler().postDelayed({
            orderDeclineBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            orderFinishedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            orderFinishedBinding.jobCode.text = Editable.Factory.getInstance().newEditable("")
            orderDeclineBinding.orderReason.text = Editable.Factory.getInstance().newEditable("")
            homeFragmentBinding.blurView.visibility = View.GONE
        }, 100)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            progressDialog.dismiss()
        } catch (e: Exception) {
        }
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            when (apiName) {
                "homeApi" -> setObserver()
                "acceptOrderApi" -> orderAcceptObserver()
                "orderDeclineApi" -> orderDeclineObserver()
                "finishOrderApi" -> orderFinishObserver()
            }
        } else {
            try {
                progressDialog.dismiss()
            } catch (e: Exception) {
            }
            (activity as BaseActivity).bottomSheetDialogMessageText.text = message
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    override fun isPermission(value: Boolean) {
        if (value) {
            getLocation()
        }
    }
}