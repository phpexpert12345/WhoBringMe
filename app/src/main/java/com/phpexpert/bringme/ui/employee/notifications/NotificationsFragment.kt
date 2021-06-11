@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.ui.employee.notifications

import android.app.ProgressDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.EmployeeFragmentNotificationsBinding
import com.phpexpert.bringme.dtos.NotificationDtoList
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.NotificationViewModel
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS

@Suppress("DEPRECATION")
class NotificationsFragment : Fragment(), AuthInterface {
    private lateinit var notificationsViewModel: NotificationViewModel
    private lateinit var notificationBinding: EmployeeFragmentNotificationsBinding
    private lateinit var arrayList: ArrayList<NotificationDtoList>
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        notificationsViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationBinding = DataBindingUtil.inflate(inflater, R.layout.employee_fragment_notifications, container, false)
        notificationBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        progressDialog = ProgressDialog(requireActivity())
        progressDialog.setMessage((activity as BaseActivity).sharedPrefrenceManager.getLanguageData().please_wait)
        progressDialog.setCancelable(false)
        progressDialog.show()
        setList()
        setObserver()
        return notificationBinding.root
    }

    private fun setList() {
        notificationBinding.notificationRV.layoutManager = LinearLayoutManager(requireActivity())
        notificationBinding.notificationRV.isNestedScrollingEnabled = false
        arrayList = ArrayList()
        notificationBinding.notificationRV.adapter = NotificationAdapter(requireActivity(), arrayList)
    }

    private fun setObserver() {
        if ((activity as BaseActivity).isOnline()) {
            if ((activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != null && (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key != "") {
                notificationsViewModel.getNotificationData(getNotificationMap()).observe(viewLifecycleOwner, {
                    progressDialog.dismiss()
                    if (it.status_code == "0") {
                        notificationBinding.nestedScrollView.visibility = View.VISIBLE
                        notificationBinding.noNotificationData.visibility = View.GONE
                        arrayList.clear()
                        arrayList.addAll(it.data!!.NotificationList!!)
                        notificationBinding.notificationRV.adapter!!.notifyDataSetChanged()
                    } else if (it.status_code == "2") {
                        (activity as BaseActivity).hitAuthApi(this)
                    } else {
                        (activity as BaseActivity).bottomSheetDialogHeadingText.visibility = View.VISIBLE
                        if (it.status_message == "11")
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().could_not_connect_server_message
                        else
                            (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                        notificationBinding.nestedScrollView.visibility = View.GONE
                        notificationBinding.noNotificationData.visibility = View.VISIBLE
                        notificationBinding.jobTitle.text = it.status_message_heading
                        notificationBinding.jobMessage.text = it.status_message
                    }
                })
            } else {
                (activity as BaseActivity).hitAuthApi(this)
            }
        } else {
            progressDialog.dismiss()
            (activity as BaseActivity).bottomSheetDialogMessageText.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().network_error
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData().ok_text
            (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
            (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                (activity as BaseActivity).bottomSheetDialog.dismiss()
            }
            (activity as BaseActivity).bottomSheetDialog.show()
        }
    }

    private fun getNotificationMap(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = (activity as BaseActivity).sharedPrefrenceManager.getAuthData()?.auth_key!!
        mapDataVal["lang_code"] = (activity as BaseActivity).sharedPrefrenceManager.getPreference(CONSTANTS.changeLanguage)!!
        return mapDataVal
    }

    override fun isAuthHit(value: Boolean, message: String) {
        if (value) {
            setObserver()
        } else {
            try {
                progressDialog.dismiss()
            } catch (e: Exception) {
            }
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
}