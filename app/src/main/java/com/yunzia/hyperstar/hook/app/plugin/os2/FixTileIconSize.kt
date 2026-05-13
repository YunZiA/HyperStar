package com.yunzia.hyperstar.hook.app.plugin.os2

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.getFloatField
import com.yunzia.hyperstar.prefs.XSPUtils

object FixTileIconSize : BasePluginHook() {

    val fix = XSPUtils.getBoolean("fix_list_tile_icon_scale",false)

    override fun init() {
        if (!fix) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            pluginClassLoader
        ).afterHookMethod(
            "getProperIconSize",
            Drawable::class.java
        ) { args, result ->
            val drawable = args[0] as Drawable
            if(drawable !is AnimatedVectorDrawable) return@afterHookMethod

            val customTileSize = thisObject.getFloatField("customTileSize")!!.toInt()
            if (drawable.intrinsicHeight < customTileSize){
                result.replace(customTileSize)
            }
        }
    }
}