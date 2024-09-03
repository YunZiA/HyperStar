package com.chaos.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import com.chaos.hyperstar.MainActivity
import com.chaos.hyperstar.R
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QsTileRadius: BaseHooker() {
    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (XSPUtils.getBoolean("is_qs_tile_radius",false)){
            startMethodsHook(classLoader)
        }

    }
    private fun startMethodsHook(classLoader: ClassLoader?) {

        val radius:Int = XSPUtils.getInt("control_center_universal_corner_radius",70)
        val classTile = XposedHelpers.findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        )
        XposedHelpers.findAndHookMethod(classTile, "setCornerRadius", Float::class.java ,object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam) {

                param.args[0] = radius
            }
            override fun afterHookedMethod(param: MethodHookParam) {
            }
        })

        hookAllMethods(classLoader,"miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            "updateIcon",
            object : MethodHook{

                override fun before(param: XC_MethodHook.MethodHookParam?) {
                    val pluginContext:Context  =
                        XposedHelpers.getObjectField(param?.thisObject, "pluginContext") as Context;
                    val warning:Int = pluginContext.getResources().getIdentifier("qs_background_warning", "drawable", "miui.systemui.plugin");
                    val enabled:Int = pluginContext.getResources().getIdentifier("qs_background_enabled", "drawable", "miui.systemui.plugin");
                    val restricted:Int = pluginContext.getResources().getIdentifier("qs_background_restricted", "drawable", "miui.systemui.plugin");
                    val disabled:Int = pluginContext.getResources().getIdentifier("qs_background_disabled", "drawable", "miui.systemui.plugin");
                    val unavailable:Int = pluginContext.getResources().getIdentifier("qs_background_unavailable", "drawable", "miui.systemui.plugin");
                    val warningD:Drawable  = pluginContext.getTheme().getDrawable(warning);
                    val enabledD:Drawable = pluginContext.getTheme().getDrawable(enabled);
                    val restrictedD:Drawable = pluginContext.getTheme().getDrawable(restricted);
                    val disabledD:Drawable = pluginContext.getTheme().getDrawable(disabled);
                    val unavailableD:Drawable = pluginContext.getTheme().getDrawable(unavailable);
                    if (warningD is GradientDrawable) {
                        warningD.cornerRadius = radius.toFloat()
                    }
                    if (enabledD is GradientDrawable) {
                        enabledD.cornerRadius = radius.toFloat()
                    }
                    if (restrictedD is GradientDrawable) {
                        restrictedD.cornerRadius = radius.toFloat()
                    }
                    if (disabledD is GradientDrawable) {
                        disabledD.cornerRadius = radius.toFloat()
                    }
                    if (unavailableD is GradientDrawable) {
                        unavailableD.cornerRadius = radius.toFloat()
                    }
                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {

                }
            })
//        XposedHelpers.findAndHookMethod(classTile, "getBackgroundDrawable", Float::class.java ,object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam) {
//
//                param.args[0] = XSPUtils.getInt("control_center_universal_corner_radius",70)
//            }
//            override fun afterHookedMethod(param: MethodHookParam) {
//            }
//        })
    }
}