package com.phpexpert.bringme.activities.employee

import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.phpexpert.bringme.R
import com.phpexpert.bringme.utilities.BaseActivity
import com.phpexpert.bringme.utilities.SoftInputAssist

class DashboardActivity : BaseActivity() {

    private lateinit var softInputAssist: SoftInputAssist
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar!!.hide()
        val navView = findViewById<BottomNavigationView>(R.id.nav_view)
        navView.menu.findItem(R.id.navigation_home).title = sharedPrefrenceManager.getLanguageData().title_home
        navView.menu.findItem(R.id.navigation_dashboard).title= sharedPrefrenceManager.getLanguageData().title_history
        navView.menu.findItem(R.id.navigation_notifications).title= sharedPrefrenceManager.getLanguageData().notification
        navView.menu.findItem(R.id.navigation_profile).title= sharedPrefrenceManager.getLanguageData().profile

        val appBarConfiguration = AppBarConfiguration.Builder(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_profile).build()
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)
        NavigationUI.setupWithNavController(navView, navController)
    }
}