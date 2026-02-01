package com.yunzia.hyperstar.hook.app.plugin.os3

import android.widget.TextView
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.util.startMarqueeOfFading
import com.yunzia.hyperstar.prefs.XSPUtils

object QSListTileLabelMarquee : BasePluginHook() {

    val labelMarquee = XSPUtils.getBoolean("list_tile_label_marquee",false)

    override fun init() {
        if (!labelMarquee) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemView", pluginClassLoader
        ).afterHookMethod(
            "init",
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView"
        ) {
            val binding = this.callMethod("getBinding")
            val label = binding.getObjectFieldAs<TextView>("tileLabel")
            label.startMarqueeOfFading(25)
        }
    }
}