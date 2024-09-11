package com.chaos.hyperstar.hook.app.plugin

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.SystemClock
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
import com.chaos.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


class QSListView : BaseHooker() {

    val clickClose = XSPUtils.getBoolean("list_tile_click_close",false)
    val labelMode = XSPUtils.getInt("is_list_label_mode",0)
    val labelSize = XSPUtils.getFloat("list_label_size",13f)
    val labelWidth = XSPUtils.getFloat("list_label_width",100f)/100
    val labelMarquee = XSPUtils.getBoolean("list_tile_label_marquee",false)
    val tileColorForIcon = XSPUtils.getBoolean("qs_list_tile_color_for_icon",false)
    val listSpacingY = XSPUtils.getFloat("list_spacing_y",100f)/100
    val listLabelSpacingY = XSPUtils.getFloat("list_label_spacing_y",100f)/100
    val isQSListTileRadius = XSPUtils.getBoolean("is_qs_list_tile_radius",false)

    val qsListTileRadius = XSPUtils.getFloat("qs_list_tile_radius",20f)

    val listIconTop = if (labelMode == 2) XSPUtils.getFloat("list_icon_top", 0F)/100 else 8f

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook(classLoader)
        qsTileRadius(classLoader)
    }

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        val QSItemViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        val QSItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)

        val MainPanelModeController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode",classLoader)

        if (clickClose){
            XposedHelpers.findAndHookMethod(QSTileItemView, "onFinishInflate\$lambda-0", QSTileItemView,View::class.java,object : XC_MethodHook() {

                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    val qSTileItemView = param?.args?.get(0) as FrameLayout

                    val lastTriggeredTime = XposedHelpers.getLongField(qSTileItemView,"lastTriggeredTime")

                    val elapsedRealtime = SystemClock.elapsedRealtime()
                    if (elapsedRealtime > lastTriggeredTime + 200) {
                        val clickAction = XposedHelpers.getObjectField(qSTileItemView,"clickAction")
                        if (clickAction == null){
                            starLog.log("clickAction == null")
                            return
                        }

                        val enumConstants: Array<out Any>? = MainPanelModeController.getEnumConstants()
                        if (enumConstants == null){
                            starLog.log("enumConstants == null")
                            return
                        }
                        val mainPanelMode = XposedHelpers.getObjectField(qSTileItemView,"mode")
                        if (mainPanelMode != enumConstants[2]) {
                            val mContext = qSTileItemView.context
                            collapseStatusBar(mContext)
                        }else{
                            starLog.log("mainPanelMode == edit")

                        }
                    }
                }

            })

        }

        if (labelMarquee || labelMode!=0 ){

            XposedHelpers.findAndHookConstructor(QSItemViewHolder,QSItemView,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    starLog.log("QSItemViewHolder is find")
                    val qSItemView = XposedHelpers.callMethod(thisObj,"getQsItemView") as FrameLayout

                    val label = qSItemView.findViewByIdName("tile_label") as TextView
                    val icon = qSItemView.findViewByIdName("icon_frame") as FrameLayout

                    if (labelMode == 1){
                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
                        qSItemView.removeView(label)
                        qSItemView.removeView(icon)
                        qSItemView.addView(icon,0)
                        qSItemView.addView(label,1)
                        val layoutParam =  label.layoutParams
                        layoutParam.width = icon.layoutParams.width/10*9
                        label.layoutParams = layoutParam
                    } else if (labelMode == 2){

                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP,labelSize)
                        val layoutParam =  label.layoutParams
                        layoutParam.width = (label.layoutParams.width*labelWidth).toInt()
                        label.layoutParams = layoutParam

                    }

                    if(labelMarquee){
                        label.ellipsize = TextUtils.TruncateAt.MARQUEE
                        label.focusable = View.NOT_FOCUSABLE
                        label.isSelected = true
                        label.setSingleLine()
                    }



                }
            })
        }


        val CommonUtils = XposedHelpers.findClass("miui.systemui.util.CommonUtils", classLoader)

        if ( labelMode != 0 ){
            val DrawableUtils = XposedHelpers.findClass("miui.systemui.util.DrawableUtils",classLoader)

            XposedHelpers.findAndHookMethod(QSTileItemView, "changeExpand", object : XC_MethodReplacement() {

                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    val thisObj = param?.thisObject as FrameLayout
                    val label = thisObj.findViewByIdName("tile_label") as TextView
                    val isShowLabel = XposedHelpers.callMethod(thisObj,"getShowLabel") as Boolean
                    val y : Float
                    var space : Int = XposedHelpers.getIntField(thisObj,"containerHeight")
                    val labelHeight = XposedHelpers.getIntField(thisObj,"labelHeight")
                    if (isShowLabel){
                        if (labelMode == 2){
                            y = 0f
                            space += labelHeight
                            space = (space*listLabelSpacingY).toInt()

                        }else if (labelMode == 1){

                            y = 2f

                        }
                        else{
                            return null
                        }
                    }else{
                        y = labelHeight.toFloat()
                        space = (space*listSpacingY).toInt()
                    }
                    label.translationY = y
                    val setLayoutHeight = XposedHelpers.findMethodBestMatch(
                        CommonUtils,"setLayoutHeight\$default",
                        CommonUtils,Class.forName("android.view.View"), Int::class.java, Boolean::class.java,Int::class.java,Object::class.java
                    )

                    setLayoutHeight.invoke(CommonUtils.newInstance(),CommonUtils.newInstance(), thisObj, space, false, 2, null)

                    return null;
                }

            })

            val QSTileItemIconView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",classLoader)

            var height :Int = 0


            XposedHelpers.findAndHookMethod(QSTileItemIconView,"getProperIconSize",Drawable::class.java,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    height = param?.result as Int
                }
            })
            XposedHelpers.findAndHookMethod(DrawableUtils, "combine",Drawable::class.java,Drawable::class.java,Int::class.java, object : XC_MethodReplacement() {
                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                    val args: Array<Any>? = param?.args
                    val dra = args?.get(0) as Drawable
                    val dra2 = args.get(1) as Drawable
                    val i = args.get(2) as Int

                    val icon = LayerDrawable(arrayOf(dra, dra2))
                    icon.setLayerGravity(1,i)

                    if (height == 0) return icon
                    if (listIconTop != 0f){
                        icon.setLayerInsetBottom(1,
                            (height/listIconTop).toInt()
                        )

                    }

                    return icon

                    }

                })


        }

        if (tileColorForIcon || labelMode != 0){
            XposedHelpers.findAndHookMethod(QSTileItemView, "updateTextAppearance", object : XC_MethodReplacement() {

                override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                    return null
                }
            })

        }



        if (tileColorForIcon){
            XposedHelpers.findAndHookMethod(QSTileItemView, "onStateUpdated",
                Boolean::class.java, object : XC_MethodHook(){
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        super.beforeHookedMethod(param)
                        val thisObj = param?.thisObject  as FrameLayout
                        val mode = XposedHelpers.getObjectField(thisObj,"mode") as Enum<*>

                        val copy :Any

                        val Companion = XposedHelpers.getStaticObjectField(QSItemView,"Companion")
                        val sta = XposedHelpers.getObjectField(thisObj,"state")

                        if (mode.ordinal == 0){
                            if (sta == null){
                                return
                            }
                            copy = sta

                        }else{
                            var customizeState = XposedHelpers.getObjectField(thisObj,"customizeState")
                            if (customizeState == null){
                                customizeState = sta
                                if (customizeState == null){
                                    return
                                }
                            }
                            copy = XposedHelpers.callMethod(customizeState,"copy")
                            XposedHelpers.setIntField(copy,"state",1)
                            XposedHelpers.callMethod(Companion,"setRestrictedCompat", copy,false)


                        }

                        val state:Int = XposedHelpers.getIntField(copy,"state")
                        val states = XposedHelpers.callMethod(Companion,"isRestrictedCompat",copy) as Boolean

                        val label = thisObj.findViewByIdName("tile_label") as TextView
                        val icon = XposedHelpers.callMethod(thisObj,"getIcon")

                        val enable = XposedHelpers.getIntField(icon,"iconColor")
                        val off = XposedHelpers.getIntField(icon,"iconColorOff")
                        val unavailable = XposedHelpers.getIntField(icon,"iconColorUnavailable")
                        val restrict = XposedHelpers.getIntField(icon,"iconColorRestrict")

                        if (state == 0) {
                            label.setTextColor(unavailable)
                        } else if (state == 1 && states) {
                            label.setTextColor(restrict)
                        } else if (state != 2) {
                            label.setTextColor(off)
                        } else {
                            label.setTextColor(enable)
                        }

                        //val ic = XposedHelpers.callMethod(thisObj,"getIcon")



                    }


                })



        }




    }

    private fun qsTileRadius(classLoader: ClassLoader?) {

        val classTile = XposedHelpers.findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        )
        if (isQSListTileRadius){
            XposedHelpers.findAndHookMethod(
                classTile,
                "setCornerRadius",
                Float::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {

                        val pluginContext: Context = XposedHelpers.getObjectField(param.thisObject, "pluginContext") as Context;


                        param.args[0] = dpToPx(pluginContext.resources,qsListTileRadius)
                    }

                    override fun afterHookedMethod(param: MethodHookParam) {
                    }
                })

        }

        hookAllMethods(classLoader, "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            "updateIcon",
            object : MethodHook {

                override fun before(param: XC_MethodHook.MethodHookParam?) {
                    if (isQSListTileRadius){
                        val pluginContext: Context =
                            XposedHelpers.getObjectField(param?.thisObject, "pluginContext") as Context;
                        val warning: Int = pluginContext.resources
                            .getIdentifier("qs_background_warning", "drawable", "miui.systemui.plugin");
                        val enabled: Int = pluginContext.resources
                            .getIdentifier("qs_background_enabled", "drawable", "miui.systemui.plugin");
                        val restricted: Int = pluginContext.resources.getIdentifier(
                            "qs_background_restricted",
                            "drawable",
                            "miui.systemui.plugin"
                        );
                        val disabled: Int = pluginContext.getResources().getIdentifier(
                            "qs_background_disabled",
                            "drawable",
                            "miui.systemui.plugin"
                        );
                        val unavailable: Int = pluginContext.getResources().getIdentifier(
                            "qs_background_unavailable",
                            "drawable",
                            "miui.systemui.plugin"
                        );
                        val warningD: Drawable = pluginContext.getTheme().getDrawable(warning);
                        val enabledD: Drawable = pluginContext.getTheme().getDrawable(enabled);
                        val restrictedD: Drawable = pluginContext.getTheme().getDrawable(restricted);
                        val disabledD: Drawable = pluginContext.getTheme().getDrawable(disabled);
                        val unavailableD: Drawable = pluginContext.getTheme().getDrawable(unavailable);
                        if (warningD is GradientDrawable) {
                            warningD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        }
                        if (enabledD is GradientDrawable) {
                            enabledD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        }
                        if (restrictedD is GradientDrawable) {
                            restrictedD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        }
                        if (disabledD is GradientDrawable) {
                            disabledD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        }
                        if (unavailableD is GradientDrawable) {
                            unavailableD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        }

                    }

                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {
                    if (labelMode == 0){
                        return
                    }
                    val thisObj = param?.thisObject
                    val Icon: ImageView =
                        XposedHelpers.getObjectField(thisObj, "icon") as ImageView;

                    val z = param?.args?.get(1) as Boolean

                    if (z) {
                        val combine = Icon.drawable

                        if (combine !is LayerDrawable) {
                            return
                        }

                        val num = combine.numberOfLayers

                        val index = num - 1
                        val disabledBg = combine.getDrawable(0)

                        val invisibleDrawableCompat = combine.getDrawable(index)

                        val icons: LayerDrawable

                        if (num == 2) {
                            icons = LayerDrawable(arrayOf(disabledBg, invisibleDrawableCompat))


                        } else if (num == 3) {
                            val enabledBg = combine.getDrawable(1)
                            icons = LayerDrawable(
                                arrayOf(
                                    disabledBg,
                                    enabledBg,
                                    invisibleDrawableCompat
                                )
                            )

                        } else {
                            return
                        }

                        val height  = combine.getLayerHeight(index)
                        val width = combine.getLayerWidth(index)

                        icons.setLayerGravity(index, Gravity.CENTER)
                        if (listIconTop != 0f){
                            icons.setLayerInsetBottom(index,
                                (height/listIconTop).toInt()
                            )

                        }
                        icons.setLayerSize(index, width, height)

                        Icon.setImageDrawable(icons)

                    }


                }
            })
    }

    fun dpToPx(resources: Resources, dp: Float): Float {
        // 获取屏幕的密度
        val density = resources.displayMetrics.density

        // 转换 dp 到 px
        return dp * density
    }


}