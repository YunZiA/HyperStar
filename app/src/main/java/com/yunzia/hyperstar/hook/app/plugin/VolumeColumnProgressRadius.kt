package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.Outline
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import yunzia.utils.DensityUtil
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers

class VolumeColumnProgressRadius :BaseHooker() {

    val isChangeVolumeProgressRadius = XSPUtils.getBoolean("is_change_volume_progress_radius",false)

    val volumeProgressRadius = XSPUtils.getFloat("volume_progress_radius",2f)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
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