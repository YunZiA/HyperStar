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


class VolumeOrQSBrightnessValue : Hooker() {
    val mainValueBlendColor = XSPUtils.getString("toggle_slider_value_color_main", "null")
    val secondaryValueBlendColor = XSPUtils.getString("toggle_slider_value_color_secondary", "null")

    val mainIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_main", "null")
    val secondaryIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_secondary", "null")


    private val volumeShow = XSPUtils.getBoolean("qs_volume_top_value_show",false)
    private val brightnessShow = XSPUtils.getBoolean("qs_brightness_top_value_show",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()

    }

    private fun startMethodsHook() {
        if (volumeShow){
            findClass(
                "miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",
                classLoader
            ).afterHookMethod(
                "updateIconProgress",
                Boolean::class.java
            ){
                val sliderHolder = this.callMethod("getHolder") ?: return@afterHookMethod
                val item = sliderHolder.getObjectField("itemView") as View

                val seekBar = item.findViewByIdName("slider") as SeekBar
                val max = seekBar.max
                val value: Int = this.callMethod("getTargetValue") as Int
                val topValue = item.findViewByIdName("top_text") as TextView

                topValue.visibility = View.VISIBLE
                topValue.text = ((value * 100) / max).toString() + "%"

            }
        }

        if (brightnessShow){
            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",
                classLoader
            ).afterHookMethod(
                "updateIconProgress"
            ){
                val sliderHolder = this.callMethod("getSliderHolder") ?: return@afterHookMethod
                val item = sliderHolder.getObjectField("itemView") as View

                val seekBar = this.callMethod("getSlider") as SeekBar
                val max = seekBar.max
                val value: Int = seekBar.progress
                val topValue = item.findViewByIdName("top_text") as TextView

                topValue.visibility = View.VISIBLE
                topValue.text = ((value * 100) / max).toString() + "%"

            }

        }

        if (!brightnessShow && !volumeShow){
            return
        }

        val controlCenterUtils = ControlCenterUtils(classLoader)
        val miBlurCompat = MiBlurCompat(classLoader)
        var colorArray : IntArray? = null
        findClass("miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",
            classLoader
        ).afterHookMethod(
            "updateBlendBlur"
        ) {

            val context = this.callMethod("getContext") as Context

            val item = this.getObjectField("itemView") as View
            val topValue = item.findViewByIdName("top_text") as TextView
            val icon = item.findViewByIdName("icon")

            if (!controlCenterUtils.getBackgroundBlurOpenedInDefaultTheme(context)){
                val colorId = context.resources.getIdentifier("toggle_slider_top_text_color", "color", "miui.systemui.plugin")
                val color = item.resources.getColor(colorId)

                topValue.setTextColor(color)

                miBlurCompat.setMiViewBlurModeCompat(topValue,0)
                miBlurCompat.clearMiBackgroundBlendColorCompat(topValue)

                return@afterHookMethod
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

    }


}
