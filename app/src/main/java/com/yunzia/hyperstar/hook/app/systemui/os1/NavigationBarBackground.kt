package com.yunzia.hyperstar.hook.app.systemui.os1

import android.view.View
import com.yunzia.hyperstar.hook.base.Hooker
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
        findClass(
            "com.android.systemui.statusbar.phone.PhoneStatusBarTransitions",
            classLoader
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
            "com.android.systemui.navigationbar.NavigationBarTransitions",
            classLoader
        )

        val NavigationBarView = findClass(
            "com.android.systemui.navigationbar.NavigationBarView",
            classLoader
        )
        val LightBarTransitionsControllerFactory = findClass(
            "com.android.systemui.statusbar.phone.LightBarTransitionsController\$Factory",
            classLoader
        )
        val DisplayTracker = findClass("com.android.systemui.settings.DisplayTracker", classLoader)


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