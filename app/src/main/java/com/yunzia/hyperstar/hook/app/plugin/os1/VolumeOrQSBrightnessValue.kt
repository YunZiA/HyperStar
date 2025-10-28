package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.util.plugin.ControlCenterUtils
import com.yunzia.hyperstar.hook.util.plugin.MiBlurCompat
import com.yunzia.hyperstar.utils.XSPUtils


class VolumeOrQSBrightnessValue : Hooker() {
    private val mainValueBlendColor = XSPUtils.getString("toggle_slider_value_color_main", "null")
    private val secondaryValueBlendColor = XSPUtils.getString("toggle_slider_value_color_secondary", "null")

    private val mainIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_main", "null")
    private val secondaryIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_secondary", "null")

    private val volumeShow = XSPUtils.getBoolean("qs_volume_top_value_show",false)
    private val brightnessShow = XSPUtils.getBoolean("qs_brightness_top_value_show",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!brightnessShow && !volumeShow) return
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
                val seekBar = item.findViewByIdNameAs<SeekBar>("slider")
                val max = seekBar.max
                val value = this.callMethodAs<Int>("getTargetValue")
                val topValue = item.findViewByIdNameAs<TextView>("top_text")

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
                val item = sliderHolder.getObjectFieldAs<View>("itemView")
                val seekBar = this.callMethodAs<SeekBar>("getSlider")!!
                val max = seekBar.max
                val value: Int = seekBar.progress
                val topValue = item.findViewByIdNameAs<TextView>("top_text")

                topValue.visibility = View.VISIBLE
                topValue.text = ((value * 100) / max).toString() + "%"
            }

        }

        val controlCenterUtils = ControlCenterUtils(classLoader)
        val miBlurCompat = MiBlurCompat(classLoader)
        var colorArray : IntArray? = null
        findClass(
            "miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",
            classLoader
        ).afterHookMethod(
            "updateBlendBlur"
        ) {
            val context = this.callMethodAs<Context>("getContext")
            val item = this.getObjectFieldAs<View>("itemView")
            val topValue = item.findViewByIdNameAs<TextView>("top_text")
            val icon = item.findViewByIdName("icon")

            if (!controlCenterUtils.getBackgroundBlurOpenedInDefaultTheme(context)){
                val color = item.resources.getColorBy("toggle_slider_top_text_color",plugin)
                topValue.setTextColor(color)
                miBlurCompat.setMiViewBlurModeCompat(topValue,0)
                miBlurCompat.clearMiBackgroundBlendColorCompat(topValue)
                return@afterHookMethod
            }
            //Color.WHITE Color.parseColor("#959595")
            topValue.setTextColor(Color.WHITE)
            miBlurCompat.setMiViewBlurModeCompat(topValue,3)
            if (colorArray == null){
                colorArray = item.resources.getIntArrayBy("toggle_slider_icon_blend_colors",plugin)
            }
            val iconColorArray  = colorArray as IntArray
            if (mainIconBlendColor != "null"){
                iconColorArray[0] = Color.parseColor(mainIconBlendColor)
            }
            if (secondaryIconBlendColor != "null"){
                iconColorArray[2] = Color.parseColor(secondaryIconBlendColor)
            }
            miBlurCompat.setMiBackgroundBlendColors(icon,iconColorArray,1f)
            val valueColorArray = colorArray as IntArray
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
