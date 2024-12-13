package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

import yunzia.utils.DensityUtil.Companion.dpToPx


class QSHeaderView(val sysClassLoader: ClassLoader) : BaseHooker() {
    var viewId : Int = 0
    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        starLog.log("ControlCenterHeaderController ***\n $sysClassLoader")

        if (!is_use_chaos_header){
            return
        }

        startMethodsHook()
        //startMethodsHook1(classLoader)
        //starLog.log(mPath)

    }

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMethodsHook() {
        var qsListControllerProvider: Any? = null

        val ControlCenterHeaderController  = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderController",sysClassLoader)

        starLog.log("ControlCenterHeaderController ***\n $ControlCenterHeaderController")
        XposedBridge.hookAllConstructors(ControlCenterHeaderController,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                starLog.log("ControlCenterHeaderController")
                val combinedHeaderController = param?.args?.get(0)
                val controlCenterHeaderView = XposedHelpers.getObjectField(combinedHeaderController,"controlCenterHeaderView") as ViewGroup
//                val thisObj = param?.thisObject

//                val sysUIContext = XposedHelpers.getObjectField(thisObj,"sysUIContext") as Context
                val mContext = controlCenterHeaderView.context
                val res = controlCenterHeaderView.resources
                //XposedHelpers.callMethod(sysUIContext.getResources().getAssets(), "addAssetPath", modulePath);

//                val ic_header_settings:Int = res.getIdentifier("ic_header_settings", "drawable", systemUI);
//                val ic_controls_edit = res.getIdentifier("ic_controls_edit","drawable",systemUI)
                val  ic_cc_qs_settings = res.getIdentifier("ic_cc_qs_settings","drawable",systemUI)

                //feedbackConstant – One of the constants defined in HapticFeedbackConstants
                val size = dpToPx(res,21.4f).toInt()
                val a = Button(mContext)
                a.setBackgroundResource(ic_cc_qs_settings)
                val lp = ViewGroup.MarginLayoutParams(size, size)
                lp.topMargin = 100
                //c.gravity = Gravity.START
                a.layoutParams = lp
                //Log.d("ggc", "afterHookedMethod: ${pxToDp(res,60f)}")

                val b = Button(mContext)
                b.setBackgroundResource(ic_cc_qs_settings)
                //c.gravity = Gravity.END
                //lp.marginStart = 15
                b.layoutParams = lp
                val c = View(mContext)
                val c_lp = LinearLayout.LayoutParams(-1,-1)
                c_lp.weight = 1f
                c.layoutParams = c_lp



                val header = LinearLayout(mContext)

                val headerLp = ViewGroup.LayoutParams(-1,-2)
                header.layoutParams = headerLp
                //header.top = 200
                header.id = View.generateViewId()
                header.gravity = Gravity.END
                viewId = header.id

                header.orientation = LinearLayout.HORIZONTAL;
                header.addView(a)
                header.addView(c)
                header.addView(b)
                controlCenterHeaderView.addView(header)

                starLog.log("ControlCenterHeaderController $controlCenterHeaderView")

                a.setOnClickListener{
                    if(controlCenterHeaderView.alpha == 0f) return@setOnClickListener
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    val intent = Intent()
                    intent.setClassName("com.android.settings", "com.android.settings.MainSettings")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    mContext.startActivity(intent)
                    collapseStatusBar(mContext)
                }

                b.setOnClickListener{
                    if(controlCenterHeaderView.alpha == 0f) return@setOnClickListener
                    it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
//                    if (qsListControllerProvider != null){
//                        starLog.log("qsListControllerProvider != null")
//                        val get = XposedHelpers.callMethod(qsListControllerProvider,"get")
//                        if (get == null){
//                            starLog.log("get == null")
//                        }else{
//                            starLog.log("get != null")
//
//                        }
//
////                        val mainPanelMode: Array<out Any>? = MainPanelModeController.getEnumConstants()
////                        if (mainPanelMode == null){
////                            starLog.log("enumConstants == null")
////                            return@setOnClickListener
////                        }
//
//                        starLog.log(""+mainPanelMode[0])
//                        XposedHelpers.callMethod(get,"startQuery",mainPanelMode[2])
//                    }

                }

                //XposedHelpers.callMethod(constraintSet,"applyTo",view)
            }
        })


        val EditButtonController_Factory = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.EditButtonController_Factory",classLoader)
        val MainPanelModeController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",classLoader)




        XposedHelpers.findAndHookMethod(EditButtonController_Factory,"get",object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject

                val qsListControllerProviders = XposedHelpers.getObjectField(thisObj,"qsListControllerProvider")
                if (qsListControllerProviders == null){
                    starLog.log("qsListControllerProviders == null")
                    return
                }
                starLog.log("qsListControllerProviders != null")
                qsListControllerProvider = qsListControllerProviders

            }
        })

//        XposedHelpers.findAndHookMethod(MainHeader, "onExpandChange" ,Float::class.java, object : XC_MethodHook() {
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                if (viewId == 0){
//                    return
//                }
//                val thisObj = param?.thisObject
//                val y = param?.args?.get(0) as Float
//                val view = XposedHelpers.callMethod(thisObj,"getView") as View
//
//                val textView: View = view.findViewById(viewId) as View
//                textView.translationY = y
//
//            }
//        })



    }

    private fun startMethodsHook1(classLoader: ClassLoader?) {
        val MainHeader  = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.StatusHeaderController",classLoader)
        val CommonUtils = XposedHelpers.findClass("miui.systemui.util.CommonUtils",classLoader)
        XposedHelpers.findAndHookMethod(MainHeader, "updateConstraint" , object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                val thisObj = param?.thisObject

                val fakeStatusBarViewController  = XposedHelpers.getObjectField(thisObj,"fakeStatusBarViewController")
                val sysUIContext : Context  = XposedHelpers.getObjectField(thisObj,"sysUIContext") as Context
                val parent = XposedHelpers.callMethod(thisObj,"getView") as ViewGroup
                val mContext = XposedHelpers.callMethod(thisObj,"getContext") as Context

                if (fakeStatusBarViewController == null) {
                    return null
                }

                val constraintSet = ConstraintSet()
                //val header_status_bar_icon:Int = sysUIContext.resources.get("header_status_bar_icons", "id", "miui.systemui.plugin");

                val header_carrier_vertical_mode_margin_bottom = mContext.resources.getIdentifier("header_carrier_vertical_mode_margin_bottom","dimen","miui.systemui.plugin")

                val header_status_bar_icons:Int = mContext.resources.getIdentifier("header_status_bar_icons", "id", "miui.systemui.plugin");
                val header_date:Int = mContext.resources.getIdentifier("header_date", "id", "miui.systemui.plugin");
                val header_carrier_view:Int = mContext.resources.getIdentifier("header_carrier_view", "id", "miui.systemui.plugin");
                val privacy_container:Int = mContext.resources.getIdentifier("privacy_container", "id", "miui.systemui.plugin");
                starLog.log(""+header_status_bar_icons+header_date+header_carrier_view+privacy_container)
                constraintSet.constrainWidth(header_status_bar_icons, -2)
                constraintSet.constrainHeight(header_status_bar_icons, -2)
                constraintSet.constrainWidth(header_date, -2)
                constraintSet.constrainHeight(header_date, -2)
                constraintSet.constrainWidth(header_carrier_view, -2)
                constraintSet.constrainHeight(header_carrier_view, -2)
                val header_privacy_container_height:Int = mContext.resources.getIdentifier("header_privacy_container_height", "dimen", "miui.systemui.plugin");

                constraintSet.constrainWidth(privacy_container, -2)
                constraintSet.constrainHeight(
                    privacy_container,
                    mContext.resources.getDimensionPixelSize(header_privacy_container_height)
                )

                val INSTANCE = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")
                val orientation = XposedHelpers.callMethod(INSTANCE,"getInVerticalMode",mContext) as Boolean

                if (orientation) {
                    constraintSet.connect(header_status_bar_icons, 4, 0, 4);
                    constraintSet.connect(header_date, 3, header_status_bar_icons, 3);
                    constraintSet.connect(header_date, 4, header_status_bar_icons, 4);
                    constraintSet.createHorizontalChainRtl(0, 6, 0, 7, intArrayOf(header_date, header_status_bar_icons), null as FloatArray? , 1);
                    val dimensionPixelSize = mContext.resources.getDimensionPixelSize(header_carrier_vertical_mode_margin_bottom);
                    constraintSet.connect(header_carrier_view, 4, header_status_bar_icons, 3, dimensionPixelSize);
                    constraintSet.connect(header_carrier_view, 7, 0, 7);
                    constraintSet.connect(privacy_container, 4, header_status_bar_icons, 3, dimensionPixelSize);
                    constraintSet.connect(privacy_container, 7, 0, 7);
                } else {
                    constraintSet.connect(header_status_bar_icons, 4, 0, 4);
                    constraintSet.connect(header_carrier_view, 3, header_status_bar_icons, 3);
                    constraintSet.connect(header_carrier_view, 4, header_status_bar_icons, 4);
                    constraintSet.connect(privacy_container, 3, header_status_bar_icons, 3);
                    constraintSet.connect(privacy_container, 4, header_status_bar_icons, 4);
                    constraintSet.connect(privacy_container, 7, 0, 7);
                    constraintSet.createHorizontalChainRtl(0, 6, privacy_container, 6,intArrayOf(header_carrier_view, header_status_bar_icons) , null as FloatArray? , 1);
                }

                XposedHelpers.callMethod(constraintSet,"applyTo", parent )

                return null;

            }

        })

    }
}


