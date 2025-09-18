package com.yunzia.hyperstar.hook.app.plugin.os3

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx

class QSListTileRadius : Hooker() {

    val isQSListTileRadius = XSPUtils.getBoolean("is_qs_list_tile_radius",false)
    val qsListTileRadius = XSPUtils.getFloat("qs_list_tile_radius",24f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!isQSListTileRadius) return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
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
            ) {
                val drawable = it.args[0] as Drawable
                if (drawable is GradientDrawable){
                    val pluginContext = this.getObjectFieldAs<Context>( "pluginContext")
                    val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                    if (drawable.cornerRadius != mRadius){
                        drawable.cornerRadius = mRadius
                        it.args[0] = drawable
                    }
                }

            }
            beforeHookMethod(
                "setEnabledBg",
                Drawable::class.java
            ){
                val drawable = it.args?.get(0) as Drawable
                if (drawable is GradientDrawable){
                    val pluginContext = this.getObjectFieldAs<Context>("pluginContext")
                    val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                    if (drawable.cornerRadius != mRadius){
                        drawable.cornerRadius = mRadius
                        it.args[0] = drawable
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