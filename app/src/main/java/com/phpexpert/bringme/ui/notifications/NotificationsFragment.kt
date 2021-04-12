package com.phpexpert.bringme.ui.notifications

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.FragmentNotificationsBinding

class NotificationsFragment : Fragment() {
    private var notificationsViewModel: NotificationsViewModel? = null
    private lateinit var notificationBinding: FragmentNotificationsBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        notificationsViewModel = ViewModelProvider(this).get(NotificationsViewModel::class.java)
        notificationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false)
        setList()
        return notificationBinding.root
    }

    private fun setList() {
        notificationBinding.notificationRV.layoutManager = LinearLayoutManager(requireActivity())
        notificationBinding.notificationRV.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        notificationBinding.notificationRV.adapter = NotificationAdapter(requireActivity(), arrayList)
    }
}