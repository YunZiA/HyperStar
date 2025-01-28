package com.yunzia.hyperstar.hook.app.plugin

import android.graphics.Typeface
import android.util.TypedValue
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.utils.XSPUtils


class QSMiplayDetailVolumeBar: Hooker() {

    val isDetailVolumebarShowValue = XSPUtils.getBoolean("is_detail_volumebar_show_value",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!isDetailVolumebarShowValue) return

        starMethodHook()
    }

    private fun starMethodHook() {
        val QSControlMiPlayDetailHeader = findClass("com.android.systemui.QSControlMiPlayDetailHeader",classLoader)

        QSControlMiPlayDetailHeader.apply {
            afterHookMethod("initUI"){
                val volumeBarContainer = this.getObjectFieldAs<RelativeLayout>("volumeBarContainer")
                val context = volumeBarContainer.context
                val res = context.resources

                val value = TextView(context).apply {
                    typeface = Typeface.DEFAULT_BOLD;
                    setTextSize(TypedValue.COMPLEX_UNIT_DIP,12.5f)
                    setTextColor(getColor(res,"miplay_detail_volume_icon_color",plugin,"#FF6E747B"))
                }

                val lp = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    addRule(RelativeLayout.CENTER_VERTICAL)
                    addRule(RelativeLayout.ALIGN_PARENT_END)
                    marginEnd = getDimension(res,"miplay_detail_header_volume_bar_icon_margin_start",plugin).toInt()
                }
                volumeBarContainer.addView(value,lp)

            }
            afterHookAllMethods("addObservers\$lambda-29"){
                val qSControlMiPlayDetailHeader = it.args[0]
                val num = it.args[1] as Int
                val volumeBarContainer = qSControlMiPlayDetailHeader.getObjectFieldAs<RelativeLayout>("volumeBarContainer")
                if (volumeBarContainer.childCount < 3) return@afterHookAllMethods
                val seekBar = qSControlMiPlayDetailHeader.getObjectFieldAs<SeekBar>("volumeBar")
                val max = seekBar.max
                val progress = seekBar.progress

                val value = volumeBarContainer.getChildAt(2) as TextView
                value.text = "${100*progress/max}%"

            }
        }

    }

}