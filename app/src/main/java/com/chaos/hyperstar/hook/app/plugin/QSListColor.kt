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

                if (warningColor != "null"){

                    val warning: Int = pluginContext.resources
                        .getIdentifier("qs_background_warning", "drawable", "miui.systemui.plugin");

                    val warningD: Drawable = pluginContext.getTheme().getDrawable(warning);

                    if (warningD is GradientDrawable) {
                        warningD.setColor(Color.parseColor(warningColor))
                    }

                }
                if (enableColor != "null"){

                    val enabled: Int = pluginContext.resources
                        .getIdentifier("qs_background_enabled", "drawable", "miui.systemui.plugin");
                    val enabledD: Drawable = pluginContext.getTheme().getDrawable(enabled);

                    if (enabledD is GradientDrawable) {

                        enabledD.setColor(Color.parseColor(enableColor))
                    }
                }
                if (restrictedColor != "null"){

                    val restricted: Int = pluginContext.resources.getIdentifier("qs_background_restricted", "drawable", "miui.systemui.plugin");
                    val restrictedD: Drawable = pluginContext.getTheme().getDrawable(restricted);
                    if (restrictedD is GradientDrawable) {
                        restrictedD.setColor(Color.parseColor(restrictedColor))
                    }

                }

                if (disableColor != "null"){

                    val disabled: Int = pluginContext.getResources().getIdentifier(
                        "qs_background_disabled",
                        "drawable",
                        "miui.systemui.plugin"
                    );
                    val disabledD: Drawable = pluginContext.getTheme().getDrawable(disabled);
                    if (disabledD is GradientDrawable) {
                        disabledD.setColor(Color.parseColor(disableColor))
                    }

                }
                if (unavailableColor != "null"){

                    val unavailable: Int = pluginContext.getResources().getIdentifier(
                        "qs_background_unavailable",
                        "drawable",
                        "miui.systemui.plugin"
                    );
                    val unavailableD: Drawable = pluginContext.getTheme().getDrawable(unavailable);
                    if (unavailableD is GradientDrawable) {

                        unavailableD.setColor(Color.parseColor(unavailableColor))
                    }

                }

            }
        })

    }

}