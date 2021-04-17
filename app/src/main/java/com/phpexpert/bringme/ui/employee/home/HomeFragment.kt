package com.phpexpert.bringme.ui.employee.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.CreateJobActivity
import com.phpexpert.bringme.databinding.EmployeeFragmentHomeBinding
import com.phpexpert.bringme.databinding.JobViewLayoutBinding


class HomeFragment : Fragment(), HomeFragmentAdapter.OnClickView {
    private var homeViewModel: HomeViewModel? = null
    private lateinit var homeFragmentBinding: EmployeeFragmentHomeBinding
    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutBinding
    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.employee_fragment_home, container, false)

        jobViewBinding = homeFragmentBinding.bottomHistryLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0
        setObserver()
        setActions()
        setList()

        return homeFragmentBinding.root
    }

    private fun setActions() {
        homeFragmentBinding.addJobButton.setOnClickListener {
            startActivity(Intent(requireActivity(), CreateJobActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    private fun setList(){
        homeFragmentBinding.homeRv.layoutManager = LinearLayoutManager(requireActivity())
        homeFragmentBinding.homeRv.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        homeFragmentBinding.homeRv.adapter = HomeFragmentAdapter(requireActivity(), arrayList, this)
    }

    private fun setObserver() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel!!.text.observe(viewLifecycleOwner, {
        })
    }

    override fun onClick(textInput: String) {
        when (textInput) {
            "viewData" -> {
                mBottomSheetFilter.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                jobViewBinding.closeView.setOnClickListener {
                    mBottomSheetFilter.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
            }
        }
    }
}