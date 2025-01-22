package com.yunzia.hyperstar.hook.base

import android.content.res.Resources
import android.graphics.Color
import com.yunzia.hyperstar.hook.util.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodHook.MethodHookParam
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

abstract class HookerHelper {

    fun setColorField(context: Any?, fieldName: String, color: String?) {
        XposedHelpers.setIntField(context, fieldName, Color.parseColor(color))
    }

    fun getColor(res: Resources, name: String, defPackage: String): Int {
        val id = res.getIdentifier(name, "color", defPackage)
        return res.getColor(id, res.newTheme())
    }

    fun getColor(res: Resources, name: String, defPackage: String, defColor: String): Int {
        try {
            val id = res.getIdentifier(name, "color", defPackage)
            return res.getColor(id, res.newTheme())
        } catch (e: Resources.NotFoundException) {
            starLog.logE("color $name is not found!")
            return Color.parseColor(defColor)
        }
    }

    fun getDimension(res: Resources, name: String, defPackage: String): Float {
        val id = res.getIdentifier(name, "dimen", defPackage)
        return res.getDimension(id)
    }

    fun getDimensionPixelOffset(res: Resources, name: String, defPackage: String): Int {
        val id = res.getIdentifier(name, "dimen", defPackage)
        return res.getDimensionPixelOffset(id)
    }

    fun getDimensionPixelSize(res: Resources, name: String, defPackage: String): Int {
        val id = res.getIdentifier(name, "dimen", defPackage)
        return res.getDimensionPixelSize(id)
    }

    fun findClass(className: String, classLoader: ClassLoader?): Class<*>? {
        val cc = XposedHelpers.findClassIfExists(className, classLoader)
        if (cc == null) {
            starLog.logE("$className is not find")
        }
        return cc
    }

    fun hookAllMethods(
        classLoader: ClassLoader?,
        className: String,
        methodName: String,
        methodHook: MethodHook
    ) {
        val hookClass = XposedHelpers.findClassIfExists(className, classLoader)
        if (hookClass == null) {
            starLog.logE("$className is not find")
            return
        }
        XposedBridge.hookAllMethods(hookClass, methodName, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                methodHook.before(param)
            }

            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                methodHook.after(param)
            }
        })
    }

    fun hookAllMethods(
        hookClass: Class<*>?,
        methodName: String,
        methodHook: MethodHook
    ) {
        if (hookClass == null) {
            starLog.logE("$methodName's class is null")
            return
        }
        XposedBridge.hookAllMethods(hookClass, methodName, object : XC_MethodHook() {
            @Throws(Throwable::class)
            override fun beforeHookedMethod(param: MethodHookParam) {
                super.beforeHookedMethod(param)
                methodHook.before(param)
            }

            @Throws(Throwable::class)
            override fun afterHookedMethod(param: MethodHookParam) {
                super.afterHookedMethod(param)
                methodHook.after(param)
            }
        })
    }


    interface MethodHook {
        fun before(param: MethodHookParam)
        fun after(param: MethodHookParam)
    }

}