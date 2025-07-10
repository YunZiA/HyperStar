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
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookAllConstructors
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.hook.util.plugin.ControlCenterUtils
import com.yunzia.hyperstar.hook.util.plugin.MiBlurCompat
import com.yunzia.hyperstar.hook.util.plugin.Util
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx


class VolumeOrQSBrightnessValue : Hooker() {
    val mainValueBlendColor = XSPUtils.getString("toggle_slider_value_color_main", "null")
    val secondaryValueBlendColor = XSPUtils.getString("toggle_slider_value_color_secondary", "null")

    val mainIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_main", "null")
    val secondaryIconBlendColor = XSPUtils.getString("toggle_slider_icon_color_secondary", "null")

    val toggleSliderValueColor = XSPUtils.getString("toggle_slider_value_color", "null")

    private val volumeShow = XSPUtils.getBoolean("volume_top_value_show",false)
    private val brightnessShow = XSPUtils.getBoolean("qs_brightness_top_value_show",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!brightnessShow && !volumeShow) return
        startMethodsHook()

    }

    private fun startMethodsHook() {

        val controlCenterUtils = ControlCenterUtils(classLoader)
        val miBlurCompat = MiBlurCompat(classLoader)

        if (volumeShow){
            val VolumeUtils = findClass("miui.systemui.util.VolumeUtils",classLoader)
            findClass(
                "miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",
                classLoader
            ).afterHookMethod(
                "updateIconProgress",
                Boolean::class.java
            ){
                val sliderHolder = this.callMethod("getHolder") ?: return@afterHookMethod
                val item = sliderHolder.getObjectFieldAs<View>("itemView")
                val topValue = item.findViewByIdNameAs<TextView>("top_text")
                val sliderMaxValue = this.getObjectFieldAs<Int>("sliderMaxValue")
                val value = this.callMethodAs<Int>("getTargetValue")!!
                val level = VolumeUtils.callStaticMethodAs<Int>("progressToLevel",sliderMaxValue,value)

                topValue.visibility = View.VISIBLE
                topValue.text = convertToPercentageProgress(level, sliderMaxValue / 1000)
            }

            val volumePanelViewController = findClass("com.android.systemui.miui.volume.VolumePanelViewController",classLoader)
            val util = Util(classLoader)

            volumePanelViewController.apply {
                //侧边音量条进度值&&All场景二级音量条进度值ui启用
                afterHookMethod(
                    "updateSuperVolumeView",
                    "com.android.systemui.miui.volume.VolumePanelViewController\$VolumeColumn"
                ){
                    val mExpanded = this.getObjectFieldAs<Boolean>("mExpanded")
                    val volumeColumn = it.args[0]
                    val superVolume = volumeColumn.getObjectFieldAs<TextView>("superVolume")
                    val mSuperVolumeBg = this.getObjectFieldAs<View>("mSuperVolumeBg")

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
                ){

                    val volumeColumn = it.args[0]
                    val mState = this.getObjectField("mState")?:return@afterHookMethod
                    val states = mState.getObjectFieldAs<SparseArray<*>>("states")
                    val stream = volumeColumn.getObjectFieldAs<Int>("stream")
                    val streamState = states.get(volumeColumn.getObjectFieldAs<Int>("stream"))
                    val mActiveStream = this.getObjectFieldAs<Int>("mActiveStream")

                    if (streamState != null){

                        val maxLevel = streamState.getObjectFieldAs<Int>("levelMax")
                        val level = streamState.getObjectFieldAs<Int>("level")

                        volumeColumn.getObjectFieldAs<TextView>("superVolume").text = convertToPercentageProgress(level,maxLevel)

                        if (stream == mActiveStream){
                            this.getObjectFieldAs<TextView>("mSuperVolume").text = convertToPercentageProgress(level,maxLevel)

                        }

                    }

                }

                //为二级进度值开启高级材质
                afterHookMethod(
                    "updateColumnIconBlendColor",
                    "com.android.systemui.miui.volume.VolumePanelViewController\$VolumeColumn"
                ){
                    val volumeColumn = it.args[0]
                    val mContext = this.getObjectFieldAs<Context>("mContext")
                    val mExpanded = this.getObjectFieldAs<Boolean>("mExpanded")
                    val mNeedShowDialog = this.getObjectFieldAs<Boolean>("mNeedShowDialog")
                    val colorArrayName = if (!mExpanded){
                        "miui_expanded_button_and_seekbar_icon_blend_colors_collapsed"
                    }else if (mNeedShowDialog){
                        "miui_seekbar_icon_blend_colors_expanded"
                    }else{
                        "miui_seekbar_icon_blend_colors_expanded_cc"
                    }
                    volumeColumn.getObjectFieldAs<TextView>("superVolume").setTextColor(Color.WHITE)
                    val colorArray = mContext.resources.getIntArrayBy(colorArrayName,plugin)
                    util.setMiViewBlurAndBlendColor(volumeColumn.getObjectFieldAs<TextView>("superVolume"),mExpanded,mContext,3,colorArray,false)

                }
            }


        }

        if (brightnessShow){
            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",
                classLoader
            ).apply {
                afterHookMethod(
                    "createViewHolder",
                    ViewGroup::class.java,Int::class.java
                ) {
                    val viewHolder = it.result
                    if (viewHolder!=null){
                        val root = viewHolder.getObjectFieldAs<ViewGroup>("itemView")
                        val topText = root.findViewByIdName("top_text") as TextView
                        topText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                        val mLayoutParams = (topText.layoutParams as FrameLayout.LayoutParams).apply {
                            width = dpToPx(root.resources,50f).toInt()
                        }
                        topText.layoutParams = mLayoutParams
                        starLog.log("toggle_slider_item_view ${topText.layoutParams}")
                    }

                }
                afterHookMethod(
                    "updateIconProgress"
                ){
                    val sliderHolder = this.callMethod("getSliderHolder") ?: return@afterHookMethod
                    val seekBar = this.callMethodAs<SeekBar>("getSlider") ?: return@afterHookMethod
                    val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
                    val topValue = itemView.findViewByIdNameAs<TextView>("top_text")

                    val str = seekBar.percentageProgress()

                    topValue.visibility = View.VISIBLE
                    topValue.text = str

                }
            }

            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessPanelSliderController",
                classLoader
            ).apply {
                afterHookAllConstructors {
                    val brightnessPanel = it.args[0] as FrameLayout

                    val topText = brightnessPanel.findViewByIdName("top_text") as TextView
                    topText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    val mLayoutParams = (topText.layoutParams as FrameLayout.LayoutParams).apply {
                        width = dpToPx(brightnessPanel.resources,50f).toInt()
                    }
                    topText.layoutParams = mLayoutParams
                    starLog.log("toggle_slider_item_view ${topText.layoutParams}")
                }
                afterHookMethod(
                    "updateIconProgress"
                ){
                    val vToggleSliderInner = this.callMethodAs<ViewGroup>("getVToggleSliderInner") ?:return@afterHookMethod
                    val seekBar = this.callMethodAs<SeekBar>("getVSlider") ?: return@afterHookMethod
                    val topValue = vToggleSliderInner.findViewByIdNameAs<TextView>("top_text")

                    topValue.visibility = View.VISIBLE
                    topValue.text = seekBar.percentageProgress()


                }
                afterHookMethod(
                    "updateBlendBlur"
                ){

                    val context = this.callMethodAs<Context>("getContext")
                    val vToggleSliderInner = this.callMethodAs<ViewGroup>("getVToggleSliderInner")
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
                ){
                    val item = this.callMethodAs<ViewGroup>("getVToggleSliderInner")
                    val topValue = item.findViewByIdNameAs<TextView>("top_text")
                    topValue.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP,15f)
                }
                afterHookMethod(
                    "updateSmallSize"
                ){
                    val item = this.callMethodAs<ViewGroup>("getVToggleSliderInner")
                    val topValue = item.findViewByIdNameAs<TextView>("top_text")
                    topValue.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13f)
                }
            }

            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessPanelAnimator",
                classLoader
            ).afterHookMethod(
                "frameCallback"
            ) {
                val sliderController = this.getObjectField("sliderController")?:return@afterHookMethod
                val item = sliderController.callMethodAs<ViewGroup>("getVToggleSliderInner")?:return@afterHookMethod
                val topValue = item.findViewByIdNameAs<TextView>("top_text")
                val icon = sliderController.callMethodAs<View>("getVIcon")?:return@afterHookMethod
                val sizeSliderX =  this.getObjectFieldAs<Float>("sizeSliderX")?:return@afterHookMethod
                val sizeBgX = this.getObjectFieldAs<Float>("sizeBgX") ?:return@afterHookMethod
                starLog.log("${topValue.left} || ${topValue.right} **$sizeSliderX")

                val left = (dpToPx(topValue.resources, 50f).toInt() - icon.layoutParams.width) / 2

                topValue.left = icon.left - left
                topValue.right = icon.right + left
                topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13f + 2f * sizeBgX)

            }

        }

        if (toggleSliderValueColor != null && toggleSliderValueColor != "null"){

            replaceColor("toggle_slider_top_text_color", plugin, toggleSliderValueColor.toColorInt())
        }


        var colorArray : IntArray? = null

        findClass(
            "miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",
            classLoader
        ).apply {
            afterHookMethod("updateSize") {
                val item = this.getObjectFieldAs<View>("itemView")
                val topValue = item.findViewByIdName("top_text") as TextView
                topValue.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                topValue.setTextSize(TypedValue.COMPLEX_UNIT_DIP,13f)
            }
            afterHookMethod(
                "updateBlendBlur"
            ){
                val context = this.callMethodAs<Context>("getContext")
                val item = this.getObjectFieldAs<View>("itemView")
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
                val inMirror = this.getObjectFieldAs<Boolean>("inMirror")
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
