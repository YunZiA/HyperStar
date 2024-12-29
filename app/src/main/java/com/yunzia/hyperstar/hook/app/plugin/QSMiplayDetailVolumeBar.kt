package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.Typeface
import android.util.TypedValue
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers


class QSMiplayDetailVolumeBar:BaseHooker() {

    val isDetailVolumebarShowValue = XSPUtils.getBoolean("is_detail_volumebar_show_value",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

        if (!isDetailVolumebarShowValue) return

        starMethodHook()
    }

    private fun starMethodHook() {
        val QSControlMiPlayDetailHeader = findClass("com.android.systemui.QSControlMiPlayDetailHeader",classLoader)

        XposedHelpers.findAndHookMethod(QSControlMiPlayDetailHeader,"initUI", object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val volumeBarContainer = XposedHelpers.getObjectField(thisObj,"volumeBarContainer") as RelativeLayout
                val context = XposedHelpers.callMethod(thisObj,"getContext") as Context
                val res = context.resources

                val value = TextView(context).apply {
                    text = "null%"
                    typeface = Typeface.DEFAULT_BOLD;
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP,12.5f)
                    setTextColor(getColor(res,"miplay_detail_volume_icon_color",plugin))
                }

                val lp = RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT).apply {
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    marginEnd = getDimension(res,"miplay_detail_header_volume_bar_icon_margin_start",plugin).toInt()
                }
                volumeBarContainer.addView(value,lp)

            }
        })

        XposedBridge.hookAllMethods(QSControlMiPlayDetailHeader,"addObservers\$lambda-29",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val qSControlMiPlayDetailHeader = param?.args?.get(0)
                val num = param?.args?.get(1) as Int
                val volumeBarContainer = XposedHelpers.getObjectField(qSControlMiPlayDetailHeader,"volumeBarContainer") as RelativeLayout
                if (volumeBarContainer.childCount < 3) return
                val seekBar = XposedHelpers.getObjectField(qSControlMiPlayDetailHeader,"volumeBar") as SeekBar
                val max = seekBar.max
                val progress = seekBar.progress

                val value = volumeBarContainer.getChildAt(2) as TextView
                value.text = "${100*progress/max}%"
            }
        })

    }

}