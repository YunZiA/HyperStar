package com.yunzia.hyperstar.hook.app.plugin.os2

import android.content.ContentResolver
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.SystemClock
import android.provider.Settings
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import yunzia.utils.DensityUtil.Companion.dpToPx


class QSListView : BaseHooker() {

    private val clickClose = XSPUtils.getBoolean("list_tile_click_close",false)
    val labelMode: Int = XSPUtils.getInt("is_list_label_mode",0)
    val labelSize = XSPUtils.getFloat("list_label_size",13f)
    val isWordlessMode0: Int = XSPUtils.getInt("is_wordless_mode_0",0)
    val isWordlessMode2: Int = XSPUtils.getInt("is_wordless_mode_2",0)
    val labelWidth = XSPUtils.getFloat("list_label_width",100f)/100f
    val labelMarquee = XSPUtils.getBoolean("list_tile_label_marquee",false)
    private val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state",0)
    val listSpacingY = XSPUtils.getFloat("list_spacing_y",100f)/100
    val listLabelSpacingY = XSPUtils.getFloat("list_label_spacing_y",100f)/100
    val isQSListTileRadius = XSPUtils.getBoolean("is_qs_list_tile_radius",false)

    val qsListTileRadius = XSPUtils.getFloat("qs_list_tile_radius",20f)

    val listIconTop = if (labelMode == 2) XSPUtils.getFloat("list_icon_top", 0f)/100 else 1/7f
    val listLabelTop = XSPUtils.getFloat("list_label_top", 0f)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook()
        qsTileRadius()
        fixTileIcon()
        fixBrightnessIcon()
    }


    private fun fixBrightnessIcon() {
        if ( labelMode != 0 ){
            val BrightnessPanelTilesController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.brightness.BrightnessPanelTilesController",classLoader)


//            XposedHelpers.findAndHookMethod(BrightnessPanelTilesController,"getTileSpecs",object :XC_MethodHook(){
//                override fun afterHookedMethod(param: MethodHookParam?) {
//                    super.afterHookedMethod(param)
//                    val list = param?.result as List<*>
//                    list.toMutableList().removeLast()
////                    list.add("reduce_brightness")
////
////                    list.add("reduce_brightness")
//                    param.result = list
//                    starLog.log("${param.result}")
//                }
//            })
            XposedHelpers.findAndHookMethod(BrightnessPanelTilesController,"getTileSpecs",object :XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any {

                    val list = listOf("night","autobrightness",  "papermode")
                    val list2 = listOf("autobrightness", "night", "reduce_brightness")
                    return list
                }

            })

//            XposedHelpers.findAndHookMethod(BrightnessPanelTilesController,"getQSTileItemView",String::class.java,object :XC_MethodHook(){
//                override fun afterHookedMethod(param: MethodHookParam?) {
//                    super.afterHookedMethod(param)
//                    val inflate = param?.result as ViewGroup
//                    val icon = inflate.findViewByIdName("icon_frame") as FrameLayout
//                    val label = inflate.findViewByIdName("tile_label") as TextView
//
//                    if (labelMode == 1){
//                        inflate.removeView(label)
//                        inflate.removeView(icon)
//                        inflate.addView(icon,0)
//                        inflate.addView(label,1)
//                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
//
//                        val layoutParam =  label.layoutParams
//                        icon.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                        layoutParam.width = icon.measuredWidth/9*8
//                        label.layoutParams = layoutParam
//                    } else if (labelMode == 2){
//                        inflate.removeView(label)
//                        inflate.removeView(icon)
//                        inflate.addView(icon,0)
//                        inflate.addView(label,1)
//
//                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP,labelSize)
//                        val layoutParam =  label.layoutParams
//                        inflate.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//                        starLog.log("${inflate.layoutParams.width}+${inflate.measuredWidth}")
//                        val width = inflate.measuredWidth*labelWidth
//                        layoutParam.width = width.toInt()
//                        label.layoutParams = layoutParam
//
//                    }
//                    if(labelMarquee){
//                        label.ellipsize = TextUtils.TruncateAt.MARQUEE
//                        label.focusable = View.NOT_FOCUSABLE
//                        label.isSelected = true
//                        label.setSingleLine()
//                    }
//
//
//
//                }
//
//            })

        }
    }

    private fun fixTileIcon() {

        val fix = XSPUtils.getBoolean("fix_list_tile_icon_scale",false)
        if (!fix) return

        val QSTileItemIconView = XposedHelpers.findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",classLoader)

        XposedHelpers.findAndHookMethod(QSTileItemIconView,"getProperIconSize",Drawable::class.java,object : XC_MethodHook(){
            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val thisObj = param?.thisObject

                val drawable = param?.args?.get(0) as Drawable

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
        val QSListController = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",classLoader)
        val WhenMappings = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController\$WhenMappings",classLoader)
        val CommonUtils = findClass("miui.systemui.util.CommonUtils",classLoader)

        val AnimValue = findClass("miui.systemui.controlcenter.panel.detail.DetailPanelAnimator\$AnimValue",classLoader)

        val DetailPanelAnimator = findClass("miui.systemui.controlcenter.panel.detail.DetailPanelAnimator",classLoader)


//        XposedHelpers.findAndHookMethod(QSItemViewHolder,"getTarget",object :XC_MethodReplacement(){
//
//            override fun replaceHookedMethod(param: MethodHookParam?): Any {
//                val thisObj = param?.thisObject
//                val itemView = XposedHelpers.getObjectField(thisObj,"itemView")
//
//                return itemView
//
//            }
//
//        })
//        XposedHelpers.findAndHookMethod(DetailPanelAnimator,"frameCallback",object :XC_MethodReplacement(){
//
//            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
//                val thisObj = param?.thisObject
//                val fromView = XposedHelpers.getObjectField(thisObj,"fromView") ?: return null
//                val animValue = XposedHelpers.getObjectField(thisObj,"animValue") ?: return null
//                val wA = XposedHelpers.callMethod(animValue,"getWidthA") as Int
//                val wB = XposedHelpers.callMethod(animValue,"getWidthB") as Int
//                val sX = XposedHelpers.getFloatField(thisObj,"sizeX")
//                val hA = XposedHelpers.callMethod(animValue,"getHeightA") as Int
//                val hB = XposedHelpers.callMethod(animValue,"getHeightB") as Int
//                val sY = XposedHelpers.getFloatField(thisObj,"sizeY")
//                val widthA: Float = wA + ((wB - wA) * sX)
//                val heightA: Float =hA + ((hB - hA) * sY)
//                val getToCenterX =  XposedHelpers.callMethod(animValue,"getToCenterX") as Float
//                val getFromCenterX =  XposedHelpers.callMethod(animValue,"getFromCenterX") as Float
//                val positionX = XposedHelpers.getFloatField(thisObj,"positionX")
//                val getToCenterY =  XposedHelpers.callMethod(animValue,"getToCenterY") as Float
//                val getFromCenterY =  XposedHelpers.callMethod(animValue,"getFromCenterY") as Float
//                val positionY = XposedHelpers.getFloatField(thisObj,"positionY")
//                val getScreenLeftA =  XposedHelpers.callMethod(animValue,"getScreenLeftA") as Int
//                val getLeftA =  XposedHelpers.callMethod(animValue,"getLeftA") as Int
//                val getScreenTopA =  XposedHelpers.callMethod(animValue,"getScreenTopA") as Int
//                val getTopA =  XposedHelpers.callMethod(animValue,"getTopA") as Int
//                val getScreenLeftB =  XposedHelpers.callMethod(animValue,"getScreenLeftB") as Int
//                val getLeftB =  XposedHelpers.callMethod(animValue,"getLeftB") as Int
//                val getScreenTopB =  XposedHelpers.callMethod(animValue,"getScreenTopB") as Int
//                val getTopB =  XposedHelpers.callMethod(animValue,"getTopB") as Int
//                val f = 2f
//                val toCenterX: Float =
//                    (((getToCenterX - getFromCenterX) * positionX) + getFromCenterX) - (widthA / f)
//                val toCenterY: Float =
//                    (((getToCenterY - getFromCenterY) * positionY) + getFromCenterY) - (heightA / f)
//                val screenLeftA: Float =
//                    (toCenterX - getScreenLeftA) + getLeftA
//                val screenTopA: Float =
//                    (toCenterY - getScreenTopA) + getTopA
//                val screenLeftB: Float =
//                    (toCenterX - getScreenLeftB) + getLeftB
//                val screenTopB: Float =
//                    (toCenterY - getScreenTopB) + getTopB
//
//                val target = XposedHelpers.callMethod(fromView,"getTarget") as View
//                val parent = target.parent as ViewGroup
//                val label = parent.findViewByIdName("tile_label")
//                parent?.setLeftTopRightBottom(screenLeftA.toInt(), screenTopA.toInt(),(screenLeftA + widthA).toInt(),(screenTopA + heightA).toInt());
//                return  null
//                //label?.setLeftTopRightBottom(target.left,target.top,target.right,target.bottom)
//            }
//        })

//        XposedHelpers.findAndHookMethod(DetailPanelAnimator,"frameCallback",object :XC_MethodHook(){
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                val thisObj = param?.thisObject
//                val fromView = XposedHelpers.getObjectField(thisObj,"fromView") ?: return
//                val animValue = XposedHelpers.getObjectField(thisObj,"animValue") ?: return
//                val wA = XposedHelpers.callMethod(animValue,"getWidthA") as Int
//                val wB = XposedHelpers.callMethod(animValue,"getWidthB") as Int
//                val sX = XposedHelpers.getFloatField(thisObj,"sizeX")
//                val hA = XposedHelpers.callMethod(animValue,"getHeightA") as Int
//                val hB = XposedHelpers.callMethod(animValue,"getHeightB") as Int
//                val sY = XposedHelpers.getFloatField(thisObj,"sizeY")
//                val widthA: Float = wA + ((wB - wA) * sX)
//                val heightA: Float =hA + ((hB - hA) * sY)
//                val getToCenterX =  XposedHelpers.callMethod(animValue,"getToCenterX") as Float
//                val getFromCenterX =  XposedHelpers.callMethod(animValue,"getFromCenterX") as Float
//                val positionX = XposedHelpers.getFloatField(thisObj,"positionX")
//                val getToCenterY =  XposedHelpers.callMethod(animValue,"getToCenterY") as Float
//                val getFromCenterY =  XposedHelpers.callMethod(animValue,"getFromCenterY") as Float
//                val positionY = XposedHelpers.getFloatField(thisObj,"positionY")
//                val getScreenLeftA =  XposedHelpers.callMethod(animValue,"getScreenLeftA") as Int
//                val getLeftA =  XposedHelpers.callMethod(animValue,"getLeftA") as Int
//                val getScreenTopA =  XposedHelpers.callMethod(animValue,"getScreenTopA") as Int
//                val getTopA =  XposedHelpers.callMethod(animValue,"getTopA") as Int
//                val getScreenLeftB =  XposedHelpers.callMethod(animValue,"getScreenLeftB") as Int
//                val getLeftB =  XposedHelpers.callMethod(animValue,"getLeftB") as Int
//                val getScreenTopB =  XposedHelpers.callMethod(animValue,"getScreenTopB") as Int
//                val getTopB =  XposedHelpers.callMethod(animValue,"getTopB") as Int
//                val f = 2f
//                val toCenterX: Float =
//                    (((getToCenterX - getFromCenterX) * positionX) + getFromCenterX) - (widthA / f)
//                val toCenterY: Float =
//                    (((getToCenterY - getFromCenterY) * positionY) + getFromCenterY) - (heightA / f)
//                val screenLeftA: Float =
//                    (toCenterX - getScreenLeftA) + getLeftA
//                val screenTopA: Float =
//                    (toCenterY - getScreenTopA) + getTopA
//                val screenLeftB: Float =
//                    (toCenterX - getScreenLeftB) + getLeftB
//                val screenTopB: Float =
//                    (toCenterY - getScreenTopB) + getTopB
//
//                val target = XposedHelpers.callMethod(fromView,"getTarget") as View
//                val parent = target.parent as ViewGroup
//                val label = parent.findViewByIdName("tile_label")
//                label?.translationY = screenTopA
//                label?.translationX = screenLeftA
//                //label?.setLeftTopRightBottom(screenLeftA.toInt(), screenTopA.toInt(),(screenLeftA + widthA).toInt(),(screenTopA + heightA).toInt());
//
//                //label?.setLeftTopRightBottom(target.left,target.top,target.right,target.bottom)
//
//
//            }
//        })

//        XposedHelpers.findAndHookMethod(DetailPanelAnimator,"calculateViewValues",object :XC_MethodHook() {
//
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                val thisObj = param?.thisObject
//                val fromView = XposedHelpers.getObjectField(thisObj, "fromView") ?: return
//
//                val target = XposedHelpers.callMethod(fromView, "getTarget") as View
//                val parent = target.parent as View
//                val label = parent.findViewByIdName("tile_label")
//                val commonUtils = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")
//                val iArr = IntArray(2)
//                XposedHelpers.callMethod(commonUtils,"getLocationInWindowWithoutTransform",label,iArr)
//
//            }
//
//        })

//        XposedHelpers.findAndHookMethod(DetailPanelAnimator,"calculateViewValues",object :XC_MethodReplacement(){
//
//            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
//                val thisObj = param?.thisObject
//                val fromView = XposedHelpers.getObjectField(thisObj,"fromView") ?: return null
//                val toView = XposedHelpers.callMethod(thisObj,"getToView")
//
//                val target = XposedHelpers.callMethod(fromView,"getTarget") as View
//                val parent = target.parent as View
//                val frame = XposedHelpers.callMethod(toView,"getFrame") as View
//                val iArr = IntArray(2)
//                val commonUtils = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")
//                XposedHelpers.callMethod(commonUtils,"getLocationInWindowWithoutTransform",parent,iArr)
//                val iArr2 = IntArray(2)
//                XposedHelpers.callMethod(commonUtils,"getLocationInWindowWithoutTransform",frame,iArr2)
//                var z = false
//
//                val animValue = XposedHelpers.newInstance(AnimValue,
//                    iArr[0],
//                    iArr[1],
//                    parent.left,
//                    parent.top,
//                    parent.width,
//                    parent.height,
//                    iArr2[0],
//                    iArr2[1],
//                    frame.left,
//                    frame.top,
//                    frame.width,
//                    frame.height,
//                    XposedHelpers.callMethod(fromView,"getCornerRadius"),
//                    XposedHelpers.callMethod(toView,"getCornerRadius")
//                )
//                val animValue2 = XposedHelpers.getObjectField(thisObj,"lastAnimValue")
//                if (animValue2 != null) {
//                    if (animValue2 != null && XposedHelpers.callMethod(animValue,"getScreenTopA")  == XposedHelpers.callMethod(animValue2,"getScreenTopA")) {
//                        z = true
//                    }
//                    if (z && !(XposedHelpers.callMethod(thisObj,"isOrientationChanged") as Boolean) && !(XposedHelpers.callMethod(thisObj,"isFoldStateChanged") as Boolean)) {
//
//                        XposedHelpers.setObjectField(thisObj,"animValue",XposedHelpers.getObjectField(thisObj,"lastAnimValue"))
//                        return null
//                    }
//                }
//                XposedHelpers.setObjectField(thisObj,"animValue",animValue)
//                XposedHelpers.setObjectField(thisObj,"lastAnimValue",animValue)
//                return null
//            }
//        })


        //进入编辑调用
//        XposedHelpers.findAndHookMethod(QSListController,"distributeTileInfo",List::class.java,object :XC_MethodHook(){
//            override fun beforeHookedMethod(param: MethodHookParam?) {
//                super.beforeHookedMethod(param)
//                val thisObj = param?.thisObject
//                val addedTiles = XposedHelpers.getObjectField(thisObj,"addedTiles") as ArrayList<*>
//                val cc =  ArrayList( addedTiles.subList(0,5))
////                XposedHelpers.setObjectField(thisObj,"addedTiles",cc)
//            }
//
//        })
//
//        XposedHelpers.findAndHookMethod(QSListController,"distributeTiles",object :XC_MethodHook(){
//            override fun beforeHookedMethod(param: MethodHookParam?) {
//                super.beforeHookedMethod(param)
//                val thisObj = param?.thisObject
//                val host = XposedHelpers.getObjectField(thisObj,"host")
//                val tt = XposedHelpers.callMethod(host,"getTiles") as Collection<*>
//                for (t in tt){
//                    starLog.log("getTiles $t")
//                }
//
//                val addedTiles = XposedHelpers.getObjectField(thisObj,"addedTiles") as ArrayList<*>
////                val cc =  emptyArray<>()
////                XposedHelpers.setObjectField(thisObj,"addedTiles",cc)
////                starLog.log("distributeTiles ${addedTiles.size}")
//            }
//
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                val thisObj = param?.thisObject
//                val addedTiles = XposedHelpers.getObjectField(thisObj,"addedTiles") as ArrayList<*>
////                val cc =  emptyArray<>()
////                XposedHelpers.setObjectField(thisObj,"addedTiles",cc)
//                starLog.log("distributeTiles ${addedTiles.size}")
//            }

//        })



//        XposedHelpers.findAndHookMethod(QSListController,"onModeChanged",object :XC_MethodHook(){
//            override fun beforeHookedMethod(param: MethodHookParam?) {
//                super.beforeHookedMethod(param)
//                val thisObj = param?.thisObject
//                //添加前的列表
//                val copiedTiles = XposedHelpers.getObjectField(thisObj,"copiedTiles") as ArrayList<*>
//                for (i in copiedTiles){
//
//                    starLog.log("copiedTiles: $i")
//                }
//                //添加后的列表
//                val addedTiles = XposedHelpers.getObjectField(thisObj,"addedTiles") as ArrayList<*>
//                for (i in addedTiles){
//
//                    starLog.log("addedTiles: $i")
//                }
//                val cc =  ArrayList( addedTiles.subList(0,5))
//                //XposedHelpers.setObjectField(thisObj,"addedTiles",cc)
//            }
//
//        })
//        XposedHelpers.findAndHookMethod(QSListController,"getListItems",object :XC_MethodHook(){
//            override fun afterHookedMethod(param: MethodHookParam?) {
//                super.afterHookedMethod(param)
//                val thisObj = param?.thisObject
//                val mainPanelController = XposedHelpers.getObjectField(thisObj,"mainPanelController")
//                val get = XposedHelpers.callMethod(mainPanelController,"get")
//                val getModeController = XposedHelpers.callMethod(get,"getModeController")
//                val getMode = XposedHelpers.callMethod(getModeController,"getMode")
//                val ordinal = XposedHelpers.callMethod(getMode,"ordinal") as Int
//                val cc = XposedHelpers.getStaticObjectField(WhenMappings,"\$EnumSwitchMapping\$0") as Array<*>
////                int i = WhenMappings.$EnumSwitchMapping$0[ordinal];
//                val i = cc[ordinal]
//
//                if (i !=1 && i != 2) return
//
//                val list = param?.result as List<*>
//                val arrayList = ArrayList<Any?>()
//                arrayList.addAll(list)
//                arrayList.addAll(list)
//
//                param.result = list.subList(0,1)
//            }
//        })

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

            val MainPanelController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.MainPanelController",classLoader)

            XposedHelpers.findAndHookConstructor(QSItemViewHolder,QSItemView,MainPanelController,object : XC_MethodHook(){
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
                        label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)

                        val layoutParam =  label.layoutParams
                        icon.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                        layoutParam.width = icon.measuredWidth/9*7
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
                        label.isFocusable = true
                        label.isSelected = true
                        label.marqueeRepeatLimit = 3
                        label.setSingleLine()
                    }



                }
            })
        }

//        XposedHelpers.findAndHookMethod(QSTileItemView,"startMarquee",object : XC_MethodReplacement(){
//
//            override fun replaceHookedMethod(param: MethodHookParam?): Any? {
//                val thisObj = param?.thisObject
//                val context = XposedHelpers.callMethod(thisObj,"getContext") as Context
//                val tile_label = context.resources.getIdentifier("tile_label","id",plugin)
//
//                val label = XposedHelpers.callMethod(thisObj,"_\$_findCachedViewById",tile_label) as TextView
//                XposedHelpers.callMethod(label,"startMarquee")
//                return null
//            }
//
//        })
//t


        if ( labelMode != 0 ){


            val QSListController = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.qs.QSListController",classLoader)

            XposedHelpers.findAndHookMethod(QSListController,"updateTextMode",object : XC_MethodHook(){
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    val thisObj = param?.thisObject
                    val contentResolver = XposedHelpers.getObjectField(thisObj,"contentResolver")  as ContentResolver
                    when (labelMode) {

                        1 -> {
                            Settings.Secure.putInt(contentResolver, "wordless_mode", 0)


                        }
                        2 -> {
                            when (isWordlessMode2) {
                                2-> Settings.Secure.putInt(contentResolver, "wordless_mode", 1)
                                1-> Settings.Secure.putInt(contentResolver, "wordless_mode", 0)

                            }
                        }
                        else -> {
                            when (isWordlessMode0) {
                                2-> Settings.Secure.putInt(contentResolver, "wordless_mode", 1)
                                1-> Settings.Secure.putInt(contentResolver, "wordless_mode", 0)

                            }
                            return
                        }
                    }



                }
            })

            XposedHelpers.findAndHookMethod(QSTileItemView, "changeExpand", object : XC_MethodReplacement() {

                override fun replaceHookedMethod(param: MethodHookParam?): Any? {
                    val thisObj = param?.thisObject as FrameLayout

                    val isDetailTile = XposedHelpers.getBooleanField(thisObj,"isDetailTile")
                    val res = thisObj.resources
                    val label = thisObj.findViewByIdName("tile_label") as TextView
                    val isShowLabel = XposedHelpers.callMethod(thisObj,"getShowLabel") as Boolean
                    val y : Float
                    var space : Int = XposedHelpers.getIntField(thisObj,"containerHeight")
                    val labelHeight = XposedHelpers.getIntField(thisObj,"labelHeight")
                    if (isShowLabel){
                        if (isDetailTile){
                            y = 0f
                            space += labelHeight
                        }else{
                            when (labelMode) {
                                2 -> {
                                    y = dpToPx(res,listLabelTop)

                                    space += labelHeight
                                    space = (space*listLabelSpacingY).toInt()

                                }
                                1 -> {

                                    y = -4f

                                }
                                else -> {
                                    return null
                                }
                            }

                        }
                    }else{

                        y = labelHeight.toFloat()
                        if (!isDetailTile){
                            space = (space*listSpacingY).toInt()
                        }
                    }
                    label.translationY = y
                    val setLayoutHeight = XposedHelpers.findMethodBestMatch(
                        CommonUtils,"setLayoutHeight\$default",
                        CommonUtils,Class.forName("android.view.View"), Int::class.java, Boolean::class.java,Int::class.java,Object::class.java
                    )

                    val INSTANCE = XposedHelpers.getStaticObjectField(CommonUtils,"INSTANCE")

                    setLayoutHeight.invoke(CommonUtils,INSTANCE, thisObj, space, false, 2, null)

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

            val updateTextSizeForKDDI = XposedHelpers.findMethodExactIfExists(QSTileItemView, "updateTextSizeForKDDI")
            if (updateTextSizeForKDDI != null){
                XposedBridge.hookMethod(updateTextSizeForKDDI, object : XC_MethodReplacement() {

                    override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                        return null
                    }
                })

            }


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

        val QSTileItemIconView = findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        )
        val ControlCenterWindowViewImpl = findClass("miui.systemui.controlcenter.windowview.ControlCenterWindowViewImpl",classLoader)
        if (isQSListTileRadius){


            XposedHelpers.findAndHookMethod(QSTileItemIconView,
                "getCornerRadius",  object : XC_MethodReplacement() {

                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        val pluginContext: Context = XposedHelpers.getObjectField(param?.thisObject, "pluginContext") as Context
                        return dpToPx(pluginContext.resources,qsListTileRadius)
                    }
                })

            XposedHelpers.findAndHookMethod(QSTileItemIconView, "setDisabledBg", Drawable::class.java, object : XC_MethodHook(){
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    val drawable = param?.args?.get(0) as Drawable
                    if (drawable is GradientDrawable){
                        val cc = drawable.cornerRadius
                        val pluginContext: Context = XposedHelpers.getObjectField(param.thisObject, "pluginContext") as Context
                        val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        if (cc != mRadius){
                            drawable.cornerRadius = mRadius
                            param.args[0] = drawable

                        }
                    }
                }
            })
            XposedHelpers.findAndHookMethod(QSTileItemIconView, "setEnabledBg", Drawable::class.java, object : XC_MethodHook(){
                override fun beforeHookedMethod(param: MethodHookParam?) {
                    super.beforeHookedMethod(param)
                    val drawable = param?.args?.get(0) as Drawable
                    if (drawable is GradientDrawable){
                        val cc = drawable.cornerRadius
                        val pluginContext: Context = XposedHelpers.getObjectField(param.thisObject, "pluginContext") as Context
                        val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        if (cc != mRadius){
                            drawable.cornerRadius = mRadius
                            param.args[0] = drawable

                        }
                    }
                }
            })


        }
        var isDetailTile = false

        var tileSize :Int = 0


        hookAllMethods(QSTileItemIconView,
            "updateIcon",
            object : MethodHook {

                override fun before(param: XC_MethodHook.MethodHookParam?) {
                    val thisObj = param?.thisObject
                    isDetailTile = XposedHelpers.getBooleanField(thisObj,"isDetailTile")
                    if ( labelMode != 0 ) {

                        tileSize = XposedHelpers.getFloatField(thisObj,"tileSize").toInt()
                    }

                }

                override fun after(param: XC_MethodHook.MethodHookParam?) {
                    if (isDetailTile) return
                    if (labelMode == 0){
                        return
                    }

                    val z = param?.args?.get(1) as Boolean

                    if (z) {
                        val thisObj = param.thisObject
                        val icon: ImageView =
                            XposedHelpers.getObjectField(thisObj, "icon") as ImageView;
                        val combine = icon.drawable

                        if (combine !is LayerDrawable) {
                            return
                        }

                        val num = combine.numberOfLayers


                        when (num) {
                            2 -> {
                                return
                                //icons = LayerDrawable(arrayOf(disabledBg, invisibleDrawableCompat))

                            }
                            3 -> {
                                starLog.log("setIcon")
                                val disabledBg = XposedHelpers.getObjectField(thisObj,"disabledBg") as Drawable
                                val enabledBg = XposedHelpers.getObjectField(thisObj,"enabledBg") as Drawable
                                val invisibleDrawableCompat = combine.getDrawable(2)
                                val iconDrawable = LayerDrawable(
                                    arrayOf(
                                        disabledBg,
                                        enabledBg,
                                        invisibleDrawableCompat
                                    )
                                )

                                val size = XposedHelpers.callMethod(thisObj, "getProperIconSize",invisibleDrawableCompat) as Int

                                iconDrawable.setLayerGravity(2, Gravity.CENTER)
                                if (listIconTop != 0f){
                                    iconDrawable.setLayerInsetBottom(2,
                                        (tileSize*listIconTop).toInt()
                                    )

                                }
                                iconDrawable.setLayerSize(2, size, size)

                                icon.setImageDrawable(iconDrawable)

                            }
                        }



                    }


                }
            })

        if ( labelMode != 0 ) {
            val DrawableUtils = findClass("miui.systemui.util.DrawableUtils", classLoader)

            XposedHelpers.findAndHookMethod(DrawableUtils, "combine", Drawable::class.java, Drawable::class.java, Int::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        val args: Array<Any> = param?.args as Array<Any>
                        val dra = args[0] as Drawable
                        val dra2 = args[1] as Drawable
                        val i = args[2] as Int

                        val icon = LayerDrawable(arrayOf(dra, dra2))
                        icon.setLayerGravity(1, i)

                        if (isDetailTile || tileSize == 0) return icon
                        if (listIconTop != 0f) {
                            icon.setLayerInsetBottom(1,
                                (tileSize * listIconTop).toInt()
                            )

                        }

                        return icon

                    }

                })
        }
    }

    private fun setRadius(
        context: Context,
        res : Resources,
        name:String
    ) {

        val id: Int = res.getIdentifier(name, "drawable", plugin)
        val drawable: Drawable = context.theme.getDrawable(id)
        if (drawable is GradientDrawable) {
            drawable.cornerRadius = dpToPx(res,qsListTileRadius)
            //warningD.setStroke(10,Color.RED)
        }
    }




}