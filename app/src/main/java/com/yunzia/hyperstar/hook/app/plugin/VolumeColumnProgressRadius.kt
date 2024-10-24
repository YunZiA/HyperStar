package com.yunzia.hyperstar.hook.app.plugin

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider
import yunzia.utils.DensityUtil
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
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


        XposedHelpers.findAndHookMethod(MiuiVolumeSeekBarProgressView,"setRoundRect", Int::class.java,object :XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any?{
                val mView = param?.thisObject as View
                val height = param.args?.get(0) as Int
                val res = mView.resources

                val miuiVolumeBgRadius = res.getIdentifier("miui_volume_bg_radius","dimen",plugin)
                val maxRadius = res.getDimensionPixelOffset(miuiVolumeBgRadius).toFloat()

                val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                mView.outlineProvider = object : ViewOutlineProvider(){
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null) return
                        if (radius >= maxRadius){
                            outline?.setRoundRect(0,view.height-height, view.width,view.height,maxRadius)

                        }else{
                            outline?.setRoundRect(0,view.height-height, view.width,view.height,radius)

                        }

                    }

                }

                mView.clipToOutline = true

                return null

            }

        })

        XposedHelpers.findAndHookMethod(MiuiVolumeSeekBarProgressView,"setRoundRectTimerProgressHorizontal", Int::class.java,object :XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any?{
                val mView = param?.thisObject as View
                val width = param.args?.get(0) as Int
                val res = mView.resources

                val miuiVolumeBgRadius = res.getIdentifier("miui_volume_bg_radius","dimen",plugin)
                val maxRadius = res.getDimensionPixelOffset(miuiVolumeBgRadius).toFloat()

                val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                mView.outlineProvider = object : ViewOutlineProvider(){
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null) return
                        if (radius >= maxRadius){
                            outline?.setRoundRect(0,0, width,view.height,maxRadius)

                        }else{
                            outline?.setRoundRect(0,0, width,view.height,radius)

                        }

                    }

                }

                mView.clipToOutline = true

                return null

            }

        })

        XposedHelpers.findAndHookMethod(MiuiVolumeSeekBarProgressView,"setRoundRectTimerProgressVertical", Int::class.java,object :XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any?{
                val mView = param?.thisObject as View
                val height = param.args?.get(0) as Int
                val res = mView.resources

                val miuiVolumeBgRadius = res.getIdentifier("miui_volume_bg_radius","dimen",plugin)
                val maxRadius = res.getDimensionPixelOffset(miuiVolumeBgRadius).toFloat()

                val radius = DensityUtil.dpToPx(res, volumeProgressRadius)

                mView.outlineProvider = object : ViewOutlineProvider(){
                    override fun getOutline(view: View?, outline: Outline?) {
                        if (view == null) return
                        if (radius >= maxRadius){
                            outline?.setRoundRect(0,view.height-height, view.width,view.height,maxRadius)

                        }else{
                            outline?.setRoundRect(0,view.height-height, view.width,view.height,radius)

                        }

                    }

                }

                mView.clipToOutline = true

                return null

            }

        })
    }
}