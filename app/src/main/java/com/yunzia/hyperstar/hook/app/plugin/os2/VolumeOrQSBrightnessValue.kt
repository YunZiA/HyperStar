package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.Context
import android.graphics.Color
import android.util.SparseArray
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.base.BaseHookHelper.getColorBy
import com.yunzia.hyperstar.hook.base.BaseHookHelper.getIntArrayBy
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.findViewByIdNameAs
import com.yunzia.hyperstar.hook.base.percentageProgress
import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.afterHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.callStaticMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldOrNullAs
import com.yunzia.hyperstar.hook.util.plugin.ControlCenterUtils
import com.yunzia.hyperstar.hook.util.plugin.MiBlurCompat
import com.yunzia.hyperstar.hook.util.plugin.Util
import com.yunzia.hyperstar.prefs.XSPUtils
import com.yunzia.hyperstar.hook.util.android.findViewByIdName
import yunzia.utils.DensityUtil.Companion.dpToPx


object VolumeOrQSBrightnessValue : BasePluginHook() {
    val mainValueBlendColor = XSPUtils.getString("toggle_slider_value_color_main", "null")
    val secondaryValueBlendColor = XSPUtils.getString("toggle_slider_value_color_secondary", "null")

    val mainIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_main", "null")
    val secondaryIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_secondary", "null")

    val toggleSliderValueColor = XSPUtils.getString("toggle_slider_value_color", "null")

    private val volumeShow = XSPUtils.getBoolean("volume_top_value_show",false)
    private val brightnessShow = XSPUtils.getBoolean("qs_brightness_top_value_show",false)

    override fun init() {
        if (!brightnessShow && !volumeShow) return
        startMethodsHook()
    }

    private fun startMethodsHook() {

        val controlCenterUtils = ControlCenterUtils(pluginClassLoader)
        val miBlurCompat = MiBlurCompat(pluginClassLoader)

        if (volumeShow){
            val VolumeUtils = findClass("miui.systemui.util.VolumeUtils",pluginClassLoader)
            findClass(
                "miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",
                pluginClassLoader
            ).afterHookMethod(
                "updateIconProgress",
                Boolean::class .java
            ) { args, result ->
                val sliderHolder = thisObject.callMethod("getHolder") ?: return@afterHookMethod
                val item = sliderHolder.getObjectFieldAs<View>("itemView")
                val topValue = item.findViewByIdNameAs<TextView>("top_text")
                val sliderMaxValue = thisObject.getObjectFieldAs<Int>("sliderMaxValue")
                val value = thisObject.callMethodAs<Int>("getTargetValue")!!
                val level = VolumeUtils.callStaticMethodAs<Int>("progressToLevel",sliderMaxValue,value)

                topValue.visibility = View.VISIBLE
                topValue.text = convertToPercentageProgress(level, sliderMaxValue / 1000)
            }

            val volumePanelViewController = findClass("com.android.systemui.miui.volume.VolumePanelViewController",pluginClassLoader)
            val util = Util(pluginClassLoader)

            volumePanelViewController.apply {
                //侧边音量条进度值&&All场景二级音量条进度值ui启用
                afterHookMethod(
                    "updateSuperVolumeView",
                    "com.android.systemui.miui.volume.VolumePanelViewController\$VolumeColumn"
                ) { args, result ->
                    val mExpanded = thisObject.getObjectFieldAs<Boolean>("mExpanded")
                    val volumeColumn = args[0]
                    val superVolume = volumeColumn.getObjectFieldAs<TextView>("superVolume")
                    val mSuperVolumeBg = thisObject.getObjectFieldAs<View>("mSuperVolumeBg")

                    util.setVisOrGone(mSuperVolumeBg,!mExpanded)//侧边音量条进度值UI显示

                    util.setVisOrGone(superVolume,mExpanded)//All场景二级音量条进度值UI显示

                }
                //All场景二级音量条百分比进度值计算显示
                afterHookMethod(
                    "updateVolumeColumnSliderH",
                    "com.android.systemui.miui.volume.VolumePanelViewController\$VolumeColumn",
                    Boolean::class.java,
                    Int::class.java,
                    Boolean::class.java,
                    Int::class.java
                ) { args, result ->
                    val volumeColumn = args[0]
                    val mState = thisObject.getObjectField("mState")?:return@afterHookMethod
                    val states = mState.getObjectFieldAs<SparseArray<*>>("states")
                    val stream = volumeColumn.getObjectFieldAs<Int>("stream")
                    val streamState = states.get(stream)
                    val mActiveStream = thisObject.getObjectFieldAs<Int>("mActiveStream")

                    if (streamState != null){

                        val maxLevel = streamState.getObjectFieldAs<Int>("levelMax")
                        val level = streamState.getObjectFieldAs<Int>("level")
                        val value = convertToPercentageProgress(level,maxLevel)

                        volumeColumn.getObjectFieldAs<TextView>("superVolume").text = value

                        if (stream == mActiveStream){
                            thisObject.getObjectFieldAs<TextView>("mSuperVolume").text = value

                        }

                    }

                }

                //为二级进度值开启高级材质
                afterHookMethod(
                    "updateColumnIconBlendColor",
                    "com.android.systemui.miui.volume.VolumePanelViewController\$VolumeColumn"
                ) { args, result ->
                    val volumeColumn = args[0]
                    val mContext = thisObject.getObjectFieldAs<Context>("mContext")
                    val mExpanded = thisObject.getObjectFieldAs<Boolean>("mExpanded")
                    val mNeedShowDialog = thisObject.getObjectFieldAs<Boolean>("mNeedShowDialog")
                    val colorArrayName = if (!mExpanded){
                        "miui_expanded_button_and_seekbar_icon_blend_colors_collapsed"
                    }else if (mNeedShowDialog){
                        "miui_seekbar_icon_blend_colors_expanded"
                    }else{
                        "miui_seekbar_icon_blend_colors_expanded_cc"
                    }
                    val colorArray = mContext.resources.getIntArrayBy(colorArrayName,plugin)
                    volumeColumn.getObjectFieldAs<TextView>("superVolume").apply {
                        setTextColor(Color.WHITE)
                        util.setMiViewBlurAndBlendColor(
                            this,
                            mExpanded,mContext,
                            3,
                            colorArray,false
                        )
                    }
                }
            }
        }

        if (brightnessShow){
            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",
                pluginClassLoader
            ).apply {
                afterHookMethod(
                    "createViewHolder",
                    ViewGroup::class.java,Int::class.java
                ) { args, result ->
                    val viewHolder = result.value
                    if (viewHolder != null){
                        val root = viewHolder.getObjectFieldAs<ViewGroup>("itemView")
                        val topText = root.findViewByIdName("top_text") as TextView
                        topText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        val mLayoutParams = (topText.layoutParams as FrameLayout.LayoutParams).apply {
                            width = dpToPx(root.resources,50f).toInt()
                        }
                        topText.layoutParams = mLayoutParams
                        log("toggle_slider_item_view ${topText.layoutParams}")
                    }

                }
                afterHookMethod(
                    "updateIconProgress"
                ) { args, result ->
                    val sliderHolder = thisObject.callMethod("getSliderHolder") ?: return@afterHookMethod
                    val seekBar = thisObject.callMethodAs<SeekBar>("getSlider") ?: return@afterHookMethod
                    val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
                    val topValue = itemView.findViewByIdNameAs<TextView>("top_text")
                    val str = seekBar.percentageProgress()
                    topValue.visibility = View.VISIBLE
                    topValue.text = str
                }
            }

            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessPanelSliderController",
                pluginClassLoader
            ).apply {
                afterHookAllConstructors { args, result ->
                    val brightnessPanel = args[0] as FrameLayout

                    val topText = brightnessPanel.findViewByIdName("top_text") as TextView
                    topText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    val mLayoutParams = (topText.layoutParams as FrameLayout.LayoutParams).apply {
                        width = dpToPx(brightnessPanel.resources,50f).toInt()
                    }
                    topText.layoutParams = mLayoutParams
                    log("toggle_slider_item_view ${topText.layoutParams}")
                }
                afterHookMethod(
                    "updateIconProgress"
                ) { args, result ->
                    val vToggleSliderInner = thisObject.callMethodAs<ViewGroup>("getVToggleSliderInner") ?:return@afterHookMethod
                    val seekBar = thisObject.callMethodAs<SeekBar>("getVSlider") ?: return@afterHookMethod
                    val topValue = vToggleSliderInner.findViewByIdNameAs<TextView>("top_text")

                    topValue.visibility = View.VISIBLE
                    topValue.text = seekBar.percentageProgress()


                }
                afterHookMethod(
                    "updateBlendBlur"
                ) { args, result ->
                    val context = thisObject.callMethodAs<Context>("getContext")
                    val vToggleSliderInner = thisObject.callMethodAs<ViewGroup>("getVToggleSliderInner")
                    val topValue = vToggleSliderInner.findViewByIdNameAs<TextView>("top_text")

                    if (!controlCenterUtils.getBackgroundBlurOpenedInDefaultTheme(context)){
                        val color = vToggleSliderInner.resources.getColorBy("toggle_slider_top_text_color",plugin)
                        topValue.setTextColor(color)
                        miBlurCompat.setMiViewBlurModeCompat(topValue,0)
                        miBlurCompat.clearMiBackgroundBlendColorCompat(topValue)
                        return@afterHookMethod
                    }

                    topValue.setTextColor(Color.WHITE)

                    //Color.WHITE Color.parseColor("#959595")
                    miBlurCompat.setMiViewBlurModeCompat(topValue,3)
                    val colorArray = vToggleSliderInner.resources.getIntArrayBy("toggle_slider_icon_blend_colors",plugin)
                    miBlurCompat.setMiBackgroundBlendColors(topValue,colorArray,1f)
                }
                afterHookMethod(
                    "updateLargeSize"
                ) { args, result ->
                    val item = thisObject.callMethodAs<ViewGroup>("getVToggleSliderInner")
                    val topValue = item.findViewByIdNameAs<TextView>("top_text")
                    topValue.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15f)
                }
                afterHookMethod(
                    "updateSmallSize"
                ) { args, result ->
                    val item = thisObject.callMethodAs<ViewGroup>("getVToggleSliderInner")
                    val topValue = item.findViewByIdNameAs<TextView>("top_text")
                    topValue.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13f)
                }
            }

            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessPanelAnimator",
                pluginClassLoader
            ).afterHookMethod(
                "frameCallback"
            ) { args, result ->
                val sliderController = thisObject.getObjectField("sliderController")?:return@afterHookMethod
                val item = sliderController.callMethodAs<ViewGroup>("getVToggleSliderInner")?:return@afterHookMethod
                val topValue = item.findViewByIdNameAs<TextView>("top_text")
                val icon = sliderController.callMethodAs<View>("getVIcon")?:return@afterHookMethod
                val sizeSliderX =  thisObject.getObjectFieldAs<Float>("sizeSliderX")?:return@afterHookMethod
                val sizeBgX = thisObject.getObjectFieldAs<Float>("sizeBgX") ?:return@afterHookMethod
                log("${topValue.left} || ${topValue.right} **$sizeSliderX")

                val left = (dpToPx(topValue.resources, 50f).toInt() - icon.layoutParams.width) / 2

                topValue.left = icon.left - left
                topValue.right = icon.right + left
                topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f + 2f * sizeBgX)

            }

        }

        if (toggleSliderValueColor != null && toggleSliderValueColor != "null"){
            colorReplaceByValue("toggle_slider_top_text_color", plugin, toggleSliderValueColor.toColorInt())
        }

        var colorArray : IntArray? = null

        findClass(
            "miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",
            pluginClassLoader
        ).apply {
            afterHookMethod("updateSize") { args, result ->
                val item = thisObject.getObjectFieldAs<View>("itemView")
                val topValue = item.findViewByIdName("top_text") as TextView
                topValue.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13f)
            }
            afterHookMethod(
                "updateBlendBlur"
            ) { args, result ->
                val context = thisObject.callMethodAs<Context>("getContext")
                val item = thisObject.getObjectFieldAs<View>("itemView")
                val topValue = item.findViewByIdName("top_text") as TextView
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
                val inMirror = thisObject.getObjectFieldAs<Boolean>("inMirror")
                if (inMirror){
                    miBlurCompat.chooseBackgroundBlurContainerCompat(
                        topValue,
                        with(this) {
                            getObjectFieldOrNullAs<View>("mirrorBlendBackground")
                                ?: getObjectFieldOrNullAs<View>("mirrorBlurProvider")
                        }
                    )
                }else{
                    miBlurCompat.chooseBackgroundBlurContainerCompat(topValue,null)
                }
                miBlurCompat.setMiViewBlurModeCompat(topValue,3)
                if (colorArray == null){
                    colorArray = item.resources.getIntArrayBy("toggle_slider_icon_blend_colors",plugin)
                }

                val iconColorArray: IntArray = colorArray
                if (mainIconBlendColor != "null"){
                    iconColorArray[0] = mainIconBlendColor!!.toColorInt()
                }
                if (secondaryIconBlendColor != "null"){
                    iconColorArray[2] = secondaryIconBlendColor!!.toColorInt()
                }

                miBlurCompat.setMiBackgroundBlendColors(icon,iconColorArray,1f)
                val valueColorArray: IntArray = colorArray
                if (mainValueBlendColor != "null"){
                    valueColorArray[0] = mainValueBlendColor!!.toColorInt()
                }
                if (secondaryValueBlendColor != "null"){
                    valueColorArray[2] = secondaryValueBlendColor!!.toColorInt()
                }
                miBlurCompat.setMiBackgroundBlendColors(topValue,valueColorArray,1f)
            }
        }

    }

    private fun convertToPercentageProgress(
        progress:Int ,
        max:Int
    ) = "${(progress * 100 / max)}%"

}
