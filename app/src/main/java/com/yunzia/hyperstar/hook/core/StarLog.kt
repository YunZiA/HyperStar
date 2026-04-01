package com.yunzia.hyperstar.hook.core

import android.util.Log
import com.yunzia.hyperstar.BuildConfig
import com.yunzia.hyperstar.prefs.XSPUtils.getInt

object StarLog {
    
    const val TAG = "HyperStar"
    val level: Int = getInt("log_level", 0)

    val debug: Boolean = level >= 1

    val error: Boolean = level >= 2

    @JvmStatic
    fun log(msg: String) {
        XposedCore.base.log(Log.INFO, TAG, msg)
    }

    @JvmStatic
    fun log(tag: String?, msg: String) {
        XposedCore.base.log(Log.INFO, TAG, "[$tag]$msg")
    }

    @JvmStatic
    fun logI(tag: String?, msg: String) {
        XposedCore.base.log(Log.INFO, TAG, "[$tag]$msg")
    }

    @JvmStatic
    fun logW(msg: String) {
        XposedCore.base.log(Log.WARN, TAG, msg)
    }
    @JvmStatic
    fun logW(tag: String?, msg: String) {
        XposedCore.base.log(Log.WARN, TAG, "[$tag]$msg")
    }

    @JvmStatic
    fun logW(tag: String?, msg: String, tr: Throwable?) {
        XposedCore.base.log(Log.WARN, TAG, "[$tag]$msg", tr)
    }

    @JvmStatic
    fun logE(msg: String) {
//        if (!error) return
        XposedCore.base.log(Log.ERROR, TAG, msg)
    }

    @JvmStatic
    fun logE(tag: String?, msg: String?) {
//        if (!error) return
        XposedCore.base.log(Log.ERROR, TAG, "[$tag]$msg")
    }

    @JvmStatic
    fun logE(tag: String?, msg: String, exception: Exception) {
//        if (!error) return
        XposedCore.base.log(Log.ERROR, TAG, "[$tag]$msg\n$exception")
    }

    @JvmStatic
    fun logE(tag: String?, msg: String, tr: Throwable?) {
//        if (!error) return
        XposedCore.base.log(Log.ERROR, TAG, "[$tag]$msg", tr)
    }

    @JvmStatic
    fun logD(msg: String) {
        if (!debug) return
        XposedCore.base.log(Log.DEBUG, TAG, msg)
    }

    @JvmStatic
    fun logD(tag: String?, msg: String) {
        if (!debug) return
        XposedCore.base.log(Log.DEBUG, TAG, "[$tag]$msg")
    }

    @JvmStatic
    fun logD(tag: String?, msg: String, tr: Throwable?) {
        if (!debug) return
        XposedCore.base.log(Log.DEBUG, TAG, "[$tag]$msg", tr)
    }
}