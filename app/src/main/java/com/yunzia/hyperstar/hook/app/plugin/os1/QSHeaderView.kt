package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintSet
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.starLog
import com.yunzia.hyperstar.utils.XSPUtils


class QSHeaderView : Hooker() {
    var viewId: Int = 0
    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header", false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        if (!is_use_chaos_header) {
            return
        }

        startMethodsHook()

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

    private fun startMethodsHook() {
        var qsListControllerProvider: Any? = null


        findClass(
            "miui.systemui.controlcenter.panel.main.qs.EditButtonController_Factory",
            classLoader
        ).apply {
            afterHookMethod(
                "get"
            ) {

                val qsListControllerProviders = this.getObjectField("qsListControllerProvider")
                if (qsListControllerProviders == null) {
                    starLog.logE("qsListControllerProviders == null")
                    return@afterHookMethod
                }
                starLog.logD("qsListControllerProviders != null")
                qsListControllerProvider = qsListControllerProviders

            }
        }
        val MainPanelModeController = findClass(
            "miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode",
            classLoader
        )



        findClass(
            "miui.systemui.controlcenter.panel.main.header.StatusHeaderController",
            classLoader
        ).apply {
            afterHookMethod(
                "createStatusBarViews"
            ) {

                val sysUIContext = this.getObjectFieldAs<Context>("sysUIContext")
                val view = this.callMethodAs<ViewGroup>("getView")!!
                val mContext = view.context
                val res = mContext.resources

                val ic_header_settings: Int = view.resources.getIdentifier(
                    "ic_header_settings",
                    "drawable",
                    "miui.systemui.plugin"
                );
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
                //(getDimensionPixelOffset(res,"header_carrier_vertical_mode_margin_bottom",plugin)*1.9).toInt()

                val lp = ViewGroup.MarginLayoutParams(size, size).apply {
                    bottomMargin = bottom
                }
                val a = Button(mContext).apply {
                    layoutParams = lp
                    setBackgroundResource(ic_header_settings)
                }
                //lp.topMargin = 100
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

                //header.top = 200
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
                        starLog.logD("qsListControllerProvider != null")
                        val get = qsListControllerProvider.callMethod("get")
                        if (get == null) {
                            starLog.logE("get == null")
                        } else {
                            val mainPanelMode: Array<out Any>? =
                                MainPanelModeController?.enumConstants
                            if (mainPanelMode == null) {
                                starLog.logE("enumConstants == null")
                                return@setOnClickListener
                            }

                            starLog.logD("" + mainPanelMode[0])
                            get.callMethod("startQuery", mainPanelMode[2])
                        }

                    }

                }
            }
            afterHookMethod(
                "onExpandChange",
                Float::class.java
            ) {
                if (viewId == 0) return@afterHookMethod

                val y = it.args[0] as Float
                val view = this.callMethodAs<ViewGroup>("getView")!!

                view.findViewById<TextView>(viewId).apply {
                    translationY = y
                }

            }
        }


    }

    private fun startMethodsHook1(classLoader: ClassLoader?) {
        val MainHeader = findClass(
            "miui.systemui.controlcenter.panel.main.header.StatusHeaderController",
            classLoader
        )
        val CommonUtils = findClass("miui.systemui.util.CommonUtils", classLoader)
        MainHeader.replaceHookMethod(
            "updateConstraint"
        ) {

            val fakeStatusBarViewController = this.getObjectField("fakeStatusBarViewController")
            val sysUIContext: Context = this.getObjectField("sysUIContext") as Context
            val parent = this.callMethod("getView") as ViewGroup
            val mContext = this.callMethod("getContext") as Context

            if (fakeStatusBarViewController == null) return@replaceHookMethod null


            val constraintSet = ConstraintSet()
            //val header_status_bar_icon:Int = sysUIContext.resources.get("header_status_bar_icons", "id", "miui.systemui.plugin");

            val header_carrier_vertical_mode_margin_bottom = mContext.resources.getIdentifier(
                "header_carrier_vertical_mode_margin_bottom",
                "dimen",
                "miui.systemui.plugin"
            )

            val header_status_bar_icons: Int = mContext.resources.getIdentifier(
                "header_status_bar_icons",
                "id",
                "miui.systemui.plugin"
            );
            val header_date: Int =
                mContext.resources.getIdentifier("header_date", "id", "miui.systemui.plugin");
            val header_carrier_view: Int = mContext.resources.getIdentifier(
                "header_carrier_view",
                "id",
                "miui.systemui.plugin"
            );
            val privacy_container: Int =
                mContext.resources.getIdentifier("privacy_container", "id", "miui.systemui.plugin");
            starLog.log("" + header_status_bar_icons + header_date + header_carrier_view + privacy_container)
            constraintSet.constrainWidth(header_status_bar_icons, -2)
            constraintSet.constrainHeight(header_status_bar_icons, -2)
            constraintSet.constrainWidth(header_date, -2)
            constraintSet.constrainHeight(header_date, -2)
            constraintSet.constrainWidth(header_carrier_view, -2)
            constraintSet.constrainHeight(header_carrier_view, -2)
            val header_privacy_container_height: Int = mContext.resources.getIdentifier(
                "header_privacy_container_height",
                "dimen",
                "miui.systemui.plugin"
            );

            constraintSet.constrainWidth(privacy_container, -2)
            constraintSet.constrainHeight(
                privacy_container,
                mContext.resources.getDimensionPixelSize(header_privacy_container_height)
            )

            val INSTANCE = CommonUtils.getStaticObjectField("INSTANCE")
            val orientation = INSTANCE.callMethod("getInVerticalMode", mContext) as Boolean

            if (orientation) {
                constraintSet.connect(header_status_bar_icons, 4, 0, 4);
                constraintSet.connect(header_date, 3, header_status_bar_icons, 3);
                constraintSet.connect(header_date, 4, header_status_bar_icons, 4);
                constraintSet.createHorizontalChainRtl(
                    0,
                    6,
                    0,
                    7,
                    intArrayOf(header_date, header_status_bar_icons),
                    null as FloatArray?,
                    1
                );
                val dimensionPixelSize = mContext.resources.getDimensionPixelSize(
                    header_carrier_vertical_mode_margin_bottom
                );
                constraintSet.connect(
                    header_carrier_view,
                    4,
                    header_status_bar_icons,
                    3,
                    dimensionPixelSize
                );
                constraintSet.connect(header_carrier_view, 7, 0, 7);
                constraintSet.connect(
                    privacy_container,
                    4,
                    header_status_bar_icons,
                    3,
                    dimensionPixelSize
                );
                constraintSet.connect(privacy_container, 7, 0, 7);
            } else {
                constraintSet.connect(header_status_bar_icons, 4, 0, 4);
                constraintSet.connect(header_carrier_view, 3, header_status_bar_icons, 3);
                constraintSet.connect(header_carrier_view, 4, header_status_bar_icons, 4);
                constraintSet.connect(privacy_container, 3, header_status_bar_icons, 3);
                constraintSet.connect(privacy_container, 4, header_status_bar_icons, 4);
                constraintSet.connect(privacy_container, 7, 0, 7);
                constraintSet.createHorizontalChainRtl(
                    0,
                    6,
                    privacy_container,
                    6,
                    intArrayOf(header_carrier_view, header_status_bar_icons),
                    null as FloatArray?,
                    1
                );
            }

            constraintSet.callMethod("applyTo", parent)

            return@replaceHookMethod null
        }

    }
}


