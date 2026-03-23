package com.yunzia.hyperstar.hook.app.plugin.os2

import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.beforeHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getBooleanField
import com.yunzia.hyperstar.hook.core.helper.getFloatField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.setLongField
import com.yunzia.hyperstar.hook.core.helper.setObjectField
import com.yunzia.hyperstar.prefs.XSPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object LongPressVolumeBarToExpand: BasePluginHook() {
    val isPressExpandVolume = XSPUtils.getBoolean("is_press_expand_volume",false)

    override fun init() {
        if (!isPressExpandVolume) return
        var longClick = false
        var longPressJob: Job? = null

        fun View.startScaleAnimation() {
            longClick = true
            this.animate()
                .scaleX(0.92f)
                .scaleY(0.92f)
                .setDuration(300)
                .start()
        }

        fun View.stopScaleAnimation() {
            // 还原到原始大小
            longClick = false
            this.animate()
                .scaleX(1.0f)
                .scaleY(1.0f)
                .setDuration(300)
                .start()
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogView",
            pluginClassLoader
        ).apply {
            afterHookMethod(
                "onFinishInflate"
            ) {  args, result ->
                thisObject.getObjectFieldAs<View>("mExpandButton").apply {
                    setOnClickListener(null)
                    alpha = 0f
                    isClickable = false
                    visibility = View.GONE
                }

            }
            afterHookMethod(
                "notifyAccessibilityChanged",
                Boolean::class.java
            ) { args, result ->
                thisObject.getObjectFieldAs<View>("mExpandButton").apply {
                    setOnClickListener(null)
                    isClickable = false
                    visibility = View.GONE
                }
            }
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogMotion", pluginClassLoader
        ).apply {
            beforeHookMethod("lambda\$processExpandTouch\$1") { args, result ->
                thisObject.setObjectField("mIsExpandButton",true)
            }
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBar",
            pluginClassLoader
        ).apply {
            afterHookMethod(
                "onTouchEvent",
                MotionEvent::class.java
            ) { args, result ->
                val mSeekBarOnclickListener = thisObject.getObjectField("mSeekBarOnclickListener")
                val mSeekBarAnimListener = thisObject.getObjectField("mSeekBarAnimListener")
                val volumePanelViewController = mSeekBarAnimListener.getObjectField("this\$0")
                val mVolumeView = volumePanelViewController.getObjectFieldAs<View>("mVolumeView")
                thisObject.setLongField("mCurrentMS",0L)
                if (mSeekBarOnclickListener != null) {
                    val motionEvent = args?.get(0) as MotionEvent
                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {

                            if ( !volumePanelViewController.getBooleanField("mExpanded")!! ){

                                // 启动长按检测协程
                                longPressJob = CoroutineScope(Dispatchers.Main).launch {
                                    mVolumeView.startScaleAnimation() // 执行缩放动画
                                    delay(300)
                                    val mMoveY = thisObject.getFloatField("mMoveY")!!
                                    if (longClick && mMoveY < 10f){
                                        mVolumeView.apply {
                                            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                                            scaleY = 1f
                                            scaleX = 1f
                                        }
                                        mSeekBarOnclickListener.callMethod( "onClick")
                                    }

                                }
                            }
                        }
                        MotionEvent.ACTION_UP -> {
                            mVolumeView.stopScaleAnimation()
                            longPressJob?.cancel()

                        }
                        MotionEvent.ACTION_CANCEL -> {
                            mVolumeView.stopScaleAnimation()
                            longPressJob?.cancel()
                        }
                    }
                }
            }
        }
    }
}