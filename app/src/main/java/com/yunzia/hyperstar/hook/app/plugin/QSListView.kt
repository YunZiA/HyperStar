package com.yunzia.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
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
import yunzia.utils.DensityUtil.Companion.dpToPx
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers


class QSListView : BaseHooker() {

    private val clickClose = XSPUtils.getBoolean("list_tile_click_close",false)
    val labelMode: Int = XSPUtils.getInt("is_list_label_mode",0)
    val labelSize = XSPUtils.getFloat("list_label_size",13f)
    val labelWidth = XSPUtils.getFloat("list_label_width",100f)/100f
    val labelMarquee = XSPUtils.getBoolean("list_tile_label_marquee",false)
    private val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state",0)
    val listSpacingY = XSPUtils.getFloat("list_spacing_y",100f)/100
    val listLabelSpacingY = XSPUtils.getFloat("list_label_spacing_y",100f)/100
    val isQSListTileRadius = XSPUtils.getBoolean("is_qs_list_tile_radius",false)

    val qsListTileRadius = XSPUtils.getFloat("qs_list_tile_radius",20f)

    val listIconTop = if (labelMode == 2) XSPUtils.getFloat("list_icon_top", 0f)/100 else 1/8f
    val listLabelTop = XSPUtils.getFloat("list_label_top", 0f)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook()
        qsTileRadius()
        fixTileIcon()
    }

    private fun fixTileIcon() {

        val fix = XSPUtils.getBoolean("fix_list_tile_icon_scale",false)
        if (!fix) return

        val QSTileItemIconView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",classLoader)

        XposedHelpers.findAndHookMethod(QSTileItemIconView,"getProperIconSize",Drawable::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                val tileSize = XposedHelpers.getFloatField(thisObj,"tileSize").toInt()
                //height = tileSize

                val drawable = param?.args?.get(0) as Drawable

                val isCustomTile = XposedHelpers.getBooleanField(thisObj,"isCustomTile")
                if (isCustomTile) return

                if(drawable !is AnimatedVectorDrawable) return

                val customTileSize = XposedHelpers.getFloatField(thisObj,"customTileSize").toInt()
                if (drawable.intrinsicHeight < customTileSize){
                    param.result = customTileSize

                }

            }
        })
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

        val QSItemViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        val QSItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)

        val MainPanelModeController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",classLoader)

        if (clickClose){
            XposedHelpers.findAndHookMethod(QSTileItemView, "onFinishInflate\$lambda-0", QSTileItemView,View::class.java,object : XC_MethodHook() {

                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val qSTileItemView = param?.args?.get(0) as FrameLayout

                    val lastTriggeredTime = XposedHelpers.getLongField(qSTileItemView,"lastTriggeredTime")

                    val elapsedRealtime = SystemClock.elapsedRealtime()
                    if (elapsedRealtime > lastTriggeredTime + 200) {
                        val clickAction =
                            XposedHelpers.getObjectField(qSTileItemView, "clickAction")
                        if (clickAction == null) {
                            starLog.log("clickAction == null")
                            return
                        }

                        val enumConstants: Array<out Any>? =
                            MainPanelModeController.getEnumConstants()
                        if (enumConstants == null) {
                            starLog.log("enumConstants == null")
                            return
                        }
                        val mainPanelMode = XposedHelpers.getObjectField(qSTileItemView, "mode")
                        if (mainPanelMode != enumConstants[2]) {
                            val mContext = qSTileItemView.context
                            collapseStatusBar(mContext)
                        } else {
                            starLog.log("mainPanelMode == edit")

                        }
                    }
                }

//                override fun beforeHookedMethod(param: MethodHookParam?) {
//                    super.beforeHookedMethod(param)
//
//
//                }

            })

        }

        if (labelMarquee || labelMode!=0 ){

            XposedHelpers.findAndHookConstructor(QSItemViewHolder,QSItemView,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val qSItemView = XposedHelpers.callMethod(thisObj,"getQsItemView") as FrameLayout

                    val label = qSItemView.findViewByIdName("tile_label") as TextView
                    val icon = qSItemView.findViewByIdName("icon_frame") as FrameLayout

                    if (labelMode == 1){
                        qSItemView.removeView(label)
                        qSItemView.removeView(icon)
                        qSItemView.addView(icon,0)
                        qSItemView.addView(label,1)
                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)

                        val layoutParam =  label.layoutParams
                        layoutParam.width = icon.layoutParams.width/10*9
                        label.layoutParams = layoutParam
                    } else if (labelMode == 2){
                        qSItemView.removeView(label)
                        qSItemView.removeView(icon)
                        qSItemView.addView(icon,0)
                        qSItemView.addView(label,1)

                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP,labelSize)
                        val layoutParam =  label.layoutParams
                        qSItemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        starLog.log("${qSItemView.layoutParams.width}+${qSItemView.measuredWidth}")
                        val width = qSItemView.measuredWidth*labelWidth
                        layoutParam.width = width.toInt()
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

            XposedHelpers.findAndHookMethod(QSTileItemView, "changeExpand", object : XC_MethodReplacement() {

                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    val thisObj = param?.thisObject as FrameLayout
                    val res = thisObj.resources
                    val label = thisObj.findViewByIdName("tile_label") as TextView
                    val isShowLabel = XposedHelpers.callMethod(thisObj,"getShowLabel") as Boolean
                    val y : Float
                    var space : Int = XposedHelpers.getIntField(thisObj,"containerHeight")
                    val labelHeight = XposedHelpers.getIntField(thisObj,"labelHeight")
                    if (isShowLabel){
                        when (labelMode) {
                            2 -> {
                                y = dpToPx(res,listLabelTop)

                                space += labelHeight
                                space = (space*listLabelSpacingY).toInt()

                            }
                            1 -> {

                                y = 2f

                            }
                            else -> {
                                return null
                            }
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

        }

        if (tileColorForState != 0 || labelMode != 0){
            XposedHelpers.findAndHookMethod(QSTileItemView, "updateTextAppearance", object : XC_MethodReplacement() {

                override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                    return null
                }
            })

        }



        if (tileColorForState != 0){

            val disableColor = XSPUtils.getString("list_title_off_color", "null")
            val enableColor = XSPUtils.getString("list_title_on_color", "null")
            val restrictedColor = XSPUtils.getString("list_title_restricted_color", "null")
            val unavailableColor = XSPUtils.getString("list_title_unavailable_color", "null")

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

                        var off = XposedHelpers.getIntField(icon,"iconColorOff")
                        var enable = XposedHelpers.getIntField(icon,"iconColor")
                        var unavailable = XposedHelpers.getIntField(icon,"iconColorUnavailable")
                        var restrict = XposedHelpers.getIntField(icon,"iconColorRestrict")
                        if (tileColorForState == 2){
                            if (disableColor != "null")  off = Color.parseColor(disableColor)
                            if (enableColor != "null")  enable = Color.parseColor(enableColor)
                            if (restrictedColor != "null")  unavailable = Color.parseColor(restrictedColor)
                            if (unavailableColor != "null")  restrict = Color.parseColor(unavailableColor)
                        }


                        if (state == 0) {
                            label.setTextColor(unavailable)
                        } else if (state == 1 && states) {
                            label.setTextColor(restrict)
                        } else if (state != 2) {
                            label.setTextColor(off)
                        } else {
                            label.setTextColor(enable)
                        }


                    }


                })



        }




    }

    private fun qsTileRadius() {

        val classTile = XposedHelpers.findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        )
        if (isQSListTileRadius){

            XposedHelpers.findAndHookMethod(classTile,
                "setCornerRadius", Float::class.java, object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam) {

                        val pluginContext: Context = XposedHelpers.getObjectField(param.thisObject, "pluginContext") as Context;

                        param.args[0] = dpToPx(pluginContext.resources,qsListTileRadius)

                    }

                    override fun afterHookedMethod(param: MethodHookParam) {

                    }
                })

        }

        var height :Int = 0


        hookAllMethods(classLoader, "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            "updateIcon",
            object : MethodHook {

                override fun before(param: XC_MethodHook.MethodHookParam?) {
                    val thisObj = param?.thisObject
                    if ( labelMode != 0 ) {

                        val tileSize = XposedHelpers.getFloatField(thisObj,"tileSize").toInt()
                        height = tileSize
                    }
                    if (isQSListTileRadius){
                        val pluginContext: Context =
                            XposedHelpers.getObjectField(param?.thisObject, "pluginContext") as Context;
                        //val enableColor = pluginContext.resources.getIdentifier("qs_enabled_color","color","miui.systemui.plugin")

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
                        val warningD: Drawable = pluginContext.theme.getDrawable(warning);
                        val enabledD: Drawable = pluginContext.theme.getDrawable(enabled);
                        val restrictedD: Drawable = pluginContext.theme.getDrawable(restricted);
                        val disabledD: Drawable = pluginContext.theme.getDrawable(disabled);
                        val unavailableD: Drawable = pluginContext.theme.getDrawable(unavailable);
                        if (warningD is GradientDrawable) {
                            warningD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                            //warningD.setStroke(10,Color.RED)
                        }
                        if (enabledD is GradientDrawable) {

                            enabledD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                            //enabledD.setStroke(10,Color.RED)
                        }
                        if (restrictedD is GradientDrawable) {
                            restrictedD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                            //restrictedD.setStroke(10,Color.RED)
                        }
                        if (disabledD is GradientDrawable) {
                            disabledD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                            //disabledD.setStroke(10,Color.RED)
                        }
                        if (unavailableD is GradientDrawable) {
                            unavailableD.cornerRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                            //unavailableD.setStroke(10,Color.RED)
                        }

                    }

                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {
                    if (labelMode == 0){
                        return
                    }
                    val thisObj = param?.thisObject
                    //val disabledBg = XposedHelpers.getObjectField(thisObj,"disabledBg") as Drawable

                    val pluginContext: Context =
                        XposedHelpers.getObjectField(param?.thisObject, "pluginContext") as Context;
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

                        when (num) {
                            2 -> {
                                icons = LayerDrawable(arrayOf(disabledBg, invisibleDrawableCompat))

                            }
                            3 -> {
                                val enabledBg = combine.getDrawable(1)
                                icons = LayerDrawable(
                                    arrayOf(
                                        disabledBg,
                                        enabledBg,
                                        invisibleDrawableCompat
                                    )
                                )

                            }
                            else -> {
                                return
                            }
                        }

                        val height  = combine.getLayerHeight(index)
                        val width = combine.getLayerWidth(index)
                        val tileSize = XposedHelpers.getFloatField(thisObj,"tileSize").toInt()

                        icons.setLayerGravity(index, Gravity.CENTER)
                        if (listIconTop != 0f){
                            icons.setLayerInsetBottom(index,
                                (tileSize*listIconTop).toInt()
                            )

                        }
                        icons.setLayerSize(index, width, height)

                        Icon.setImageDrawable(icons)


                    }


                }
            })

        if ( labelMode != 0 ) {
            val DrawableUtils = XposedHelpers.findClass("miui.systemui.util.DrawableUtils", classLoader)

            XposedHelpers.findAndHookMethod(DrawableUtils, "combine", Drawable::class.java, Drawable::class.java, Int::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        val args: Array<Any> = param?.args as Array<Any>
                        val dra = args[0] as Drawable
                        val dra2 = args[1] as Drawable
                        val i = args[2] as Int

                        val icon = LayerDrawable(arrayOf(dra, dra2))
                        icon.setLayerGravity(1, i)

                        if (height == 0) return icon
                        if (listIconTop != 0f) {
                            icon.setLayerInsetBottom(1,
                                (height * listIconTop).toInt()
                            )

                        }

                        return icon

                    }

                })
        }
    }




}