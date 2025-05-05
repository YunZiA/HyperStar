package com.yunzia.hyperstar.utils

import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yunzia.hyperstar.MainActivity

class PremissionHelper {
}

fun MainActivity.getInstalledApps(){
    try {
        val permissionInfo = this.applicationContext.packageManager.getPermissionInfo(
            "com.android.permission.GET_INSTALLED_APPS",
            0
        )
        if (permissionInfo != null && permissionInfo.packageName == "com.lbe.security.miui") {
            //MIUI 系统支持动态申请该权限
            if (ContextCompat.checkSelfPermission(
                    this.applicationContext,
                    "com.android.permission.GET_INSTALLED_APPS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //没有权限，需要申请
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("com.android.permission.GET_INSTALLED_APPS"),
                    999
                )
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
}