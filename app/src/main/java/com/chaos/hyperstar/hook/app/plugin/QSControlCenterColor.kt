package com.chaos.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.AnimatedVectorDrawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook
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



    }

    private fun startToggleSliderColorHook() {
        val mainProgressBlendColor = XSPUtils.getString("toggle_slider_progress_color_main", "null")
        val secondaryProgressBlendColor = XSPUtils.getString("toggle_slider_progress_color_secondary", "null")

        val progressColor = XSPUtils.getString("toggle_slider_progress_color", "null")
        val valueColor = XSPUtils.getString("toggle_slider_value_color", "null")

        ReplaceColor("toggle_slider_progress_color",progressColor)
        //ReplaceColor("toggle_slider_icon_color",valueColor)

        ReplaceColor("toggle_slider_top_text_color",valueColor)

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

        val mainBackgroundBlendColor = XSPUtils.getString("background_blend_color_main", "null")
        val secondaryBackgroundBlendColor = XSPUtils.getString("background_blend_color_secondary", "null")

        if (backgroundColor != "null"){

            ReplaceColor("qs_card_disabled_color",backgroundColor)
            ReplaceColor("external_entry_background_color",backgroundColor)
            ReplaceColor("toggle_slider_progress_background_color",backgroundColor)
            ReplaceColor("qs_disabled_color",backgroundColor)


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

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        startCardTitleHook()
        startCardIconHook()
        startMediaColorsHook()
        startToggleSliderIconColorHook()
        startListIconColor()

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

        XposedHelpers.findAndHookConstructor(MediaPlayerViewHolder,View::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val itemView = XposedHelpers.getObjectField(thisObj,"itemView") as View
                if (deviceIconColor != null){
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

        XposedHelpers.findAndHookMethod(QSCardItemView,"updateBackground",object : XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                val thisObj = param?.thisObject  as LinearLayout

                val Companion = XposedHelpers.getStaticObjectField(QSItemView,"Companion")
                val sta = XposedHelpers.getObjectField(thisObj,"state")

                val states = XposedHelpers.callMethod(Companion,"isRestrictedCompat",sta) as Boolean

                val state = XposedHelpers.getObjectField(sta,"state")
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