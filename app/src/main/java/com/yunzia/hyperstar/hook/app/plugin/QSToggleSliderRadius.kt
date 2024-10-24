package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.Resources
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


class QSToggleSliderRadius : BaseHooker() {

    val progressRadius = XSPUtils.getFloat("qs_progress_radius",2f)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (!XSPUtils.getBoolean("is_change_qs_progress_radius",false)) return

        startMethodsHook()
    }

    private fun startMethodsHook() {
        val ToggleSliderViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.recyclerview.ToggleSliderViewHolder",classLoader)

        XposedHelpers.findAndHookMethod(ToggleSliderViewHolder,"updateSize",object : XC_MethodHook(){


            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mContext = XposedHelpers.callMethod(thisObj,"getResources") as Resources

                XposedHelpers.setFloatField(thisObj,"progressRadius",dpToPx(mContext,progressRadius))



            }
        })


    }

    fun dpToPx(resources: Resources, dp: Float): Float {
        // 获取屏幕的密度
        val density = resources.displayMetrics.density

        // 转换 dp 到 px
        return dp * density
    }


}