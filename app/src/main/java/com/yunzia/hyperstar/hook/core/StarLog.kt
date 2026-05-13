package com.yunzia.hyperstar.hook.core

import android.util.Log
import com.yunzia.hyperstar.prefs.XSPUtils.getInt

object StarLog {

    const val TAG = "HyperStar"
    val level: Int = getInt("log_level", 0)
    val debug: Boolean = level >= 1
    val error: Boolean = level >= 2

    @JvmStatic
    fun log(msg: String) {
        logInternal(Log.INFO, msg)
    }

    @JvmStatic
    fun log(tag: String?, msg: String) {
        logInternal(Log.INFO, tagged(tag, msg))
    }

    @JvmStatic
    fun logI(tag: String?, msg: String) {
        logInternal(Log.INFO, tagged(tag, msg))
    }

    @JvmStatic
    fun logW(msg: String) {
        logInternal(Log.WARN, msg, enabled = debug)
    }

    @JvmStatic
    fun logW(tag: String?, msg: String) {
        logInternal(Log.WARN, tagged(tag, msg), enabled = debug)
    }

    @JvmStatic
    fun logW(tag: String?, msg: String, tr: Throwable?) {
        logInternal(Log.WARN, tagged(tag, msg), tr, enabled = debug)
    }

    @JvmStatic
    fun logE(msg: String) {
        logInternal(Log.ERROR, msg, enabled = error)
    }

    @JvmStatic
    fun logE(tag: String?, msg: String?) {
        logInternal(Log.ERROR, tagged(tag, msg), enabled = error)
    }

    @JvmStatic
    fun logE(tag: String?, msg: String, exception: Exception) {
        logInternal(Log.ERROR, "${tagged(tag, msg)}\n$exception", enabled = error)
    }

    @JvmStatic
    fun logE(tag: String?, msg: String, tr: Throwable?) {
        logInternal(Log.ERROR, tagged(tag, msg), tr, enabled = error)
    }

    @JvmStatic
    fun logD(msg: String) {
        logInternal(Log.DEBUG, msg, enabled = debug)
    }

    @JvmStatic
    fun logD(tag: String?, msg: String) {
        logInternal(Log.DEBUG, tagged(tag, msg), enabled = debug)
    }

    @JvmStatic
    fun logD(tag: String?, msg: String, tr: Throwable?) {
        logInternal(Log.DEBUG, tagged(tag, msg), tr, enabled = debug)
    }

    private fun tagged(tag: String?, msg: Any?) = "[$tag]$msg"

    private fun logInternal(
        priority: Int,
        msg: String,
        tr: Throwable? = null,
        enabled: Boolean = true
    ) {
        if (!enabled) return
        if (tr == null) {
            XposedCore.base.log(priority, TAG, msg)
        } else {
            XposedCore.base.log(priority, TAG, msg, tr)
        }
    }
}
