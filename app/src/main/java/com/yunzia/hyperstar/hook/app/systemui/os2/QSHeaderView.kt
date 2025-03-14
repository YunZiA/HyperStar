package com.yunzia.hyperstar.hook.app.systemui.os2

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
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.callbacks.XC_InitPackageResources
import java.util.Locale
import kotlin.math.pow


class QSHeaderView() : Hooker() {
    var viewId : Int = 0
    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)
    var settingIcon = 0
    var editIcon = 0
    var editId = 0

    override fun initResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.initResources(resparam, modRes)
        settingIcon = resparam?.res?.addResource(modRes,R.drawable.ic_header_settings)!!
        editIcon = resparam.res?.addResource(modRes, R.drawable.ic_controls_edit)!!
        editId = resparam.res?.addResource(modRes,R.id.cc_header_edit)!!
    }

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)

        if (!is_use_chaos_header) return

        startMethodsHook()

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
        val MiuiConfigs = findClass("com.miui.utils.configs.MiuiConfigs",classLoader)

        ControlCenterHeaderController.apply {
            afterHookAllConstructors {
                val combinedHeaderController = it.args[0]
                val controlCenterHeaderView = combinedHeaderController.getObjectFieldAs<ViewGroup>("controlCenterHeaderView")
                header = addButton(controlCenterHeaderView)

            }
            afterHookAllMethods(
                "updateDateVisibility"
            ) {
                val mView = this.getObjectFieldAs<View>("mView")
                val context = mView.context
                val localId = Settings.System.getInt(context.contentResolver,"cc_edit_Id",0)
                if (localId != editId){
                    Settings.System.putInt(context.contentResolver,"cc_edit_Id",editId)
                }
                val isVerticalMode = MiuiConfigs.callStaticMethodAs<Boolean>("isVerticalMode",context)
                if (isVerticalMode ){
                    header?.visibility = View.VISIBLE
                }else{
                    header?.visibility = View.GONE

                }

            }

        }

        val ControlCenterHeaderExpandController = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController",classLoader)
        val Folme = findClass("miuix.animation.Folme",classLoader)
        val IFolme = findClass("miuix.animation.IFolme",classLoader)

        findClass(
            "com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController\$controlCenterCallback\$1",
            classLoader
        ).apply {
            afterHookMethod(
                "onAppearanceChanged",
                Boolean::class.java,
                Boolean::class.java
            ) {
                val z = it.args[0] as Boolean
                val z2 = it.args[1] as Boolean
                val controlCenterHeaderExpandController = this.getObjectField("this\$0")
                val context =
                    controlCenterHeaderExpandController.getObjectFieldAs<Context>("context")
                val controlCenterCarrierViewFolme = Folme.callStaticMethod("useAt", arrayOf(header))
                val setting = header?.get(0)
                val settingViewFolme = Folme.callStaticMethod(
                    "useAt",
                    arrayOf(setting)
                )

                val isVerticalMode =
                    MiuiConfigs.callStaticMethodAs<Boolean>("isVerticalMode", context)
                if (isVerticalMode) {
                    val headers:View? = header
                    val alpha = arrayOf(
                        ControlCenterHeaderExpandController.findMethodBestMatchIfExist(
                            "startFolmeAnimationAlpha",
                            View::class.java,
                            IFolme!!,
                            Float::class.java,
                            Boolean::class.java
                        ),
                        ControlCenterHeaderExpandController.findMethodBestMatchIfExist(
                            "access\$startFolmeAnimationAlpha",
                            ControlCenterHeaderExpandController,
                            View::class.java,
                            IFolme!!,
                            Float::class.java,
                            Boolean::class.java
                        )
                    )

                    if (!z) {
//                        ControlCenterHeaderExpandController.callStaticMethods(
//                            "access\$startFolmeAnimationAlpha",
//                            arrayOf(
//                                ControlCenterHeaderExpandController!!,
//                                View::class.java,
//                                Folme!!,
//                                Float::class.java,
//                                Boolean::class.java
//                            ),
//                            controlCenterHeaderExpandController,
//                            headers,
//                            controlCenterCarrierViewFolme,
//                            0f, z2
//                        )
                        alpha.onlyInvoke(
                            null,
                            headers,
                            controlCenterCarrierViewFolme,
                            0f, z2
                        )
                        val normalControlDateTranslationX =
                            controlCenterHeaderExpandController.getIntField(
                                "normalControlDateTranslationX"
                            )
                        ControlCenterHeaderExpandController.callStaticMethod(
                            "access\$startFolmeAnimationTranslationX",
                            controlCenterHeaderExpandController,
                            setting,
                            settingViewFolme,
                            (normalControlDateTranslationX * 0.6).toInt(), z2
                        )

                    } else {

//                        ControlCenterHeaderExpandController.callStaticMethods(
//                            "access\$startFolmeAnimationAlpha",
//                            arrayOf(
//                                ControlCenterHeaderExpandController!!,
//                                View::class.java,
//                                Folme!!,
//                                Float::class.java,
//                                Boolean::class.java
//                            ),
//                            controlCenterHeaderExpandController,
//                            headers,
//                            controlCenterCarrierViewFolme,
//                            1f, z2
//                        )
                        alpha.onlyInvoke(
                            null,
                            headers,
                            controlCenterCarrierViewFolme,
                            1f, z2
                        )
                        ControlCenterHeaderExpandController.callStaticMethod(
                            "access\$startFolmeAnimationTranslationX",
                            controlCenterHeaderExpandController,
                            setting,
                            settingViewFolme,
                            0, z2
                        )


                    }

                }
            }
            afterHookMethod(
                "onExpansionChanged",
                Float::class.java
            ) {
                val f = it.args[0] as Float
                if (f in 0f..1f){
                    val controlCenterHeaderExpandController = this.getObjectField("this\$0")
                    val context = controlCenterHeaderExpandController.getObjectFieldAs<Context>("context")

                    val headerController = controlCenterHeaderExpandController.getObjectField("headerController")
                    val combinedHeaderController = headerController.callMethod( "get")
                    val switching = combinedHeaderController.getBooleanField("switching")
                    if (!switching || f!= 1f){
                        val isVerticalMode = MiuiConfigs.callStaticMethodAs<Boolean>("isVerticalMode",context)
                        if (isVerticalMode ){
                            val f2 = 1-f
                            val normalControlStatusBarTranslationY = controlCenterHeaderExpandController.getIntField(
                                "normalControlStatusBarTranslationY"
                            )
                            header?.translationY = normalControlStatusBarTranslationY * f2
                        }

                    }


                }
            }

        }

        val PanelExpandControllerExt = findClass("com.miui.interfaces.shade.PanelExpandControllerExt",classLoader)

        findClass(
            "com.android.systemui.controlcenter.shade.CombinedHeaderController",
            classLoader
        ).afterHookMethod(
            "onSwitchProgressChanged",
            Float::class.java
        ){
            val f = it.args[0] as Float
            val context = this.getObjectFieldAs<Context>("context")
            val controlCenterExpandController = this.getObjectField("controlCenterExpandController")
            val getAppearance = PanelExpandControllerExt.findMethodExactIfExists("getAppearance")
            if (!(getAppearance?.invoke(controlCenterExpandController) as Boolean)) {
                return@afterHookMethod
            }

            if ( f <= 0.5f ){
                val controlLocationX =  this.getFloatField("controlLocationX")
                val notificationLocationY =  this.getFloatField("notificationLocationY")
                val notificationLocationX =  this.getFloatField("notificationLocationX")
                val controlLocationY =  this.getFloatField("controlLocationY")
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
                val isFlipTinyScreen = MiuiConfigs.callStaticMethodAs<Boolean>("isFlipTinyScreen",context)
                if (isFlipTinyScreen){
                    header?.translationY = moveY
                    header?.get(0)?.translationX = -moveX
                    header?.get(2)?.translationX = -moveX
                    return@afterHookMethod

                }
                val isVerticalMode = MiuiConfigs.callStaticMethodAs<Boolean>("isVerticalMode",context)
                if (isVerticalMode){
                    header?.translationY = moveY
                    header?.get(0)?.translationX = -moveX
                    header?.get(2)?.translationX = moveX
                    return@afterHookMethod

                }
            }

        }

    }

    private fun startMethodsHook1(classLoader: ClassLoader?) {
        val CommonUtils = findClass("miui.systemui.util.CommonUtils",classLoader)
        findClass(
            "miui.systemui.controlcenter.panel.main.header.StatusHeaderController",
            classLoader
        ).replaceHookMethod(
            "updateConstraint"
        ) {

            val fakeStatusBarViewController  = this.getObjectField("fakeStatusBarViewController")
            val sysUIContext   = this.getObjectField("sysUIContext") as Context
            val parent = this.callMethod("getView") as ViewGroup
            val mContext = this.callMethod("getContext") as Context

            if (fakeStatusBarViewController == null) {
                return@replaceHookMethod null
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

            val INSTANCE = CommonUtils.getStaticObjectField("INSTANCE")
            val orientation = INSTANCE.callMethod("getInVerticalMode",mContext) as Boolean

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

            constraintSet.callMethod("applyTo", parent )

            return@replaceHookMethod null;
        }

    }

    fun addButton(
        controlCenterHeaderView: ViewGroup,
    ): LinearLayout {
        val context = controlCenterHeaderView.context
        val res = controlCenterHeaderView.resources

        val size = (getDimensionPixelOffset(res,"shade_header_control_center_carrier_text_size",systemUI)/2*3).toInt()
        val bottom = (getDimensionPixelOffset(res,"shade_header_bottom_padding",systemUI)*2.85).toInt()
        //dpToPx(res,21.4f).toInt()
        val lp = ViewGroup.MarginLayoutParams(size, size).apply {
            bottomMargin = bottom
            //topMargin = 100
        }

        val setting = Button(context).apply {
            setBackgroundResource(settingIcon)
            layoutParams = lp
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
        starLog.logD("id = ${edit.id}\nedit = $editId")
        Settings.System.putInt(context.contentResolver,"cc_edit_Id",editId)

        val spaceLp = LinearLayout.LayoutParams(-1,-1).apply {
            weight = 1f
        }
        val space = View(context).apply {
            layoutParams = spaceLp
        }
        ViewGroup.LayoutParams.WRAP_CONTENT

        val header = LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(-1,-1)
            gravity = Gravity.END+Gravity.BOTTOM
            orientation = LinearLayout.HORIZONTAL
            addView(setting)
            addView(space)
            addView(edit)

        }

        controlCenterHeaderView.addView(header)

//        controlCenterHeaderView as ConstraintLayout
//        val setaddtop = ConstraintSet()
//        setaddtop.clone(controlCenterHeaderView)
//        setaddtop.connect(R.id.button_addtop, ConstraintSet.TOP, textView.id, ConstraintSet.BOTTOM)
//        setaddtop.applyTo(root)

        starLog.logD("ControlCenterHeaderController ${controlCenterHeaderView.findViewById<View>(editId)}")

        return header
    }
}


