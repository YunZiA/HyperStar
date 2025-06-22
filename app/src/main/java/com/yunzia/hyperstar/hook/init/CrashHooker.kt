package com.yunzia.hyperstar.hook.init

import android.app.ApplicationErrorReport.CrashInfo
import android.content.Context
import android.provider.Settings
import com.yunzia.hyperstar.hook.base.InitHooker
import com.yunzia.hyperstar.hook.tool.starLog.logE
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge.hookMethod
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.XposedHelpers.findClassIfExists
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Method


class CrashHooker : InitHooker() {

    override fun initHook(lpparam: XC_LoadPackage.LoadPackageParam) {

        lpparam.classLoader?.let { init(it) }

    }

    @Throws(Exception::class)
    fun init(classLoader: ClassLoader) {
        val appError = findClassIfExists("com.android.server.am.AppErrors", classLoader)
            ?: throw ClassNotFoundException("No such 'com.android.server.am.AppErrors' classLoader: $classLoader")
        var hookError: Method? = null
        for (error in appError.declaredMethods) {
            if ("handleAppCrashInActivityController" == error.name) if (error.returnType == Boolean::class.javaPrimitiveType) {
                hookError = error
                break
            }
        }
        if (hookError == null) {
            throw NoSuchMethodException("No such Method: handleAppCrashInActivityController, ClassLoader: $classLoader")
        }

        hookMethod(hookError, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val mContext = XposedHelpers.getObjectField(param?.thisObject, "mContext") as Context
                val args = param?.args
                val proc = args?.get(0)
                val crashInfo = args?.get(1) as CrashInfo
                val shortMsg = args[2] as String
                val longMsg = args[3] as String
                val stackTrace = args[4] as String
                val timeMillis = args[5] as Long
                val callingPid = args[6] as Int
                val callingUid = args[7] as Int
                logE(
                    "CrashHook",
                    ("context: " + mContext + " pkg: " + mContext.packageName + " proc: " + proc + " crash: " + crashInfo + " short: " + shortMsg
                            + " long: " + longMsg + " stack: " + stackTrace + " time: " + timeMillis + " pid: " + callingPid + " uid: " + callingUid)
                )
                if (mContext.packageName == "com.android.systemui"){
                    Settings.System.putString(mContext.contentResolver, "hyperstar_crash_record_data", mContext.packageName);

                }

            }

        }
        )
        /*
        findAndHookMethod("com.android.server.wm.BackgroundActivityStartController", "checkCrossUidActivitySwitchFromBelow", "com.android.server.wm.ActivityRecord", int.class, "com.android.server.wm.BackgroundActivityStartController$BlockActivityStart", new MethodHook(){
            @Override
            protected void before(MethodHookParam param) throws Throwable {
                param.setResult(param.args[2]);
            }
        });*/
    }

}