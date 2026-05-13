package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callMethodAs
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.prefs.XSPUtils


object QSHeaderView : BasePluginHook() {
    var viewId: Int = 0
    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header", false)

    override fun init() {
        if (!is_use_chaos_header) return

        var qsListControllerProvider: Any? = null

        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController_Factory",
            pluginClassLoader
        ).apply {
            afterHookMethod(
                "get"
            ) { args, result ->
                val qsListControllerProviders = thisObject.getObjectField("qsListControllerProvider")
                if (qsListControllerProviders == null) {
                    logE("qsListControllerProviders == null")
                    return@afterHookMethod
                }
                qsListControllerProvider = qsListControllerProviders
            }
        }

        val MainPanelModeController = findClass(
            "miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode",
            pluginClassLoader
        )

        findClass(
            "miui.systemui.controlcenter.panel.main.header.StatusHeaderController",
            pluginClassLoader
        ).apply {
            afterHookMethod(
                "createStatusBarViews"
            ) { args, result ->
                val sysUIContext = thisObject.getObjectFieldAs<Context>("sysUIContext")
                val view = thisObject.callMethodAs<ViewGroup>("getView")!!
                val mContext = view.context
                val res = mContext.resources

                val ic_header_settings: Int = view.resources.getIdentifier(
                    "ic_header_settings",
                    "drawable",
                    "miui.systemui.plugin"
                )
                val ic_controls_edit = view.resources.getIdentifier(
                    "ic_controls_edit",
                    "drawable",
                    "miui.systemui.plugin"
                )
                val size = getDimensionPixelOffset(res, "header_text_size", plugin) / 2 * 3
                val bottom = (getDimensionPixelOffset(
                    res,
                    "header_carrier_vertical_mode_margin_bottom",
                    plugin
                ) * 3.8).toInt()

                val lp = ViewGroup.MarginLayoutParams(size, size).apply {
                    bottomMargin = bottom
                }
                val a = Button(mContext).apply {
                    layoutParams = lp
                    setBackgroundResource(ic_header_settings)
                }
                val b = Button(mContext).apply {
                    layoutParams = lp
                    setBackgroundResource(ic_controls_edit)
                }
                val c_lp = LinearLayout.LayoutParams(-1, -1).apply {
                    weight = 1f
                }
                val c = View(mContext).apply {
                    layoutParams = c_lp
                }
                val headerLp = ViewGroup.LayoutParams(-1, -1)
                val header = LinearLayout(sysUIContext).apply {
                    layoutParams = headerLp
                    id = View.generateViewId()
                    gravity = Gravity.END + Gravity.BOTTOM
                    orientation = LinearLayout.HORIZONTAL
                    addView(a)
                    addView(c)
                    addView(b)
                }
                viewId = header.id
                view.addView(header)
                a.setOnClickListener {
                    if (view.alpha == 0f) return@setOnClickListener
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    val intent = Intent().apply {
                        setClassName("com.android.settings", "com.android.settings.MainSettings")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    sysUIContext.startActivity(intent)
                    collapseStatusBar(sysUIContext)
                }

                b.setOnClickListener {
                    if (view.alpha == 0f) return@setOnClickListener
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    if (qsListControllerProvider != null) {
                        val get = qsListControllerProvider.callMethod("get")
                        if (get == null) {
                            logE("get == null")
                        } else {
                            val mainPanelMode: Array<out Any>? =
                                MainPanelModeController?.enumConstants
                            if (mainPanelMode == null) {
                                logE("enumConstants == null")
                                return@setOnClickListener
                            }
                            get.callMethod("startQuery", mainPanelMode[2])
                        }
                    }
                }
            }
            afterHookMethod(
                "onExpandChange",
                Float::class.java
            ) { args, result ->
                if (viewId == 0) return@afterHookMethod

                val y = args[0] as Float
                val view = thisObject.callMethodAs<ViewGroup>("getView")!!

                view.findViewById<LinearLayout>(viewId).apply {
                    translationY = y
                }
            }
        }
    }

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0))
                .invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
