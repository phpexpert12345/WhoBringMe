package com.phpexpert.bringme.activities

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.Window
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.SwipeViewActivity
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.CONSTANTS

@Suppress("DEPRECATION")
class SplashActivity : BaseActivity(), AuthInterface {
    private var handler: Handler? = null
    private var runnable: Runnable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val window: Window = window
        @Suppress("DEPRECATION")
        window.statusBarColor = Color.parseColor("#00000000")
        setContentView(R.layout.activity_splash)
        hitAuthApi(this)
        gettingLocation()
        doTimeDelay()
        handler!!.postDelayed(runnable!!, 3000)
    }

    private fun doTimeDelay() {
        handler = Handler()
        runnable = Runnable {
            sharedPrefrenceManager.saveLanguageData(LanguageDtoData())
            if (sharedPrefrenceManager.getPreference(CONSTANTS.isLogin) == "true") {
                if (sharedPrefrenceManager.getProfile().account_type == "Client / Receiver" || sharedPrefrenceManager.getProfile().account_type == "1") {
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

    override fun isAuthHit(value: Boolean, message:String) {
        /*if (value) {
            doTimeDelay()
            handler!!.postDelayed(runnable!!, 3000)
        }*/
    }
}