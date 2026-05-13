package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.Resources
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.setFloatField
import com.yunzia.hyperstar.prefs.XSPUtils


object QSToggleSliderRadius : BasePluginHook() {

    val progressRadius = XSPUtils.getFloat("qs_progress_radius",2f)

    override fun init() {
        if (!XSPUtils.getBoolean("is_change_qs_progress_radius",false)) return
        findClass(
            "miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",
            pluginClassLoader
        ).afterHookMethod("updateSize") { args, result ->
            val mContext = thisObject.callMethodAs<Resources>("getResources")
            thisObject.setFloatField("progressRadius",dpToPx(mContext,progressRadius))
        }
    }

    fun dpToPx(resources: Resources, dp: Float): Float {
        // 获取屏幕的密度
        val density = resources.displayMetrics.density

        // 转换 dp 到 px
        return dp * density
    }


}