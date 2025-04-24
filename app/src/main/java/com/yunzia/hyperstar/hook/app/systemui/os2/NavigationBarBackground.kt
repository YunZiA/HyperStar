package com.yunzia.hyperstar.hook.app.systemui.os2

import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookConstructor
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils


class NavigationBarBackground : Hooker() {

    private val isTransparentNavigationBarBackground = XSPUtils.getBoolean("is_transparent_navigationBar_background",false)
    private val isTransparentStatusBarBackground = XSPUtils.getBoolean("is_transparent_statusBar_background",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        transparentNavigationBarBackground()
        transparentStatusBarBackground()

    }

    private fun transparentStatusBarBackground() {
        if (!isTransparentStatusBarBackground) return

        val BarTransitions = findClass(
            "com.android.systemui.statusbar.phone.BarTransitions",
            classLoader
        )
        findClass(
            "com.android.systemui.dagger.DaggerReferenceGlobalRootComponent\$StatusBarFragmentComponentImpl\$SwitchingProvider",
            classLoader
        ).afterHookMethod("get"){
            val id = this.getIntField("id")
            when(id){
                4->{

                    val mBarBackground = it.result.getObjectField("mBarBackground")

                    mBarBackground.setIntField("mSemiTransparent", 0)

                }
            }

        }


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

        val NavigationBarView = findClass(
            "com.android.systemui.navigationbar.NavigationBarView",
            classLoader
        )
        val SwitchingProvider9 = findClass(
            "com.android.systemui.dagger.DaggerReferenceGlobalRootComponent\$ReferenceSysUIComponentImpl\$SwitchingProvider\$9",
            classLoader
        )
        val DisplayTracker = findClass("com.android.systemui.settings.DisplayTracker", classLoader)

        findClass(
            "com.android.systemui.navigationbar.NavigationBarTransitions",
            classLoader
        ).afterHookConstructor(
            NavigationBarView,
            SwitchingProvider9,
            DisplayTracker
        ){
            val mView = this.getObjectField( "mView")
            val mBarBackground = this.getObjectField("mBarBackground")
            //Object mSemiTransparent = XposedHelpers.getIntField(thisObject,"mSemiTransparent");
            mBarBackground.setIntField("mSemiTransparent", 0)

        }
    }

}