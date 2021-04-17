package com.phpexpert.bringme.ui.employee.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.EmployeeFragmentHistoryBinding
import com.phpexpert.bringme.databinding.JobViewLayoutBinding
import com.phpexpert.bringme.databinding.WriteReviewLayoutBinding

class HistoryFragment : Fragment(), HistoryFragmentAdapter.OnClickView {
    private var dashboardViewModel: DashboardViewModel? = null
    private lateinit var historyBinding: EmployeeFragmentHistoryBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding

    private lateinit var mBottomSheetReview: BottomSheetBehavior<View>
    private lateinit var reviewBinding: WriteReviewLayoutBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        dashboardViewModel = ViewModelProvider(this).get(DashboardViewModel::class.java)

        historyBinding = DataBindingUtil.inflate(inflater, R.layout.employee_fragment_history, container, false)
        setList()
        jobViewBinding = historyBinding.bottomHistryLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        reviewBinding = historyBinding.reviewLayout
        mBottomSheetReview = BottomSheetBehavior.from(reviewBinding.root)
        mBottomSheetReview.isDraggable = false
        mBottomSheetReview.peekHeight = 0

        return historyBinding.root
    }

    private fun setList() {
        historyBinding.historyRV.layoutManager = LinearLayoutManager(requireActivity())
        historyBinding.historyRV.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        historyBinding.historyRV.adapter = HistoryFragmentAdapter(requireActivity(), arrayList, this)
    }

    override fun onClick(textInput: String) {
        when (textInput) {
            "viewData" -> {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
                historyBinding.blurView.visibility = View.VISIBLE
                jobViewBinding.closeView.setOnClickListener {
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    historyBinding.blurView.visibility = View.GONE
                }
            }
            else -> {
                mBottomSheetReview.state = BottomSheetBehavior.STATE_EXPANDED
                historyBinding.blurView.visibility = View.VISIBLE
                reviewBinding.closeIcon.setOnClickListener {
                    mBottomSheetReview.state = BottomSheetBehavior.STATE_COLLAPSED
                    historyBinding.blurView.visibility = View.GONE
                }
            }
        }
    }
}