package com.phpexpert.bringme.ui.delivery.myjob

import android.annotation.SuppressLint
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
import com.phpexpert.bringme.databinding.FragmentMyJobBinding
import com.phpexpert.bringme.databinding.MyJobViewLayoutDeliveryBinding
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.dtos.MyJobDtoList
import com.phpexpert.bringme.models.MyJobDataModel
import com.phpexpert.bringme.utilities.BaseActivity

class MyJobFragment : Fragment(), MyJobAdapter.OnClickView {

    private lateinit var myJobBinding: FragmentMyJobBinding
    private lateinit var arrayList: ArrayList<MyJobDtoList>
    private lateinit var myJobModel: MyJobDataModel
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: MyJobViewLayoutDeliveryBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        myJobBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_job, container, false)
        myJobModel = ViewModelProvider(this).get(MyJobDataModel::class.java)
        jobViewBinding = myJobBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0
        myJobBinding.jobRV.layoutManager = LinearLayoutManager(requireActivity())
        myJobBinding.jobRV.isNestedScrollingEnabled = false
        arrayList = ArrayList()
        myJobBinding.jobRV.adapter = MyJobAdapter(requireActivity(), arrayList, this)
        setObserver()
        return myJobBinding.root
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            myJobModel.getMyJobData(getMapData()).observe(viewLifecycleOwner, {
                if (it.status_code == "0") {
                    myJobBinding.noDataFoundLayout.visibility = View.VISIBLE
                    myJobBinding.nestedScrollView.visibility = View.GONE
                    myJobBinding.runningOrders.text = it.Total_Orders
                    myJobBinding.totalAmount.text = String.format("%.2f", it.Total_Order_Amount)
                    arrayList.clear()
                    arrayList.addAll(it.data!!.OrderList!!)
                    myJobBinding.jobRV.adapter!!.notifyDataSetChanged()

                } else {
                    if (it.status != "") {
                        myJobBinding.noDataFoundLayout.visibility = View.VISIBLE
                        myJobBinding.nestedScrollView.visibility = View.GONE
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
            (activity as BaseActivity).bottomSheetDialogMessageText.text = getString(R.string.network_error)
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
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
        mapDataValue["lang_code"] = AuthSingleton.authObject.lang_code!!
        mapDataValue["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataValue
    }

    override fun onClick(textInput: String, position: Int) {
        mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
        myJobBinding.blurView.visibility = View.VISIBLE
        jobViewBinding.closeView.setOnClickListener {
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
            myJobBinding.blurView.visibility = View.GONE
        }

        Glide.with(requireActivity()).load(arrayList[position]).centerCrop().placeholder(R.drawable.user_placeholder).into(jobViewBinding.userImage)
        jobViewBinding.data = arrayList[position]
    }
}