package com.yunzia.hyperstar.hook.app.systemui.os1

import android.view.View
import com.yunzia.hyperstar.hook.core.BaseHook
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.finder.findClass
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
        findClass(
            "com.android.systemui.statusbar.phone.PhoneStatusBarTransitions"
        ).afterHookConstructor(
            View::class.java
        ) {
            val mView = it.args[0] as View
            val mBarBackground = this.getObjectField("mBarBackground")
            mBarBackground.setIntField( "mSemiTransparent", 0)

        }
    }

    private fun transparentNavigationBarBackground() {
        if (!isTransparentNavigationBarBackground) return
        val navigationBarTransitions = findClass(
            "com.android.systemui.navigationbar.NavigationBarTransitions"
        )

        val NavigationBarView = findClass(
            "com.android.systemui.navigation.NavigationBarView"
        )
        val LightBarTransitionsControllerFactory = findClass(
            "com.android.systemui.statusbar.phone.LightBarTransitionsController\$Factory"
        )
        val DisplayTracker = findClass("com.android.systemui.settings.DisplayTracker")


        navigationBarTransitions.afterHookConstructor(
            NavigationBarView,
            LightBarTransitionsControllerFactory,
            DisplayTracker
        ){
            val mView = this.getObjectField("mView")
            val mBarBackground = this.getObjectField("mBarBackground")
            //Object mSemiTransparent = XposedHelpers.getIntField(thisObject,"mSemiTransparent");
            mBarBackground.setIntField("mSemiTransparent", 0)

        }

    }

}