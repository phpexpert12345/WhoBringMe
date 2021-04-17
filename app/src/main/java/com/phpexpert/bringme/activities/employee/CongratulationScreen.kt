package com.phpexpert.bringme.activities.employee

import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.JobViewLayoutBinding
import com.phpexpert.bringme.databinding.PaymentSuccessfullPageBinding
import com.phpexpert.bringme.utilities.BaseActivity

class CongratulationScreen : BaseActivity() {

    private lateinit var congratulationScreen: PaymentSuccessfullPageBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
        congratulationScreen = DataBindingUtil.setContentView(this, R.layout.payment_successfull_page)
        jobViewBinding = congratulationScreen.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0
        congratulationScreen.viewJobIcon.setOnClickListener {
            mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
            congratulationScreen.jobViewBlur.visibility = View.VISIBLE
            jobViewBinding.closeView.setOnClickListener {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                congratulationScreen.jobViewBlur.visibility = View.GONE
            }
        }

        congratulationScreen.backArrow.setOnClickListener {
            finish()
        }
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