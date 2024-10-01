package com.chaos.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QSListColor :BaseHooker() {

    val disableColor = XSPUtils.getString("list_disabled_color", "null")
    val enableColor = XSPUtils.getString("list_enabled_color", "null")
    val restrictedColor = XSPUtils.getString("list_restricted_color", "null")
    val warningColor = XSPUtils.getString("list_warning_color", "null")
    val unavailableColor = XSPUtils.getString("list_unavailable_color", "null")

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        val QSTileItemIconView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView", classLoader)

        XposedHelpers.findAndHookConstructor(QSTileItemIconView,Context::class.java,Context::class.java,AttributeSet::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val pluginContext: Context =
                    XposedHelpers.getObjectField(param?.thisObject, "pluginContext") as Context;
                //val enableColor = pluginContext.resources.getIdentifier("qs_enabled_color","color","miui.systemui.plugin")

                val warning: Int = pluginContext.resources
                    .getIdentifier("qs_background_warning", "drawable", "miui.systemui.plugin");
                val enabled: Int = pluginContext.resources
                    .getIdentifier("qs_background_enabled", "drawable", "miui.systemui.plugin");
                val restricted: Int = pluginContext.resources.getIdentifier(
                    "qs_background_restricted",
                    "drawable",
                    "miui.systemui.plugin"
                );
                val disabled: Int = pluginContext.getResources().getIdentifier(
                    "qs_background_disabled",
                    "drawable",
                    "miui.systemui.plugin"
                );
                val unavailable: Int = pluginContext.getResources().getIdentifier(
                    "qs_background_unavailable",
                    "drawable",
                    "miui.systemui.plugin"
                );
                val warningD: Drawable = pluginContext.getTheme().getDrawable(warning);
                val enabledD: Drawable = pluginContext.getTheme().getDrawable(enabled);
                val restrictedD: Drawable = pluginContext.getTheme().getDrawable(restricted);
                val disabledD: Drawable = pluginContext.getTheme().getDrawable(disabled);
                val unavailableD: Drawable = pluginContext.getTheme().getDrawable(unavailable);

                if (warningD is GradientDrawable) {
                    if (warningColor != "null"){
                        warningD.setColor(Color.parseColor(warningColor))
                    }

                }
                if (enabledD is GradientDrawable) {

                    if (enableColor != "null"){
                        enabledD.setColor(Color.parseColor(enableColor))
                    }
                }
                if (restrictedD is GradientDrawable) {
                    if (restrictedColor != "null"){
                        restrictedD.setColor(Color.parseColor(restrictedColor))
                    }

                }
                if (disabledD is GradientDrawable) {
                    if (disableColor != "null"){
                        disabledD.setColor(Color.parseColor(disableColor))
                    }

                }
                if (unavailableD is GradientDrawable) {
                    if (unavailableColor != "null"){
                        unavailableD.setColor(Color.parseColor(unavailableColor))
                    }

                }

            }
        })

    }

}