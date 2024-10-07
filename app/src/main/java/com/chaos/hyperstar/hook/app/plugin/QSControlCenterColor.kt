package com.chaos.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import android.content.res.XResources
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
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
        startMediaColorHook()
        startListColorHook()



    }

    private fun startMediaColorHook() {

//        ReplaceColor("media_player_title_text_color","#ff000000")
//        ReplaceColor("media_player_artist_text_color","#ff000000")
//        ReplaceColor("media_player_empty_state_text_color","#ff000000")
//        ReplaceColor("miplay_detail_header_action","#ff000000")
//        ReplaceColor("miplay_detail_header_action_disabled","#ff000000")
//        resparam.res.setReplacement(plugin,"drawable","ic_media_device_default",object : XResources.DrawableLoader(){
//            override fun newDrawable(res: XResources?, id: Int): Drawable {
//
//                val d = res?.getDrawable(id,res.newTheme())
//                d?.colorFilter = PorterDuffColorFilter(Color.parseColor("ff000000"), PorterDuff.Mode.SRC_ATOP);
//
////                if (d is VectorDrawable){
////                    d.alpha = 255
////                    d.setTint(Color.parseColor("#ffffffff"))
////                }
//                return d!!
//
//            }
//
//        })

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
        startListIconColor()

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