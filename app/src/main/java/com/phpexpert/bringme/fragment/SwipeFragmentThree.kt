package com.phpexpert.bringme.fragment

import android.content.Intent
import android.os.Bundle
import android.os.Handler
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
            layoutSwipeFragmentThree.buttonGetStart.startAnimation()
            Handler().postDelayed({
                val login = Intent(activity, LoginActivity::class.java)
                login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(login)
            }, 1000)

        }
        return layoutSwipeFragmentThree.root
    }

    override fun onPause() {
        super.onPause()
        layoutSwipeFragmentThree.buttonGetStart.revertAnimation()
    }

}