package com.phpexpert.bringme.ui.employee.notifications

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
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.dtos.NotificationDtoList
import com.phpexpert.bringme.models.NotificationViewModel
import com.phpexpert.bringme.utilities.BaseActivity

class NotificationsFragment : Fragment() {
    private lateinit var notificationsViewModel: NotificationViewModel
    private lateinit var notificationBinding: EmployeeFragmentNotificationsBinding
    private lateinit var arrayList: ArrayList<NotificationDtoList>

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        notificationsViewModel = ViewModelProvider(this).get(NotificationViewModel::class.java)
        notificationBinding = DataBindingUtil.inflate(inflater, R.layout.employee_fragment_notifications, container, false)
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
            notificationsViewModel.getNotificationData(getNotificationMap()).observe(viewLifecycleOwner, {
                if (it.status_code == "0") {
                    notificationBinding.nestedScrollView.visibility = View.VISIBLE
                    notificationBinding.noNotificationData.visibility = View.GONE
                    arrayList.clear()
                    arrayList.addAll(it.data!!.NotificationList!!)
                    notificationBinding.notificationRV.adapter!!.notifyDataSetChanged()
                } else {
                    if (it.status == "") {
                        (activity as BaseActivity).bottomSheetDialogMessageText.text = it.status_message
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.text = "Ok"
                        (activity as BaseActivity).bottomSheetDialogMessageCancelButton.visibility = View.GONE
                        (activity as BaseActivity).bottomSheetDialogMessageOkButton.setOnClickListener {
                            (activity as BaseActivity).bottomSheetDialog.dismiss()
                        }
                        (activity as BaseActivity).bottomSheetDialog.show()
                    } else {
                        notificationBinding.nestedScrollView.visibility = View.GONE
                        notificationBinding.noNotificationData.visibility = View.VISIBLE
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

    private fun getNotificationMap(): Map<String, String> {
        val mapDataVal = HashMap<String, String>()
        mapDataVal["LoginId"] = (activity as BaseActivity).sharedPrefrenceManager.getLoginId()
        mapDataVal["auth_key"] = AuthSingleton.authObject.auth_key!!
        return mapDataVal
    }
}