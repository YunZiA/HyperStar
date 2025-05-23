package com.yunzia.hyperstar.hook.app.plugin.os1

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.content.res.XModuleResources
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.SeekBar
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookConstructor
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelSize
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import de.robv.android.xposed.callbacks.XC_LayoutInflated
import kotlin.math.roundToInt

class PadVolume : Hooker() {

    private val padVolume = XSPUtils.getBoolean("is_use_pad_volume", false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!padVolume){
            return
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeTimerDrawableHelper",
            classLoader
        ).beforeHookMethod(
            "updateDrawables"
        ){
            this.setObjectField( "mIsVerticalSeekBar", false)
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVerticalVolumeTimerSeekBar",
            classLoader
        ).apply {
            afterHookConstructor(
                Context::class.java,
                AttributeSet::class.java,
                Int::class.java,
            ){
                this.callMethod( "setLayoutDirection", 0)
                val mInjector = this.getObjectField( "mInjector")
                mInjector.callMethod("setVertical", true)

            }
            replaceHookMethod(
                "transformTouchEvent",
                MotionEvent::class.java
            ){
                return@replaceHookMethod null
            }
            replaceHookMethod(
                "onDraw",
                Canvas::class.java
            ){
                val canvas = it.args[0] as Canvas
                val progressDrawable: Drawable = (
                        callMethod( "getProgressDrawable") ?: return@replaceHookMethod null
                        ) as Drawable
                canvas.save()
                progressDrawable.draw(canvas)
                canvas.restore()
                return@replaceHookMethod null
            }
        }


    }

    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)

        if (!padVolume){
            return
        }

        if (resparam == null || modRes == null) {
            return
        }

        resparam.res.setReplacement(
            plugin,
            "integer",
            "expand_silent_dnd_orientation",
            modRes.fwd(R.integer.expand_silent_dnd_orientation)
        )
        resparam.res.setReplacement(
            plugin,
            "integer",
            "miui_volume_ringer_gravity",
            modRes.fwd(R.integer.miui_volume_ringer_gravity)
        )
        resparam.res.setReplacement(
            plugin,
            "integer",
            "miui_volume_dialog_gravity_expanded",
            modRes.fwd(R.integer.miui_volume_dialog_gravity_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "integer",
            "miui_volume_dialog_gravity_collapsed",
            modRes.fwd(R.integer.miui_volume_dialog_gravity_collapsed)
        )
        resparam.res.setReplacement(
            plugin,
            "integer",
            "miui_volume_layout_orientation_expanded",
            modRes.fwd(R.integer.miui_volume_layout_orientation_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "integer",
            "miui_volume_dialog_large_display_orientation",
            modRes.fwd(R.integer.miui_volume_dialog_large_display_orientation)
        )
        resparam.res.setReplacement(
            plugin,
            "integer",
            "miui_volume_ringer_layout_orientation_expand",
            modRes.fwd(R.integer.miui_volume_ringer_layout_orientation_expand)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_background_height_t",
            modRes.fwd(R.dimen.miui_volume_background_height_t)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_background_margin_top_t",
            modRes.fwd(R.dimen.miui_volume_background_margin_top_t)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_background_padding_t",
            modRes.fwd(R.dimen.miui_volume_background_padding_t)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_background_padding_t_4stream",
            modRes.fwd(R.dimen.miui_volume_background_padding_t_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_background_width_t",
            modRes.fwd(R.dimen.miui_volume_background_width_t)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_background_width_t_4stream",
            modRes.fwd(R.dimen.miui_volume_background_width_t_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_bg_radius",
            modRes.fwd(R.dimen.miui_volume_bg_radius)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_bg_radius_expanded",
            modRes.fwd(R.dimen.miui_volume_bg_radius_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_blur_bg_radius",
            modRes.fwd(R.dimen.miui_volume_blur_bg_radius)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_button_size",
            modRes.fwd(R.dimen.miui_volume_button_size)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_height",
            modRes.fwd(R.dimen.miui_volume_column_height)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_height_expanded",
            modRes.fwd(R.dimen.miui_volume_column_height_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_margin_horizontal_expanded",
            modRes.fwd(R.dimen.miui_volume_column_margin_horizontal_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_margin_horizontal_expanded_t",
            modRes.fwd(R.dimen.miui_volume_column_margin_horizontal_expanded_t)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_margin_horizontal_expanded_t_4stream",
            modRes.fwd(R.dimen.miui_volume_column_margin_horizontal_expanded_t_4stream)
        )

        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_width_expanded",
            modRes.fwd(R.dimen.miui_volume_column_width_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_column_width_expanded_4stream",
            modRes.fwd(R.dimen.miui_volume_column_width_expanded_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_content_width_expanded",
            modRes.fwd(R.dimen.miui_volume_content_width_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_content_width_expanded_4stream",
            modRes.fwd(R.dimen.miui_volume_content_width_expanded_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_countdown_progressbar_height",
            modRes.fwd(R.dimen.miui_volume_countdown_progressbar_height)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_countdown_progressbar_width",
            modRes.fwd(R.dimen.miui_volume_countdown_progressbar_width)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_countdown_radius",
            modRes.fwd(R.dimen.miui_volume_countdown_radius)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_dialog_shadow_margin_top",
            modRes.fwd(R.dimen.miui_volume_dialog_shadow_margin_top)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_footer_margin_left_expanded",
            modRes.fwd(R.dimen.miui_volume_footer_margin_left_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_footer_margin_top",
            modRes.fwd(R.dimen.miui_volume_footer_margin_top)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_footer_margin_top_expanded",
            modRes.fwd(R.dimen.miui_volume_footer_margin_top_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_margin_left_expanded",
            modRes.fwd(R.dimen.miui_volume_margin_left_expanded)
        )

        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_margin_top_expanded",
            modRes.fwd(R.dimen.miui_volume_margin_top_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_offset_end",
            modRes.fwd(R.dimen.miui_volume_offset_end)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_offset_end_expanded",
            modRes.fwd(R.dimen.miui_volume_offset_end_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_offset_top_collapsed",
            modRes.fwd(R.dimen.miui_volume_offset_top_collapsed)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_offset_top_expand",
            modRes.fwd(R.dimen.miui_volume_offset_top_expand)
        )

        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_ringer_btn_height",
            modRes.fwd(R.dimen.miui_volume_ringer_btn_height)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_ringer_btn_layout_width",
            modRes.fwd(R.dimen.miui_volume_ringer_btn_layout_width)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_ringer_btn_width",
            modRes.fwd(R.dimen.miui_volume_ringer_btn_width)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_ringer_divider_height",
            modRes.fwd(R.dimen.miui_volume_ringer_divider_height)
        )

        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_ringer_layout_width_expanded",
            modRes.fwd(R.dimen.miui_volume_ringer_layout_width_expanded)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_ringer_layout_width_expanded_4stream",
            modRes.fwd(R.dimen.miui_volume_ringer_layout_width_expanded_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_seekbar_icon_margin_bottom",
            modRes.fwd(R.dimen.miui_volume_seekbar_icon_margin_bottom)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_seekbar_icon_margin_bottom_expand",
            modRes.fwd(R.dimen.miui_volume_seekbar_icon_margin_bottom_expand)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_segment_indicator_height",
            modRes.fwd(R.dimen.miui_volume_segment_indicator_height)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_segment_indicator_width",
            modRes.fwd(R.dimen.miui_volume_segment_indicator_width)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_silence_button_height",
            modRes.fwd(R.dimen.miui_volume_silence_button_height)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_silence_button_width",
            modRes.fwd(R.dimen.miui_volume_silence_button_width)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_corner_radius",
            modRes.fwd(R.dimen.miui_volume_timer_corner_radius)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_margin_bottom",
            modRes.fwd(R.dimen.miui_volume_timer_margin_bottom)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_margin_left",
            modRes.fwd(R.dimen.miui_volume_timer_margin_left)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_margin_left_4stream",
            modRes.fwd(R.dimen.miui_volume_timer_margin_left_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_seekbar_height",
            modRes.fwd(R.dimen.miui_volume_timer_seekbar_height)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_seekbar_width",
            modRes.fwd(R.dimen.miui_volume_timer_seekbar_width)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_seekbar_width_4stream",
            modRes.fwd(R.dimen.miui_volume_timer_seekbar_width_4stream)
        )
        resparam.res.setReplacement(
            plugin,
            "dimen",
            "miui_volume_timer_time_text_size",
            modRes.fwd(R.dimen.miui_volume_timer_time_text_size)
        )


        resparam.res.hookLayout(plugin,"layout","miui_volume_timer_layout",object : XC_LayoutInflated() {
            @SuppressLint("DiscouragedApi")
            override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                val root = liparam?.view as ViewGroup
                //val mContext = root.context

                val res = root.resources
                if (res.configuration?.orientation != Configuration.ORIENTATION_LANDSCAPE){
                    return
                }

                val timerSeekbar = root.findViewByIdName("timer_seekbar") as SeekBar
                timerSeekbar.maxHeight = getDimensionPixelSize(res,"miui_volume_timer_seekbar_height", plugin)
                val timerSeekbarlp = timerSeekbar.layoutParams
                if (timerSeekbarlp is ViewGroup.MarginLayoutParams) {
                    val marginParams = timerSeekbarlp
                    marginParams.topMargin = getDimensionPixelSize(res,"miui_volume_timer_margin", plugin)
                    marginParams.bottomMargin = getDimensionPixelSize(res,"miui_volume_timer_margin", plugin)
                    timerSeekbar.layoutParams = marginParams
                }

                val volumeTimerView = root.findViewByIdName("volume_timer_view") as FrameLayout

                val volumeTimerViewlp = volumeTimerView.layoutParams
                if (volumeTimerViewlp is ViewGroup.MarginLayoutParams) {
                    val marginParams = volumeTimerViewlp
                    marginParams.topMargin = getDimensionPixelSize(res,"miui_volume_timer_margin", plugin)
                    marginParams.bottomMargin = getDimensionPixelSize(res,"miui_volume_timer_margin", plugin)

                    volumeTimerView.layoutParams = marginParams
                }
                val timeAbove = root.getChildAt(2) as TextView
                root.removeViewAt(2)
                timeAbove.id = res.getIdentifier("ticking_time_above_progress_view","id",plugin)
                //val timeAbovelp = timeAbove.layoutParams
                val timeAbovelp = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                timeAbovelp.gravity = Gravity.CENTER_VERTICAL

                volumeTimerView.addView(timeAbove,timeAbovelp)

                val timerBg = root.findViewByIdName("timer_bg") as FrameLayout

                val timerBglp = timerBg.layoutParams
                if (timerBglp is ViewGroup.MarginLayoutParams) {
                    val marginParams = timerBglp
                    marginParams.topMargin = getDimensionPixelSize(res,"miui_volume_timer_popup_text_margin_top", plugin)

                    marginParams.leftMargin = 0

                    timerBg.layoutParams = marginParams
                }

                val timerText = timerBg.findViewByIdName("timer_text") as TextView

                timerText.setPadding(
                    dpToPx(res, 9.089997f),
                    dpToPx(res, 6.539979f),
                    dpToPx(res, 9.089997f),0)


            }
        })


        resparam.res.hookLayout(plugin,"layout","miui_ringer_mode_layout",object : XC_LayoutInflated(){
            override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                val root = liparam?.view as ViewGroup
                val res = root.context.resources
                if (res.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
                    if (root.childCount != 2){
                        return
                    }
                    val timer = root.getChildAt(0)
                    val bgBlur = root.getChildAt(1)
                    if (timer !is FrameLayout){
                        return
                    }

                    timer.visibility = View.GONE
                    root.removeAllViews()
                    root.addView(bgBlur,0)
                    root.addView(timer,1)

                }
            }
        })

        resparam.res.hookLayout(plugin,"layout","miui_volume_dialog_ringer_mode",object : XC_LayoutInflated(){
            override fun handleLayoutInflated(liparam: LayoutInflatedParam?) {
                val root = liparam?.view as ViewGroup
                val res = root.context.resources
                if (res.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){

                    val text = root.findViewByIdName("timer_text_landscape")

                    if (text !is TextView){
                        return
                    }

                    root.removeViewInLayout(text)

                }
            }
        })


    }

    private fun dpToPx(resources: Resources, dp: Float): Int {
        // 获取屏幕的密度
        val density = resources.displayMetrics.density

        // 转换 dp 到 px
        return (dp * density).roundToInt()
    }
}

