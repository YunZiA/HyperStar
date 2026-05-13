package com.yunzia.hyperstar.hook.app

import android.content.ContentResolver
import android.provider.Settings
import com.yunzia.hyperstar.hook.core.base.BaseHook
import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.XposedCore
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.HookResult
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.libxposed.api.XposedInterface

//From "https://github.com/xfqwdsj/IAmNotADeveloper/blob/main/app/src/main/kotlin/xyz/xfqlittlefan/notdeveloper/xposed/Hook.kt"

object NotDeveloperHook : BaseHook() {
    private val tag = "NotDeveloper"

    val DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled"
    val ADB_ENABLED = "adb_enabled"
    val ADB_WIFI_ENABLED = "adb_wifi_enabled"

    override fun init() {
        val packageName = XposedCore.hookedPackageName
        if (packageName.startsWith("android")
            || packageName.startsWith("com.android")
            || packageName.startsWith("com.miui")
            || packageName.startsWith("miui")
        ) {
            return
        }

        logD("$tag: processing $packageName")

        val newApiCallback : XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit = { args, result -> hookResultToZero(args, result, DEVELOPMENT_SETTINGS_ENABLED, ADB_ENABLED, ADB_WIFI_ENABLED) }
        val oldApiCallback:  XposedInterface.Chain.(MutableList<Any?>, HookResult<Any?>) -> Unit = { args, result -> hookResultToZero(args, result, DEVELOPMENT_SETTINGS_ENABLED, ADB_ENABLED) }

        Settings.Global::class.java.apply {
            beforeHookMethod(
                "getInt",
                ContentResolver::class.java,
                String::class.java,
                Int::class.java,
                block = newApiCallback,
            )
            beforeHookMethod(
                "getInt",
                ContentResolver::class.java,
                String::class.java,
                block = newApiCallback,
            )

        }
        Settings.Secure::class.java.apply {

            beforeHookMethod(
                "getInt",
                ContentResolver::class.java,
                String::class.java,
                Int::class.java,
                block = oldApiCallback,
            )

            beforeHookMethod(
                "getInt",
                ContentResolver::class.java,
                String::class.java,
                block = oldApiCallback,
            )
        }

        if (XSPUtils.getBoolean(ADB_ENABLED, true)) {
            hideSystemProps()
        }
    }

    private fun hideSystemProps() {
        val clazz = findClass(
            "android.os.SystemProperties"
        )

        if (clazz == null) {
            logE("$tag: props cannot find SystemProperties class")
            return
        }

        val ffsReady = "sys.usb.ffs.ready"
        val usbState = "sys.usb.state"
        val usbConfig = "sys.usb.config"
        val rebootFunc = "persist.sys.usb.reboot.func"
        val svcadbd= "init.svc.adbd"
        val methodGet = "get"
        val methodGetProp = "getprop"
        val methodGetBoolean = "getBoolean"
        val methodGetInt = "getInt"
        val methodGetLong = "getLong"
        val overrideAdb = "mtp"
        val overridesvcadbd = "stopped"
        clazz.apply {
            listOf(methodGet, methodGetProp, methodGetBoolean, methodGetInt, methodGetLong).forEach {
                this.afterHookAllMethods(it) { args, result ->
                    val arg = args[0] as String
                    val memberName = executable.name
                    logD("$tag: processing $memberName from ${XposedCore.hookedPackageName} with arg $arg")

                    if (arg != ffsReady && memberName != it) {
                        logE("$tag:  props processed $memberName from ${XposedCore.hookedPackageName} receiving invalid arg $arg")
                        return@afterHookAllMethods
                    }

                    when (arg) {
                        ffsReady -> {
                            when (memberName) {
                                methodGet -> result.replace("0")
                                methodGetProp -> result.replace("0")
                                methodGetBoolean -> result.replace("false")
                                methodGetInt -> result.replace(0)
                                methodGetLong -> result.replace(0L)
                            }
                        }

                        usbState -> result.replace(overrideAdb)
                        usbConfig -> result.replace(overrideAdb)
                        rebootFunc -> result.replace(overrideAdb)
                        svcadbd -> result.replace(overridesvcadbd)

                    }

                    logD("$tag: hooked $memberName($arg): ${result.value}")

                }
            }
        }
    }

    private fun XposedInterface.Chain.hookResultToZero(
        args: MutableList<Any?>,
        result: HookResult<Any?>,
        vararg keys: String
    ) {
        val arg = args[1] as String
        val memberName = executable.name
        logD("$tag: processing $memberName from ${XposedCore.hookedPackageName} with arg $arg")

        keys.forEach { key ->
            if (XSPUtils.getBoolean(key, true) && arg == key) {
                result.replace(0)
                log("$tag: hooked $memberName($arg): ${result.value}")
                return
            }
        }

        logD("$tag: processed $memberName without changing result")
    }
}
