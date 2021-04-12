package com.phpexpert.bringme.ui.home

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.CreateJobActivity
import com.phpexpert.bringme.databinding.FragmentHomeBinding


class HomeFragment : Fragment() {
    private var homeViewModel: HomeViewModel? = null
    private lateinit var homeFragmentBinding: FragmentHomeBinding

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        homeFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)
        val window: Window = requireActivity().window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.colorLoginButton)
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

    private fun setList(){
        homeFragmentBinding.homeRv.layoutManager = LinearLayoutManager(requireActivity())
        homeFragmentBinding.homeRv.isNestedScrollingEnabled = false
        val arrayList = ArrayList<String>()
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        arrayList.add("abc")
        homeFragmentBinding.homeRv.adapter = HomeFragmentAdapter(requireActivity(), arrayList)
    }

    private fun setObserver() {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModel!!.text.observe(viewLifecycleOwner, {
        })
    }
}