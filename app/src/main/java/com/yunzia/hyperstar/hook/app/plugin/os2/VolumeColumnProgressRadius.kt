package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.util.AttributeSet
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil

class VolumeColumnProgressRadius : Hooker() {

    val isChangeVolumeProgressRadius = XSPUtils.getBoolean("is_change_volume_progress_radius",false)

    val volumeProgressRadius = XSPUtils.getFloat("volume_progress_radius",2f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!isChangeVolumeProgressRadius) return
        startMethodsHook()
    }

    private fun startMethodsHook() {
        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBarProgressView",
            classLoader
        ).afterHookConstructor(
            Context::class.java,
            AttributeSet::class.java,
            Int::class.java
        ){
            val mContext = this.getObjectFieldAs<Context>("mContext")
            val res = mContext.resources
            val maxRadius = getDimensionPixelOffset(res,"miui_volume_bg_radius",plugin).toFloat()
            val radius = DensityUtil.dpToPx(res, volumeProgressRadius)
            this.setFloatField("mProgressRadius",
                if (radius >= maxRadius){
                    maxRadius
                }else{
                    DensityUtil.dpToPx(res, volumeProgressRadius)
                }
            )
        }

    }
}