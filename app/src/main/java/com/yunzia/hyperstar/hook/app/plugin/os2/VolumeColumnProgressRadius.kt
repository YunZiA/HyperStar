package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.util.AttributeSet
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.setFloatField
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
        ).afterHookConstructor(
            Context::class.java,
            AttributeSet::class.java,
            Int::class.java
        ) { args, result ->
            val mContext = thisObject.getObjectFieldAs<Context>("mContext")
            val res = mContext.resources
            val maxRadius = getDimensionPixelOffset(res,"miui_volume_bg_radius",plugin).toFloat()
            val radius = DensityUtil.dpToPx(res, volumeProgressRadius)
            thisObject.setFloatField("mProgressRadius",
                if (radius >= maxRadius){
                    maxRadius
                }else{
                    DensityUtil.dpToPx(res, volumeProgressRadius)
                }
            )
        }

    }
}