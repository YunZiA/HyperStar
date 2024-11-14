package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.XModuleResources
import android.graphics.Color
import android.view.ViewGroup
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated

class QSMiplayAppIconRadius: BaseHooker() {

    private val disableAppIconRadius = XSPUtils.getBoolean("qs_detail_app_icon_radius",false)
    private val qsDetailProgressBgRadius = XSPUtils.getBoolean("qs_detail_progress_bg_radius",false)

    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {

        if (disableAppIconRadius) {

            resparam?.res?.hookLayout(plugin,"layout","qs_control_detail_header_metainfo_layout",object : XC_LayoutInflated(){
                override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                    val root = liparam?.view as ViewGroup
                    val icon = root.findViewByIdName("app_icon")
                    val card = icon?.parent
                    //starLog.log("$card")
                    XposedHelpers.callMethod(card,"setCardBackgroundColor",Color.TRANSPARENT)
                    XposedHelpers.callMethod(card,"setRadius",0f)


                }

            })
        }

        if (qsDetailProgressBgRadius){

            resparam?.res?.setReplacement(plugin,"dimen","miplay_seekbar_progress_bg_corner_radiu", modRes?.fwd(R.dimen.miplay_seekbar_progress_bg_corner_radiu))
        }




    }
}