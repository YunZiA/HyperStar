package com.yunzia.hyperstar.hook.app.systemui

import android.view.View
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
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
        val SwitchingProvider = XposedHelpers.findClass(
            "com.android.systemui.dagger.DaggerReferenceGlobalRootComponent\$StatusBarFragmentComponentImpl\$SwitchingProvider",
            classLoader
        )
        val BarTransitions = XposedHelpers.findClass(
            "com.android.systemui.statusbar.phone.BarTransitions",
            classLoader
        )
        XposedHelpers.findAndHookMethod(SwitchingProvider,"get", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val thisObject = param.thisObject
                val id = XposedHelpers.getIntField(thisObject, "id")
                when(id){
                    4->{

                        val result = param.result
                        val mBarBackground = XposedHelpers.getObjectField(result, "mBarBackground")

                        XposedHelpers.setIntField(mBarBackground, "mSemiTransparent", 0)

                    }
                }


            }
        })

//        XposedHelpers.findAndHookConstructor(PhoneStatusBarTransitions, View::class.java,
//            object : XC_MethodHook() {
//                override fun afterHookedMethod(param: MethodHookParam) {
//                    super.afterHookedMethod(param)
//                    val thisObject = param.thisObject
//                    val mView = param.args[0] as View
//                    val mBarBackground = XposedHelpers.getObjectField(thisObject, "mBarBackground")
//
//                    XposedHelpers.setIntField(mBarBackground, "mSemiTransparent", 0)
//                }
//            })
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
        val SwitchingProvider9 = XposedHelpers.findClass(
            "com.android.systemui.dagger.DaggerReferenceGlobalRootComponent\$ReferenceSysUIComponentImpl\$SwitchingProvider\$9",
            classLoader
        )
        val DisplayTracker = XposedHelpers.findClass("com.android.systemui.settings.DisplayTracker", classLoader)


        XposedHelpers.findAndHookConstructor(NavigationBarTransitions, NavigationBarView, SwitchingProvider9, DisplayTracker,
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