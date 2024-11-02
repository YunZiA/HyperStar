package com.yunzia.hyperstar.hook.app.plugin

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
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSControlCenterColor :BaseHooker() {


    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        starBackgroundColorHook()
        startCardColorHook()
        startToggleSliderColorHook()
        startListColorHook()
        startDeviceColorRes()
//        resparam?.res?.setReplacement(plugin, "drawable", "qs_background_disabled", object : XResources.DrawableLoader() {
//            override fun newDrawable(res: XResources?, id: Int): Drawable {
//                val newDraw = res?.getDrawable(id) as Drawable
//                if (newDraw is GradientDrawable){
//                    newDraw.setStroke(20,Color.RED)
//                }
//                return newDraw
//
//
//            }
//        })

    }

    private fun startDeviceColorRes() {

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
                    starLog.log("${newDraw.alpha}")
                    // newDraw.alpha
                    newDraw.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterItemBackgroundColor),PorterDuff.Mode.SRC_IN)
                    return newDraw
                }

            })

        }


    }

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

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
        val ConfigUtils = XposedHelpers.findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)
        val EditButtonViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController\$EditButtonViewHolder",classLoader)

        if (editTitleColor != "null"){

            XposedHelpers.findAndHookConstructor(EditButtonViewHolder,View::class.java,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View

                    if (editTitleColor != "null"){
                        val text =  itemView.findViewByIdName("text") as TextView
                        text.setTextColor(Color.parseColor(editTitleColor))
                    }


                }
            })

        }
        if (editTitleColor != "null"){
            XposedHelpers.findAndHookMethod(EditButtonViewHolder,"onConfigurationChanged", Int::class.java, object :XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val p1 = param?.args?.get(0)
                    val thisObj = param?.thisObject
                    val INSTANCE = XposedHelpers.getStaticObjectField(ConfigUtils,"INSTANCE")

                    val textAppearanceChanged = XposedHelpers.callMethod(INSTANCE,"textAppearanceChanged",p1) as Boolean
                    if (textAppearanceChanged){

                        val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                        val text =  itemView.findViewByIdName("text") as TextView
                        text.setTextColor(Color.parseColor(editTitleColor))

                    }


                }
            })

        }


    }

    private fun startDeviceColor() {
        val deviceCenterIconColor = XSPUtils.getString("device_center_icon_color","null")
        val deviceCenterTitleColor = XSPUtils.getString("device_center_title_color","null")

        val ConfigUtils = XposedHelpers.findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)
        val EmptyDeviceViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.devicecenter.devices.EmptyDeviceViewHolder",classLoader)


        if (deviceCenterTitleColor != "null" || deviceCenterIconColor != "null"){

            XposedHelpers.findAndHookConstructor(EmptyDeviceViewHolder,View::class.java,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                    if (deviceCenterIconColor != "null"){
                        val icon = itemView.findViewByIdName("icon") as ImageView
                        icon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceCenterIconColor),PorterDuff.Mode.SRC_IN)
                    }
                    if (deviceCenterTitleColor != "null"){
                        val title =  itemView.findViewByIdName("title") as TextView
                        title.setTextColor(Color.parseColor(deviceCenterTitleColor))
                    }


                }
            })

        }
        if (deviceCenterTitleColor != "null"){
            XposedHelpers.findAndHookMethod(EmptyDeviceViewHolder,"onConfigurationChanged", Int::class.java, object :XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val p1 = param?.args?.get(0)
                    val thisObj = param?.thisObject
                    val INSTANCE = XposedHelpers.getStaticObjectField(ConfigUtils,"INSTANCE")

                    val textAppearanceChanged = XposedHelpers.callMethod(INSTANCE,"textAppearanceChanged",p1) as Boolean
                    if (textAppearanceChanged){

                        val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                        val title =  itemView.findViewByIdName("title") as TextView
                        title.setTextColor(Color.parseColor(deviceCenterTitleColor))

                    }


                }
            })

        }
        val deviceControlIconColor = XSPUtils.getString("device_control_icon_color","null")
        val deviceControlTitleColor = XSPUtils.getString("device_control_title_color","null")

        val DeviceControlEntryViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.devicecontrol.DeviceControlsEntryController\$DeviceControlEntryViewHolder",classLoader)
        if (deviceControlTitleColor != "null" || deviceControlIconColor != "null"){
            XposedHelpers.findAndHookConstructor(DeviceControlEntryViewHolder,View::class.java,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                    if (deviceCenterIconColor != "null"){
                        val entryIcon = itemView.findViewByIdName("entry_icon") as ImageView
                        entryIcon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceControlIconColor),PorterDuff.Mode.SRC_IN)
                    }
                    if (deviceCenterTitleColor != "null"){
                        val entryTitle =  itemView.findViewByIdName("entry_title") as TextView
                        entryTitle.setTextColor(Color.parseColor(deviceControlTitleColor))
                    }


                }
            })

        }
        if (deviceControlTitleColor != "null" ){
            XposedHelpers.findAndHookMethod(DeviceControlEntryViewHolder,"onConfigurationChanged", Int::class.java, object :XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val p1 = param?.args?.get(0)
                    val thisObj = param?.thisObject
                    val INSTANCE = XposedHelpers.getStaticObjectField(ConfigUtils,"INSTANCE")

                    val textAppearanceChanged = XposedHelpers.callMethod(INSTANCE,"textAppearanceChanged",p1) as Boolean
                    if (textAppearanceChanged){

                        val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                        val entryTitle =  itemView.findViewByIdName("entry_title") as TextView
                        entryTitle.setTextColor(Color.parseColor(deviceControlTitleColor))

                    }
                }
            })

        }

    }

    private fun startToggleSliderColorHook() {
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



    private fun starBackgroundColorHook() {
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
                "control_center_edit_button_blend_colors"
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

    private fun startCardColorHook() {
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

    private fun startListColorHook() {

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

        val BrightnessSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessSliderController",classLoader)
        val VolumeSliderController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.volume.VolumeSliderController",classLoader)


        if (iconColor != "null"){

            XposedHelpers.findAndHookMethod(BrightnessSliderController,"updateIconB",object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val sliderHolder = XposedHelpers.callMethod(thisObj,"getSliderHolder")
                    val itemView = XposedHelpers.getObjectField(sliderHolder,"itemView") as View
                    val icon = itemView.findViewByIdName("icon") as ImageView
                    val drawable = icon.drawable
                    if (drawable is AnimatedVectorDrawable){
                        drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(iconColor),PorterDuff.Mode.SRC_IN)
                    }

                }
            })

            XposedHelpers.findAndHookMethod(VolumeSliderController,"updateIconB",object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val sliderHolder = XposedHelpers.callMethod(thisObj,"getSlider")
                    val itemView = XposedHelpers.getObjectField(sliderHolder,"itemView") as View
                    val icon = itemView.findViewByIdName("icon") as ImageView
                    val drawable = icon.drawable
                    if (drawable is AnimatedVectorDrawable){
                        drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(iconColor),PorterDuff.Mode.SRC_IN)
                    }

                }
            })

        }

        //ReplaceColor("toggle_slider_icon_color",iconColor)
    }

    private fun startMediaColorsHook() {
        val titleColor = XSPUtils.getString("media_title_color", "null")
        val artistColor = XSPUtils.getString("media_artist_color", "null")
        val emptyStateColor = XSPUtils.getString("media_empty_state_color", "null")
        val disabledIconColor = XSPUtils.getString("media_icon_color_disabled", "null")
        val enabledIconColor = XSPUtils.getString("media_icon_color_enabled", "null")
        val deviceIconColor = XSPUtils.getString("media_device_icon_color", "null")

        val MediaPlayerViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",classLoader)
        val ConfigUtils = XposedHelpers.findClass("miui.systemui.controlcenter.ConfigUtils",classLoader)
        val MediaPlayerIconsInfo = XposedHelpers.findClass("miui.systemui.controlcenter.media.MediaPlayerIconsInfo",classLoader)

        XposedBridge.hookAllConstructors(MediaPlayerViewHolder, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                if (deviceIconColor != "null"){
                    val deviceIcon = itemView.findViewByIdName("device_icon") as ImageView
                    deviceIcon.alpha = 1f
                    deviceIcon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceIconColor), PorterDuff.Mode.SRC_IN)

                }

                if (titleColor != "null"){
                    val title = itemView.findViewByIdName("title") as TextView
                    title.setTextColor(Color.parseColor(titleColor))

                }

                if(artistColor != "null"){
                    val artist = itemView.findViewByIdName("artist") as TextView
                    artist.setTextColor(Color.parseColor(artistColor))

                }

                if (enabledIconColor != "null"){

                    val emptyState = itemView.findViewByIdName("empty_state") as TextView
                    emptyState.setTextColor(Color.parseColor(emptyStateColor))
                }
            }
        })

        if (deviceIconColor != null){
            XposedHelpers.findAndHookMethod(MediaPlayerViewHolder,"updateIconsInfo",MediaPlayerIconsInfo, Boolean::class.java ,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val deviceRes = XposedHelpers.getObjectField(thisObj,"deviceRes")
                    val mediaPlayerIconsInfo = param?.args?.get(0)
                    val boolean = param?.args?.get(1) as Boolean
                    val getDeviceRes = XposedHelpers.callMethod(mediaPlayerIconsInfo,"getDeviceRes") as Int
                    if (deviceRes != getDeviceRes || boolean){
                        val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                        val deviceIcon = itemView.findViewByIdName("device_icon") as ImageView
                        deviceIcon.alpha = 1f
                        deviceIcon.colorFilter = PorterDuffColorFilter(Color.parseColor(deviceIconColor), PorterDuff.Mode.SRC_IN)

                    }




                }
            })

        }




        XposedHelpers.findAndHookMethod(MediaPlayerViewHolder,"onConfigurationChanged", Int::class.java ,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                val configuration = param?.args?.get(0)
                val instances = XposedHelpers.getStaticObjectField(ConfigUtils,"INSTANCE")

                val textAppearanceChanged = XposedHelpers.callMethod(instances,"textAppearanceChanged",configuration) as Boolean
                if (textAppearanceChanged){
                    if (titleColor != "null"){
                        val title = itemView.findViewByIdName("title") as TextView
                        title.setTextColor(Color.parseColor(titleColor))

                    }

                    if(artistColor != "null"){
                        val artist = itemView.findViewByIdName("artist") as TextView
                        artist.setTextColor(Color.parseColor(artistColor))

                    }

                    if (enabledIconColor != "null"){

                        val emptyState = itemView.findViewByIdName("empty_state") as TextView
                        emptyState.setTextColor(Color.parseColor(emptyStateColor))
                    }

                }


            }
        })

        XposedHelpers.findAndHookMethod(MediaPlayerViewHolder,"disableMediaController",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                if (disabledIconColor != "null"){
                    val prev = itemView.findViewByIdName("prev") as ImageView
                    val next = itemView.findViewByIdName("next") as ImageView
                    prev.colorFilter = PorterDuffColorFilter(Color.parseColor(disabledIconColor), PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(Color.parseColor(disabledIconColor), PorterDuff.Mode.SRC_IN)

                }
                if (enabledIconColor != "null"){
                    val play = itemView.findViewByIdName("play") as ImageView

                    play.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                }


            }
        })


        if (enabledIconColor != "null"){

            XposedHelpers.findAndHookMethod(MediaPlayerViewHolder,"enableMediaController",object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                    val prev = itemView.findViewByIdName("prev") as ImageView
                    val play = itemView.findViewByIdName("play") as ImageView
                    val next = itemView.findViewByIdName("next") as ImageView
                    prev.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                    play.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)
                    next.colorFilter = PorterDuffColorFilter(Color.parseColor(enabledIconColor), PorterDuff.Mode.SRC_IN)


                }
            })

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


        val QSItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSCardItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemView", classLoader)

        XposedBridge.hookAllMethods(QSCardItemView,"updateState",object : XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject

                if (thisObj == null) {
                    starLog.log("QSControlCenterColor View is null")
                    return
                }

                val Companion = XposedHelpers.getStaticObjectField(QSItemView,"Companion")

                if (Companion == null) {
                    starLog.log("QSItemView Companion is null")
                    return
                }
                val sta = XposedHelpers.getObjectField(thisObj,"state")

                val states = XposedHelpers.callMethod(Companion,"isRestrictedCompat",sta) as Boolean

                val state = XposedHelpers.getObjectField(sta,"state")
                thisObj as LinearLayout
                val title = thisObj.findViewByIdName("title") as TextView

                val status = thisObj.findViewByIdName("status") as TextView

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

        })
    }

    private fun startListIconColor() {
        val QSTileItemIconView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView", classLoader)
        val offColor = XSPUtils.getString("list_icon_off_color", "null")
        val onColor = XSPUtils.getString("list_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("list_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("list_icon_unavailable_color", "null")
        XposedHelpers.findAndHookMethod(QSTileItemIconView,"updateResources",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject

                if (onColor != "null"){
                    setColorField(thisObj,"iconColor",onColor)
                }
                if (offColor != "null"){
                    setColorField(thisObj,"iconColorOff",offColor)
                }
                if (restrictedColor != "null"){
                    setColorField(thisObj,"iconColorRestrict",restrictedColor)
                }
                if (unavailableColor != "null"){
                    setColorField(thisObj,"iconColorUnavailable",unavailableColor)
                }
            }
        })
    }

    private fun startCardIconHook() {

        val offColor = XSPUtils.getString("card_icon_off_color", "null")
        val onColor = XSPUtils.getString("card_icon_on_color", "null")
        val restrictedColor = XSPUtils.getString("card_icon_restricted_color", "null")
        val unavailableColor = XSPUtils.getString("card_icon_unavailable_color", "null")

        val QSCardItemIconView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSCardItemIconView", classLoader)
        XposedHelpers.findAndHookMethod(QSCardItemIconView,"updateResources",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject

                if (onColor != "null"){
                    setColorField(thisObj,"iconColor",onColor)
                }
                if (offColor != "null"){
                    setColorField(thisObj,"iconColorOff",offColor)
                }
                if (restrictedColor != "null"){
                    setColorField(thisObj,"iconColorRestricted",restrictedColor)
                }
                if (unavailableColor != "null"){
                    setColorField(thisObj,"iconColorUnavailable",unavailableColor)
                }
            }
        })



    }

}