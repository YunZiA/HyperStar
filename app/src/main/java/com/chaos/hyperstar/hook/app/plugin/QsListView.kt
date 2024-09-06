package com.chaos.hyperstar.hook.app.plugin

import android.graphics.Bitmap
import android.graphics.Color
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.view.get
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Field
import java.util.Objects

class QsListView : BaseHooker() {

    val labelInside = XSPUtils.getBoolean("list_tile_label_inside",false)
    val labelSize = XSPUtils.getFloat("list_label_size",13f)
    val labelMarquee = XSPUtils.getBoolean("list_tile_label_marquee",false)
    val tileColorForIcon = XSPUtils.getBoolean("qs_list_tile_color_for_icon",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook(classLoader)
    }

    private fun startMethodsHook(classLoader: ClassLoader?) {

        val QSItemViewHolder = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        val QSItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)

        XposedHelpers.findAndHookConstructor(QSItemViewHolder,QSItemView,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject
                starLog.log("QSItemViewHolder is find")
                val qSItemView = XposedHelpers.callMethod(thisObj,"getQsItemView") as FrameLayout

                val label = qSItemView.findViewByIdName("tile_label") as TextView
                val icon = qSItemView.findViewByIdName("icon_frame") as FrameLayout

                if(labelMarquee){
                    label.ellipsize = TextUtils.TruncateAt.MARQUEE
                    label.focusable = View.NOT_FOCUSABLE
                    label.isSelected = true
                    label.setSingleLine()
                }
                if (labelInside){
                    qSItemView.removeView(label)
                    qSItemView.removeView(icon)
                    qSItemView.addView(icon,0)
                    qSItemView.addView(label,1)
                    val layoutParam =  label.layoutParams
                    layoutParam.width = icon.layoutParams.width
                    label.layoutParams = layoutParam
                }



            }
        })

        val CommonUtils = XposedHelpers.findClass("miui.systemui.util.CommonUtils", classLoader)

        XposedHelpers.findAndHookMethod(QSTileItemView, "changeExpand", object : XC_MethodReplacement() {

            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                val thisObj = param?.thisObject as FrameLayout
                val label = thisObj.findViewByIdName("tile_label") as TextView
                val isShowLabel = XposedHelpers.callMethod(thisObj,"getShowLabel") as Boolean
                val y : Float
                var space : Int = XposedHelpers.getIntField(thisObj,"containerHeight")
                val labelHeight = XposedHelpers.getIntField(thisObj,"labelHeight")
                if (isShowLabel){
                    if (!labelInside){
                        y = 0f
                        space += labelHeight

                    }else{
                        y = labelHeight.toFloat()/11.25f

                    }
                }else{
                    y = labelHeight.toFloat()
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

        XposedHelpers.findAndHookMethod(QSTileItemView, "onFinishInflate", object : XC_MethodHook(){
            override fun beforeHookedMethod(param: MethodHookParam?) {
                super.beforeHookedMethod(param)
                val thisObj = param?.thisObject as FrameLayout
                val label = thisObj.findViewByIdName("tile_label") as TextView
                if(labelSize != 13f){
                    
                    label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, if (labelInside) 8f else labelSize)

                }


            }
        })

        XposedHelpers.findAndHookMethod(QSTileItemView, "updateTextAppearance", object : XC_MethodReplacement() {

            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                super.beforeHookedMethod(param)

                return null
            }
        })

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
                        //starLog.log("onStateUpdated at ${mode.ordinal}  in ${ state} is ${states}")

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

                    }
                })

        }



    }


}