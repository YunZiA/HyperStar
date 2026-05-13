package com.yunzia.hyperstar.hook.app.plugin.powermenu

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.app.plugin.powermenu.action.Action
import com.yunzia.hyperstar.hook.app.plugin.powermenu.action.Action.bootloader
import com.yunzia.hyperstar.hook.app.plugin.powermenu.action.Action.recovery
import com.yunzia.hyperstar.hook.app.plugin.powermenu.menu.menuA
import com.yunzia.hyperstar.hook.app.plugin.powermenu.menu.menuB
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.prefs.XSPUtils

object PowerMenuHook : BasePluginHook() {

    private val isPowerMenuNavShow = XSPUtils.getBoolean("is_power_menu_nav_show", false)
    private val isPowerMenuStyle = XSPUtils.getInt("is_power_menu_style", 0)

    override fun init() {
        val dialog = findClass(
            "com.android.systemui.miui.globalactions.MiuiGlobalActionsDialog",
            pluginClassLoader
        )
        val volumeUtil = findClass(
            "com.android.systemui.miui.volume.VolumeUtil",
            pluginClassLoader
        )

        if (isPowerMenuNavShow) initNavBarHook(dialog)
        if (isPowerMenuStyle != 0) initCustomMenu(dialog, volumeUtil)
    }

    private fun initNavBarHook(dialog: Class<*>?) {
        dialog.afterHookMethod("initViews") { _, _ ->
            val flags = View.SYSTEM_UI_FLAG_VISIBLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            thisObject.getObjectFieldAs<FrameLayout>("mRoot").systemUiVisibility = flags
        }
    }

    private fun initCustomMenu(dialog: Class<*>?, volumeUtil: Class<*>?) {
        var group: View? = null

        dialog.afterHookMethod("initViews") { _, _ ->
            val mContext = thisObject.getObjectFieldAs<Context>("mContext")
            val sysUIContext = thisObject.getObjectFieldAs<Context>("mSysUIContext")
            val talkbackLayout = thisObject.getObjectFieldAs<FrameLayout>("mTalkbackLayout")
            val sliderView = thisObject.getObjectFieldAs<FrameLayout>("mSliderView")

            group = when (isPowerMenuStyle) {
                1 -> {
                    val items = listOf(
                        recovery(sysUIContext, R.drawable.ic_recovery),
                        bootloader(sysUIContext, R.drawable.ic_bootloader)
                    )
                    menuB(mContext, this, items, talkbackLayout, sliderView)
                }
                2 -> {
                    val items = (0..7)
                        .map { i -> XSPUtils.getString("power_menu_style_b_$i", "null") }
                        .filter { it != "null" }
                        .map { Action.getAction(sysUIContext, volumeUtil!!, it) }
                    menuA(mContext, this, items, talkbackLayout, sliderView)
                }
                else -> null
            }
        }

        dialog.afterHookMethod("dismiss", Int::class.java) { _, _ ->
            group?.let {
                it.visibility = View.GONE
                thisObject.getObjectFieldAs<FrameLayout>("mSliderView").removeView(it)
            }
        }

        dialog.afterHookMethod("sliderViewDismiss") { _, _ ->
            thisObject.getObjectField("mDialog")?.let {
                if (thisObject.callMethodAs<Boolean>("isShowing")) {
                    group = null
                }
            }
        }

        findClass(
            "com.android.systemui.miui.globalactions.SliderView",
            pluginClassLoader
        ).afterHookMethod("handleActionMoveForAlpha", Float::class.java) { _, _ ->
            group?.let {
                val mDark = thisObject.getObjectFieldAs<View>("mDark")
                it.alpha = 1 - mDark.alpha
            }
        }
    }
}

