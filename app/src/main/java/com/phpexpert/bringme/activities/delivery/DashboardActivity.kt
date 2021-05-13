package com.phpexpert.bringme.activities.delivery

import android.Manifest
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.utilities.BaseActivity

class DashboardActivity : BaseActivity() {

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CALL_PHONE
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_delivery)
        supportActionBar!!.hide()
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.menu.findItem(R.id.navigation_home).title = sharedPrefrenceManager.getLanguageData().title_home
        navView.menu.findItem(R.id.navigation_dashboard).title= sharedPrefrenceManager.getLanguageData().my_job
        navView.menu.findItem(R.id.navigation_notifications).title= sharedPrefrenceManager.getLanguageData().my_earning
        navView.menu.findItem(R.id.navigation_profile).title= sharedPrefrenceManager.getLanguageData().profile

        val appBarConfiguration = AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
        isCheckPermissions(this, perission)
    }
}