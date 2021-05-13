package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.adapters.NotificationAdapter
import com.phpexpert.bringme.databinding.NotificationActivityBinding
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.dtos.NotificationDtoList
import com.phpexpert.bringme.models.NotificationViewModel
import com.phpexpert.bringme.utilities.BaseActivity

class NotificationActivity : BaseActivity() {
    private lateinit var notificationsViewModel: NotificationViewModel
    private lateinit var notificationBinding: NotificationActivityBinding
    private lateinit var arrayList: ArrayList<NotificationDtoList>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationBinding = DataBindingUtil.setContentView(this, R.layout.notification_activity)
        notificationsViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationBinding.backArrow.setOnClickListener {
            finish()
        }
        setList()
        setObserver()
    }

    private fun setList() {
        notificationBinding.notificationRV.layoutManager = LinearLayoutManager(this)
        notificationBinding.notificationRV.isNestedScrollingEnabled = false
        arrayList = ArrayList()
        notificationBinding.notificationRV.adapter = NotificationAdapter(this, arrayList)
    }

    @SuppressLint("SetTextI18n")
    private fun setObserver() {
        if (isOnline()) {
            notificationsViewModel.getNotificationData(getNotificationMap()).observe(this, {
                if (it.status_code == "0") {
                    notificationBinding.nestedScrollView.visibility = View.VISIBLE
                    notificationBinding.noNotificationData.visibility = View.GONE
                    arrayList.clear()
                    arrayList.addAll(it.data!!.NotificationList!!)
                    notificationBinding.notificationRV.adapter!!.notifyDataSetChanged()
                } else {
                    if (it.status == "") {
                        bottomSheetDialogMessageText.text = it.status_message
                        bottomSheetDialogMessageOkButton.text = "Ok"
                        bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            bottomSheetDialog.dismiss()
                        }
                        bottomSheetDialog.show()
                    } else {
                        notificationBinding.nestedScrollView.visibility = View.GONE
                        notificationBinding.noNotificationData.visibility = View.VISIBLE
                    }
                }
            })
        } else {
            bottomSheetDialogMessageText.text = getString(R.string.network_error)
            bottomSheetDialogMessageOkButton.text = "Ok"
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            bottomSheetDialogMessageOkButton.setOnClickListener {
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.show()
        }
    }

    private fun getNotificationMap(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataVal
    }
}