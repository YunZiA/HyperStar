package com.yunzia.hyperstar.hook.app.plugin.os2

import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.utils.XSPUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LongPressVolumeBarToExpand: Hooker() {
    val isPressExpandVolume = XSPUtils.getBoolean("is_press_expand_volume",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

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
            classLoader
        ).apply {
            afterHookMethod(
                "onFinishInflate"
            ) {
                this.getObjectFieldAs<View>("mExpandButton").apply {
                    setOnClickListener(null)
                    alpha = 0f
                    isClickable = false
                    visibility = View.GONE
                }

            }
            afterHookMethod(
                "notifyAccessibilityChanged",
                Boolean::class.java
            ) {
                this.getObjectFieldAs<View>("mExpandButton").apply {
                    setOnClickListener(null)
                    isClickable = false
                    visibility = View.GONE
                }

            }
        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeDialogMotion", classLoader
        ).apply {
            beforeHookMethod("lambda\$processExpandTouch\$1") {
                this.setObjectField("mIsExpandButton",true)
            }

        }

        findClass(
            "com.android.systemui.miui.volume.MiuiVolumeSeekBar",
            classLoader
        ).apply {
            afterHookMethod(
                "onTouchEvent",
                MotionEvent::class.java
            ) {
                val mSeekBarOnclickListener = this.getObjectField("mSeekBarOnclickListener")
                val mSeekBarAnimListener = this.getObjectField("mSeekBarAnimListener")
                val volumePanelViewController = mSeekBarAnimListener.getObjectField("this\$0")
                val mVolumeView = volumePanelViewController.getObjectFieldAs<View>("mVolumeView")


                this.setLongField("mCurrentMS",0L)
                if (mSeekBarOnclickListener != null) {
                    val motionEvent = it.args?.get(0) as MotionEvent


                    when (motionEvent.action) {
                        MotionEvent.ACTION_DOWN -> {

                            if ( !volumePanelViewController.getBooleanField("mExpanded") ){

                                // 启动长按检测协程
                                longPressJob = CoroutineScope(Dispatchers.Main).launch {
                                    mVolumeView.startScaleAnimation() // 执行缩放动画
                                    delay(300)
                                    val mMoveY = this@afterHookMethod.getFloatField("mMoveY")

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