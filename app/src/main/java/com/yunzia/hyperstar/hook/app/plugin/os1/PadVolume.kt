package com.yunzia.hyperstar.hook.app.plugin.os1

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
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
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.helper.afterHookConstructor
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelSize
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.dimenReplaceById
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.dimenReplaceByValue
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.hookLayout
import com.yunzia.hyperstar.hook.core.helper.ResourcesHelper.integerReplaceById
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.setObjectField
import com.yunzia.hyperstar.prefs.XSPUtils
import io.github.kyuubiran.ezxhelper.android.util.ViewUtil.findViewByIdName
import kotlin.math.roundToInt
import androidx.core.graphics.withSave

object PadVolume : BasePluginHook() {

    private val padVolume = XSPUtils.getBoolean("is_use_pad_volume", false)

    override fun init() {

        if (!padVolume){
            return
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeTimerDrawableHelper",
            pluginClassLoader
        ).beforeHookMethod(
            "updateDrawables"
        ){
            this.setObjectField( "mIsVerticalSeekBar", false)
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVerticalVolumeTimerSeekBar",
            pluginClassLoader
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
                canvas.withSave {
                    progressDrawable.draw(this)
                }
                return@replaceHookMethod null
            }
        }

        integerReplaceById(
            plugin,
            "expand_silent_dnd_orientation",
            R.integer.expand_silent_dnd_orientation
        )
        integerReplaceById(
            plugin,
            "miui_volume_ringer_gravity",
            R.integer.miui_volume_ringer_gravity
        )
        integerReplaceById(
            plugin,
            "miui_volume_dialog_gravity_expanded",
            R.integer.miui_volume_dialog_gravity_expanded
        )
        integerReplaceById(
            plugin,
            "miui_volume_dialog_gravity_collapsed",
            R.integer.miui_volume_dialog_gravity_collapsed
        )
        integerReplaceById(
            plugin,
            "miui_volume_layout_orientation_expanded",
            R.integer.miui_volume_layout_orientation_expanded
        )
        integerReplaceById(
            plugin,
            "miui_volume_dialog_large_display_orientation",
            R.integer.miui_volume_dialog_large_display_orientation
        )
        integerReplaceById(
            plugin,
            "miui_volume_ringer_layout_orientation_expand",
            R.integer.miui_volume_ringer_layout_orientation_expand
        )
        dimenReplaceById(
            "miui_volume_background_height_t",
            plugin,
            R.dimen.miui_volume_background_height_t
        )
        dimenReplaceById(
            plugin,
            "miui_volume_background_margin_top_t",
            R.dimen.miui_volume_background_margin_top_t
        )
        dimenReplaceById(
            plugin,
            "miui_volume_background_padding_t",
            R.dimen.miui_volume_background_padding_t
        )
        dimenReplaceById(
            plugin,
            "miui_volume_background_padding_t_4stream",
            R.dimen.miui_volume_background_padding_t_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_background_width_t",
            R.dimen.miui_volume_background_width_t
        )
        dimenReplaceById(
            plugin,
            "miui_volume_background_width_t_4stream",
            R.dimen.miui_volume_background_width_t_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_bg_radius",
            R.dimen.miui_volume_bg_radius
        )
        dimenReplaceById(
            plugin,
            "miui_volume_bg_radius_expanded",
            R.dimen.miui_volume_bg_radius_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_blur_bg_radius",
            R.dimen.miui_volume_blur_bg_radius
        )
        dimenReplaceById(
            plugin,
            "miui_volume_button_size",
            R.dimen.miui_volume_button_size
        )
        dimenReplaceById(
            plugin,
            "miui_volume_column_height",
            R.dimen.miui_volume_column_height
        )
        dimenReplaceById(
            plugin,
            "miui_volume_column_height_expanded",
            R.dimen.miui_volume_column_height_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_column_margin_horizontal_expanded",
            R.dimen.miui_volume_column_margin_horizontal_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_column_margin_horizontal_expanded_t",
            R.dimen.miui_volume_column_margin_horizontal_expanded_t
        )
        dimenReplaceById(
            plugin,
            "miui_volume_column_margin_horizontal_expanded_t_4stream",
            R.dimen.miui_volume_column_margin_horizontal_expanded_t_4stream
        )

        dimenReplaceById(
            plugin,
            "miui_volume_column_width_expanded",
            R.dimen.miui_volume_column_width_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_column_width_expanded_4stream",
            R.dimen.miui_volume_column_width_expanded_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_content_width_expanded",
            R.dimen.miui_volume_content_width_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_content_width_expanded_4stream",
            R.dimen.miui_volume_content_width_expanded_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_countdown_progressbar_height",
            R.dimen.miui_volume_countdown_progressbar_height
        )
        dimenReplaceById(
            plugin,
            "miui_volume_countdown_progressbar_width",
            R.dimen.miui_volume_countdown_progressbar_width
        )
        dimenReplaceById(
            plugin,
            "miui_volume_countdown_radius",
            R.dimen.miui_volume_countdown_radius
        )
        dimenReplaceById(
            plugin,
            "miui_volume_dialog_shadow_margin_top",
            R.dimen.miui_volume_dialog_shadow_margin_top
        )
        dimenReplaceById(
            plugin,
            "miui_volume_footer_margin_left_expanded",
            R.dimen.miui_volume_footer_margin_left_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_footer_margin_top",
            R.dimen.miui_volume_footer_margin_top
        )
        dimenReplaceById(
            plugin,
            "miui_volume_footer_margin_top_expanded",
            R.dimen.miui_volume_footer_margin_top_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_margin_left_expanded",
            R.dimen.miui_volume_margin_left_expanded
        )

        dimenReplaceById(
            plugin,
            "miui_volume_margin_top_expanded",
            R.dimen.miui_volume_margin_top_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_offset_end",
            R.dimen.miui_volume_offset_end
        )
        dimenReplaceById(
            plugin,
            "miui_volume_offset_end_expanded",
            R.dimen.miui_volume_offset_end_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_offset_top_collapsed",
            R.dimen.miui_volume_offset_top_collapsed
        )
        dimenReplaceById(
            plugin,
            "miui_volume_offset_top_expand",
            R.dimen.miui_volume_offset_top_expand
        )

        dimenReplaceById(
            plugin,
            "miui_volume_ringer_btn_height",
            R.dimen.miui_volume_ringer_btn_height
        )
        dimenReplaceById(
            plugin,
            "miui_volume_ringer_btn_layout_width",
            R.dimen.miui_volume_ringer_btn_layout_width
        )
        dimenReplaceById(
            plugin,
            "miui_volume_ringer_btn_width",
            R.dimen.miui_volume_ringer_btn_width
        )
        dimenReplaceById(
            plugin,
            "miui_volume_ringer_divider_height",
            R.dimen.miui_volume_ringer_divider_height
        )

        dimenReplaceById(
            plugin,
            "miui_volume_ringer_layout_width_expanded",
            R.dimen.miui_volume_ringer_layout_width_expanded
        )
        dimenReplaceById(
            plugin,
            "miui_volume_ringer_layout_width_expanded_4stream",
            R.dimen.miui_volume_ringer_layout_width_expanded_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_seekbar_icon_margin_bottom",
            R.dimen.miui_volume_seekbar_icon_margin_bottom
        )
        dimenReplaceById(
            plugin,
            "miui_volume_seekbar_icon_margin_bottom_expand",
            R.dimen.miui_volume_seekbar_icon_margin_bottom_expand
        )
        dimenReplaceById(
            plugin,
            "miui_volume_segment_indicator_height",
            R.dimen.miui_volume_segment_indicator_height
        )
        dimenReplaceById(
            plugin,
            "miui_volume_segment_indicator_width",
            R.dimen.miui_volume_segment_indicator_width
        )
        dimenReplaceById(
            plugin,
            "miui_volume_silence_button_height",
            R.dimen.miui_volume_silence_button_height
        )
        dimenReplaceById(
            plugin,
            "miui_volume_silence_button_width",
            R.dimen.miui_volume_silence_button_width
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_corner_radius",
            R.dimen.miui_volume_timer_corner_radius
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_margin_bottom",
            R.dimen.miui_volume_timer_margin_bottom
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_margin_left",
            R.dimen.miui_volume_timer_margin_left
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_margin_left_4stream",
            R.dimen.miui_volume_timer_margin_left_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_seekbar_height",
            R.dimen.miui_volume_timer_seekbar_height
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_seekbar_width",
            R.dimen.miui_volume_timer_seekbar_width
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_seekbar_width_4stream",
            R.dimen.miui_volume_timer_seekbar_width_4stream
        )
        dimenReplaceById(
            plugin,
            "miui_volume_timer_time_text_size",
            R.dimen.miui_volume_timer_time_text_size
        )

        hookLayout("miui_volume_timer_layout", plugin) {
            this as ViewGroup
            if (resources.configuration?.orientation != Configuration.ORIENTATION_LANDSCAPE) return@hookLayout
            val timerSeekbar = this.findViewByIdName("timer_seekbar") as SeekBar
            timerSeekbar.maxHeight = getDimensionPixelSize(resources,"miui_volume_timer_seekbar_height", plugin)
            val timerSeekbarlp = timerSeekbar.layoutParams
            if (timerSeekbarlp is ViewGroup.MarginLayoutParams) {
                val marginParams = timerSeekbarlp
                marginParams.topMargin = getDimensionPixelSize(resources,"miui_volume_timer_margin", plugin)
                marginParams.bottomMargin = getDimensionPixelSize(resources,"miui_volume_timer_margin", plugin)
                timerSeekbar.layoutParams = marginParams
            }
            val volumeTimerView = this.findViewByIdName("volume_timer_view") as FrameLayout
            val volumeTimerViewlp = volumeTimerView.layoutParams
            if (volumeTimerViewlp is ViewGroup.MarginLayoutParams) {
                val marginParams = volumeTimerViewlp
                marginParams.topMargin = getDimensionPixelSize(resources,"miui_volume_timer_margin", plugin)
                marginParams.bottomMargin = getDimensionPixelSize(resources,"miui_volume_timer_margin", plugin)
                volumeTimerView.layoutParams = marginParams
            }
            val timeAbove = getChildAt(2) as TextView
            removeViewAt(2)
            timeAbove.id = resources.getIdentifier("ticking_time_above_progress_view","id",plugin)
            //val timeAbovelp = timeAbove.layoutParams
            val timeAbovelp = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            timeAbovelp.gravity = Gravity.CENTER_VERTICAL
            volumeTimerView.addView(timeAbove,timeAbovelp)
            val timerBg = findViewByIdName("timer_bg") as FrameLayout

            val timerBglp = timerBg.layoutParams
            if (timerBglp is ViewGroup.MarginLayoutParams) {
                val marginParams = timerBglp
                marginParams.topMargin = getDimensionPixelSize(resources,"miui_volume_timer_popup_text_margin_top", plugin)
                marginParams.leftMargin = 0
                timerBg.layoutParams = marginParams
            }
            val timerText = timerBg.findViewByIdName("timer_text") as TextView
            timerText.setPadding(
                dpToPx(resources, 9.089997f),
                dpToPx(resources, 6.539979f),
                dpToPx(resources, 9.089997f),
                0
            )
        }
        hookLayout("miui_ringer_mode_layout",plugin) {
            this as ViewGroup
            val res = context.resources
            if (res.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
                if (childCount != 2) return@hookLayout
                val timer = getChildAt(0)
                val bgBlur = getChildAt(1)
                if (timer !is FrameLayout) return@hookLayout
                timer.visibility = View.GONE
                removeAllViews()
                addView(bgBlur,0)
                addView(timer,1)

            }
        }
        hookLayout("miui_volume_dialog_ringer_mode",plugin) {
            this as ViewGroup
            val res = context.resources
            if (res.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE){
                val text = findViewByIdName("timer_text_landscape")
                if (text !is TextView) return@hookLayout
                removeViewInLayout(text)
            }
        }

    }

    private fun dpToPx(resources: Resources, dp: Float): Int {
        // 获取屏幕的密度
        val density = resources.displayMetrics.density

        // 转换 dp 到 px
        return (dp * density).roundToInt()
    }
}

