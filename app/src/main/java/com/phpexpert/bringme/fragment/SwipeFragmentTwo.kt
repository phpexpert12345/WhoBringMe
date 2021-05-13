package com.phpexpert.bringme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.FragmentTwoBinding
import com.phpexpert.bringme.utilities.BaseActivity

class SwipeFragmentTwo : Fragment() {
    private lateinit var fragmentTwoBinding:FragmentTwoBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        fragmentTwoBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_two, container, false)
        fragmentTwoBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        return fragmentTwoBinding.root
    }
}