package com.yunzia.hyperstar.hook.app.plugin.os1

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import yunzia.utils.DensityUtil

object VolumeColumnProgressRadius : BasePluginHook() {

    val isChangeVolumeProgressRadius = XSPUtils.getBoolean("is_change_volume_progress_radius",false)

    val volumeProgressRadius = XSPUtils.getFloat("volume_progress_radius",2f)

    override fun init() {
        if (!isChangeVolumeProgressRadius) return
        startMethodsHook()
    }

    private fun startMethodsHook() {
        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBarProgressView",
            pluginClassLoader
        ).apply {
            replaceHookMethod(
                "setRoundRect",
                Int::class .java
            ) { args ->
                (thisObject as View).apply {
                    val height = args[0] as Int
                    val res = resources
                    val maxRadius = getDimensionPixelOffset(res,"miui_volume_bg_radius",plugin).toFloat()
                    val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                    clipToOutline = true
                    outlineProvider = object : ViewOutlineProvider(){
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null) return
                            if (radius >= maxRadius){
                                outline?.setRoundRect(0,view.height-height, view.width,view.height,maxRadius)
                            }else{
                                outline?.setRoundRect(0,view.height-height, view.width,view.height,radius)
                            }
                        }
                    }

                }
                return@replaceHookMethod null
            }
            replaceHookMethod(
                "setRoundRectTimerProgressHorizontal",
                Int::class .java
            ) { args ->
                (thisObject as View).apply {
                    val width = args[0] as Int
                    val res = resources
                    val maxRadius = getDimensionPixelOffset(res,"miui_volume_bg_radius",plugin).toFloat()
                    val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                    clipToOutline = true
                    outlineProvider = object : ViewOutlineProvider(){
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null) return
                            if (radius >= maxRadius){
                                outline?.setRoundRect(0,0, width,view.height,maxRadius)
                            } else{
                                outline?.setRoundRect(0,0, width,view.height,radius)

                            }
                        }
                    }
                }
                return@replaceHookMethod null
            }
            replaceHookMethod(
                "setRoundRectTimerProgressVertical",
                Int::class .java
            ) { args ->
                (thisObject as View).apply {
                    val height = args[0] as Int
                    val res = resources
                    val maxRadius = getDimensionPixelOffset(res,"miui_volume_bg_radius",plugin).toFloat()
                    val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                    clipToOutline = true
                    outlineProvider = object : ViewOutlineProvider(){
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null) return
                            if (radius >= maxRadius){
                                outline?.setRoundRect(0,view.height-height, view.width,view.height,maxRadius)
                            }else{
                                outline?.setRoundRect(0,view.height-height, view.width,view.height,radius)
                            }
                        }
                    }
                }
                return@replaceHookMethod null
            }
        }
    }
}