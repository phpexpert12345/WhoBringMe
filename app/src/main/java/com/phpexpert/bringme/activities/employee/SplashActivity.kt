package com.phpexpert.bringme.activities.employee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.utilities.BaseActivity

class SplashActivity : BaseActivity() {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val w: Window = window
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
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

    override fun onPause() {
        super.onPause()

        val window: Window = window
        window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
}