package com.yunzia.hyperstar.hook.app.plugin.powermenu


import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.view.View
import android.view.animation.Interpolator
import android.widget.FrameLayout
import com.yunzia.hyperstar.hook.app.plugin.powermenu.action.Action
import com.yunzia.hyperstar.hook.app.plugin.powermenu.base.MenuItem
import com.yunzia.hyperstar.hook.app.plugin.powermenu.menu.menuB
import com.yunzia.hyperstar.hook.app.plugin.powermenu.menu.menuA
import com.yunzia.hyperstar.hook.core.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.prefs.XSPUtils


object PowerMenuHook : BasePluginHook() {

    val isPowerMenuNavShow = XSPUtils.getBoolean("is_power_menu_nav_show",false)
    val isPowerMenuStyle = XSPUtils.getInt("is_power_menu_style",0)

    override fun init() {
        val MiuiGlobalActionsDialog = findClass("com.android.systemui.miui.globalactions.MiuiGlobalActionsDialog",pluginClassLoader)
        val VolumeUtil = findClass("com.android.systemui.miui.volume.VolumeUtil",pluginClassLoader)

        if (isPowerMenuNavShow) {
            MiuiGlobalActionsDialog.afterHookMethod(
                "initViews"
            ) {
                val flags = (
                        View.SYSTEM_UI_FLAG_VISIBLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )
                this.getObjectFieldAs<FrameLayout>("mRoot").systemUiVisibility = flags

            }
        }


        if (isPowerMenuStyle == 0) return
        var group: View? = null
        MiuiGlobalActionsDialog.apply {
            afterHookMethod(
                "initViews"
            ) {
                val mContext = this.getObjectFieldAs<Context>("mContext")
                val res = mContext.resources
                val mTalkbackLayout = this.getObjectFieldAs<FrameLayout>("mTalkbackLayout")
                val mSliderView = this.getObjectFieldAs<FrameLayout>("mSliderView")

                val s = mSliderView.layoutParams as FrameLayout.LayoutParams
                //mSliderView.translationX = 250f
                val action = Action(mContext, VolumeUtil!!)
                when(isPowerMenuStyle){
                    1->{
                        val items1: List<MenuItem?> = listOf(
                            action.getAction("recovery"),
                            action.getAction("bootloader"),
                        )
                        group = menuB(
                            mContext, this, items1, mTalkbackLayout, mSliderView
                        )
                    }
                    2->{
                        val items: MutableList<MenuItem?> = (0..7)
                            .map { i -> XSPUtils.getString("power_menu_style_b_$i", "null").toString() }
                            .filter { it != "null" }
                            .map { action.getAction(it) }
                            .toMutableList()

                        group = menuA(
                            mContext, this, items.toList(), mTalkbackLayout, mSliderView
                        )
                    }
                }
            }
            afterHookMethod(
                "dismiss",
                Int::class.java
            ){
                group?.apply {
                    val mSliderView = this@afterHookMethod.getObjectFieldAs<FrameLayout>("mSliderView")
                    visibility = View.GONE
                    mSliderView.removeView(group)

                }
            }
            afterHookMethod("sliderViewDismiss") {
                this.getObjectField("mDialog")?.apply {
                    if (this.callMethodAs<Boolean>("isShowing")){
                        group = null
                    }
                }

            }
        }
        findClass(
            "com.android.systemui.miui.globalactions.SliderView",
            pluginClassLoader
        ).afterHookMethod(
            "handleActionMoveForAlpha",
            Float::class.java
        ) {
            group?.apply {
                val mDark = this@afterHookMethod.getObjectFieldAs<View>("mDark")
                alpha = (1 - mDark.alpha)

            }
        }

    }


    fun View.alphaAnimator(z: Boolean) {
        val i: Int
        var f = 0.0f
        var f2 = 1.0f
        if (z) {
            i = 300
        } else {
            i = 200
            f2 = 0.0f
            f = 1.0f
        }
        val animatorSet = AnimatorSet()
        animatorSet.playTogether(this.createAlphaAnimator(f, f2, i))
        animatorSet.start()
    }

    private fun View.createAlphaAnimator(f: Float, f2: Float, i: Int): ObjectAnimator {
        val ofFloat = ObjectAnimator.ofFloat(this, "alpha", f, f2)
        ofFloat.setDuration(i.toLong())
        ofFloat.interpolator = QuadraticEaseOutInterpolator()
        return ofFloat
    }


    class QuadraticEaseOutInterpolator : Interpolator {
        public override fun getInterpolation(f: Float): Float {
            return (-f) * (f - 2.0f)
        }
    }



}

