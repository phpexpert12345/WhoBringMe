package com.phpexpert.bringme.ui.delivery.home

import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.phpexpert.bringme.R
import com.phpexpert.bringme.databinding.*


class HomeFragment : Fragment(), HomeFragmentAdapter.OnClickView {
    private var homeViewModel: HomeViewModel? = null
    private lateinit var homeFragmentBinding: DeliveryFragmentHomeBinding

    private lateinit var mBottomSheetFilter: BottomSheetBehavior<View>
    private lateinit var jobViewBinding: JobViewLayoutDeliveryBinding

    private lateinit var orderAcceptBehavior: BottomSheetBehavior<View>
    private lateinit var orderAcceptBinding: LayoutOrderAcceptBottomSheetBinding

    private lateinit var orderFinishedBehavior: BottomSheetBehavior<View>
    private lateinit var orderFinishedBinding: OrderFinishedBottomSheetBinding

    private lateinit var orderDeclineBehavior: BottomSheetBehavior<View>
    private lateinit var orderDeclineBinding: OrderDeclineBottomSheetBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.delivery_fragment_home, container, false)

        jobViewBinding = homeFragmentBinding.jobViewLayout
        mBottomSheetFilter = BottomSheetBehavior.from(jobViewBinding.root)
        mBottomSheetFilter.isDraggable = false
        mBottomSheetFilter.peekHeight = 0

        orderAcceptBinding = homeFragmentBinding.orderAcceptLayout
        orderAcceptBehavior = BottomSheetBehavior.from(orderAcceptBinding.root)
        orderAcceptBehavior.isDraggable = false
        orderAcceptBehavior.peekHeight = 0

        orderFinishedBinding = homeFragmentBinding.finishedOrderLayout
        orderFinishedBehavior = BottomSheetBehavior.from(orderFinishedBinding.root)
        orderFinishedBehavior.isDraggable = false
        orderFinishedBehavior.peekHeight = 0

        orderDeclineBinding = homeFragmentBinding.declineOrderLayout
        orderDeclineBehavior = BottomSheetBehavior.from(orderDeclineBinding.root)
        orderDeclineBehavior.isDraggable = false
        orderDeclineBehavior.peekHeight = 0
        setObserver()
        setActions()
        setList()

        return homeFragmentBinding.root
    }

    private fun setActions() {

    }

    override fun onResume() {
        super.onResume()
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
    }

    private fun setList() {
        homeFragmentBinding.homeRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        homeFragmentBinding.homeRecyclerView.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        homeFragmentBinding.homeRecyclerView.adapter = HomeFragmentAdapter(requireActivity(), arrayList, this)
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
            "acceptData" -> {
                orderAcceptBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                orderAcceptBinding.noLayout.setOnClickListener {
                    orderAcceptBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderAcceptBinding.yesLayout.setOnClickListener {
                    orderAcceptBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
            }
            "finishedJob" -> {
                orderFinishedBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                orderFinishedBinding.noLayout.setOnClickListener {
                    orderFinishedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderFinishedBinding.yesLayout.setOnClickListener {
                    orderFinishedBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
            }
            "declineJob" -> {
                orderDeclineBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                homeFragmentBinding.blurView.visibility = View.VISIBLE
                orderDeclineBinding.noLayout.setOnClickListener {
                    orderDeclineBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
                orderDeclineBinding.yesLayout.setOnClickListener {
                    orderDeclineBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
                    homeFragmentBinding.blurView.visibility = View.GONE
                }
            }
        }
    }
}