@file:Suppress("DEPRECATION")

package com.phpexpert.bringme.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.phpexpert.bringme.fragment.SwipeFragmentOne
import com.phpexpert.bringme.fragment.SwipeFragmentThree
import com.phpexpert.bringme.fragment.SwipeFragmentTwo

class ViewPagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    override fun getItem(position: Int): Fragment {
        var fragment: Fragment? = null
        return when (position) {
            0 -> SwipeFragmentOne().also { fragment = it }
            1 -> SwipeFragmentTwo().also { fragment = it }
            2 -> SwipeFragmentThree().also { fragment = it }
            else -> fragment!!
        }
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return 3
    }
}