package com.yunzia.hyperstar.hook.app.systemui

import android.content.Context
import android.content.Intent
import android.content.res.XModuleResources
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.get
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import yunzia.utils.DensityUtil.Companion.dpToPx
import java.lang.reflect.Method
import java.util.Locale
import kotlin.math.pow


class QSHeaderView() : BaseHooker() {
    var viewId : Int = 0
    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)
    var settingIcon = 0
    var editIcon = 0
    var editId = 0

    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        settingIcon = resparam?.res?.addResource(modRes,R.drawable.ic_header_settings)!!
        editIcon = resparam.res?.addResource(modRes, R.drawable.ic_controls_edit)!!
        editId = resparam.res?.addResource(modRes,R.id.cc_header_edit)!!
    }

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)

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

        var header : ViewGroup? = null
        val ControlCenterHeaderController  = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderController",classLoader)

        XposedBridge.hookAllConstructors(ControlCenterHeaderController,object :XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam?) {
                val combinedHeaderController = param?.args?.get(0)
                val controlCenterHeaderView = XposedHelpers.getObjectField(combinedHeaderController,"controlCenterHeaderView") as ViewGroup
                header = addButton(controlCenterHeaderView)
            }
        })

        val MiuiConfigs = findClass("com.miui.utils.configs.MiuiConfigs",classLoader)
        XposedBridge.hookAllMethods(ControlCenterHeaderController,"updateDateVisibility",object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val mView = XposedHelpers.getObjectField(thisObj,"mView") as View
                val context = mView.context
                val localId = Settings.System.getInt(context.contentResolver,"cc_edit_Id",0)
                if (localId != editId){
                    Settings.System.putInt(context.contentResolver,"cc_edit_Id",editId)
                }
                val isVerticalMode = XposedHelpers.callStaticMethod(MiuiConfigs,"isVerticalMode",context) as Boolean
                if (isVerticalMode ){
                    header?.visibility = View.VISIBLE
                }else{
                    header?.visibility = View.GONE

                }
            }
        })

        val controlCenterCallback = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController\$controlCenterCallback\$1",classLoader)
        val ControlCenterHeaderExpandController = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController",classLoader)
        val Folme = findClass("miuix.animation.Folme",classLoader)

        XposedHelpers.findAndHookMethod(controlCenterCallback,"onAppearanceChanged",Boolean::class.java,Boolean::class.java,object :XC_MethodHook(){

            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val z = param?.args?.get(0) as Boolean
                val z2 = param.args?.get(1) as Boolean
                val controlCenterHeaderExpandController = XposedHelpers.getObjectField(thisObj,"this\$0")
                val context = XposedHelpers.getObjectField(controlCenterHeaderExpandController,"context") as Context
                val controlCenterCarrierViewFolme = XposedHelpers.callStaticMethod(Folme,"useAt",arrayOf(header))
                val setting = header?.get(0)
                val settingViewFolme = XposedHelpers.callStaticMethod(Folme,"useAt",arrayOf(setting))

                val isVerticalMode = XposedHelpers.callStaticMethod(MiuiConfigs,"isVerticalMode",context) as Boolean
                if (isVerticalMode ) {
                    if (!z){

                        XposedHelpers.callStaticMethod(ControlCenterHeaderExpandController,"access\$startFolmeAnimationAlpha",
                            controlCenterHeaderExpandController,
                            header,
                            controlCenterCarrierViewFolme,
                            0f,z2
                        )
                        val normalControlDateTranslationX = XposedHelpers.getIntField(controlCenterHeaderExpandController,"normalControlDateTranslationX")
                        XposedHelpers.callStaticMethod(ControlCenterHeaderExpandController,"access\$startFolmeAnimationTranslationX",
                            controlCenterHeaderExpandController,
                            setting,
                            settingViewFolme,
                            (normalControlDateTranslationX*0.5).toInt(),z2
                        )

                    }else{

                        XposedHelpers.callStaticMethod(ControlCenterHeaderExpandController,"access\$startFolmeAnimationAlpha",
                            controlCenterHeaderExpandController,
                            header,
                            controlCenterCarrierViewFolme,
                            1f,z2
                        )
                        XposedHelpers.callStaticMethod(ControlCenterHeaderExpandController,"access\$startFolmeAnimationTranslationX",
                            controlCenterHeaderExpandController,
                            setting,
                            settingViewFolme,
                            0,z2
                        )



                    }
                }



            }
        })

        XposedHelpers.findAndHookMethod(controlCenterCallback,"onExpansionChanged",Float::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val f = param?.args?.get(0) as Float
                if (f in 0f..1f){
                    val controlCenterHeaderExpandController = XposedHelpers.getObjectField(thisObj,"this\$0")
                    val context = XposedHelpers.getObjectField(controlCenterHeaderExpandController,"context") as Context

                    val headerController = XposedHelpers.getObjectField(controlCenterHeaderExpandController,"headerController")
                    val combinedHeaderController = XposedHelpers.callMethod(headerController, "get")
                    val switching = XposedHelpers.getBooleanField(combinedHeaderController,"switching")
                    if (!switching || f!= 1f){
                        val isVerticalMode = XposedHelpers.callStaticMethod(MiuiConfigs,"isVerticalMode",context) as Boolean
                        if (isVerticalMode ){
                            val f2 = 1-f
                            val normalControlStatusBarTranslationY = XposedHelpers.getIntField(controlCenterHeaderExpandController,"normalControlStatusBarTranslationY")
                            header?.translationY = normalControlStatusBarTranslationY * f2
                        }

                    }


                }


            }
        })

        val CombinedHeaderController = findClass("com.android.systemui.controlcenter.shade.CombinedHeaderController",classLoader)
        val PanelExpandControllerExt = findClass("com.miui.interfaces.shade.PanelExpandControllerExt",classLoader)


        XposedHelpers.findAndHookMethod(CombinedHeaderController,"onSwitchProgressChanged",Float::class.java,object :XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val f = param?.args?.get(0) as Float
                val context = XposedHelpers.getObjectField(thisObj,"context") as Context
                val controlCenterExpandController = XposedHelpers.getObjectField(thisObj,"controlCenterExpandController")

                val getAppearance = XposedHelpers.findMethodExactIfExists(PanelExpandControllerExt,"getAppearance")

                if (!(getAppearance.invoke(controlCenterExpandController) as Boolean)) {
                    return;
                }

                if ( f <= 0.5f ){
                    val controlLocationX =  XposedHelpers.getFloatField(thisObj,"controlLocationX")
                    val notificationLocationY =  XposedHelpers.getFloatField(thisObj,"notificationLocationY")
                    val notificationLocationX =  XposedHelpers.getFloatField(thisObj,"notificationLocationX")
                    val controlLocationY =  XposedHelpers.getFloatField(thisObj,"controlLocationY")
                    val f2 = 1f
                    val f3 = 2f
                    val pow = (f2-(f2 - (f * f3)).coerceIn(0.0f, 1.0f)).pow(2.0f)
                    var moveX: Float =
                        (controlLocationX - notificationLocationX) * pow / f3
                    var moveY: Float =
                        (notificationLocationY - controlLocationY) * pow / f3
                    if (TextUtils.getLayoutDirectionFromLocale(Locale.getDefault()) == 1) {
                        moveX = -moveX
                        moveY = -moveY
                    }
                    val isFlipTinyScreen = XposedHelpers.callStaticMethod(MiuiConfigs,"isFlipTinyScreen",context) as Boolean
                    if (isFlipTinyScreen){
                        header?.translationY = moveY
                        header?.get(0)?.translationX = -moveX
                        header?.get(2)?.translationX = -moveX
                        return

                    }
                    val isVerticalMode = XposedHelpers.callStaticMethod(MiuiConfigs,"isVerticalMode",context) as Boolean
                    if (isVerticalMode){
                        header?.translationY = moveY
                        header?.get(0)?.translationX = -moveX
                        header?.get(2)?.translationX = moveX
                        return

                    }
                }


            }
        })



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

    fun addButton(
        controlCenterHeaderView: ViewGroup,
    ): LinearLayout {
        val context = controlCenterHeaderView.context
        val res = controlCenterHeaderView.resources

        val size = dpToPx(res,21.4f).toInt()
        val lp = ViewGroup.MarginLayoutParams(size, size).apply {
            topMargin = 100
        }

        val setting = Button(context).apply {
            setBackgroundResource(settingIcon)
            layoutParams =lp
            setOnClickListener{
                if(controlCenterHeaderView.alpha == 0f) return@setOnClickListener
                it.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                val intent = Intent()
                intent.setClassName("com.android.settings", "com.android.settings.MainSettings")
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                collapseStatusBar(context)
            }
        }

        val edit = Button(context).apply {
            id = editId
            setBackgroundResource(editIcon)
            layoutParams =lp
        }
        starLog.log("id = ${edit.id}\nedit = $editId")
        Settings.System.putInt(context.contentResolver,"cc_edit_Id",editId)

        val spaceLp = LinearLayout.LayoutParams(-1,-1).apply {
            weight = 1f
        }
        val space = View(context).apply {
            layoutParams = spaceLp
        }

        val header = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(-1,-2)
            gravity = Gravity.END
            orientation = LinearLayout.HORIZONTAL
            addView(setting)
            addView(space)
            addView(edit)

        }

        controlCenterHeaderView.addView(header)

        starLog.log("ControlCenterHeaderController ${controlCenterHeaderView.findViewById<View>(editId)}")

        return header
    }
}


