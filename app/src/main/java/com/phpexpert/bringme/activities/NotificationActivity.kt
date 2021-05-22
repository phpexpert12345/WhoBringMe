package com.phpexpert.bringme.activities

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.adapters.NotificationAdapter
import com.phpexpert.bringme.databinding.NotificationActivityBinding
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.dtos.NotificationDtoList
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.NotificationViewModel
import com.phpexpert.bringme.utilities.BaseActivity

class NotificationActivity : BaseActivity(), AuthInterface {
    private lateinit var notificationsViewModel: NotificationViewModel
    private lateinit var notificationBinding: NotificationActivityBinding
    private lateinit var arrayList: ArrayList<NotificationDtoList>
    private lateinit var languageDtoData: LanguageDtoData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        notificationBinding = DataBindingUtil.setContentView(this, R.layout.notification_activity)
        notificationsViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationBinding.languageModel = sharedPrefrenceManager.getLanguageData()
        languageDtoData = sharedPrefrenceManager.getLanguageData()

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

    private fun setObserver() {
        if (isOnline()) {
            if (sharedPrefrenceManager.getAuthData()?.auth_key != null && sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                notificationsViewModel.getNotificationData(getNotificationMap()).observe(this, {
                    if (it.status_code == "0") {
                        notificationBinding.nestedScrollView.visibility = View.VISIBLE
                        notificationBinding.noNotificationData.visibility = View.GONE
                        arrayList.clear()
                        arrayList.addAll(it.data!!.NotificationList!!)
                        notificationBinding.notificationRV.adapter!!.notifyDataSetChanged()
                    } else {
                        if (it.status_message == "1")
                            bottomSheetDialogMessageText.text = it.status_message
                        else
                            bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
                        bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        bottomSheetDialogMessageOkButton.setOnClickListener {
                            bottomSheetDialog.dismiss()
                        }
                        bottomSheetDialog.show()
                        notificationBinding.nestedScrollView.visibility = View.GONE
                        notificationBinding.noNotificationData.visibility = View.VISIBLE
                    }
                })
            } else {
                hitAuthApi(this)
            }
        } else {
            bottomSheetDialogMessageText.text = languageDtoData.network_error
            bottomSheetDialogMessageOkButton.text = languageDtoData.ok_text
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
        mapDataVal["auth_key"] = sharedPrefrenceManager.getAuthData()?.auth_key!!
        return mapDataVal
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {

        } else {

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
}