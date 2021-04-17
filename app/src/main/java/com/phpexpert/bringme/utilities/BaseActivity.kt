package com.phpexpert.bringme.utilities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.AuthModel


@Suppress("DEPRECATION")
open class BaseActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthModel

    fun hitAuthApi(isAuthHit: AuthInterface) = if (isOnline()) {
        authViewModel = ViewModelProvider(this).get(AuthModel::class.java)
        authViewModel.getAuthDataModel(this).observe(this, {
            if (it.status_code == "0") {
                isAuthHit.isAuthHit(true)
                AuthSingleton.authObject = it.data!![0]
            } else {
                Toast.makeText(this, it.status_message, Toast.LENGTH_LONG).show()
            }
        })
    } else {
        isAuthHit.isAuthHit(false)
        Toast.makeText(this, getString(R.string.network_error), Toast.LENGTH_LONG).show()
    }

    open fun isOnline(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    @SuppressLint("ObsoleteSdkInt")
    fun isCheckPermissions(activity: Activity, permissions: Array<String>): Boolean {
        try {
            var result: Int
            val listPermissionNeeded = ArrayList<String>()
            for (value in permissions) {
                result = ContextCompat.checkSelfPermission(activity, value)
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionNeeded.add(value)
                }
            }
            try {
                if (listPermissionNeeded.isNotEmpty()) {
                    ActivityCompat.requestPermissions(
                            activity,
                            permissions,
                            100
                    )
                    return false
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }
}