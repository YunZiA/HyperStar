package com.yunzia.hyperstar.utils

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import yunzia.utils.SystemProperties



val androidVersion by lazy { Build.VERSION.SDK_INT }

val systemVersionIncremental by lazy { SystemProperties.get("ro.mi.os.version.incremental").ifEmpty { SystemProperties.get("ro.system.build.version.incremental") } }

val deviceName by lazy { Build.DEVICE }
val marketName by lazy { SystemProperties.get("ro.product.marketname") }

val OSVersion by lazy { SystemProperties.getInt("ro.mi.os.version.code", 1) }

val isBetaOS by lazy { ReflectUtils.getStaticFieldValue(Build::class.java, "IS_DEV_VERSION", Boolean::class.java,false) }

//val isBetaOS = ReflectUtils.getStaticFieldValue(Build::class.java, "IS_DEV_VERSION", Boolean::class.java,false)

fun isFold():Boolean {
    val local = PreferencesUtil.getInt("isFold", 0)
    return when (local) {
        0 -> {
            val systemValue = SystemProperties.getInt("persist.sys.muiltdisplay_type", 0)
            val isFoldNow = systemValue == 2
            PreferencesUtil.putInt("isFold", if (isFoldNow) 2 else 1)
            isFoldNow
        }
        2 -> true
        else -> false
    }
}

fun isPad() : Boolean {

    val local = PreferencesUtil.getInt("isPad", 0)
    return when (local) {
        0 -> {
            val isPadNow = SystemProperties.get("ro.build.characteristics").contains("tablet")
            PreferencesUtil.putInt("isFold", if (isPadNow) 2 else 1)
            isPadNow
        }
        2 -> true
        else -> false
    }
}

fun isOS2():Boolean{
    if (isBetaOS) return false
    return OSVersion == 2
}
fun isOS2Settings():Boolean{
    return getSettingChannel() == 2
}

fun getHookChannel():Int{
    return XSPUtils.getInt("is_Hook_Channel",if (isBetaOS) 1 else OSVersion)
}


fun getSettingChannel():Int{
    return SPUtils.getInt("is_Hook_Channel",if (isBetaOS) 1 else OSVersion)
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



