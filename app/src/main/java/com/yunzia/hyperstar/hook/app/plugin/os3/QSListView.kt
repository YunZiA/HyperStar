package com.yunzia.hyperstar.hook.app.plugin.os3

import android.content.ContentResolver
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.provider.Settings
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.getDimensionPixelSize
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.hook.util.plugin.CommonUtils
import com.yunzia.hyperstar.hook.util.plugin.ControlCenterUtils
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import yunzia.utils.DensityUtil.Companion.dpToPx


class QSListView : Hooker() {

    val labelMode: Int = XSPUtils.getInt("is_list_label_mode",0)
    val labelSize = XSPUtils.getFloat("list_label_size",13f)
    val isWordlessMode0: Int = XSPUtils.getInt("is_wordless_mode_0",0)
    val isWordlessMode2: Int = XSPUtils.getInt("is_wordless_mode_2",0)
    val labelWidth = XSPUtils.getFloat("list_label_width",100f)/100f
    private val tileColorForState = XSPUtils.getInt("qs_list_tile_color_for_state",0)
    val listSpacingY = XSPUtils.getFloat("list_spacing_y",100f)/100
    val listLabelSpacingY = XSPUtils.getFloat("list_label_spacing_y",100f)/100


    val listIconTop = if (labelMode == 2) XSPUtils.getFloat("list_icon_top", 0f)/100 else 1/7f
    val listLabelTop = XSPUtils.getFloat("list_label_top", 0f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        setLabelLayoutAndSize()
        resetWordlessMode()
        cc()
        qsTileRadius()
        //titleFollowAnimation()
        //fixBrightnessIcon()
    }


    private fun fixBrightnessIcon() {
        if ( labelMode != 0 ){
            findClass(
                "miui.systemui.controlcenter.panel.main.brightness.BrightnessPanelTilesController",
                classLoader
            ).replaceHookMethod(
                "getTileSpecs"
            ){
                val list = listOf("night","autobrightness",  "papermode")
                val list2 = listOf("autobrightness", "night", "reduce_brightness")
                return@replaceHookMethod list
            }

        }
    }

    private fun titleFollowAnimation(){

        if (!XSPUtils.getBoolean("title_follow_anim", false)) return

        val QSItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        QSItemViewHolder.findMethodExt(
            "getIconFrame",
            { isBridge && isSynthetic }
        ).replace {
            val itemView = this.getObjectField("itemView")
            return@replace itemView
        }
    }

    private fun setLabelLayoutAndSize() {

        if (labelMode != 0){

            var rootViewWidth = 0

            val commonUtils = CommonUtils(classLoader)
            fun Any?.set(){
                if (this.getObjectField("icon").getBooleanField("isDetailTile")){
                    rootViewWidth = 0
                    return
                }
                val binding = this.callMethod("getBinding")
                val rootView = binding.getObjectFieldAs<ViewGroup>("rootView")
                val label = binding.getObjectFieldAs<TextView>("tileLabel")
                if (labelMode == 1) {
                    val iconWith = getDimensionPixelSize(rootView.resources,"qs_tile_item_icon_size",plugin)/ 9 * 7
                    val layoutParam = label.layoutParams.apply {
                        width = iconWith
                    }
                    label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
                    rootView.apply {
                        removeView(label)
                        addView(label, 1,layoutParam)
                    }
                } else if (labelMode == 2) {
                    val layoutParam = label.layoutParams
                    rootView.apply {
                        removeView(label)
                        addView(label, 1)
                        post {
                            if (rootViewWidth == 0){
                                rootViewWidth = rootView.width
                                starLog.logD("TAG", "width = " + rootView.width)
                            }else{
                                starLog.logD("TAG", "rootViewWidth = " + rootView.width)
                            }
                            layoutParam.width = rootViewWidth * labelWidth.toInt()
                            label.layoutParams = layoutParam
                        }
                    }
                    label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, labelSize)
                }

            }

            findClass(
                "miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader
            ).apply {
                replaceHookMethod(
                    "changeExpand"
                ) { this as FrameLayout
                    val binding = this.callMethod("getBinding")
                    val icon = this.getObjectField("icon")
                    val isDetailTile = icon.getBooleanField("isDetailTile")
                    val res = this.resources
                    val label = binding.getObjectFieldAs<TextView>("tileLabel")
                    val isShowLabel = this.callMethod("getShowLabel") as Boolean
                    var space : Int = this.getIntField("containerHeight")
                    val labelHeight = this.getIntField("labelHeight")
                    var y : Float = 0f
                    label.visibility = View.VISIBLE
                    starLog.logD("isShowLabel == $isShowLabel")
                    if (isShowLabel){
                        if (isDetailTile){
                            y = 0f
                            space += labelHeight
                        }else{
                            when (labelMode) {
                                2 -> {
                                    y = dpToPx(res, listLabelTop)
                                    space += labelHeight
                                    space = (space * listLabelSpacingY).toInt()
                                }
                                1 -> {
                                    y = - 4f
                                }
                                else -> {
                                    return@replaceHookMethod null
                                }
                            }

                        }
                    }else{
                        y = labelHeight.toFloat()
                        if (!isDetailTile){
                            space = (space * listSpacingY).toInt()
                        }
                    }
                    label.translationY = y
                    commonUtils.setLayoutHeightDefault(this, space, false, 2, null)
                    return@replaceHookMethod null
                }

                afterHookMethod(
                    "init",
                    "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView"
                ) {
                    this.set()
                }
                afterHookMethod("updateSize"){
                    this.set()
                }
            }
        }
    }

    fun resetWordlessMode(){

        if ( labelMode != 0 ){

            findClass(
                "miui.systemui.controlcenter.panel.main.qs.WordlessModeController",
                classLoader
            ).beforeHookMethod(
                "getSettings",
                Int::class.java
            ) {
                val contentResolver = this.getObjectFieldAs<ContentResolver>("contentResolver")
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
                        return@beforeHookMethod
                    }
                }
            }

        }
    }
    fun cc(){
        val QSItemView =
            findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView =
            findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)


        if (tileColorForState != 0 || labelMode != 0){
            QSTileItemView.apply {
                replaceHookMethod("getLabelVisible"){
                    return@replaceHookMethod true
                }
                replaceHookMethod(
                    "updateTextAppearance"
                ) { this as FrameLayout
                    val icon = this.getObjectField("icon")
                    val isDetailTile = icon.getBooleanField("isDetailTile")
                    if (isDetailTile) return@replaceHookMethod null
                    val label = this.findViewByIdNameAs<TextView>("tile_label")
                    label.updateLayoutParams {  }
                    label.apply {
                        if (labelMode == 1){
                            setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
                        } else if (labelMode == 2){
                            setTextSize(TypedValue.COMPLEX_UNIT_DIP,labelSize)
                        }
                    }

                    return@replaceHookMethod null
                }
                findMethodExactIfExists("updateTextSizeForKDDI")?.replace {
                    return@replace null
                }
            }

        }

//        findClass(
//            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
//            classLoader
//        ).afterHookMethod(
//            "drawableTint",
//            "com.android.systemui.plugins.qs.QSTile\$State",
//            Drawable::class.java,
//        ){
//            if ( labelMode == 0 || this.getBooleanField("isDetailTile")) return@afterHookMethod
//            val drawable = it.args[1] as Drawable
//            val tileSize = this.callMethod("getProperIconSize",drawable) as Int
//            drawable.setBounds(0, 0, tileSize, tileSize);
//            val insetDrawable = InsetDrawable(
//                drawable,
//                0,
//                0,
//                0,
//                200//(tileSize * listIconTop).toInt()
//            )
//            starLog.logD("drawableTint = ${drawable.intrinsicHeight}")
//            it.args[1] = insetDrawable
//
//        }

        if (tileColorForState != 0){

            val disableColor = XSPUtils.getString("list_title_off_color", "null")
            val enableColor = XSPUtils.getString("list_title_on_color", "null")
            val restrictedColor = XSPUtils.getString("list_title_restricted_color", "null")
            val unavailableColor = XSPUtils.getString("list_title_unavailable_color", "null")



            QSTileItemView.beforeHookMethod(
                "onStateUpdated",
                Boolean::class.java
            ) { this as FrameLayout
                val mode = this.getObjectFieldAs<Enum<*>>("mode")
                val Companion = QSItemView.getStaticObjectField("Companion")
                val sta = this.getObjectField("state")
                var copy: Any? = null

                if (mode.ordinal == 0){
                    if (sta == null) return@beforeHookMethod
                    copy = sta
                }else{
                    var customizeState = this.getObjectField("customizeState")
                    if (customizeState == null){
                        customizeState = sta
                        if (customizeState == null){
                            return@beforeHookMethod
                        }
                    }
                    copy = customizeState.callMethod("copy")!!
                    copy.setIntField("state",1)
                    Companion.callMethod("setRestrictedCompat", copy,false)
                }
                val state:Int = copy.getIntField("state")
                val states = Companion.callMethodAs<Boolean>("isRestrictedCompat",copy)!!
                val label = this.findViewByIdNameAs<TextView>("tile_label")
                val icon = this.callMethod("getIcon")
                var off = icon.getIntField("iconColorOff")
                var enable = icon.getIntField("iconColor")
                var unavailable = icon.getIntField("iconColorUnavailable")
                var restrict = icon.getIntField("iconColorRestrict")
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

        }




    }


    private fun qsTileRadius() {

        val controlCenterUtils = ControlCenterUtils(classLoader)
        val QSTileItemIconView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemIconView", classLoader)
        val ControlCenterWindowViewImpl = findClass("miui.systemui.controlcenter.windowview.ControlCenterWindowViewImpl",classLoader)

        var isDetailTile = false

        var tileSize = 0

        QSTileItemIconView.apply {
            beforeHookAllMethods("updateIcon"){
                isDetailTile = this.getBooleanField("isDetailTile")
                if ( labelMode != 0 ) {
                    tileSize = this.getFloatField("tileSize").toInt()
                }

            }
            afterHookMethod(
                "updateIconInternal",
                "com.android.systemui.plugins.qs.QSTile\$State",
                Boolean::class.java,
                Boolean::class.java,
                Boolean::class.java,
                Boolean::class.java
            ){
                val context = this.callMethodAs<Context>("getContext")
                //if (controlCenterUtils.getBackgroundBlurOpenedInDefaultTheme(context)) return@afterHookMethod
                val icon = this.getObjectFieldAs<ImageView>("icon")
                val drawable = icon.drawable
                if (drawable is LayerDrawable){
                    val num = drawable.numberOfLayers - 1
                    drawable.apply {
                        if (listIconTop != 0f) {
                            setLayerInsetBottom(
                                num,
                                (tileSize * listIconTop).toInt()
                            )
                        }
                    }
                    starLog.logD("updateIconInternal + $num")
                    icon.setImageDrawable(drawable)

                }

            }
        }

        if ( labelMode != 0 ) {
            findClass(
                "miui.systemui.util.DrawableUtils",
                classLoader
            ).replaceHookMethod(
                "combine",
                Drawable::class.java,
                Drawable::class.java,
                Int::class.java
            ){
                val args = it.args
                val dra = args[0] as Drawable
                val dra2 = args[1] as Drawable
                val i = args[2] as Int

                val icon = LayerDrawable(
                    arrayOf(dra, dra2)
                ).apply {
                    setLayerGravity(1, i)
                }
                if (isDetailTile) return@replaceHookMethod icon

                if (listIconTop != 0f) {
                    icon.setLayerInsetBottom(
                        1,
                        200
                    )
                }
                starLog.logD("DrawableUtils combine")
                return@replaceHookMethod icon
            }

        }
    }






}