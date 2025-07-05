package com.yunzia.hyperstar.utils

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize


@Composable
fun appIcon(packageName: String, content: Context = LocalContext.current): MutableState<Drawable?> {
    var appIcon: MutableState<Drawable?> = remember { mutableStateOf(null) }

    LaunchedEffect(packageName) {
        val pm = content.packageManager
        try {
            val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            appIcon.value =  info.applicationInfo?.loadIcon(pm)

        } catch (e: PackageManager.NameNotFoundException) {
            // Handle the case where the package is not found
        }
    }

    return appIcon
}

@Composable
fun loadAppInfo(packageName: String, content: Context = LocalContext.current): MutableState<AppInfo?> {
    val appInfo: MutableState<AppInfo?> = remember { mutableStateOf(null) }

    LaunchedEffect(packageName) {
        val pm = content.packageManager
        try {

            val info = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            val applicationInfo = pm.getApplicationInfo(packageName, 0)

            appInfo.value = AppInfo(
                info.applicationInfo?.loadIcon(pm),
                pm.getApplicationLabel(info.applicationInfo!!).toString(),
                info.versionName,
                info.longVersionCode
            )

        } catch (e: PackageManager.NameNotFoundException) {
            // Handle the case where the package is not found
        }
    }

    return appInfo
}

@Composable
fun rememberWindowSize(): State<DpSize> {
    val windowInfo = LocalWindowInfo.current
    val density = LocalDensity.current

    val size =  remember {
        derivedStateOf {
            with(density) {
                DpSize(
                    width = windowInfo.containerSize.width.toDp(),
                    height = windowInfo.containerSize.height.toDp()
                )
            }
        }
    }
    return size
}


data class AppInfo(
    val appIcon: Drawable?,
    val appName: String,
    val versionName: String?,
    val versionCode: Long
)



