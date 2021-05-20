package com.phpexpert.bringme.utilities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.telephony.TelephonyManager
import android.util.Base64
import android.util.Patterns
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.phpexpert.bringme.R
import com.phpexpert.bringme.dtos.LanguageDtoData
import com.phpexpert.bringme.interfaces.AuthInterface
import com.phpexpert.bringme.models.AuthModel
import java.io.File
import java.io.FileOutputStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@Suppress("DEPRECATION", "DEPRECATED_IDENTITY_EQUALS")
open class BaseActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthModel
    lateinit var bottomSheetDialog: BottomSheetDialog
    lateinit var bottomSheetDialogMessageText: TextView
    lateinit var bottomSheetDialogHeadingText:TextView
    lateinit var bottomSheetDialogMessageOkButton: TextView
    lateinit var bottomSheetDialogMessageCancelButton: TextView


    private var currencyLocaleMap: SortedMap<Currency, Locale>? = null
    private lateinit var mLocationCallback: LocationCallback
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var sharedPrefrenceManager: SharedPrefrenceManager

    @SuppressLint("InlinedApi")
    private var perission = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerSharedPreferComponent.builder()
                .sharedPreferModule(SharedPreferModule(this))
                .build()
                .inject(this)
        super.onCreate(savedInstanceState)
        sharedPrefrenceManager.saveLanguageData(LanguageDtoData())
        bottomSheetDialog = BottomSheetDialog(this, R.style.SheetDialog)
        bottomSheetDialog.setContentView(R.layout.bottom_dialog_layout)
        bottomSheetDialog.setCancelable(false)
        bottomSheetDialog.findViewById<TextView>(R.id.textHeading)?.text = sharedPrefrenceManager.getLanguageData().alert_text
        bottomSheetDialog.findViewById<TextView>(R.id.cancelText)?.text = sharedPrefrenceManager.getLanguageData().cancel
        bottomSheetDialog.findViewById<TextView>(R.id.okText)?.text = sharedPrefrenceManager.getLanguageData().ok_text
        bottomSheetDialogHeadingText = bottomSheetDialog.findViewById(R.id.textHeading)!!
        bottomSheetDialogMessageText = bottomSheetDialog.findViewById(R.id.message)!!
        bottomSheetDialogMessageOkButton = bottomSheetDialog.findViewById(R.id.okText)!!
        bottomSheetDialogMessageCancelButton = bottomSheetDialog.findViewById(R.id.cancelText)!!
        authViewModel = ViewModelProvider(this).get(AuthModel::class.java)

        currencyLocaleMap = TreeMap { c1, c2 -> c1.currencyCode.compareTo(c2.currencyCode) }
        for (locale in Locale.getAvailableLocales()) {
            try {
                val currency = Currency.getInstance(locale)
                (currencyLocaleMap as TreeMap<Currency, Locale>)[currency] = locale!!
            } catch (e: java.lang.Exception) {
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun gettingLocation() {
        try {
            val mLocationRequest = LocationRequest.create()
            mLocationRequest.interval = 60000
            mLocationRequest.fastestInterval = 5000
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mLocationCallback = object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    for (location in locationResult.locations) {
                        try {
                            if (location != null) {
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.currentLatitue, location.latitude.toString())
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.currentLongitude, location.longitude.toString())
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.isLocation, "true")
                            } else {
                                sharedPrefrenceManager.savePrefrence(CONSTANTS.isLocation, "false")

                            }
                            break
                        } catch (e: Exception) {
                            sharedPrefrenceManager.savePrefrence(CONSTANTS.isLocation, "false")

                        }
                    }
                    mFusedLocationClient.removeLocationUpdates(mLocationCallback)
//                    v.putExtra("postDataModel", postDataOtp)
//                    startActivity(v)
                }
            }
            @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null)

//                        val mLocation =
//                                LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient)

        } catch (e: Exception) {
            sharedPrefrenceManager.savePrefrence(CONSTANTS.isLocation, "false")
            /* bottomSheetDialogMessageText.text = sharedPrefrenceManager.getLanguageData().something_is_wrong
             bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
             bottomSheetDialogMessageCancelButton.visibility = View.GONE
             bottomSheetDialogMessageOkButton.setOnClickListener {
                 bottomSheetDialog.dismiss()
             }
             bottomSheetDialog.show()*/
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun hitAuthApi(authData: AuthInterface) {
        authViewModel.getAuthDataModel().observe(this, {
            bottomSheetDialogMessageText.text = it.status_message
            bottomSheetDialogMessageOkButton.text = sharedPrefrenceManager.getLanguageData().ok_text
            bottomSheetDialogMessageCancelButton.visibility = View.GONE
            if (it.status_code == "0") {
                sharedPrefrenceManager.saveAuthData(it.data!![0])
                authData.isAuthHit(true, it.status_message!!)
            } else {
                authData.isAuthHit(false, it!!.status_message!!)
            }
        })
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {

            100 -> { // Allowed was selected so Permission granted
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //nothing else
                } else {
                    // User selected the Never Ask Again Option Change settings in app settings manually
                    val alertDialogBuilder = AlertDialog.Builder(this)
                    alertDialogBuilder.setTitle("Change Permissions in Settings")
                    alertDialogBuilder
                            .setMessage("" +
                                    "\nClick SETTINGS to Manually Set\n" + "Permissions to use Database Storage")
                            .setCancelable(false)
                            .setPositiveButton("SETTINGS") { _: DialogInterface, _: Int ->
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivityForResult(intent, 1000)
                            }

                    val alertDialog = alertDialogBuilder.create()
                    alertDialog.show()
                }
            }
        }
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

    fun String?.formatChange() = run {
        try {
//            val formatter = NumberFormat.getInstance(Locale(sharedPrefrenceManager.getAuthData().lang_code!!, "DE"))
//            formatter.format(this?.toFloat())
            val symbols = DecimalFormatSymbols(Locale(sharedPrefrenceManager.getAuthData().lang_code!!, "DE"))
            val formartter = (DecimalFormat("##.##", symbols))
            formartter.format(this?.toFloat())
        } catch (e: Exception) {
            this
        }
    }

    open fun getCurrencySymbol(): String? {
        val currency = Currency.getInstance(sharedPrefrenceManager.getAuthData().currency_code)
        println(sharedPrefrenceManager.getAuthData().currency_code + ":-" + currency.getSymbol(currencyLocaleMap?.get(currency)))
        return currency.getSymbol(currencyLocaleMap?.get(currency))
    }

    fun isLocationEnabled():Boolean = (getSystemService(LOCATION_SERVICE) as LocationManager).isProviderEnabled(LocationManager.GPS_PROVIDER)
}