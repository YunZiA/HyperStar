package com.yunzia.hyperstar.hook.app.plugin.os3

import android.view.ViewGroup
import android.widget.TextView
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookAllConstructors
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.util.startMarqueeOfFading
import com.yunzia.hyperstar.utils.XSPUtils

class QSListTileLabelMarquee : Hooker() {

    val labelMarquee = XSPUtils.getBoolean("list_tile_label_marquee",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!labelMarquee) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader
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