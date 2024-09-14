package com.chaos.hyperstar.hook.app.plugin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


class QSHeaderView : BaseHooker() {

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        //startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {
        val MainHeader  = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.StatusHeaderController",classLoader)
        XposedHelpers.findAndHookMethod(MainHeader, "updateConstraint" , object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)

                val thisObj = param?.thisObject

                val sysUIContext = XposedHelpers.getObjectField(thisObj,"sysUIContext") as Context
                val view = XposedHelpers.callMethod(thisObj,"getView") as ViewGroup

                val fakeStatusBarViewController = XposedHelpers.getObjectField(thisObj,"fakeStatusBarViewController")
                if (fakeStatusBarViewController == null){
                    return
                }
                val layout = LayoutInflater.from(sysUIContext)

                val b = TextView(sysUIContext)
                b.setText("cnm")
                b.id
                view.addView(b)



            }
        })



    }

    private fun startMethodsHook1(classLoader: ClassLoader?) {
        val MainHeader  = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.header.StatusHeaderController",classLoader)
        XposedHelpers.findAndHookMethod(MainHeader, "updateConstraint" , object : XC_MethodReplacement(){
            override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                val thisObj = param?.thisObject

                val fakeStatusBarViewController  = XposedHelpers.getObjectField(thisObj,"fakeStatusBarViewController")
                val sysUIContext : Context  = XposedHelpers.getObjectField(thisObj,"sysUIContext") as Context



                if (fakeStatusBarViewController == null) {
                    return null
                }
                val constraintSet: ConstraintSet = ConstraintSet()
                //val header_status_bar_icon:Int = sysUIContext.resources.get("header_status_bar_icons", "id", "miui.systemui.plugin");

                val header_status_bar_icons:Int = sysUIContext.resources.getIdentifier("header_status_bar_icons", "id", "miui.systemui.plugin");
                val header_date:Int = sysUIContext.resources.getIdentifier("header_date", "id", "miui.systemui.plugin");
                val header_carrier_view:Int = sysUIContext.resources.getIdentifier("header_carrier_view", "id", "miui.systemui.plugin");
                val privacy_container:Int = sysUIContext.resources.getIdentifier("privacy_container", "id", "miui.systemui.plugin");
                starLog.log(""+header_status_bar_icons+header_date+header_carrier_view+privacy_container)
                constraintSet.constrainWidth(header_status_bar_icons, -2)
                constraintSet.constrainHeight(header_status_bar_icons, -2)
                constraintSet.constrainWidth(header_date, -2)
                constraintSet.constrainHeight(header_date, -2)
                constraintSet.constrainWidth(header_carrier_view, -2)
                constraintSet.constrainHeight(header_carrier_view, -2)
                val header_privacy_container_height:Int = sysUIContext.getResources().getIdentifier("header_privacy_container_height", "dimen", "miui.systemui.plugin");

////                constraintSet.constrainWidth(0x7f0a04c3, -2)
////                constraintSet.constrainHeight(0x7f0a04c3, -2)
//                constraintSet.constrainWidth(privacy_container, -2)
//                constraintSet.constrainHeight(
//                    privacy_container,
//                    thisObj.getResources().getDimensionPixelSize(header_privacy_container_height)
//                )
//                constraintSet.applyTo(thisO.getView())
//                if (CommonUtils.INSTANCE.getInVerticalMode(getContext())) {
//                    constraintSet.connect(R.id.header_status_bar_icons, 4, 0, 4)
//                    constraintSet.connect(R.id.header_carrier_view, 4, 0, 4)
//                    constraintSet.createHorizontalChainRtl(
//                        0,
//                        6,
//                        0,
//                        7,
//                        intArrayOf(R.id.header_carrier_view, R.id.header_status_bar_icons),
//                        null as FloatArray?,
//                        1
//                    )
//                    val i4: Int = R.id.header_status_bar_icons
//                    val i5: Int = R.id.header_carrier_view
//                    constraintSet.connect(i5, 3, i4, 3)
//                    constraintSet.connect(i5, 4, i4, 4)
//                    val i6: Int = R.id.header_date
//                    constraintSet.connect(i6, 3, 0x7f0a04c3, 3)
//                    constraintSet.connect(i6, 4, 0x7f0a04c3, 4)
//                    constraintSet.createHorizontalChainRtl(
//                        0,
//                        6,
//                        R.id.privacy_container,
//                        6,
//                        intArrayOf(R.id.header_date, 2131363011),
//                        null as FloatArray?,
//                        1
//                    )
//                    constraintSet.connect(
//                        0x7f0a04c3,
//                        4,
//                        R.id.header_status_bar_icons,
//                        3,
//                        getResources().getDimensionPixelSize(R.dimen.header_carrier_vertical_mode_margin_bottom)
//                    )
//                    constraintSet.connect(R.id.privacy_container, 7, 0, 7)
//                    constraintSet.connect(R.id.privacy_container, 3, 0x7f0a04c3, 3)
//                    constraintSet.connect(R.id.privacy_container, 4, 0x7f0a04c3, 4)
//                } else {
//                    constraintSet.connect(R.id.header_status_bar_icons, 4, 0, 4)
//                    val ChangeLandHeaderView: Int =
//                        SettingManager.ChangeLandHeaderView(R.id.header_carrier_view)
//                    constraintSet.connect(ChangeLandHeaderView, 3, R.id.header_status_bar_icons, 3)
//                    constraintSet.connect(ChangeLandHeaderView, 4, R.id.header_status_bar_icons, 4)
//                    constraintSet.connect(
//                        R.id.privacy_container,
//                        3,
//                        R.id.header_status_bar_icons,
//                        3
//                    )
//                    constraintSet.connect(
//                        R.id.privacy_container,
//                        4,
//                        R.id.header_status_bar_icons,
//                        4
//                    )
//                    constraintSet.connect(R.id.privacy_container, 7, 0, 7)
//                    constraintSet.createHorizontalChainRtl(
//                        0,
//                        6,
//                        R.id.privacy_container,
//                        6,
//                        intArrayOf(
//                            SettingManager.ChangeLandHeaderView(R.id.header_carrier_view),
//                            R.id.header_status_bar_icons
//                        ),
//                        null as FloatArray?,
//                        1
//                    )
//                }

                val getArtMethod = MainHeader.getDeclaredMethod("getView")

                val view: ConstraintLayout = getArtMethod.invoke(null) as ConstraintLayout
                constraintSet.applyTo(view)



                return null;

            }

        })

    }
}