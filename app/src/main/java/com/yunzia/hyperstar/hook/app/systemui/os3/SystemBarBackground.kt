package com.yunzia.hyperstar.hook.app.systemui.os3

import android.view.View
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookConstructor
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils


class SystemBarBackground : Hooker() {

    private val isTransparentNavigationBarBackground = XSPUtils.getBoolean("is_transparent_navigationBar_background",false)
    private val isTransparentStatusBarBackground = XSPUtils.getBoolean("is_transparent_statusBar_background",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!isTransparentNavigationBarBackground && !isTransparentStatusBarBackground) return

        findClass(
            "com.android.systemui.shared.statusbar.phone.BarTransitions",
            classLoader
        ).afterHookConstructor(
            Int::class.java,
            View::class.java
        ){
            val view = it.args[1] as View

            val name = view.context.resources.getResourceEntryName(view.id)

            if ((isTransparentNavigationBarBackground && name == "navigation_bar_view") ||
                (isTransparentStatusBarBackground && name == "status_bar_container")
            ) {
                this.getObjectField("mBarBackground")?.setIntField("mSemiTransparent", 0)
            }

        }

    }



}