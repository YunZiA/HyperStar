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
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookConstructor
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.base.replaceHookMethod
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.hook.util.plugin.CommonUtils
import com.yunzia.hyperstar.hook.util.startMarqueeOfFading
import com.yunzia.hyperstar.utils.XSPUtils
import yunzia.utils.DensityUtil.Companion.dpToPx


class QSListView : Hooker() {

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

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()
        qsTileRadius()
        titleFollowAnimation()
        fixTileIcon()
        fixBrightnessIcon()
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

    private fun fixTileIcon() {

        val fix = XSPUtils.getBoolean("fix_list_tile_icon_scale",false)
        if (!fix) return

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        ).afterHookMethod(
            "getProperIconSize",
            Drawable::class.java
        ){
            val drawable = it.args[0] as Drawable
            if(drawable !is AnimatedVectorDrawable) return@afterHookMethod

            val customTileSize = this.getFloatField("customTileSize").toInt()
            if (drawable.intrinsicHeight < customTileSize){
                it.result = customTileSize

            }

        }

    }

    fun collapseStatusBar(context: Context) {
        try {
            val systemService = context.getSystemService("statusbar")
            systemService.javaClass.getMethod("collapsePanels", *arrayOfNulls(0)).invoke(systemService, *arrayOfNulls(0))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun titleFollowAnimation(){

        if (!XSPUtils.getBoolean("title_follow_anim", false)) return

        val QSItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        QSItemViewHolder.findMethodExt(
            "getTarget",
            { isBridge && isSynthetic }
        ).replace {
            val itemView = this.getObjectField("itemView")
            return@replace itemView
        }
    }

    private fun startMethodsHook() {

        val QSItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        val QSItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)
        val commonUtils = CommonUtils(classLoader)
        val QSListController = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController", classLoader)
        val WhenMappings = findClass("miui.systemui.controlcenter.panel.main.qs.QSListController\$WhenMappings", classLoader)
        val AnimValue = findClass("miui.systemui.controlcenter.panel.detail.DetailPanelAnimator\$AnimValue", classLoader)
        val DetailPanelAnimator = findClass("miui.systemui.controlcenter.panel.detail.DetailPanelAnimator", classLoader)



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

        val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController\$Mode",classLoader)

        if (clickClose){
            QSTileItemView.afterHookMethod(
                "onFinishInflate\$lambda-0",
                QSTileItemView,View::class.java
            ){
                val qSTileItemView = it.args[0] as FrameLayout
                val lastTriggeredTime = qSTileItemView.getLongField("lastTriggeredTime")
                val elapsedRealtime = SystemClock.elapsedRealtime()

                if (elapsedRealtime > lastTriggeredTime + 200) {
                    val clickAction = qSTileItemView.getObjectField("clickAction")
                    if (clickAction == null) {
                        starLog.logE("clickAction == null")
                        return@afterHookMethod
                    }

                    val enumConstants: Array<out Any>? = MainPanelModeController?.enumConstants
                    if (enumConstants == null) {
                        starLog.logE("enumConstants == null")
                        return@afterHookMethod
                    }

                    val mainPanelMode = qSTileItemView.getObjectField("mode")
                    if (mainPanelMode != enumConstants[2]) {
                        collapseStatusBar(qSTileItemView.context)
                    } else {
                        starLog.logE("mainPanelMode == edit")

                    }
                }
            }

        }

        if (labelMarquee || labelMode != 0 ){

            val MainPanelController = findClass("miui.systemui.controlcenter.panel.main.MainPanelController",classLoader)

            QSItemViewHolder.afterHookConstructor(
                QSItemView,
                MainPanelController
            ) {
                val qSItemView = this.callMethodAs<FrameLayout>("getQsItemView")!!
                val label = qSItemView.findViewByIdNameAs<TextView>("tile_label")
                val icon = qSItemView.findViewByIdNameAs<FrameLayout>("icon_frame")

                if (labelMode == 1){
                    qSItemView.apply {
                        removeView(label)
                        addView(label,1)
                    }
                    icon.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val layoutParam =  label.layoutParams.apply {
                        width = icon.measuredWidth/9*7
                    }
                    label.apply {
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9f)
                        layoutParams = layoutParam
                    }
                } else if (labelMode == 2){
                    qSItemView.apply {
                        removeView(label)
                        addView(label,1)
                    }

                    qSItemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    starLog.logD("${qSItemView.layoutParams.width}+${qSItemView.measuredWidth}")

                    val layoutParam =  label.layoutParams.apply {
                        width = qSItemView.measuredWidth*labelWidth.toInt()
                    }
                    label.apply {
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP,labelSize)
                        layoutParams = layoutParam
                    }

                }
                if(labelMarquee){
                    label.startMarqueeOfFading(25)

                }


            }

        }

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

            findClass(
                "miui.systemui.controlcenter.panel.main.qs.QSListController",
                classLoader
            ).apply {
                beforeHookMethod(
                    "updateTextMode"
                ){
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
                beforeHookMethod(
                    "updateTextMode",
                    Boolean::class.java
                ){
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
            QSTileItemView.replaceHookMethod(
                "changeExpand"
            ) { this as FrameLayout
                val icon = this.getObjectField("icon")
                val isDetailTile = icon.getBooleanField("isDetailTile")
                val res = this.resources
                val label = this.findViewByIdName("tile_label") as TextView
                val isShowLabel = this.callMethod("getShowLabel") as Boolean
                var space : Int = this.getIntField("containerHeight")
                val labelHeight = this.getIntField("labelHeight")
                val y : Float
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

        }

        if (tileColorForState != 0 || labelMode != 0){
            QSTileItemView.apply {
                replaceHookMethod(
                    "updateTextAppearance"
                ) { this as FrameLayout
                    val icon = this.getObjectField("icon")
                    val isDetailTile = icon.getBooleanField("isDetailTile")
                    if (isDetailTile) return@replaceHookMethod null
                    val label = this.findViewByIdNameAs<TextView>("tile_label")
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
                val copy: Any

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

        val QSTileItemIconView = findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        )
        val ControlCenterWindowViewImpl = findClass("miui.systemui.controlcenter.windowview.ControlCenterWindowViewImpl",classLoader)
        if (isQSListTileRadius){

            QSTileItemIconView.apply {
                replaceHookMethod(
                    "getCornerRadius"
                ) {
                    val pluginContext = getObjectFieldAs<Context>( "pluginContext")
                    return@replaceHookMethod dpToPx(
                        pluginContext.resources,
                        qsListTileRadius
                    )
                }
                beforeHookMethod(
                    "setDisabledBg",
                    Drawable::class.java
                ) {
                    val drawable = it.args[0] as Drawable
                    if (drawable is GradientDrawable){
                        val pluginContext = this.getObjectFieldAs<Context>( "pluginContext")
                        val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        if (drawable.cornerRadius != mRadius){
                            drawable.cornerRadius = mRadius
                            it.args[0] = drawable
                        }
                    }

                }
                beforeHookMethod(
                    "setEnabledBg",
                    Drawable::class.java
                ){
                    val drawable = it.args?.get(0) as Drawable
                    if (drawable is GradientDrawable){
                        val pluginContext = this.getObjectFieldAs<Context>("pluginContext")
                        val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        if (drawable.cornerRadius != mRadius){
                            drawable.cornerRadius = mRadius
                            it.args[0] = drawable
                        }
                    }

                }
            }


        }
        var isDetailTile = false

        var tileSize = 0

        QSTileItemIconView.apply {
            beforeHookAllMethods("updateIcon"){
                isDetailTile = this.getBooleanField("isDetailTile")
                if ( labelMode != 0 ) {
                    tileSize = this.getFloatField("tileSize").toInt()
                }

            }
            afterHookAllMethods("updateIcon"){
                if (isDetailTile || labelMode == 0) return@afterHookAllMethods

                val z = it.args[1] as Boolean

                if (z) {
                    val icon = this.getObjectFieldAs<ImageView>("icon")
                    val combine = icon.drawable
                    if (combine !is LayerDrawable) return@afterHookAllMethods
                    val num = combine.numberOfLayers

                    when (num) {
                        //icons = LayerDrawable(arrayOf(disabledBg, invisibleDrawableCompat))
                        2 -> return@afterHookAllMethods
                        3 -> {
                            val disabledBg = this.getObjectFieldAs<Drawable>( "disabledBg")
                            val enabledBg = this.getObjectFieldAs<Drawable>( "enabledBg")
                            val invisibleDrawableCompat = combine.getDrawable(2)
                            val iconDrawable = LayerDrawable(
                                arrayOf(
                                    disabledBg,
                                    enabledBg,
                                    invisibleDrawableCompat
                                )
                            )

                            val size = this.callMethodAs<Int>("getProperIconSize", invisibleDrawableCompat)!!

                            iconDrawable.apply {
                                setLayerGravity(2, Gravity.CENTER)
                                setLayerSize(2, size, size)
                                if (listIconTop != 0f) {
                                    setLayerInsetBottom(
                                        2,
                                        (tileSize * listIconTop).toInt()
                                    )
                                }
                            }

                            icon.setImageDrawable(iconDrawable)

                        }
                    }
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
                if (isDetailTile || tileSize == 0) return@replaceHookMethod icon

                if (listIconTop != 0f) {
                    icon.setLayerInsetBottom(
                        1,
                        (tileSize * listIconTop).toInt()
                    )
                }
                return@replaceHookMethod icon
            }

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
        }
    }




}