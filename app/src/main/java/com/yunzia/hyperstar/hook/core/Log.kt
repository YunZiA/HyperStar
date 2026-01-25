package com.yunzia.hyperstar.hook.core

import com.yunzia.hyperstar.prefs.XSPUtils.getInt
import io.github.kyuubiran.ezxhelper.xposed.api.XposedApi

object Log {

    val level: Int = getInt("log_level", 0)

    val debug: Boolean = level >= 1

    val error: Boolean = level >= 2

    fun log(msg: String?) {
        XposedApi.log("{ $msg }")
    }

    fun log(tag: String?, msg: String?) {
        XposedApi.log("[I][$tag]: $msg")
    }

    fun logI(msg: String?) {
        XposedApi.log(" [I]: $msg")
    }

    fun logI(tagOpkg: String?, msg: String?) {
        XposedApi.log(" [I][$tagOpkg]: $msg")
    }

    fun logI(tag: String?, pkg: String?, msg: String?) {
        XposedApi.log("[I][$pkg][$tag]: $msg")
    }

    fun logW(msg: String?) {
        XposedApi.log(" [W]: $msg")
    }

    fun logW(tag: String?, pkg: String?, msg: String?) {
        XposedApi.log("[W][$pkg][$tag]: $msg")
    }

    fun logW(tag: String?, pkg: String?, log: Throwable?) {
        XposedApi.log("[W][$pkg][$tag]: $log")
    }

    fun logW(tag: String?, pkg: String?, msg: String?, exp: Exception?) {
        XposedApi.log("[W][$pkg][$tag]: $msg, by: $exp")
    }

    fun logW(tag: String?, pkg: String?, msg: String?, log: Throwable?) {
        XposedApi.log("[W][$pkg][$tag]: $msg, by: $log")
    }

    fun logW(tag: String?, msg: String?) {
        XposedApi.log("[W][$tag]: $msg")
    }

    fun logW(tag: String?, log: Throwable?) {
        XposedApi.log("[W][$tag]: $log")
    }

    fun logW(tag: String?, msg: String?, exp: Exception?) {
        XposedApi.log("[W][$tag]: $msg, by: $exp")
    }

    fun logE(tag: String?, msg: String?) {
        if (!error) return
        XposedApi.log("[E][$tag]: $msg")
    }

    fun logE(msg: String?) {
        if (!error) return
        XposedApi.log("[E]: $msg")
    }

    fun logE(tag: String?, log: Throwable?) {
        if (!error) return
        XposedApi.log("[E][$tag]: $log")
    }

    fun logE(tag: String?, pkg: String?, msg: String?) {
        if (!error) return
        XposedApi.log("[E][$pkg][$tag]: $msg")
    }

    fun logE(tag: String?, pkg: String?, log: Throwable?) {
        if (!error) return
        XposedApi.log("[E][$pkg][$tag]: $log")
    }

    fun logE(tag: String?, pkg: String?, exp: Exception?) {
        if (!error) return
        XposedApi.log("[E][$pkg][$tag]: $exp")
    }

    fun logE(tag: String?, pkg: String?, msg: String?, log: Throwable?) {
        if (!error) return
        XposedApi.log("[E][$pkg][$tag]: $msg, by: $log")
    }

    fun logE(tag: String?, pkg: String?, msg: String?, exp: Exception?) {
        if (!error) return
        XposedApi.log("[E]{$pkg][$tag]: $msg, by: $exp")
    }

    fun logD(msg: String?) {
        if (!debug) return
        XposedApi.log("[D]: $msg")
    }

    fun logD(tag: String?, msg: String?) {
        if (!debug) return
        XposedApi.log("[D][$tag]: $msg")
    }

    fun logD(tag: String?, pkg: String?, msg: String?) {
        if (!debug) return
        XposedApi.log("[D][$pkg][$tag]: $msg")
    }
}