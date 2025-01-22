package xyz.xfqlittlefan.notdeveloper.xposed

import android.content.ContentResolver
import android.provider.Settings
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.util.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

//From "https://github.com/xfqwdsj/IAmNotADeveloper/blob/main/app/src/main/kotlin/xyz/xfqlittlefan/notdeveloper/xposed/Hook.kt"
class NotDeveloperHooker : InitHooker() {
    private val tag = "NotDeveloper"

    val DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled"
    val ADB_ENABLED = "adb_enabled"
    val ADB_WIFI_ENABLED = "adb_wifi_enabled"


    override fun initHook(lpparam: LoadPackageParam) {
        if (lpparam.packageName.startsWith("android")
            || lpparam.packageName.startsWith("com.android")
            || lpparam.packageName.startsWith("com.miui")
            || lpparam.packageName.startsWith("miui")
            ) {
            return
        }

        starLog.logD("$tag: processing " + lpparam.packageName)


        val newApiCallback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                hookResultToZero(
                    lpparam,
                    param,
                    DEVELOPMENT_SETTINGS_ENABLED,
                    ADB_ENABLED,
                    ADB_WIFI_ENABLED
                )
            }
        }

        val oldApiCallback = object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {
                hookResultToZero(lpparam, param, DEVELOPMENT_SETTINGS_ENABLED, ADB_ENABLED)
            }
        }

        XposedHelpers.findAndHookMethod(
            Settings.Global::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.java,
            newApiCallback,
        )

        XposedHelpers.findAndHookMethod(
            Settings.Global::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            newApiCallback,
        )

        XposedHelpers.findAndHookMethod(
            Settings.Secure::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            Int::class.java,
            oldApiCallback,
        )

        XposedHelpers.findAndHookMethod(
            Settings.Secure::class.java,
            "getInt",
            ContentResolver::class.java,
            String::class.java,
            oldApiCallback,
        )

        if (XSPUtils.getBoolean(ADB_ENABLED, true)) {
            hideSystemProps(lpparam)
        }
    }

    private fun hideSystemProps(lpparam: LoadPackageParam) {
        val clazz = XposedHelpers.findClassIfExists(
            "android.os.SystemProperties", lpparam.classLoader
        )

        if (clazz == null) {
            starLog.logE("$tag: props cannot find SystemProperties class")
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

        listOf(methodGet, methodGetProp, methodGetBoolean, methodGetInt, methodGetLong).forEach {
            XposedBridge.hookAllMethods(
                clazz, it,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {
                        val arg = param.args[0] as String
                        starLog.logD("$tag: processing ${param.method.name} from ${lpparam.packageName} with arg $arg")

                        if (arg != ffsReady && param.method.name != methodGet) {
                            starLog.logE("$tag:  props processed ${param.method.name} from ${lpparam.packageName} receiving invalid arg $arg")
                            return
                        }

                        when (arg) {
                            ffsReady -> {
                                when (param.method.name) {
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

                        starLog.logD("$tag: hooked ${param.method.name}($arg): ${param.result}")
                    }
                }
            )
        }
    }

    private fun hookResultToZero(
        lpparam: LoadPackageParam,
        param: MethodHookParam,
        vararg keys: String
    ) {
        val arg = param.args[1] as String
        starLog.logD("$tag: processing ${param.method.name} from ${lpparam.packageName} with arg $arg")

        keys.forEach { key ->
            if (XSPUtils.getBoolean(key, true) && arg == key) {
                param.result = 0
                starLog.log("$tag: hooked ${param.method.name}($arg): ${param.result}")
                return
            }
        }

        starLog.logD("$tag: processed ${param.method.name} without changing result")
    }
}
