package com.yunzia.hyperstar.hook.app.systemui.os2

import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.getIntField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.setIntField
import com.yunzia.hyperstar.prefs.XSPUtils


object SystemBarBackground : BaseHook() {

    private val isTransparentNavigationBarBackground = XSPUtils.getBoolean("is_transparent_navigationBar_background",false)
    private val isTransparentStatusBarBackground = XSPUtils.getBoolean("is_transparent_statusBar_background",false)

    override fun init() {
        

        transparentNavigationBarBackground()
        transparentStatusBarBackground()

    }

    private fun transparentStatusBarBackground() {
        if (!isTransparentStatusBarBackground) return

//        val BarTransitions = findClass(
//            "com.android.systemui.statusbar.phone.BarTransitions",
//            classLoader
//        )
        findClass(
            "com.android.systemui.dagger.DaggerReferenceGlobalRootComponent\$StatusBarFragmentComponentImpl\$SwitchingProvider"
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
            "com.android.systemui.navigationbar.NavigationBarView"
        )
        val SwitchingProvider9 = findClass(
            "com.android.systemui.dagger.DaggerReferenceGlobalRootComponent\$ReferenceSysUIComponentImpl\$SwitchingProvider\$9"
        )
        val DisplayTracker = findClass("com.android.systemui.settings.DisplayTracker")

        findClass(
            "com.android.systemui.navigationbar.NavigationBarTransitions"
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