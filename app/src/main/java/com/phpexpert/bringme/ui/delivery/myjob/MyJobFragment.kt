package com.phpexpert.bringme.ui.delivery.myjob

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.FragmentMyJobBinding

class MyJobFragment : Fragment() {

    private lateinit var myJobBinding: FragmentMyJobBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        myJobBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_job, container, false)
        myJobBinding.jobRV.layoutManager = LinearLayoutManager(requireActivity())
        myJobBinding.jobRV.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        myJobBinding.jobRV.adapter = MyJobAdapter(requireActivity(), arrayList)
        return myJobBinding.root
    }
}