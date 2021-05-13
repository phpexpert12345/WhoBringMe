package com.phpexpert.bringme.activities.delivery

import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.adapters.TransactionHistoryAdapter
import com.phpexpert.bringme.databinding.LayoutTransactionHistoryActivityBinding
import com.phpexpert.bringme.utilities.BaseActivity

class TransactionActivity : BaseActivity() {
    private lateinit var transactionActivity: LayoutTransactionHistoryActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        transactionActivity = DataBindingUtil.setContentView(this, R.layout.layout_transaction_history_activity)
        transactionActivity.languageModel = sharedPrefrenceManager.getLanguageData()
        transactionActivity.historyTransactionRV.layoutManager = LinearLayoutManager(this)
        transactionActivity.historyTransactionRV.isNestedScrollingEnabled = false
        transactionActivity.backArrow.setOnClickListener {
            finish()
        }
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        transactionActivity.historyTransactionRV.adapter = TransactionHistoryAdapter(this, arrayList)
    }

    override fun onResume() {
        super.onResume()
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    override fun onPause() {
        super.onPause()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}