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
import android.util.Patterns
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
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
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetDialogMessageText: TextView
    lateinit var bottomSheetDialogMessageOkButton: TextView
    lateinit var bottomSheetDialogMessageCancelButton: TextView

    @Inject
    lateinit var sharedPrefrenceManager: SharedPrefrenceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSharedPreferComponent.builder()
                .sharedPreferModule(SharedPreferModule(this))
                .build()
                .inject(this)
        super.onCreate(savedInstanceState)
        bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_layout)
        bottomSheetDialogMessageText = bottomSheetDialog.findViewById(R.id.message)!!
        bottomSheetDialogMessageOkButton = bottomSheetDialog.findViewById(R.id.okText)!!
        bottomSheetDialogMessageCancelButton = bottomSheetDialog.findViewById(R.id.cancelText)!!
    }

    @SuppressLint("SetTextI18n")
    fun hitAuthApi(authData: AuthInterface) = if (isOnline()) {
        authViewModel = ViewModelProvider(this).get(AuthModel::class.java)
        authViewModel.getAuthDataModel().observe(this, {
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = "OK"
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            if (it.status_code == "0") {
                AuthSingleton.authObject = it.data!![0]
                authData.isAuthHit(true)
            } else {
                bottomSheetDialogMessageOkButton.setOnClickListener {
                    authData.isAuthHit(false)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetDialog.show()
            }
        })

    } else {
        bottomSheetDialogMessageText.text = getString(R.string.network_error)
        bottomSheetDialogMessageOkButton.text = "OK"
        bottomSheetDialogMessageCancelButton.visibility = View.GONE
        bottomSheetDialogMessageOkButton.setOnClickListener {
            authData.isAuthHit(false)
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
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

    fun CharSequence?.isValidEmail() = !isNullOrEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()

}