package com.phpexpert.bringme.ui.delivery.earning

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.delivery.TransactionActivity
import com.phpexpert.bringme.activities.delivery.WithdrawActivity
import com.phpexpert.bringme.databinding.FragmentEarningBinding
import com.phpexpert.bringme.utilities.BaseActivity

class EarningFragment : Fragment() {

    private lateinit var fragmentEarningBinding: FragmentEarningBinding
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        fragmentEarningBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_earning, container, false)
        fragmentEarningBinding.languageModel = (activity as BaseActivity).sharedPrefrenceManager.getLanguageData()

        fragmentEarningBinding.earningRV.layoutManager = LinearLayoutManager(requireActivity())

        fragmentEarningBinding.currencyCode.text = (context as BaseActivity).getCurrencySymbol()
        fragmentEarningBinding.currencyCode1.text = (context as BaseActivity).getCurrencySymbol()
        fragmentEarningBinding.earningRV.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        fragmentEarningBinding.earningRV.adapter = EarningAdapter(requireActivity(), arrayList)
        fragmentEarningBinding.transactionLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), TransactionActivity::class.java))
        }
        fragmentEarningBinding.withdrawLayout.setOnClickListener {
            startActivity(Intent(requireActivity(), WithdrawActivity::class.java))
        }


        return fragmentEarningBinding.root
    }
}