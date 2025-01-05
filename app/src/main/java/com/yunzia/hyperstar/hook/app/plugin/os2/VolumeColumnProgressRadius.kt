package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.util.AttributeSet
import yunzia.utils.DensityUtil
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class VolumeColumnProgressRadius : Hooker() {

    val isChangeVolumeProgressRadius = XSPUtils.getBoolean("is_change_volume_progress_radius",false)

    val volumeProgressRadius = XSPUtils.getFloat("volume_progress_radius",2f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!isChangeVolumeProgressRadius) return
        startMethodsHook()
    }

    private fun startMethodsHook() {
        val MiuiVolumeSeekBarProgressView = XposedHelpers.findClass("com.android.systemui.miui.volume.MiuiVolumeSeekBarProgressView",classLoader)


        XposedHelpers.findAndHookConstructor(MiuiVolumeSeekBarProgressView,Context::class.java,AttributeSet::class.java,Int::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.getObjectField(thisObj,"mContext") as Context
                val res = mContext.resources

                val miuiVolumeBgRadius = res.getIdentifier("miui_volume_bg_radius","dimen",plugin)
                val maxRadius = res.getDimensionPixelOffset(miuiVolumeBgRadius).toFloat()

                val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                if (radius >= maxRadius){
                    XposedHelpers.setFloatField(thisObj,"mProgressRadius",maxRadius)

                }else{
                    XposedHelpers.setFloatField(thisObj,"mProgressRadius",DensityUtil.dpToPx(res, volumeProgressRadius))

                }


            }
        })

    }
}