package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.res.XModuleResources
import android.content.res.XResources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.plugin.ConfigUtils
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSControlCenterColor : Hooker() {


    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)
        starBackgroundColorsByRes()
        startCardColorsByRes()
        startToggleSliderColorsByRes()
        startListColorsByRes()
        startDeviceColorsByRes()


    }

    private fun startDeviceColorsByRes() {

        val deviceCenterItemBackgroundColor = XSPUtils.getString("device_center_item_background_color","null")
        val deviceCenterDetailIconColor = XSPUtils.getString("device_center_detail_icon_color","null")

        if (deviceCenterDetailIconColor != "null"){
            resparam.res.setReplacement(plugin, "drawable", "ic_device_center_detail_item", object : XResources.DrawableLoader(){
                override fun newDrawable(res: XResources?, id: Int): Drawable {
                    val newDraw = res?.getDrawable(id) as Drawable
                    newDraw.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterDetailIconColor),PorterDuff.Mode.SRC_IN)
                    return newDraw
                }

            })

        }

        if (deviceCenterItemBackgroundColor != "null"){
            resparam.res.setReplacement(plugin, "drawable", "ic_device_center_item_background_default", object : XResources.DrawableLoader(){
                override fun newDrawable(res: XResources?, id: Int): Drawable {
                    val newDraw = res?.getDrawable(id) as Drawable
                    starLog.logD("${newDraw.alpha}")
                    // newDraw.alpha
                    newDraw.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterItemBackgroundColor),PorterDuff.Mode.SRC_IN)
                    return newDraw
                }

            })

        }


    }

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        startCardTitleHook()
        startCardIconHook()
        startMediaColorsHook()
        startToggleSliderIconColorHook()
        startListIconColor()
        startDeviceColor()
        startEditColor()

    }

    private fun startEditColor() {
        val editTitleColor = XSPUtils.getString("edit_title_color","null")
        val ConfigUtils = findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)
        val EditButtonViewHolder = findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController\$EditButtonViewHolder",classLoader)


        EditButtonViewHolder.apply {
            if (editTitleColor != "null") {
                afterHookConstructor(
                    View::class.java
                ){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    val text =  itemView.findViewByIdName("text") as TextView
                    text.setTextColor(Color.parseColor(editTitleColor))

                }
            }
            if (editTitleColor != "null"){
                afterHookMethod(
                    "onConfigurationChanged",
                    Int::class.java
                ){
                    val p1 = it.args[0]
                    val INSTANCE = ConfigUtils.getStaticObjectField("INSTANCE")
                    val textAppearanceChanged = INSTANCE.callMethodAs<Boolean>("textAppearanceChanged",p1)!!

                    if (textAppearanceChanged){
                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val text =  itemView.findViewByIdName("text") as TextView
                        text.setTextColor(Color.parseColor(editTitleColor))
                    }

                }
            }

        }

    }

    private fun startDeviceColor() {
        val deviceCenterIconColor = XSPUtils.getString("device_center_icon_color","null")
        val deviceCenterTitleColor = XSPUtils.getString("device_center_title_color","null")

        val configUtils = ConfigUtils(classLoader)
        val EmptyDeviceViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.EmptyDeviceViewHolder",classLoader)

        EmptyDeviceViewHolder.apply {
            if (deviceCenterTitleColor != "null" || deviceCenterIconColor != "null"){
                afterHookConstructor(
                    View::class.java
                ){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    if (deviceCenterIconColor != "null"){
                        val icon = itemView.findViewByIdName("icon") as ImageView
                        icon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterIconColor),PorterDuff.Mode.SRC_IN)
                    }
                    if (deviceCenterTitleColor != "null"){
                        val title =  itemView.findViewByIdName("title") as TextView
                        title.setTextColor(Color.parseColor(deviceCenterTitleColor))
                    }

                }
            }
            if (deviceCenterTitleColor != "null"){
                afterHookMethod(
                    "onConfigurationChanged",
                    Int::class.java
                ){

                    if (configUtils.textAppearanceChanged(it.args[0])){

                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val title =  itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(Color.parseColor(deviceCenterTitleColor))

                    }

                }

            }

        }

        val deviceControlIconColor = XSPUtils.getString("device_control_icon_color","null")
        val deviceControlTitleColor = XSPUtils.getString("device_control_title_color","null")
        val DeviceControlEntryViewHolder = findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController\$DeviceControlEntryViewHolder",classLoader)

        DeviceControlEntryViewHolder.apply {
            if (deviceControlTitleColor != "null" || deviceControlIconColor != "null"){
                afterHookConstructor(
                    View::class.java
                ){
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
                afterHookMethod(
                    "onConfigurationChanged",
                    Int::class.java
                ){
                    if (configUtils.textAppearanceChanged(it.args[0])){

                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val entryTitle =  itemView.findViewByIdNameAs<TextView>("entry_title")
                        entryTitle.setTextColor(Color.parseColor(deviceControlTitleColor))

                    }

                }
            }

        }

    }

    private fun startToggleSliderColorsByRes() {
        val mainProgressBlendColor = XSPUtils.getString("toggle_slider_progress_color_main", "null")
        val secondaryProgressBlendColor = XSPUtils.getString("toggle_slider_progress_color_secondary", "null")

        val progressColor = XSPUtils.getString("toggle_slider_progress_color", "null")
        val valueColor = XSPUtils.getString("toggle_slider_value_color", "null")

        if (progressColor != "null") ReplaceColor("toggle_slider_progress_color",progressColor)


        //ReplaceColor("toggle_slider_icon_color",valueColor)

        if (valueColor != "null") ReplaceColor("toggle_slider_top_text_color",valueColor)

        ReplaceIntArray(
            "toggle_slider_progress_blend_colors"
        ) { array ->
            if (mainProgressBlendColor != "null"){
                array[0] = Color.parseColor(mainProgressBlendColor)

            }
            if (secondaryProgressBlendColor != "null"){
                array[2] = Color.parseColor(secondaryProgressBlendColor)

            }

        }


    }



    private fun starBackgroundColorsByRes() {
        val backgroundColor = XSPUtils.getString("background_color", "null")
        val editBackgroundColor = XSPUtils.getString("edit_background_color", "null")
        val editBackgroundMode =XSPUtils.getInt("edit_background_mode",0)

        val mainBackgroundBlendColor = XSPUtils.getString("background_blend_color_main", "null")
        val secondaryBackgroundBlendColor = XSPUtils.getString("background_blend_color_secondary", "null")

        val mainEditBackgroundBlendColor = XSPUtils.getString("edit_background_blend_color_main", "null")
        val secondaryEditBackgroundBlendColor = XSPUtils.getString("edit_background_blend_color_secondary", "null")

        if (backgroundColor != "null"){

            ReplaceColor("qs_card_disabled_color",backgroundColor)
            ReplaceColor("external_entry_background_color",backgroundColor)
            ReplaceColor("toggle_slider_progress_background_color",backgroundColor)
            ReplaceColor("qs_disabled_color",backgroundColor)


        }
        if (editBackgroundMode == 0){
            if (editBackgroundColor != "null"){
                ReplaceColor("qs_customize_entry_button_background_color",editBackgroundColor)

            }
            ReplaceIntArray(
                "control_center_edit_button_blend_colors",

                ) { array ->
                if (mainEditBackgroundBlendColor != "null"){
                    array[0] = Color.parseColor(mainEditBackgroundBlendColor)

                }
                if (secondaryEditBackgroundBlendColor != "null"){
                    array[2] = Color.parseColor(secondaryEditBackgroundBlendColor)

                }

            }

        }else{

            val res = resparam.res

            val array = res.getIntArray(res.getIdentifier("control_center_list_items_blend_colors", "array", plugin))
            res.setReplacement(plugin,"array","control_center_edit_button_blend_colors",array)
            val color = res.getColor(res.getIdentifier("external_entry_background_color","color",plugin))
            res.setReplacement(plugin,"color","qs_customize_entry_button_background_color",color)

        }


        if (mainBackgroundBlendColor != "null" || secondaryBackgroundBlendColor != "null"){
            ReplaceIntArray(
                "control_center_qs_items_blend_colors"
            ) { array ->
                if (mainBackgroundBlendColor != "null"){
                    array[0] = Color.parseColor(mainBackgroundBlendColor)

                }
                if (secondaryBackgroundBlendColor != "null"){
                    array[2] = Color.parseColor(secondaryBackgroundBlendColor)

                }

            }
            ReplaceIntArray(
                "control_center_list_items_blend_colors"
            ) { array ->
                if (mainBackgroundBlendColor != "null"){
                    array[0] = Color.parseColor(mainBackgroundBlendColor)

                }
                if (secondaryBackgroundBlendColor != "null"){
                    array[2] = Color.parseColor(secondaryBackgroundBlendColor)

                }


            }
        }

    }

    private fun startCardColorsByRes() {
        val enableColor = XSPUtils.getString("card_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("card_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_unavailable_color", "null")


        if (enableColor != "null"){
            ReplaceColor("qs_card_enabled_color",enableColor)
            ReplaceColor("qs_card_cellular_color",enableColor)
            ReplaceColor("qs_card_flashlight_color",enableColor)
        }

        if (restrictedColor != "null"){
            ReplaceColor("qs_card_unavailable_color",restrictedColor)
        }

        if (unavailableColor != "null"){
            ReplaceColor("qs_card_disabled_color",unavailableColor)
        }

    }

    private fun startListColorsByRes() {

        val enableColor = XSPUtils.getString("list_enabled_color", "null")
        val restrictedColor = XSPUtils.getString("list_restricted_color", "null")
        val warningColor = XSPUtils.getString("list_warning_color", "null")
        val unavailableColor = XSPUtils.getString("list_unavailable_color", "null")
        val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state",0)

        if (tileColorForState == 0){
            val titleColor = XSPUtils.getString("list_title_color", "null")
            if (titleColor != "null") ReplaceColor("qs_text_disabled_color",titleColor)

        }


        if (enableColor != "null"){
            ReplaceColor("qs_enabled_color",enableColor)

        }
        if (warningColor != "null"){
            ReplaceColor("qs_warning_color",warningColor)

        }

        if (restrictedColor != "null"){
            ReplaceColor("qs_restrict_color",restrictedColor)

        }

        if (unavailableColor != "null"){
            ReplaceColor("qs_unavailable_color",unavailableColor)

        }


    }



    private fun startToggleSliderIconColorHook() {

        val iconColor = XSPUtils.getString("toggle_slider_icon_color", "null")

        val BrightnessSliderController = findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)
        val VolumeSliderController = findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)


        if (iconColor != "null"){

            BrightnessSliderController.afterHookConstructor(
                "updateIconB"
            ){
                val sliderHolder = this.callMethod("getSliderHolder")
                val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
                val icon = itemView.findViewByIdNameAs<ImageView>("icon")
                val drawable = icon.drawable
                if (drawable is AnimatedVectorDrawable){
                    drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(iconColor),PorterDuff.Mode.SRC_IN)
                }

            }

            VolumeSliderController.afterHookMethod(
                "updateIconB"
            ){
                val sliderHolder = this.callMethod("getSlider")
                val itemView = sliderHolder.getObjectFieldAs<View>("itemView")
                val icon = itemView.findViewByIdNameAs<ImageView>("icon")
                val drawable = icon.drawable
                if (drawable is AnimatedVectorDrawable){
                    drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(iconColor),PorterDuff.Mode.SRC_IN)
                }

            }

        }

        //ReplaceColor("toggle_slider_icon_color",iconColor)
    }

    private fun startMediaColorsHook() {
        val configUtils = ConfigUtils(classLoader)
        val titleColor = XSPUtils.getString("media_title_color", "null")
        val artistColor = XSPUtils.getString("media_artist_color", "null")
        val emptyStateColor = XSPUtils.getString("media_empty_state_color", "null")
        val disabledIconColor = XSPUtils.getString("media_icon_color_disabled", "null")
        val enabledIconColor = XSPUtils.getString("media_icon_color_enabled", "null")
        val deviceIconColor = XSPUtils.getString("media_device_icon_color", "null")

        val MediaPlayerViewHolder = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",classLoader)
        val MediaPlayerIconsInfo = findClass("miui.systemui.controlcenter.media.MediaPlayerIconsInfo",classLoader)

        MediaPlayerViewHolder.apply {
            afterHookConstructor(
                View::class.java
            ){
                val itemView = this.getObjectFieldAs<View>("itemView")
                if (deviceIconColor != "null"){
                    val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                    deviceIcon.alpha = 1f
                    deviceIcon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceIconColor), PorterDuff.Mode.SRC_IN)

                }

                if (titleColor != "null"){
                    val title = itemView.findViewByIdNameAs<TextView>("title")
                    title.setTextColor(Color.parseColor(titleColor))

                }

                if(artistColor != "null"){
                    val artist = itemView.findViewByIdNameAs<TextView>("artist")
                    artist.setTextColor(Color.parseColor(artistColor))
                }

                if (emptyStateColor != "null"){
                    val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                    emptyState.setTextColor(Color.parseColor(emptyStateColor))
                }

            }
            if (deviceIconColor != null){
                afterHookMethod(
                    "updateIconsInfo",
                    MediaPlayerIconsInfo,
                    Boolean::class.java
                ){
                    val deviceRes = this.getObjectField("deviceRes")
                    val mediaPlayerIconsInfo = it.args[0]
                    val boolean = it.args[1] as Boolean
                    val getDeviceRes = mediaPlayerIconsInfo.callMethodAs<Int>("getDeviceRes")
                    if (deviceRes != getDeviceRes || boolean){
                        val itemView = this.getObjectFieldAs<View>("itemView")
                        val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
                        deviceIcon.alpha = 1f
                        deviceIcon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceIconColor), PorterDuff.Mode.SRC_IN)

                    }

                }
            }
            afterHookMethod(
                "onConfigurationChanged",
                Int::class.java
            ){
                val itemView = this.getObjectFieldAs<View>("itemView")
                val configuration = it.args[0]

                if (configUtils.textAppearanceChanged(configuration)){
                    if (titleColor != "null"){
                        val title = itemView.findViewByIdNameAs<TextView>("title")
                        title.setTextColor(Color.parseColor(titleColor))

                    }

                    if(artistColor != "null"){
                        val artist = itemView.findViewByIdNameAs<TextView>("artist")
                        artist.setTextColor(Color.parseColor(artistColor))

                    }

                    if (enabledIconColor != "null"){
                        val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                        emptyState.setTextColor(Color.parseColor(emptyStateColor))
                    }

                }

            }
            afterHookMethod(
                "disableMediaController"
            ){
                val itemView = this.getObjectFieldAs<View>("itemView")
                if (disabledIconColor != "null"){
                    val prev = itemView.findViewByIdNameAs<ImageView>("prev")
                    val next = itemView.findViewByIdNameAs<ImageView>("next")
                    prev.colorFilter = PorterDuffColorFilter(Color.parseColor(disabledIconColor), PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(Color.parseColor(disabledIconColor), PorterDuff.Mode.SRC_IN)

                }
                if (enabledIconColor != "null"){
                    val play = itemView.findViewByIdNameAs<ImageView>("play")
                    play.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                }

            }
            if (enabledIconColor != "null"){
                afterHookMethod(
                    "enableMediaController"
                ){
                    val itemView = this.getObjectFieldAs<View>("itemView")
                    val prev = itemView.findViewByIdNameAs<ImageView>("prev")
                    val play = itemView.findViewByIdNameAs<ImageView>("play")
                    val next = itemView.findViewByIdNameAs<ImageView>("next")
                    prev.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                    play.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                }
            }

        }

    }


    private fun startCardTitleHook() {
        val disablePrimaryColor = XSPUtils.getString("card_primary_disabled_color", "null")
        val enablePrimaryColor = XSPUtils.getString("card_primary_enabled_color", "null")
        val restrictedPrimaryColor = XSPUtils.getString("card_primary_restricted_color", "null")
        val unavailablePrimaryColor = XSPUtils.getString("card_primary_unavailable_color", "null")

        val disableSecondaryColor = XSPUtils.getString("card_secondary_disabled_color", "null")
        val enableSecondaryColor = XSPUtils.getString("card_secondary_enabled_color", "null")
        val restrictedSecondaryColor = XSPUtils.getString("card_secondary_restricted_color", "null")
        val unavailableSecondaryColor = XSPUtils.getString("card_secondary_unavailable_color", "null")


        val QSItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSCardItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemView", classLoader)

        QSCardItemView.beforeHookMethod(
            "updateBackground"
        ){
           this  as LinearLayout

            val Companion = QSItemView.getStaticObjectField("Companion")
            val sta = this.getObjectField("state")

            val states = Companion.callMethodAs<Boolean>("isRestrictedCompat",sta)!!

            val state = sta.getObjectField("state")
            val title = this.findViewByIdNameAs<TextView>("title")

            val status = this.findViewByIdNameAs<TextView>("status")

            if (state == 0) {

                if (disablePrimaryColor != "null") title.setTextColor(Color.parseColor(disablePrimaryColor))

                if (disableSecondaryColor != "null") status.setTextColor(Color.parseColor(disableSecondaryColor))

            } else if (state == 1 && states) {

                if (restrictedPrimaryColor != "null") title.setTextColor(Color.parseColor(restrictedPrimaryColor))

                if (restrictedSecondaryColor != "null") status.setTextColor(Color.parseColor(restrictedSecondaryColor))

            } else if (state != 2) {

                if (unavailablePrimaryColor != "null") title.setTextColor(Color.parseColor(unavailablePrimaryColor))

                if (unavailableSecondaryColor != "null") status.setTextColor(Color.parseColor(unavailableSecondaryColor))

            } else {

                if (enablePrimaryColor != "null") title.setTextColor(Color.parseColor(enablePrimaryColor))

                if (enableSecondaryColor != "null") status.setTextColor(Color.parseColor(enableSecondaryColor))

            }

        }

    }

    private fun startListIconColor() {
        val QSTileItemIconView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView", classLoader)
        val offColor = XSPUtils.getString("list_icon_off_color", "null")
        val onColor = XSPUtils.getString("list_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("list_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("list_icon_unavailable_color", "null")

        QSTileItemIconView.afterHookMethod(
            "updateResources"
        ){

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

    private fun startCardIconHook() {

        val offColor = XSPUtils.getString("card_icon_off_color", "null")
        val onColor = XSPUtils.getString("card_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("card_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_icon_unavailable_color", "null")

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSCardItemIconView",
            classLoader
        ).afterHookMethod(
            "updateResources"
        ){

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