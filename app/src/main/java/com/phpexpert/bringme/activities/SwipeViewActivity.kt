package com.phpexpert.bringme.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.Utilities.BaseActivity
import com.phpexpert.bringme.adapters.ViewPagerAdapter
import com.phpexpert.bringme.databinding.ActivitySwipeViewBinding

@Suppress("DEPRECATION")
class SwipeViewActivity : BaseActivity() {
    private var myAdapter: ViewPagerAdapter? = null
    lateinit var swipeViewBinding: ActivitySwipeViewBinding
    private val handler = Handler()
    private lateinit var runnable: Runnable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        swipeViewBinding = DataBindingUtil.setContentView(this, R.layout.activity_swipe_view)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = resources.getColor(R.color.white)
        myAdapter = ViewPagerAdapter(supportFragmentManager)
        swipeViewBinding.viewPager.adapter = myAdapter
        setActions()
    }

    private fun setActions(){
        swipeViewBinding.viewPager.addOnPageChangeListener(object :ViewPager.OnPageChangeListener{
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onPageSelected(position: Int) {
                swipeViewBinding.position.text = "${position+1}/3"
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })


        runnable = Runnable {
            if (swipeViewBinding.viewPager.currentItem==2){
                handler.removeCallbacks(runnable)
            }
            else{
                swipeViewBinding.viewPager.currentItem = swipeViewBinding.viewPager.currentItem+1
                handler.postDelayed(runnable, 1000)
            }
        }

        handler.postDelayed(runnable, 1000)

        swipeViewBinding.skip.setOnClickListener {
            swipeViewBinding.viewPager.currentItem = 3
        }
    }
    override fun onPause() {
        super.onPause()
        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }
}