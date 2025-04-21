package com.yunzia.hyperstar.utils

import android.content.pm.PackageManager
import androidx.activity.compose.LocalActivity
import androidx.compose.runtime.Composable
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.yunzia.hyperstar.MainActivity

class PremissionHelper {
}

@Composable
fun GetInstalledApps(
    activity: MainActivity = LocalActivity.current as MainActivity
){
    try {
        val permissionInfo = activity.applicationContext.packageManager.getPermissionInfo(
            "com.android.permission.GET_INSTALLED_APPS",
            0
        )
        if (permissionInfo != null && permissionInfo.packageName == "com.lbe.security.miui") {
            //MIUI 系统支持动态申请该权限
            if (ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    "com.android.permission.GET_INSTALLED_APPS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //没有权限，需要申请
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf("com.android.permission.GET_INSTALLED_APPS"),
                    999
                )
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
}