package com.phpexpert.bringme.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.LoginActivity
import com.phpexpert.bringme.databinding.FragmentThreeBinding

class SwipeFragmentThree : Fragment() {
    lateinit var layoutSwipeFragmentThree: FragmentThreeBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        layoutSwipeFragmentThree = DataBindingUtil.inflate(inflater, R.layout.fragment_three, container, false)

        layoutSwipeFragmentThree.buttonGetStart.setOnClickListener {
            val login = Intent(activity, LoginActivity::class.java)
            startActivity(login)
        }
        return layoutSwipeFragmentThree.root
    }

}