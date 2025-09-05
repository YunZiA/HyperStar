package com.yunzia.hyperstar.hook.app.plugin.os1

import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils

class FixTileIconSize : Hooker() {

    val fix = XSPUtils.getBoolean("fix_list_tile_icon_scale",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!fix) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        ).apply {
            afterHookMethod(
                "getProperIconSize",
                Drawable::class.java
            ){
                val drawable = it.args[0] as Drawable
                val isCustomTile = this.getBooleanField("isCustomTile")
                if (isCustomTile) return@afterHookMethod
                if(drawable !is AnimatedVectorDrawable) return@afterHookMethod
                val customTileSize = this.getFloatField("customTileSize").toInt()
                if (drawable.intrinsicHeight < customTileSize){
                    it.result = customTileSize
                }
            }
        }
    }
}