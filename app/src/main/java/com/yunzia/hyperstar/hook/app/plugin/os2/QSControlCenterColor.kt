package com.yunzia.hyperstar.hook.app.plugin.os2

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.hook.base.BaseHookHelper.setColorField
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.findViewByIdNameAs
import com.yunzia.hyperstar.hook.core.Log
import com.yunzia.hyperstar.hook.core.Log.logD
import com.yunzia.hyperstar.hook.core.Log.logE
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByIdName
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.colorReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.drawableReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.intArrayReplaceByIdName
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.intArrayReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.afterHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getIntField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField
import com.yunzia.hyperstar.hook.util.plugin.ConfigUtils
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.android.util.ViewUtil.findViewByIdName

//这里是改变控制中心臭臭颜色的地方
object QSControlCenterColor : BasePluginHook() {

    override fun init() {
        startCardTitle() //卡片磁贴标题颜色
        startCardIcon()
        startMediaColors()
        startToggleSliderIconColor()
        startListIconColor()
        startDeviceColor()
        startEditColor()

        starBackgroundColors() //背景颜色-资源替换
        startCardColors() //卡片磁贴颜色替换
        startToggleSliderColors() //滑条颜色-资源替换
        startListColors() //普通磁贴颜色-资源替换
        startDeviceColors() //设备中心-资源替换
    }

    private fun startDeviceColors() {

        val deviceCenterItemBackgroundColor = XSPUtils.getString("device_center_item_background_color","null")
        val deviceCenterDetailIconColor = XSPUtils.getString("device_center_detail_icon_color","null")
        if (deviceCenterDetailIconColor != "null"){
            drawableReplaceByValue(plugin, "ic_device_center_detail_item") {
                colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterDetailIconColor),PorterDuff.Mode.SRC_IN)
            }
        }
        //单个设备项颜色替换
        if (deviceCenterItemBackgroundColor != "null"){
            drawableReplaceByValue(plugin, "ic_device_center_item_background_default") {
                colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterItemBackgroundColor),PorterDuff.Mode.SRC_IN)
            }
        }
    }



    private fun startEditColor() {
        val editTitleColor = XSPUtils.getString("edit_title_color","null")
        val ConfigUtils = findClass("miui.systemui.controlcenter.ConfigUtils",pluginClassLoader)
        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController\$EditButtonViewHolder",
            pluginClassLoader
        ).apply {
            if (editTitleColor != "null"){
                afterHookConstructor(View::class.java){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    val text =  itemView.findViewByIdName("text") as TextView
                    text.setTextColor(Color.parseColor(editTitleColor))
                }
            }
            if (editTitleColor != "null"){
                afterHookMethod("onConfigurationChanged", Int::class.java){
                    val p1 = it.args[0]
                    val INSTANCE = ConfigUtils.getStaticObjectField("INSTANCE")

                    val textAppearanceChanged = INSTANCE.callMethodAs<Boolean>("textAppearanceChanged",p1)!!
                    if (textAppearanceChanged){
                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val text = itemView.findViewByIdNameAs<TextView>("text")
                        text.setTextColor(Color.parseColor(editTitleColor))
                    }
                }
            }
        }




    }

    private fun startDeviceColor() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val deviceCenterIconColor = XSPUtils.getString("device_center_icon_color","null")
        val deviceCenterTitleColor = XSPUtils.getString("device_center_title_color","null")

        //val ConfigUtils = XposedHelpers.findClass("miui.systemui.controlcenter.ConfigUtils",pluginClassLoader)
        findClass(
            "miui.systemui.controlcenter.panel.main.devicecenter.devices.EmptyDeviceViewHolder",
            pluginClassLoader
        ).apply {
            if (deviceCenterTitleColor != "null" || deviceCenterIconColor != "null"){
                afterHookConstructor(View::class.java){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    if (deviceCenterIconColor != "null"){
                        val icon = itemView.findViewByIdNameAs<ImageView>("icon")
                        icon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterIconColor),PorterDuff.Mode.SRC_IN)
                    }
                    if (deviceCenterTitleColor != "null"){
                        val title =  itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(Color.parseColor(deviceCenterTitleColor))
                    }

                }
            }
            if (deviceCenterTitleColor != "null"){
                afterHookMethod("onConfigurationChanged", Int::class.java){
                    val p1 = it.args[0]
                    if (configUtils.textAppearanceChanged(p1)){
                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val title = itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(Color.parseColor(deviceCenterTitleColor))
                    }

                }
            }

        }

        val deviceControlIconColor = XSPUtils.getString("device_control_icon_color","null")
        val deviceControlTitleColor = XSPUtils.getString("device_control_title_color","null")
        findClass(
            "miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController\$DeviceControlEntryViewHolder",
            pluginClassLoader
        ).apply {
            if (deviceControlTitleColor != "null" || deviceControlIconColor != "null"){
                afterHookConstructor(View::class.java){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    if (deviceCenterIconColor != "null"){
                        val entryIcon = itemView.findViewByIdNameAs<ImageView>("entry_icon")
                        entryIcon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceControlIconColor),PorterDuff.Mode.SRC_IN)
                    }
                    if (deviceCenterTitleColor != "null"){
                        val entryTitle =  itemView.findViewByIdNameAs<TextView>("entry_title")
                        entryTitle.setTextColor(Color.parseColor(deviceControlTitleColor))
                    }

                }
            }
            if (deviceControlTitleColor != "null" ){
                afterHookMethod("onConfigurationChanged", Int::class.java){
                    val p1 = it.args[0]
                    if (configUtils.textAppearanceChanged(p1)){
                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val entryTitle =  itemView.findViewByIdNameAs<TextView>("entry_title")
                        entryTitle.setTextColor(Color.parseColor(deviceControlTitleColor))
                    }

                }
            }

        }


    }

    private fun startToggleSliderColors() {
        val mainProgressBlendColor = XSPUtils.getString("toggle_slider_progress_color_main", "null")
        val secondaryProgressBlendColor = XSPUtils.getString("toggle_slider_progress_color_secondary", "null")

        val progressColor = XSPUtils.getString("toggle_slider_progress_color", "null")
        val valueColor = XSPUtils.getString("toggle_slider_value_color", "null")

        if (progressColor != "null") colorReplaceByValue("toggle_slider_progress_color", plugin, progressColor)

//        colorReplaceByValue("toggle_slider_icon_color", plugin, valueColor)

        if (valueColor != "null") colorReplaceByValue("toggle_slider_top_text_color", plugin, valueColor)

        intArrayReplaceByValue(
            "toggle_slider_progress_blend_colors",
            plugin
        ) {
            if (mainProgressBlendColor != "null"){
                this[0] = Color.parseColor(mainProgressBlendColor)
            }
            if (secondaryProgressBlendColor != "null"){
                this[2] = Color.parseColor(secondaryProgressBlendColor)
            }
        }


    }



    private fun starBackgroundColors() {
        val backgroundColor = XSPUtils.getString("background_color", "null")
        val editBackgroundColor = XSPUtils.getString("edit_background_color", "null")
        val editBackgroundMode =XSPUtils.getInt("edit_background_mode",0)

        val mainBackgroundBlendColor = XSPUtils.getString("background_blend_color_main", "null")
        val secondaryBackgroundBlendColor = XSPUtils.getString("background_blend_color_secondary", "null")

        val mainEditBackgroundBlendColor = XSPUtils.getString("edit_background_blend_color_main", "null")
        val secondaryEditBackgroundBlendColor = XSPUtils.getString("edit_background_blend_color_secondary", "null")

        if (backgroundColor != "null"){
            colorReplaceByValue("qs_card_disabled_color", plugin, backgroundColor)
            colorReplaceByValue("external_entry_background_color", plugin, backgroundColor)
            colorReplaceByValue("toggle_slider_progress_background_color", plugin, backgroundColor)
            colorReplaceByValue("qs_disabled_color", plugin, backgroundColor)
        }

        if (mainBackgroundBlendColor != "null" || secondaryBackgroundBlendColor != "null"){
            intArrayReplaceByValue(
                "control_center_list_items_blend_colors",
                plugin
            ) {
                if (mainBackgroundBlendColor != "null"){
                    this[0] = Color.parseColor(mainBackgroundBlendColor)
                }
                if (secondaryBackgroundBlendColor != "null"){
                    this[2] = Color.parseColor(secondaryBackgroundBlendColor)
                }
            }
        }
        if (editBackgroundMode == 0){
            if (editBackgroundColor != "null"){
                colorReplaceByValue("qs_customize_entry_button_background_color", plugin, editBackgroundColor)
            }
            intArrayReplaceByValue(
                "control_center_edit_button_blend_colors",
                plugin
            ) {
                if (mainEditBackgroundBlendColor != "null"){
                    this[0] = Color.parseColor(mainEditBackgroundBlendColor)

                }
                if (secondaryEditBackgroundBlendColor != "null"){
                    this[2] = Color.parseColor(secondaryEditBackgroundBlendColor)
                }
            }
        }else{
            intArrayReplaceByIdName("array","control_center_edit_button_blend_colors",plugin,"control_center_list_items_blend_colors")
            colorReplaceByIdName("qs_customize_entry_button_background_color",plugin,"external_entry_background_color")
        }
    }

    private fun startCardColors() {
        val enableColor = XSPUtils.getString("card_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("card_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_unavailable_color", "null")

        if (enableColor != "null"){
            colorReplaceByValue("qs_card_enabled_color", plugin, enableColor)
            colorReplaceByValue("qs_card_cellular_color", plugin, enableColor)
            colorReplaceByValue("qs_card_flashlight_color", plugin, enableColor)
        }
        if (restrictedColor != "null"){
            colorReplaceByValue("qs_card_unavailable_color", plugin, restrictedColor)
        }

        if (unavailableColor != "null"){
            colorReplaceByValue("qs_card_disabled_color", plugin, unavailableColor)
        }

    }

    private fun startListColors() {
        val enableColor = XSPUtils.getString("list_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("list_restricted_color", "null")
        val warningColor = XSPUtils.getString("list_warning_color", "null")
        val unavailableColor = XSPUtils.getString("list_unavailable_color", "null")
        val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state",0)

        if (tileColorForState == 0){
            val titleColor = XSPUtils.getString("list_title_color", "null")
            if (titleColor != "null") colorReplaceByValue("qs_text_disabled_color", plugin, titleColor)
        }
        if (enableColor != "null"){
            colorReplaceByValue("qs_enabled_color", plugin, enableColor)
            colorReplaceByValue("qs_detail_enabled_color", plugin, enableColor)
        }
        if (warningColor != "null"){
            colorReplaceByValue("qs_warning_color", plugin, warningColor)
            colorReplaceByValue("qs_detail_warning_color", plugin, warningColor)
        }
        if (restrictedColor != "null"){
            colorReplaceByValue("qs_restrict_color", plugin, restrictedColor)
            colorReplaceByValue("qs_detail_restrict_color", plugin, restrictedColor)
        }
        if (unavailableColor != "null"){
            colorReplaceByValue("qs_unavailable_color", plugin, unavailableColor)
            colorReplaceByValue("qs_detail_unavailable_color", plugin, unavailableColor)
        }
    }

    private fun startToggleSliderIconColor() {

        val iconColor = XSPUtils.getString("toggle_slider_icon_color", "null")
        val BrightnessSliderController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",pluginClassLoader)
        val VolumeSliderController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",pluginClassLoader)
        if (iconColor != "null"){
            BrightnessSliderController.afterHookMethod("updateIcon"){
                val sliderHolder = this.callMethod("getSliderHolder")
                val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
                val icon = itemView.findViewByIdNameAs<ImageView>("icon")
                val drawable = icon.drawable
                if (drawable is AnimatedVectorDrawable){
                    drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(iconColor),PorterDuff.Mode.SRC_IN)
                }
            }
            VolumeSliderController.afterHookMethod("updateIcon",Boolean::class.java){
                val sliderHolder = this.callMethod("getSliderHolder")
                val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
                val icon = itemView.findViewByIdNameAs<ImageView>("icon")
                val drawable = icon.drawable
                if (drawable is AnimatedVectorDrawable){
                    drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(iconColor),PorterDuff.Mode.SRC_IN)
                }

            }

        }

        //colorReplaceByValue("toggle_slider_icon_color",iconColor)
    }

    private fun startMediaColors() {
        val configUtils = ConfigUtils(pluginClassLoader)
        val titleColor = XSPUtils.getString("media_title_color", "null")
        val artistColor = XSPUtils.getString("media_artist_color", "null")
        val emptyStateColor = XSPUtils.getString("media_empty_state_color", "null")
        val disabledIconColor = XSPUtils.getString("media_icon_color_disabled", "null")
        val enabledIconColor = XSPUtils.getString("media_icon_color_enabled", "null")
        val deviceIconColor = XSPUtils.getString("media_device_icon_color", "null")
        val MediaPlayerIconsInfo = findClass("miui.systemui.controlcenter.media.MediaPlayerIconsInfo",pluginClassLoader)

        findClass(
            "miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",
            pluginClassLoader
        ).apply {
            afterHookAllConstructors {
                val itemView = this.getObjectFieldAs<View>("itemView")
                if (deviceIconColor != "null"){
                    val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                    deviceIcon.alpha = 1f
                    logE("$deviceIconColor")
                    deviceIcon.colorFilter = PorterDuffColorFilter(deviceIconColor!!.toColorInt(), PorterDuff.Mode.SRC_IN)
                }

                if (titleColor != "null"){
                    val title = itemView.findViewByIdNameAs<TextView>("title")
                    title.setTextColor(titleColor!!.toColorInt())
                }

                if(artistColor != "null"){
                    val artist = itemView.findViewByIdNameAs<TextView>("artist")
                    artist.setTextColor(artistColor!!.toColorInt())
                }

                if (emptyStateColor != "null"){
                    val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                    emptyState.setTextColor(emptyStateColor!!.toColorInt())
                }
            }
            afterHookMethod("onConfigurationChanged", Int::class.java){
                val itemView = this.getObjectFieldAs<View>("itemView")
                val configuration = it.args[0]
                if (configUtils.textAppearanceChanged(configuration)){
                    if (titleColor != "null"){
                        val title = itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(titleColor!!.toColorInt())
                    }
                    if(artistColor != "null"){
                        val artist = itemView.findViewByIdNameAs<TextView>("artist")
                        artist.setTextColor(artistColor!!.toColorInt())
                    }
                    if (emptyStateColor != "null"){
                        val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                        emptyState.setTextColor(emptyStateColor!!.toColorInt())
                    }
                }

            }
            afterHookMethod("disableMediaController"){
                val itemView = this.getObjectFieldAs<View>("itemView")
                if (disabledIconColor != "null"){
                    val prev = itemView.findViewByIdNameAs<ImageView>("prev")
                    val next = itemView.findViewByIdNameAs<ImageView>("next")
                    prev.colorFilter = PorterDuffColorFilter(disabledIconColor!!.toColorInt(), PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(disabledIconColor.toColorInt(), PorterDuff.Mode.SRC_IN)
                }
                if (enabledIconColor != "null"){
                    val play = itemView.findViewByIdName("play") as ImageView

                    play.colorFilter = PorterDuffColorFilter(enabledIconColor!!.toColorInt(), PorterDuff.Mode.SRC_IN)
                }
            }
            if (deviceIconColor != null){
                afterHookMethod("updateIconsInfo",MediaPlayerIconsInfo, Boolean::class.java){
                    val deviceRes = this.getObjectField("deviceRes")
                    val mediaPlayerIconsInfo = it.args[0]
                    val boolean = it.args[1] as Boolean
                    val getDeviceRes = mediaPlayerIconsInfo.callMethodAs<Int>("getDeviceRes")
                    if (deviceRes != getDeviceRes || boolean){
                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                        deviceIcon.alpha = 1f
                        deviceIcon.colorFilter = PorterDuffColorFilter(deviceIconColor.toColorInt(), PorterDuff.Mode.SRC_IN)
                    }
                }
            }
            if (enabledIconColor != "null"){
                afterHookMethod("enableMediaController"){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    val prev = itemView.findViewByIdNameAs<ImageView>("prev")
                    val play = itemView.findViewByIdNameAs<ImageView>("play")
                    val next = itemView.findViewByIdNameAs<ImageView>("next")
                    prev.colorFilter = PorterDuffColorFilter(enabledIconColor!!.toColorInt(), PorterDuff.Mode.SRC_IN)
                    play.colorFilter = PorterDuffColorFilter(enabledIconColor.toColorInt(), PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(enabledIconColor.toColorInt(), PorterDuff.Mode.SRC_IN)
                }
            }
        }

    }


    private fun startCardTitle() {
        val disablePrimaryColor = XSPUtils.getString("card_primary_disabled_color", "null")
        val enablePrimaryColor = XSPUtils.getString("card_primary_enabled_color", "null")
        val restrictedPrimaryColor = XSPUtils.getString("card_primary_restricted_color", "null")
        val unavailablePrimaryColor = XSPUtils.getString("card_primary_unavailable_color", "null")

        val disableSecondaryColor = XSPUtils.getString("card_secondary_disabled_color", "null")
        val enableSecondaryColor = XSPUtils.getString("card_secondary_enabled_color", "null")
        val restrictedSecondaryColor = XSPUtils.getString("card_secondary_restricted_color", "null")
        val unavailableSecondaryColor = XSPUtils.getString("card_secondary_unavailable_color", "null")


        val QSItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", pluginClassLoader)
        val QSCardItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemView", pluginClassLoader)

        QSCardItemView.afterHookAllMethods(
            "updateState"
        ) { this as LinearLayout

            val Companion = QSItemView.getStaticObjectField("Companion")
            if (Companion == null) {
                logE("QSItemView Companion is null")
                return@afterHookAllMethods
            }
            val sta = this.getObjectField("state")
            val states = Companion.callMethodAs<Boolean>("isRestrictedCompat",sta)
            val state = sta.getIntField("state")
            val title = this.findViewByIdNameAs<TextView>("title")
            val status = this.findViewByIdNameAs<TextView>("status")
            logD("status = $state")

            when(state){

                1->{
                    if (disablePrimaryColor != "null") title.setTextColor(disablePrimaryColor!!.toColorInt())
                    if (disableSecondaryColor != "null") status.setTextColor(disableSecondaryColor!!.toColorInt())
                }
                2->{
                    if (enablePrimaryColor != "null") title.setTextColor(enablePrimaryColor!!.toColorInt())
                    if (enableSecondaryColor != "null") status.setTextColor(enableSecondaryColor!!.toColorInt())
                }
                else->{
                    if (states) {
                        if (restrictedPrimaryColor != "null") title.setTextColor(
                            restrictedPrimaryColor!!.toColorInt())
                        if (restrictedSecondaryColor != "null") status.setTextColor(
                            restrictedSecondaryColor!!.toColorInt())
                    }else{
                        if (unavailablePrimaryColor != "null") title.setTextColor(
                            unavailablePrimaryColor!!.toColorInt())
                        if (unavailableSecondaryColor != "null") status.setTextColor(
                            unavailableSecondaryColor!!.toColorInt())
                    }

                }

            }

        }

    }

    private fun startListIconColor() {
        val offColor = XSPUtils.getString("list_icon_off_color", "null")
        val onColor = XSPUtils.getString("list_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("list_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("list_icon_unavailable_color", "null")

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            pluginClassLoader
        ).afterHookMethod(
            "updateResources"
        ) {

            if (onColor != "null"){
                setColorField(this,"iconColor",onColor)
            }
            if (offColor != "null"){
                setColorField(this,"iconColorOff",offColor)
            }
            if (restrictedColor != "null"){
                setColorField(this,"iconColorRestrict",restrictedColor)
            }
            if (unavailableColor != "null"){
                setColorField(this,"iconColorUnavailable",unavailableColor)
            }

        }

    }

    private fun startCardIcon() {

        val offColor = XSPUtils.getString("card_icon_off_color", "null")
        val onColor = XSPUtils.getString("card_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("card_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_icon_unavailable_color", "null")

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSCardItemIconView",
            pluginClassLoader
        ).afterHookMethod("updateResources"){
            if (onColor != "null"){
                setColorField(this,"iconColor",onColor)
            }
            if (offColor != "null"){
                setColorField(this,"iconColorOff",offColor)
            }
            if (restrictedColor != "null"){
                setColorField(this,"iconColorRestricted",restrictedColor)
            }
            if (unavailableColor != "null"){
                setColorField(this,"iconColorUnavailable",unavailableColor)
            }

        }

    }

}