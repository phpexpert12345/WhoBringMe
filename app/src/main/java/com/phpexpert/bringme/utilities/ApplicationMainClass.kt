package com.phpexpert.bringme.utilities

import android.app.Application
import org.acra.ACRA
import org.acra.ReportField
import org.acra.annotation.ReportsCrashes
import org.acra.sender.HttpSender

@ReportsCrashes(mailTo = "hunny226@gmail.com",
        formUri = "https://{myusername}.cloudant.com/acra-{myapp}/_design/acra-storage/_update/report",
        reportType = HttpSender.Type.JSON,
        httpMethod = HttpSender.Method.POST,
        formUriBasicAuthLogin = "GENERATED_USERNAME_WITH_WRITE_PERMISSIONS",
        formUriBasicAuthPassword = "GENERATED_PASSWORD",
        formKey = "", // This is required for backward compatibility but not used
        customReportContent = [ReportField.APP_VERSION_CODE, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION, ReportField.PACKAGE_NAME, ReportField.REPORT_ID, ReportField.BUILD, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.PHONE_MODEL, ReportField.LOGCAT])


class ApplicationMainClass : Application() {
    override fun onCreate() {
        super.onCreate()
        ACRA.init(this);

    }
}