@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.delivery.home

import android.annotation.SuppressLint
import android.app.ProgressDialog
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.text.Editable
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.*
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.dtos.LatestJobDeliveryDataList
import com.phpexpert.bringme.models.LatestJobDeliveryViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


@Suppress("DEPRECATION", "NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class HomeFragment : Fragment(), HomeFragmentAdapter.OnClickView {
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

    private lateinit var progressDialog: ProgressDialog
    private var searOrderString: String = ""


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_fragment_home, container, false)

        latestJobViewModel = ViewModelProvider(this).get(LatestJobDeliveryViewModel::class.java)
        jobViewBinding = homeFragmentBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        orderAcceptBinding = homeFragmentBinding.orderAcceptLayout
        orderAcceptBehavior = BottomSheetBehavior.from(orderAcceptBinding.root)
        orderAcceptBehavior.isDraggable = false
        orderAcceptBehavior.peekHeight = 0

        orderFinishedBinding = homeFragmentBinding.finishedOrderLayout
        orderFinishedBehavior = BottomSheetBehavior.from(orderFinishedBinding.root)
        orderFinishedBehavior.isDraggable = false
        orderFinishedBehavior.peekHeight = 0

        orderDeclineBinding = homeFragmentBinding.declineOrderLayout
        orderDeclineBehavior = BottomSheetBehavior.from(orderDeclineBinding.root)
        orderDeclineBehavior.isDraggable = false
        orderDeclineBehavior.peekHeight = 0
        setActions()
        setList()
        initValues()
        return homeFragmentBinding.root
    }

    @SuppressLint("MissingPermission")
    private fun initValues() {

        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage("Please Wait...")
        progressDialog.setCancelable(false)
        progressDialog.show()
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
                        mLocation = location
                        val geocoder = Geocoder(requireActivity(), Locale.getDefault())
                        val addresses = geocoder.getFromLocation(mLocation!!.latitude, mLocation!!.longitude, 1)
                        val stringBuilder = StringBuilder()
                        for (i in 0..addresses[0]!!.maxAddressLineIndex)
                            stringBuilder.append(addresses[0]!!.getAddressLine(i) + ",")
                        currentLocation = mLocation!!
                        address = addresses[0]
                        setObserver()
                        break
                    }
                }
                mFusedLocationClient.removeLocationUpdates(mLocationCallback)
            }
        }
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    private fun setActions() {
        homeFragmentBinding.searchIcon.setOnClickListener {
            if (homeFragmentBinding.textHeading.visibility == View.VISIBLE) {
                homeFragmentBinding.textHeading.visibility = View.GONE
                homeFragmentBinding.notificationIcon.visibility = View.GONE
                homeFragmentBinding.searchET.visibility = View.VISIBLE
                homeFragmentBinding.closeIcon.visibility = View.VISIBLE
            } else {
                searOrderString = homeFragmentBinding.searchET.text.toString()
                setObserver()
            }
        }

        homeFragmentBinding.closeIcon.setOnClickListener {
            homeFragmentBinding.searchET.text = Editable.Factory.getInstance().newEditable("")
            this.searOrderString = ""
            homeFragmentBinding.textHeading.visibility = View.VISIBLE
            homeFragmentBinding.searchET.visibility = View.GONE
            homeFragmentBinding.notificationIcon.visibility = View.VISIBLE
            homeFragmentBinding.closeIcon.visibility = View.GONE
            setObserver()
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
        homeFragmentBinding.homeRecyclerView.adapter = HomeFragmentAdapter(requireActivity(), arrayList, this)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            latestJobViewModel!!.getLatestJobDeliveryData(mapData()).observe(viewLifecycleOwner, {
                progressDialog.dismiss()
                if (it.status_code == "0") {
                    homeFragmentBinding.noDataFoundLayout.visibility = View.GONE
                    homeFragmentBinding.nestedScrollView.visibility = View.VISIBLE
                    arrayList.clear()
                    arrayList.addAll(it.data!!.OrderList)
                    homeFragmentBinding.homeRecyclerView.adapter!!.notifyDataSetChanged()
                    homeFragmentBinding.runningOrders.text = it.Total_Running_Orders
                    homeFragmentBinding.totalAmount.text = it.Total_Running_Order_Amount
                } else {
                    if (it.status == "") {
                        homeFragmentBinding.noDataFoundLayout.visibility = View.VISIBLE
                        homeFragmentBinding.nestedScrollView.visibility = View.GONE
                    } else {
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                }
            })

        } else {
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = getString(R.string.network_error)
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun orderAcceptObserver() {
        if ((activity as BaseActivity).isOnline()) {
            latestJobViewModel!!.orderAcceptData(orderMapData()).observe(viewLifecycleOwner, {
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                    if (it.status_code == "0") {
                        setObserver()
                    }
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            })
        } else {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = getString(R.string.network_error)
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun orderDeclineObserver() {
        if ((activity as BaseActivity).isOnline()) {
            latestJobViewModel!!.orderDeclineData(orderDeclineData()).observe(viewLifecycleOwner, {
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                    if (it.status_code == "0") {
                        setObserver()
                    }
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            })
        } else {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = getString(R.string.network_error)
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun orderFinishObserver() {
        if ((activity as BaseActivity).isOnline()) {
            latestJobViewModel!!.orderFinishData(orderFinishData()).observe(viewLifecycleOwner, {
                (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener { _ ->
                    if (it.status_code == "0") {
                        setObserver()
                    }
                    (activity as BaseActivity).bottomSheetDialog.dismiss()
                }
                (activity as BaseActivity).bottomSheetDialog.show()
            })
        } else {
            (activity as BaseActivity).bottomSheetDialogMessageText.text = getString(R.string.network_error)
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
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

        mapDataVal["current_lat"] = currentLocation.latitude.toString()
        mapDataVal["current_long"] = currentLocation.longitude.toString()
        mapDataVal["current_city"] = (activity as BaseActivity).base64Encoded(address.adminArea)
        mapDataVal["current_locality"] = (activity as BaseActivity).base64Encoded(address.locality)
        mapDataVal["current_zipcode"] = address.postalCode
        mapDataVal["lang_code"] = AuthSingleton.authObject.lang_code!!
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        mapDataVal["Order_Number"] = searOrderString
        return mapDataVal
    }

    private fun orderMapData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["job_order_id"] = arrayList[selectedPosition].job_order_id!!
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataVal
    }

    private fun orderDeclineData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["job_order_id"] = arrayList[selectedPosition].job_order_id!!
        mapDataVal["order_decline_reason"] = (activity as BaseActivity).base64Encoded(orderDelcineString)
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataVal
    }

    private fun orderFinishData(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["job_order_id"] = arrayList[selectedPosition].job_order_id!!
        mapDataVal["job_OTP_code"] = orderFinishedBinding.otp1.text.toString() + orderFinishedBinding.otp2.text.toString() + orderFinishedBinding.otp3.text.toString() + orderFinishedBinding.otp4.text.toString()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataVal
    }

    @SuppressLint("SetTextI18n")
    override fun onClick(textInput: String, position: Int) {
        selectedPosition = position
        when (textInput) {
            "viewData" -> {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                jobViewBinding.closeView.setOnClickListener {
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }

                Glide.with(requireActivity()).load(arrayList[position]).centerCrop().placeholder(R.drawable.user_placeholder).into(jobViewBinding.userImage)
                jobViewBinding.data = arrayList[position]
            }
            "acceptData" -> {
                orderAcceptBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                orderAcceptBinding.orderId.text = arrayList[position].job_order_id!!
                orderAcceptBinding.noLayout.setOnClickListener {
                    orderAcceptBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderAcceptBinding.yesLayout.setOnClickListener {
                    orderAcceptBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                    orderAcceptObserver()
                }
            }
            "finishedJob" -> {
                orderFinishedBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                orderFinishedBinding.orderId.text = arrayList[position].job_order_id!!

                orderFinishedBinding.noLayout.setOnClickListener {
                    orderFinishedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderFinishedBinding.yesLayout.setOnClickListener {
                    if (orderFinishedBinding.otp1.text.toString().trim() != "" && orderFinishedBinding.otp2.text.toString().trim() != "" && orderFinishedBinding.otp3.text.toString().trim() != "" && orderFinishedBinding.otp4.text.toString().trim() != "") {
                        orderFinishedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        homeFragmentBinding.blurView.visibility = View.GONE
                        orderFinishObserver()
                    } else {
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = "Please enter order otp first"
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                }
            }
            "declineJob" -> {
                orderDeclineBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                orderDeclineBinding.orderId.text = arrayList[position].job_order_id!!
                orderDeclineBinding.noLayout.setOnClickListener {
                    orderDeclineBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderDeclineBinding.yesLayout.setOnClickListener {
                    if (orderDeclineBinding.orderReason.text.toString().trim() != "") {
                        orderDeclineBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                        homeFragmentBinding.blurView.visibility = View.GONE
                        orderDelcineString = orderDeclineBinding.orderReason.text.toString()
                        orderDeclineObserver()
                    } else {
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = "Please enter order decline reason"
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    }
                }
            }
        }
    }
}