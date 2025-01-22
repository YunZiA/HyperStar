package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.plugin.ControlCenterUtils
import com.yunzia.hyperstar.hook.util.plugin.MiBlurCompat
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


class QSVolumeOrBrightnessValue : Hooker() {
    val mainValueBlendColor = XSPUtils.getString("toggle_slider_value_color_main", "null")
    val secondaryValueBlendColor = XSPUtils.getString("toggle_slider_value_color_secondary", "null")

    val mainIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_main", "null")
    val secondaryIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_secondary", "null")


    private val volumeShow = XSPUtils.getBoolean("qs_volume_top_value_show",false)
    val volumeShowStyle = XSPUtils.getInt("qs_volume_top_value",0)
    private val brightnessShow = XSPUtils.getBoolean("qs_brightness_top_value_show",false)
    val brightnessShowStyle = XSPUtils.getInt("qs_brightness_top_value",0)
    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()


    }

    private fun startMethodsHook() {
        if (volumeShow){
            val VolumeSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)
            XposedHelpers.findAndHookMethod(VolumeSliderController,"updateIconProgress",
                Boolean::class.java,object : XC_MethodHook(){
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)
                        val thisObj = param?.thisObject
                        val str: String
                        val sliderHolder = XposedHelpers.callMethod(thisObj,"getHolder") ?: return
                        val item = XposedHelpers.getObjectField(sliderHolder,"itemView") as View

                        val seekBar = item.findViewByIdName("slider") as SeekBar
                        val max = seekBar.max
                        val value: Int = XposedHelpers.callMethod(thisObj,"getTargetValue") as Int
                        val topValue = item.findViewByIdName("top_text") as TextView
                        str = if (volumeShowStyle == 0) ((value * 100) / max).toString() + "%" else value.toString()

                        topValue.visibility = View.VISIBLE
                        topValue.text = str

                    }
                })
        }

        if (brightnessShow){
            val BrightnessSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)

            XposedHelpers.findAndHookMethod(BrightnessSliderController,"updateIconProgress",object  : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val str: String
                    val sliderHolder = XposedHelpers.callMethod(thisObj,"getSliderHolder")
                    if (sliderHolder == null){
                        return
                    }
                    val item = XposedHelpers.getObjectField(sliderHolder,"itemView") as View

                    val seekBar = XposedHelpers.callMethod(thisObj,"getSlider") as SeekBar
                    val max = seekBar.max
                    val value: Int = seekBar.progress
                    val topValue = item.findViewByIdName("top_text") as TextView
                    str = if (brightnessShowStyle == 0) ((value * 100) / max).toString() + "%" else value.toString()

                    topValue.visibility = View.VISIBLE
                    topValue.text = str


                }
            })
        }

        if (!brightnessShow && !volumeShow){
            return
        }

        val ToggleSliderViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",classLoader)
        val controlCenterUtils = ControlCenterUtils(classLoader)
        val miBlurCompat = MiBlurCompat(classLoader)
        var colorArray : IntArray? = null

        XposedHelpers.findAndHookMethod(ToggleSliderViewHolder,"updateBlendBlur",object : XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject

                val context = XposedHelpers.callMethod(thisObj,"getContext") as Context

                val item = XposedHelpers.getObjectField(thisObj,"itemView") as View
                val topValue = item.findViewByIdName("top_text") as TextView
                val icon = item.findViewByIdName("icon")

                if (!controlCenterUtils.getBackgroundBlurOpenedInDefaultTheme(context)){
                    val colorId = context.resources.getIdentifier("toggle_slider_top_text_color", "color", "miui.systemui.plugin")
                    val color = item.resources.getColor(colorId)

                    topValue.setTextColor(color)

                    miBlurCompat.setMiViewBlurModeCompat(topValue,0)
                    miBlurCompat.clearMiBackgroundBlendColorCompat(topValue)

                    return
                }
                //Color.WHITE Color.parseColor("#959595")
                topValue.setTextColor(Color.WHITE)
                miBlurCompat.setMiViewBlurModeCompat(topValue,3)
                if (colorArray == null){
                    val array = context.resources.getIdentifier("toggle_slider_icon_blend_colors", "array", "miui.systemui.plugin")

                    colorArray = item.resources.getIntArray(array)
                }

                val iconColorArray : IntArray = colorArray as IntArray

                if (mainIconBlendColor != "null"){
                    iconColorArray[0] = Color.parseColor(mainIconBlendColor)

                }
                if (secondaryIconBlendColor != "null"){
                    iconColorArray[2] = Color.parseColor(secondaryIconBlendColor)

                }
                miBlurCompat.setMiBackgroundBlendColors(icon,iconColorArray,1f)

                val valueColorArray : IntArray = colorArray as IntArray

                if (mainValueBlendColor != "null"){
                    valueColorArray[0] = Color.parseColor(mainValueBlendColor)

                }
                if (secondaryValueBlendColor != "null"){
                    valueColorArray[2] = Color.parseColor(secondaryValueBlendColor)

                }
                miBlurCompat.setMiBackgroundBlendColors(topValue,valueColorArray,1f)

            }
        })

    }


}
