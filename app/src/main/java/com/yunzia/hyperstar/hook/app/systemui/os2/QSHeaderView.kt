package com.yunzia.hyperstar.hook.app.systemui.os2

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.TextUtils
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.core.view.get
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.app.plugin.HideVolumeCollpasedFootButton.plugin
import com.yunzia.hyperstar.hook.base.BaseHookHelper.findMethodBestMatchIfExist
import com.yunzia.hyperstar.hook.base.BaseHookHelper.findMethodExactIfExists
import com.yunzia.hyperstar.hook.base.BaseHookHelper.getId
import com.yunzia.hyperstar.hook.core.base.BaseHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelOffset
import com.yunzia.hyperstar.hook.core.StarLog.log
import com.yunzia.hyperstar.hook.core.StarLog.logD
import com.yunzia.hyperstar.hook.core.XposedCore
import com.yunzia.hyperstar.hook.core.helper.afterHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.afterHookAllMethods
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.callStaticMethod
import com.yunzia.hyperstar.hook.core.helper.callStaticMethodAs
import com.yunzia.hyperstar.hook.core.helper.getBooleanField
import com.yunzia.hyperstar.hook.core.helper.getFloatField
import com.yunzia.hyperstar.hook.core.helper.getIntField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.hook.core.helper.getStaticObjectField
import com.yunzia.hyperstar.hook.util.ConstraintSet
import com.yunzia.hyperstar.prefs.XSPUtils
import com.yunzia.hyperstar.hook.core.provider.ClassLoaderProvider
import java.util.Locale
import kotlin.math.pow


object QSHeaderView : BaseHook() {
    private val is_use_chaos_header = XSPUtils.getBoolean("is_use_chaos_header",false)

    override fun init() {
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
        val ControlCenterHeaderController  = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderController")
        val MiuiConfigs = findClass("com.miui.utils.configs.MiuiConfigs")

        ControlCenterHeaderController.apply {
            afterHookAllConstructors { args, result ->
                val combinedHeaderController = args[0]
                val controlCenterHeaderView = combinedHeaderController.getObjectFieldAs<ViewGroup>("controlCenterHeaderView")
                header = addButton(controlCenterHeaderView)

            }
            afterHookAllMethods(
                "updateDateVisibility"
            ) { args, result ->
                val mView = thisObject.getObjectFieldAs<View>("mView")
                val context = mView.context
                val localId = Settings.System.getInt(context.contentResolver,"cc_edit_Id",0)
                if (localId != R.id.cc_header_edit){
                    Settings.System.putInt(context.contentResolver,"cc_edit_Id",R.id.cc_header_edit)
                }
                val isVerticalMode = MiuiConfigs.callStaticMethodAs<Boolean>("isVerticalMode",context)
                if (isVerticalMode ){
                    header?.visibility = View.VISIBLE
                }else{
                    header?.visibility = View.GONE

                }

            }

        }

        val ControlCenterHeaderExpandController = findClass("com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController")
        val Folme = findClass("miuix.animation.Folme")
        val IFolme = findClass("miuix.animation.IFolme")

        findClass(
            "com.android.systemui.controlcenter.shade.ControlCenterHeaderExpandController\$controlCenterCallback\$1"
        ).apply {
            afterHookMethod(
                "onAppearanceChanged",
                Boolean::class.java,
                Boolean::class.java
            ) { args, result ->
                val z = args[0] as Boolean
                val z2 = args[1] as Boolean
                val controlCenterHeaderExpandController = thisObject.getObjectField("this\$0")
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

                    val alphaNew  = ControlCenterHeaderExpandController.findMethodBestMatchIfExist(
                        "startFolmeAnimationAlpha",
                        View::class.java,
                        IFolme!!,
                        Float::class.java,
                        Boolean::class.java
                    )
                    val alphaOld = ControlCenterHeaderExpandController.findMethodBestMatchIfExist(
                            "access\$startFolmeAnimationAlpha",
                            ControlCenterHeaderExpandController,
                            View::class.java,
                            IFolme!!,
                            Float::class.java,
                            Boolean::class.java
                        )

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
                        alphaNew?.invoke(
                            null,
                            headers,
                            controlCenterCarrierViewFolme,
                            0f, z2
                        )
                        alphaOld?.invoke(
                            null,
                            ControlCenterHeaderExpandController,
                            headers,
                            controlCenterCarrierViewFolme,
                            0f, z2
                        )
                        val normalControlDateTranslationX =
                            controlCenterHeaderExpandController.getIntField(
                                "normalControlDateTranslationX"
                            )!!
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
                        alphaNew?.invoke(
                            null,
                            headers,
                            controlCenterCarrierViewFolme,
                            1f, z2
                        )
                        alphaOld?.invoke(
                            null,
                            ControlCenterHeaderExpandController,
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
            ) { args, result ->
                val f = args[0] as Float
                if (f in 0f..1f){
                    val controlCenterHeaderExpandController = thisObject.getObjectField("this\$0")
                    val context = controlCenterHeaderExpandController.getObjectFieldAs<Context>("context")

                    val headerController = controlCenterHeaderExpandController.getObjectField("headerController")
                    val combinedHeaderController = headerController.callMethod( "get")
                    val switching = combinedHeaderController.getBooleanField("switching")!!
                    if (!switching || f!= 1f){
                        val isVerticalMode = MiuiConfigs.callStaticMethodAs<Boolean>("isVerticalMode",context)
                        if (isVerticalMode ){
                            val f2 = 1-f
                            val normalControlStatusBarTranslationY = controlCenterHeaderExpandController.getIntField(
                                "normalControlStatusBarTranslationY"
                            )!!
                            header?.translationY = normalControlStatusBarTranslationY * f2
                        }

                    }


                }
            }

        }

        val PanelExpandControllerExt = findClass("com.miui.interfaces.shade.PanelExpandControllerExt")

        findClass(
            "com.android.systemui.controlcenter.shade.CombinedHeaderController").afterHookMethod(
            "onSwitchProgressChanged",
            Float::class.java
        ) { args, result ->
            val f = args[0] as Float
            val context = thisObject.getObjectFieldAs<Context>("context")
            val controlCenterExpandController = thisObject.getObjectField("controlCenterExpandController")
            val getAppearance = PanelExpandControllerExt.findMethodExactIfExists("getAppearance")
            if (!(getAppearance?.invoke(controlCenterExpandController) as Boolean)) {
                return@afterHookMethod
            }

            if ( f <= 0.5f ){
                val controlLocationX =  thisObject.getFloatField("controlLocationX")!!
                val notificationLocationY =  thisObject.getFloatField("notificationLocationY")!!
                val notificationLocationX =  thisObject.getFloatField("notificationLocationX")!!
                val controlLocationY =  thisObject.getFloatField("controlLocationY")!!
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

    private fun startMethodsHook1() {
        val CommonUtils = findClass("miui.systemui.util.CommonUtils")
        findClass(
            "com.android.systemui.controlcenter.shade.ControlCenterHeaderController"
        ).afterHookMethod(
            "updateConstraint"
        ) { args, result ->

            val fakeStatusBarViewController  = thisObject.getObjectField("fakeStatusBarViewController")?:return@afterHookMethod

            val sysUIContext   = thisObject.getObjectField("sysUIContext") as Context
            val parent = thisObject.callMethod("getView") as ViewGroup
            val mContext = thisObject.callMethod("getContext") as Context
            val res = mContext.resources

            val constraintSet = ConstraintSet(ClassLoaderProvider.safeClassLoader)

            //val header_status_bar_icon:Int = sysUIContext.resources.get("header_status_bar_icons", "id", "miui.systemui.plugin");

            val header_carrier_vertical_mode_margin_bottom = mContext.resources.getIdentifier("header_carrier_vertical_mode_margin_bottom","dimen",plugin)

            val header_status_bar_icons: Int = res.getId("header_status_bar_icons", plugin)
            val header_date: Int = res.getId("header_date",plugin)

            val header_carrier_view:Int = res.getId("header_carrier_view", plugin)
            val privacy_container:Int = res.getId("privacy_container", plugin)
            log(""+header_status_bar_icons+header_date+header_carrier_view+privacy_container)
            constraintSet.constrainWidth(header_status_bar_icons, -2)
            constraintSet.constrainHeight(header_status_bar_icons, -2)
            constraintSet.constrainWidth(header_date, -2)
            constraintSet.constrainHeight(header_date, -2)
            constraintSet.constrainWidth(header_carrier_view, -2)
            constraintSet.constrainHeight(header_carrier_view, -2)
            val header_privacy_container_height:Int = res.getIdentifier("header_privacy_container_height", "dimen", plugin)

            constraintSet.constrainWidth(privacy_container, -2)
            constraintSet.constrainHeight(
                privacy_container,
                res.getDimensionPixelSize(header_privacy_container_height)
            )

            val INSTANCE = CommonUtils.getStaticObjectField("INSTANCE")
            val orientation = INSTANCE.callMethod("getInVerticalMode",mContext) as Boolean

            if (orientation) {
                constraintSet.connect(header_status_bar_icons, 4, 0, 4);
                constraintSet.connect(header_date, 3, header_status_bar_icons, 3);
                constraintSet.connect(header_date, 4, header_status_bar_icons, 4);
                constraintSet.createHorizontalChainRtl(0, 6, 0, 7, intArrayOf(header_date, header_status_bar_icons), null as FloatArray? , 1);
                val dimensionPixelSize = res.getDimensionPixelSize(header_carrier_vertical_mode_margin_bottom);
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
            constraintSet.applyTo(parent)

            return@afterHookMethod
        }

    }

    fun addButton(
        controlCenterHeaderView: ViewGroup,
    ): LinearLayout {
        val context = controlCenterHeaderView.context
        val res = controlCenterHeaderView.resources

        val size = (getDimensionPixelOffset(res,"shade_header_control_center_carrier_text_size", XposedCore.hookedPackageName))/ 2 * 3
        val bottom = (getDimensionPixelOffset(res,"shade_header_bottom_padding", XposedCore.hookedPackageName)*2.85).toInt()
        //dpToPx(res,21.4f).toInt()
        val lp = ViewGroup.MarginLayoutParams(size, size).apply {
            bottomMargin = bottom
            //topMargin = 100
        }

        val setting = Button(context).apply {
            setBackgroundResource(R.drawable.ic_header_settings)
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
            id = R.id.cc_header_edit
            setBackgroundResource(R.drawable.ic_controls_edit)
            layoutParams =lp
        }
        logD("id = ${edit.id}\nedit = ${R.id.cc_header_edit}")
        Settings.System.putInt(context.contentResolver,"cc_edit_Id",R.id.cc_header_edit)

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

        logD("ControlCenterHeaderController ${controlCenterHeaderView.findViewById<View>(R.id.cc_header_edit)}")

        return header
    }
}


