package com.yunzia.hyperstar.hook.app

import android.content.ContentResolver
import android.provider.Settings
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.Log
import com.yunzia.hyperstar.hook.core.Log.log
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.hookMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.xposed.EzXposed
import io.github.kyuubiran.ezxhelper.xposed.common.BeforeHookParam
import io.github.kyuubiran.ezxhelper.xposed.dsl.HookFactory

//From "https://github.com/xfqwdsj/IAmNotADeveloper/blob/main/app/src/main/kotlin/xyz/xfqlittlefan/notdeveloper/xposed/Hook.kt"

object NotDeveloperHook : BaseHook() {
    private val tag = "NotDeveloper"

    val DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled"
    val ADB_ENABLED = "adb_enabled"
    val ADB_WIFI_ENABLED = "adb_wifi_enabled"

    override fun init() {
        val packageName = EzXposed.hookedPackageName
        if (packageName.startsWith("android")
            || packageName.startsWith("com.android")
            || packageName.startsWith("com.miui")
            || packageName.startsWith("miui")
        ) {
            return
        }

        logD("$tag: processing $packageName")

        val newApiCallback :Any?.(BeforeHookParam) -> Unit = { hookResultToZero(it, DEVELOPMENT_SETTINGS_ENABLED, ADB_ENABLED, ADB_WIFI_ENABLED) }
        val oldApiCallback: Any?.(BeforeHookParam) -> Unit = { hookResultToZero(it, DEVELOPMENT_SETTINGS_ENABLED, ADB_ENABLED) }

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
                this.afterHookAllMethods(it) {param ->
                    val arg = param.args[0] as String
                    logD("$tag: processing ${param.member.name} from ${EzXposed.hookedPackageName} with arg $arg")

                    if (arg != ffsReady && param.member.name != methodGet) {
                        logE("$tag:  props processed ${param.member.name} from ${EzXposed.hookedPackageName} receiving invalid arg $arg")
                        return@afterHookAllMethods
                    }

                    when (arg) {
                        ffsReady -> {
                            when (param.member.name) {
                                methodGet -> param.result = "0"
                                methodGetProp -> param.result = "0"
                                methodGetBoolean -> param.result = false
                                methodGetInt -> param.result = 0
                                methodGetLong -> param.result = 0L
                            }
                        }

                        usbState -> param.result = overrideAdb
                        usbConfig -> param.result = overrideAdb
                        rebootFunc -> param.result = overrideAdb
                        svcadbd -> param.result = overridesvcadbd

                    }

                    logD("$tag: hooked ${param.member.name}($arg): ${param.result}")

                }
            }
        }
    }

    private fun Any?.hookResultToZero(
        param: BeforeHookParam,
        vararg keys: String
    ) {
        val arg = param.args[1] as String
        logD("$tag: processing ${param.member.name} from ${EzXposed.hookedPackageName} with arg $arg")

        keys.forEach { key ->
            if (XSPUtils.getBoolean(key, true) && arg == key) {
                param.result = 0
                log("$tag: hooked ${param.member.name}($arg): ${param.result}")
                return
            }
        }

        logD("$tag: processed ${param.member.name} without changing result")
    }
}
