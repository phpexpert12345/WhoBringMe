package com.phpexpert.bringme.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.SwipeViewActivity
import com.phpexpert.bringme.utilities.BaseActivity

class SplashActivity : BaseActivity() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.parseColor("#00000000")
        setContentView(R.layout.activity_splash)
        doTimeDelay()
        handler!!.postDelayed(runnable!!, 3000)
    }

    private fun doTimeDelay() {
        handler = Handler()
        runnable = Runnable {
            val intent = Intent(this@SplashActivity, SwipeViewActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            startActivity(intent)
            handler!!.removeCallbacks(runnable!!)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        /*val w: Window = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)*/

    }

    override fun onPause() {
        super.onPause()

        /*val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)*/
    }
}