package com.yunzia.hyperstar.hook.app.plugin.os1

import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.dimenReplaceById
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.hookLayout
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.android.util.ViewUtil.findViewByIdName

object QSMiplayAppIconRadius: BasePluginHook() {

    private val disableAppIconRadius = XSPUtils.getBoolean("qs_detail_app_icon_radius",false)
    private val qsDetailProgressBgRadius = XSPUtils.getBoolean("qs_detail_progress_bg_radius",false)
    override fun init() {
        if (disableAppIconRadius) {
            hookLayout("qs_control_detail_header_metainfo_layout",plugin){
                this as ViewGroup
                val icon = findViewByIdName("app_icon")
                val card = icon?.parent
                //log("$card")
                card.callMethod("setCardBackgroundColor",Color.TRANSPARENT)
                card.callMethod("setRadius",0f)
            }
            try {
                hookLayout("qs_control_detail_header_metainfo_layout_support_land", plugin){
                    this as ViewGroup
                    val icon = findViewByIdName("app_icon")
                    val card = icon?.parent
                    card.callMethod("setCardBackgroundColor",Color.TRANSPARENT)
                    card.callMethod("setRadius",0f)

                }

            }catch (e: Exception) {
                Log.d("ggc", "${e.message}")
            }


            //resparam?.res?.getIdentifier("qs_control_detail_header_metainfo_layout_support_land","layout",plugin)


        }

        if (qsDetailProgressBgRadius){
            dimenReplaceById(plugin, "miplay_seekbar_progress_bg_corner_radiu", R.dimen.miplay_seekbar_progress_bg_corner_radiu)
        }

    }

//    override fun initResources(
//        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
//        modRes: XModuleResources?
//    ) {
//
//
//
//
//    }
}