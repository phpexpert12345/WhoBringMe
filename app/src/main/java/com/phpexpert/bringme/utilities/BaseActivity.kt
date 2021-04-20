package com.phpexpert.bringme.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.phpexpert.bringme.R
import com.phpexpert.bringme.dtos.AuthSingleton
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.AuthModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")
open class BaseActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthModel

    @Inject
    lateinit var sharedPrefrenceManager: SharedPrefrenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSharedPreferComponent.builder()
                .sharedPreferModule(SharedPreferModule(this))
                .build()
                .inject(this)
        super.onCreate(savedInstanceState)

    }

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

    fun base64Encoded(dataValue: String?): String {
        val data = dataValue?.toByteArray(charset("UTF-8"))
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

    @SuppressLint("HardwareIds", "MissingPermission")
    open fun getIMEI(context: Context): String? {
        val mngr =
                context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return try {
            when {
                ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_PHONE_STATE
                ) !== PackageManager.PERMISSION_GRANTED -> {
                    ""
                }
                mngr.deviceId == null -> {
                    Settings.Secure.getString(
                            context.contentResolver, Settings.Secure.ANDROID_ID
                    )
                }
                else -> mngr.deviceId
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            Settings.Secure.getString(
                    context.contentResolver, Settings.Secure.ANDROID_ID
            )
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun saveBitmapIntoSDCardImage(finalBitmap: Bitmap): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val myDir = File(cacheDir, "$timeStamp-image")
        if (!myDir.exists()) {
            myDir.mkdirs()
        }
        /* if (myDir.exists()) {`
            myDir.delete();
        }*/

        val fname = "$timeStamp.jpg"
        val file = File(myDir, fname)
        try {
            val out = FileOutputStream(file)
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 60, out)
            out.flush()
            out.close()
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return file
    }
}