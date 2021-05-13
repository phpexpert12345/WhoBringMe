package com.phpexpert.bringme.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.FragmentOneBinding
import com.phpexpert.bringme.utilities.BaseActivity

class SwipeFragmentOne : Fragment() {

    private lateinit var fragmentOneBinding: FragmentOneBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        fragmentOneBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_one, container, false)
        fragmentOneBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()
        return fragmentOneBinding.root
    }
}