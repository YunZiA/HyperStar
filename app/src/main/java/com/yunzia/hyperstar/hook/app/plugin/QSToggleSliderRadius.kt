package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.Resources
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils


class QSToggleSliderRadius : Hooker() {

    val progressRadius = XSPUtils.getFloat("qs_progress_radius",2f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!XSPUtils.getBoolean("is_change_qs_progress_radius",false)) return

        startMethodsHook()
    }

    private fun startMethodsHook() {
        findClass(
            "miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",
            classLoader
        ).afterHookMethod("updateSize"){
            val mContext = this.callMethodAs<Resources>("getResources")!!
            this.setFloatField("progressRadius",dpToPx(mContext,progressRadius))
        }

    }

    fun dpToPx(resources: Resources, dp: Float): Float {
        // 获取屏幕的密度
        val density = resources.displayMetrics.density

        // 转换 dp 到 px
        return dp * density
    }


}