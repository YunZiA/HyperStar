package com.yunzia.hyperstar.hook.app.plugin.os3

import android.content.ContentResolver
import android.graphics.Color
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
import yunzia.utils.DensityUtil.Companion.dpToPx


class QSListView : Hooker() {

    val labelMode: Int = XSPUtils.getInt("is_list_label_mode",0)
    val labelSize = XSPUtils.getFloat("list_label_size",13f)
    val isWordlessMode0: Int = XSPUtils.getInt("is_wordless_mode_0",0)
    val isWordlessMode2: Int = XSPUtils.getInt("is_wordless_mode_2",0)
    val labelWidth = XSPUtils.getFloat("list_label_width",100f)/100f
    private val labelColorFollowTileState = XSPUtils.getInt("qs_list_tile_color_for_state",0)
    val listSpacingY = XSPUtils.getFloat("list_spacing_y",100f)/100
    val listLabelSpacingY = XSPUtils.getFloat("list_label_spacing_y",100f)/100


    val listIconTop = if (labelMode == 2) XSPUtils.getFloat("list_icon_top", 0f)/100 else 1/7f
    val listLabelTop = XSPUtils.getFloat("list_label_top", 0f)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        setLabelLayoutAndSize()
        resetWordlessMode()
        labelColorFollowTileState()
        resetIconBottom()
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
                rootView.post {
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
    fun labelColorFollowTileState(){
        val QSItemView =
            findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView =
            findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)


        if (labelColorFollowTileState != 0 || labelMode != 0){
            QSTileItemView.apply {
//                replaceHookMethod("getLabelVisible"){
//                    return@replaceHookMethod true
//                }
                replaceHookMethod(
                    "updateTextAppearance"
                ) { this as FrameLayout
                    val icon = this.callMethod("getIcon")
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

        starLog.logD("tileState = ${labelColorFollowTileState}")

        if (labelColorFollowTileState != 0){

            val disableColor = XSPUtils.getString("list_title_off_color", "null")
            val enableColor = XSPUtils.getString("list_title_on_color", "null")
            val restrictedColor = XSPUtils.getString("list_title_restricted_color", "null")
            val unavailableColor = XSPUtils.getString("list_title_unavailable_color", "null")

            QSTileItemView.afterHookMethod(
                "onStateUpdated",
                Boolean::class.java
            ) { this as FrameLayout
                val binding = this.callMethod("getBinding")
                val Companion = QSItemView.getStaticObjectField("Companion")
                val icon = this.callMethod("getIcon")
                val state = icon.getObjectField("state")?:return@afterHookMethod
                val states = state.getIntField("state")
                val isRestrictedCompat = Companion.callMethodAs<Boolean>("isRestrictedCompat",state)!!
                val label = binding.getObjectFieldAs<TextView>("tileLabel")
                var off = icon.getIntField("defaultIconColorOff")
                var enable = icon.getIntField("defaultIconColor")
                var unavailable = icon.getIntField("defaultIconColorUnavailable")
                var restrict = icon.getIntField("defaultIconColorRestrict")
                if (labelColorFollowTileState == 2){
                    if (disableColor != "null")  off = Color.parseColor(disableColor)
                    if (enableColor != "null")  enable = Color.parseColor(enableColor)
                    if (restrictedColor != "null")  unavailable = Color.parseColor(restrictedColor)
                    if (unavailableColor != "null")  restrict = Color.parseColor(unavailableColor)
                }

                when(states) {
                    0 -> {
                        label.setTextColor(unavailable)
                    }
                    1 -> {
                        if (isRestrictedCompat){
                            label.setTextColor(restrict)
                        }else{
                            label.setTextColor(off)
                        }
                    }
                    2 -> {
                        val spec = state.getStringField("spec")
                        when(spec) {
                            "batterysaver" -> {
                                label.setTextColor(icon.getIntField("powerSaferTileIconColor"))
                            }
                            "flashlight" -> {
                                label.setTextColor(icon.getIntField("miuiFlashlightTileIconColor"))
                            }
                            "autobrightness" -> {
                                label.setTextColor(icon.getIntField("autoBrightnessTileIconColor"))
                            }
                            "cell" -> {
                                label.setTextColor(icon.getIntField("miuiCellularTileIconColor"))
                            }
                            "mute" -> {
                                label.setTextColor(icon.getIntField("muteTileIconColor"))
                            }
                            "papermode" -> {
                                label.setTextColor(icon.getIntField("paperModeTileIconColor"))
                            }
                            "quietmode" -> {
                                label.setTextColor(icon.getIntField("quietModeTileIconColor"))
                            }
                            else -> {
                                label.setTextColor(enable)
                            }
                        }
                    }
                    else -> {
                        return@afterHookMethod
                    }
                }

            }

        }




    }


    private fun resetIconBottom() {

        if ( labelMode == 0 )  return
        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        ).apply {
            afterHookMethod(
                "updateIconInternal",
                "com.android.systemui.plugins.qs.QSTile\$State",
                Boolean::class.java,
                Boolean::class.java,
                Boolean::class.java,
                Boolean::class.java
            ){
                if (this.getBooleanField("isDetailTile")) return@afterHookMethod
                val icon = this.getObjectFieldAs<ImageView>("icon")
                val drawable = icon.drawable
                if (drawable is LayerDrawable) {
                    icon.post {
                        starLog.logD("updateIconInternal","${drawable.numberOfLayers}")
                        val num = drawable.numberOfLayers
                        val last = num - 1
                        val properIconSize = this@afterHookMethod.callMethodAs<Int>("getProperIconSize", drawable.getDrawable(last))
                        val array = Array(num) { index -> drawable.getDrawable(index) }
                        val imageDrawable = LayerDrawable(array)
                        imageDrawable.apply {
                            setLayerGravity(last, Gravity.CENTER)
                            setLayerSize(last,properIconSize,properIconSize)
                            if ( listIconTop != 0f){
                                setLayerInsetBottom(
                                    last,
                                    (properIconSize * listIconTop).toInt()
                                )
                            }
                        }
                        starLog.logD("updateIconInternal"," ${drawable.getLayerGravity(last)} + ${drawable.getLayerInsetBottom(last)}")
                        icon.setImageDrawable(imageDrawable)
                    }
                }
            }
        }

    }






}