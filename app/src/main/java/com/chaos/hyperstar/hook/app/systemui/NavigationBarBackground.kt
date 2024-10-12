package com.chaos.hyperstar.hook.app.systemui

import android.view.View
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class NavigationBarBackground :BaseHooker() {

    private val isTransparentNavigationBarBackground = XSPUtils.getBoolean("is_transparent_navigationBar_background",false)
    private val isTransparentStatusBarBackground = XSPUtils.getBoolean("is_transparent_statusBar_background",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        transparentNavigationBarBackground()
        transparentStatusBarBackground()

    }

    private fun transparentStatusBarBackground() {
        if (!isTransparentStatusBarBackground) return
        val PhoneStatusBarTransitions = XposedHelpers.findClass(
            "com.android.systemui.statusbar.phone.PhoneStatusBarTransitions",
            classLoader
        )

        XposedHelpers.findAndHookConstructor(PhoneStatusBarTransitions, View::class.java,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val thisObject = param.thisObject
                    //val mView = param.args[0]
                    val mBarBackground = XposedHelpers.getObjectField(thisObject, "mBarBackground")
                    //Object mSemiTransparent = XposedHelpers.getIntField(thisObject,"mSemiTransparent");
                    XposedHelpers.setIntField(mBarBackground, "mSemiTransparent", 0)
                }
            })
    }

    private fun transparentNavigationBarBackground() {
        if (!isTransparentNavigationBarBackground) return
        val NavigationBarTransitions = XposedHelpers.findClass(
            "com.android.systemui.navigationbar.NavigationBarTransitions",
            classLoader
        )

        val NavigationBarView = XposedHelpers.findClass(
            "com.android.systemui.navigationbar.NavigationBarView",
            classLoader
        )
        val LightBarTransitionsControllerFactory = XposedHelpers.findClass(
            "com.android.systemui.statusbar.phone.LightBarTransitionsController\$Factory",
            classLoader
        )
        val DisplayTracker =
            XposedHelpers.findClass("com.android.systemui.settings.DisplayTracker", classLoader)


        XposedHelpers.findAndHookConstructor(NavigationBarTransitions, NavigationBarView, LightBarTransitionsControllerFactory, DisplayTracker,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    super.afterHookedMethod(param)
                    val thisObject = param.thisObject
                    val mView = XposedHelpers.getObjectField(thisObject, "mView")
                    val mBarBackground = XposedHelpers.getObjectField(thisObject, "mBarBackground")
                    //Object mSemiTransparent = XposedHelpers.getIntField(thisObject,"mSemiTransparent");
                    XposedHelpers.setIntField(mBarBackground, "mSemiTransparent", 0)
                }
            })
    }

}