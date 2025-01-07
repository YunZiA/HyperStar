package com.yunzia.hyperstar.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import yunzia.utils.SystemProperties



fun getAndroidVersion(): Int = Build.VERSION.SDK_INT

fun getSystemVersionIncremental(): String = SystemProperties.get("ro.mi.os.version.incremental").ifEmpty { SystemProperties.get("ro.system.build.version.incremental") }

fun getDeviceName(): String = Build.DEVICE
fun getMarketName(): String = SystemProperties.get("ro.product.marketname")

fun getOSVersion()= SystemProperties.getInt("ro.mi.os.version.code", 1)

fun isBetaOs() = ReflectUtils.getStaticFieldValue(Build::class.java, "IS_DEV_VERSION", Boolean::class.java,false)

fun isFold()= SystemProperties.getInt("persist.sys.muiltdisplay_type", 0) == 2

fun isPad()= SystemProperties.get("ro.build.characteristics").contains("tablet")

fun isOS2():Boolean{
    if (isBetaOs()) return false
    return getOSVersion() == 2
}
fun isOS2Settings():Boolean{
    return SPUtils.getInt("is_Hook_Channel",if (isOS2()) 1 else 0) == 1
}
fun isOS2Hook():Boolean{
    return XSPUtils.getInt("is_Hook_Channel",if (isOS2()) 1 else 0) == 1
}
fun isHookChannel():Int{
    return XSPUtils.getInt("is_Hook_Channel",if (isOS2()) 1 else 0)
}



fun getVersionCode(mContext: Context): Int {
    var versionCode = 0
    try {
        //获取软件版本号，对应AndroidManifest.xml下android:versionCode
        versionCode = mContext.packageManager.getPackageInfo(mContext.packageName, 0).longVersionCode.toInt()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return versionCode
}

/**
 * 获取版本号名称
 *
 * @param context 上下文
 * @return
 */
fun getVerName(context: Context): String {
    var verName = ""
    try {
        verName = context.packageManager.getPackageInfo(context.packageName, 0).versionName.toString()
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
    return verName
}



