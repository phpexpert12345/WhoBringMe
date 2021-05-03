package com.phpexpert.bringme.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.WindowManager
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.SwipeViewActivity
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS

class SplashActivity : BaseActivity(), AuthInterface {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.parseColor("#00000000")
        setContentView(R.layout.activity_splash)
        hitAuthApi(this)
    }

    private fun doTimeDelay() {
        handler = Handler()
        runnable = Runnable {
            if (sharedPrefrenceManager.getPreference(CONSTANTS.isLogin) == "true") {
                if (sharedPrefrenceManager.getProfile().account_type == "Client / Receiver" || sharedPrefrenceManager.getProfile().account_type=="1") {
                    val intent = Intent(this@SplashActivity, com.phpexpert.bringme.activities.employee.DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    handler!!.removeCallbacks(runnable!!)
                    finish()
                } else {
                    val intent = Intent(this@SplashActivity, com.phpexpert.bringme.activities.delivery.DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    startActivity(intent)
                    handler!!.removeCallbacks(runnable!!)
                    finish()
                }
            } else {
                val intent = Intent(this@SplashActivity, SwipeViewActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
                handler!!.removeCallbacks(runnable!!)
                finish()
            }
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

    override fun isAuthHit(value: Boolean) {
        if (value) {
            doTimeDelay()
            handler!!.postDelayed(runnable!!, 3000)
        }
    }
}