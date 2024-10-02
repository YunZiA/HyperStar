package com.chaos.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import android.content.res.XResources
import android.graphics.Color
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources

class QSControlCenterColor :BaseHooker() {




    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        starBackgroundColorHook()
        startCardColorHook()
        startListColorHook()


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

        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {



    }

}