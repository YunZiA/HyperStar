package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx

object QSListTileRadius : BasePluginHook() {

    val isQSListTileRadius = XSPUtils.getBoolean("is_qs_list_tile_radius",false)
    val qsListTileRadius = XSPUtils.getFloat("qs_list_tile_radius",24f)

    override fun init() {
        if (!isQSListTileRadius) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            pluginClassLoader
        ).apply {
            replaceHookMethod(
                "getCornerRadius"
            ) {
                val pluginContext = getObjectFieldAs<Context>( "pluginContext")
                return@replaceHookMethod dpToPx(
                    pluginContext.resources,
                    qsListTileRadius
                )
            }
            beforeHookMethod(
                "setDisabledBg",
                Drawable::class.java
            ) { args, result ->
                val drawable = args[0] as Drawable
                if (drawable is GradientDrawable){
                    val pluginContext = thisObject.getObjectFieldAs<Context>( "pluginContext")
                    val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                    if (drawable.cornerRadius != mRadius){
                        drawable.cornerRadius = mRadius
                        args[0] = drawable
                    }
                }

            }
            beforeHookMethod(
                "setEnabledBg",
                Drawable::class .java
            ) { args, result ->
                val drawable = args[0] as Drawable
                if (drawable is GradientDrawable){
                    val pluginContext = thisObject.getObjectFieldAs<Context>("pluginContext")
                    val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                    if (drawable.cornerRadius != mRadius){
                        drawable.cornerRadius = mRadius
                        args[0] = drawable
                    }
                }

            }
        }
    }

    private fun setRadius(
        context: Context,
        res : Resources,
        name:String
    ) {

        val id: Int = res.getIdentifier(name, "drawable", plugin)
        val drawable: Drawable = context.theme.getDrawable(id)
        if (drawable is GradientDrawable) {
            drawable.cornerRadius = dpToPx(res,qsListTileRadius)
        }
    }

}