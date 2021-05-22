package com.phpexpert.bringme.fcm

import android.app.ActivityManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.phpexpert.bringme.R
import com.phpexpert.bringme.activities.employee.DashboardActivity


@Suppress("DEPRECATION", "LocalVariableName")
class MyFirebaseMessagingService : FirebaseMessagingService() {

/*    @Inject
    lateinit var sharedPrefrenceManager: SharedPrefrenceManager*/

    override fun onNewToken(p0: String) {

    }


    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        val jsonObject = (Gson().toJsonTree(p0.data)).asJsonObject

        if(isAppRunning(this, applicationContext.packageName)) {
            val intent = Intent(this, DashboardActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT)
            val builder = NotificationCompat.Builder(this, "11")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(jsonObject.get("title").asString)
                    .setContentText(jsonObject.get("text").asString)
                    .setStyle(
                            NotificationCompat.BigTextStyle()
                                    .bigText(jsonObject.get("text").asString)
                    )
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("11", "name", importance).apply {
                    description = "descriptionText"
                }
                val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        }else{
            val builder = NotificationCompat.Builder(this, "11")
                    .setSmallIcon(R.drawable.app_icon)
                    .setContentTitle(jsonObject.get("title").asString)
                    .setContentText(jsonObject.get("text").asString)
                    .setStyle(
                            NotificationCompat.BigTextStyle()
                                    .bigText(jsonObject.get("text").asString)
                    )
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true)


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel("11", "name", importance).apply {
                    description = "descriptionText"
                }
                val notificationManager: NotificationManager =
                        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        }
    }

    private fun isAppRunning(context: Context, packageName: String):Boolean {
        val activityManager = context.getSystemService(ACTIVITY_SERVICE) as ActivityManager
        val procInfos = activityManager.runningAppProcesses
        if (procInfos != null) {
            for (processInfo in procInfos) {
                if (processInfo.processName == packageName) {
                    return true
                }
            }
        }
        return false
    }
}
