package com.yunzia.hyperstar.hook.app.plugin.os1

import android.content.ContentResolver
import android.content.Context
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
    val isWordlessMode0: Int = XSPUtils.getInt("is_wordless_mode_0",0)
    val isWordlessMode2: Int = XSPUtils.getInt("is_wordless_mode_2",0)
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

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()
        titleFollowAnimation()
        qsTileRadius()
        fixTileIcon()
    }

    private fun fixTileIcon() {

        val fix = XSPUtils.getBoolean("fix_list_tile_icon_scale",false)
        if (!fix) return

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        ).apply {
            afterHookMethod(
                "getProperIconSize",
                Drawable::class.java
            ){
                val tileSize = this.getFloatField("tileSize").toInt()
                //height = tileSize

                val drawable = it.args[0] as Drawable

                val isCustomTile = this.getBooleanField("isCustomTile")
                if (isCustomTile) return@afterHookMethod

                if(drawable !is AnimatedVectorDrawable) return@afterHookMethod

                val customTileSize = this.getFloatField("customTileSize").toInt()
                if (drawable.intrinsicHeight < customTileSize){
                    it.result = customTileSize

                }

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

        if (!XSPUtils.getBoolean("title_follow_anim",false)) return

        val QSItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        for (method in QSItemViewHolder!!.getDeclaredMethods()) {
            if (
                method.name == "getTarget" &&
                method.isBridge &&
                method.isSynthetic
            ) {
                starLog.logD("Found bridge & synthetic method: $method")
                method.replace {
                    val itemView = this.getObjectField("itemView")
                    return@replace itemView
                }

            }
        }
    }


    private fun startMethodsHook() {

        val QSItemViewHolder = findClass("miui.systemui.controlcenter.panel.main.qs.QSItemViewHolder", classLoader)
        val QSItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSItemView", classLoader)
        val QSTileItemView = findClass("miui.systemui.controlcenter.qs.tileview.QSTileItemView", classLoader)

        val MainPanelModeController = findClass("miui.systemui.controlcenter.panel.main.MainPanelModeController\$MainPanelMode",classLoader)

        QSTileItemView.apply {
            if (clickClose) {
                beforeHookMethod(
                    "onFinishInflate\$lambda-0",
                    QSTileItemView,
                    View::class.java
                ){
                    val qSTileItemView = it.args[0] as FrameLayout
                    val lastTriggeredTime = qSTileItemView.getLongField("lastTriggeredTime")
                    val elapsedRealtime = SystemClock.elapsedRealtime()

                    if (elapsedRealtime > lastTriggeredTime + 200) {
                        val clickAction = qSTileItemView.getObjectField("clickAction")
                        if (clickAction == null){
                            starLog.logE("clickAction == null")
                            return@beforeHookMethod
                        }

                        val enumConstants: Array<out Any>? = MainPanelModeController?.getEnumConstants()
                        if (enumConstants == null){
                            starLog.logE("enumConstants == null")
                            return@beforeHookMethod
                        }
                        val mainPanelMode = qSTileItemView.getObjectField("mode")
                        if (mainPanelMode != enumConstants[2]) {
                            val mContext = qSTileItemView.context
                            collapseStatusBar(mContext)
                        }else{
                            starLog.logD("mainPanelMode == edit")

                        }
                    }

                }

            }

        }

        if (labelMarquee || labelMode!=0 ){

            QSItemViewHolder.afterHookConstructor(
                QSItemView
            ){
                val qSItemView = this.callMethodAs<FrameLayout>("getQsItemView")!!

                val label = qSItemView.findViewByIdNameAs<TextView>("tile_label")
                val icon = qSItemView.findViewByIdNameAs<FrameLayout>("icon_frame")

                if (labelMode == 1){
                    qSItemView.apply {
                        removeView(label)
                        addView(label,1)
                    }
                    val layoutParam =  label.layoutParams.apply {
                        width = icon.layoutParams.width/10*9
                    }
                    label.apply {
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
                        layoutParams = layoutParam
                    }
                } else if (labelMode == 2){
                    qSItemView.apply {
                        removeView(label)
                        addView(label,1)
                    }
                    qSItemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
                    val layoutWidth = qSItemView.measuredWidth*labelWidth
                    val layoutParam =  label.layoutParams.apply {
                        width = layoutWidth.toInt()
                    }
                    label.apply {
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP,labelSize)
                        layoutParams = layoutParam
                    }
                    starLog.logD("${qSItemView.layoutParams.width}+${qSItemView.measuredWidth}")
                }
                if(labelMarquee){
                    label.startMarqueeOfFading(25)
                }

            }

        }



        if ( labelMode != 0 ){

            val commonUtils = CommonUtils(classLoader)
            findClass(
                "miui.systemui.controlcenter.panel.main.qs.QSListController",
                classLoader
            ).beforeHookMethod(
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

            QSTileItemView.replaceHookMethod(
                "changeExpand"
            ){
                this as FrameLayout
                val res = this.resources
                val label = this.findViewByIdNameAs<TextView>("tile_label")
                val isShowLabel = this.callMethodAs<Boolean>("getShowLabel")!!
                var space : Int = this.getIntField("containerHeight")
                val labelHeight = this.getIntField("labelHeight")
                val y : Float
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
                            return@replaceHookMethod null
                        }
                    }
                }else{
                    y = labelHeight.toFloat()
                    space = (space*listSpacingY).toInt()
                }
                label.translationY = y

                commonUtils.setLayoutHeightDefault(this, space, false, 2, null)

                return@replaceHookMethod null
            }

        }

        if (tileColorForState != 0 || labelMode != 0){
            QSTileItemView.replaceHookMethod(
                "updateTextAppearance"
            ){ this as FrameLayout
                val label = this.findViewByIdNameAs<TextView>("tile_label")
                label.apply {
                    if (labelMode == 1){
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, 8f)
                    } else if (labelMode == 2){
                        setTextSize(TypedValue.COMPLEX_UNIT_DIP, labelSize)
                    }
                }
                return@replaceHookMethod null
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
            ){
                this as FrameLayout
                val mode = this.getObjectFieldAs<Enum<*>>("mode")
                val copy :Any
                val Companion = QSItemView.getStaticObjectField("Companion")
                val sta = this.getObjectField("state")

                if (mode.ordinal == 0){
                    if (sta == null){
                        return@beforeHookMethod
                    }
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

        var height = 0

        findClass(
            "miui.systemui.controlcenter.qs.tileview.QSTileItemIconView",
            classLoader
        ).apply {

            if (isQSListTileRadius){

                replaceHookMethod("getCornerRadius"){
                    val pluginContext = getObjectFieldAs<Context>( "pluginContext")
                    return@replaceHookMethod dpToPx(pluginContext.resources,qsListTileRadius)

                }
                beforeHookMethod("setDisabledBg", Drawable::class.java){
                    val drawable = it.args.get(0) as Drawable
                    if (drawable is GradientDrawable){
                        val cc = drawable.cornerRadius
                        val pluginContext = this.getObjectFieldAs<Context>( "pluginContext")
                        val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        if (cc != mRadius){
                            drawable.cornerRadius = mRadius
                            it.args[0] = drawable

                        }
                    }

                }
                beforeHookMethod("setEnabledBg", Drawable::class.java){
                    val drawable = it.args?.get(0) as Drawable
                    if (drawable is GradientDrawable){
                        val cc = drawable.cornerRadius
                        val pluginContext = this.getObjectFieldAs<Context>("pluginContext")
                        val mRadius = dpToPx(pluginContext.resources,qsListTileRadius)
                        if (cc != mRadius){
                            drawable.cornerRadius = mRadius
                            it.args[0] = drawable
                        }
                    }

                }


            }
            beforeHookAllMethods("updateIcon"){
                if ( labelMode != 0 ) {
                    val tileSize = this.getFloatField("tileSize").toInt()
                    height = tileSize
                }
            }
            afterHookAllMethods("updateIcon"){
                if (labelMode == 0) return@afterHookAllMethods


                val z = it.args[1] as Boolean

                if (z) {
                    val Icon = this.getObjectFieldAs<ImageView>("icon")
                    val combine = Icon.drawable
                    if (combine !is LayerDrawable) return@afterHookAllMethods

                    val num = combine.numberOfLayers

                    when (num) {
                        2 -> return@afterHookAllMethods

                        3 -> {
                            val disabledBg = this.getObjectFieldAs<Drawable>("disabledBg")
                            val enabledBg = this.getObjectFieldAs<Drawable>("enabledBg")
                            val invisibleDrawableCompat = combine.getDrawable(2)

                            val size = this.callMethodAs<Int>(
                                "getProperIconSize",
                                invisibleDrawableCompat
                            )!!
                            val tileSize = this.getFloatField("tileSize").toInt()

                            val iconDrawable = LayerDrawable(
                                arrayOf(
                                    disabledBg,
                                    enabledBg,
                                    invisibleDrawableCompat
                                )
                            ).apply {
                                setLayerGravity(2, Gravity.CENTER)
                                if (listIconTop != 0f) {
                                    setLayerInsetBottom(
                                        2,
                                        (tileSize * listIconTop).toInt()
                                    )

                                }
                                setLayerSize(2, size, size)
                            }

                            Icon.setImageDrawable(iconDrawable)

                        }

                        else -> {
                            return@afterHookAllMethods
                        }
                    }
                }

            }
        }

        if ( labelMode != 0 ) {
            findClass(
                "miui.systemui.util.DrawableUtils",
                classLoader
            ).apply {
                replaceHookMethod(
                    "combine",
                    Drawable::class.java,
                    Drawable::class.java,
                    Int::class.java
                ){
                    val args: Array<Any> = it.args as Array<Any>
                    val dra = args[0] as Drawable
                    val dra2 = args[1] as Drawable
                    val i = args[2] as Int

                    val icon = LayerDrawable(arrayOf(dra, dra2)).apply {
                        setLayerGravity(1, i)
                    }
                    if (height == 0) return@replaceHookMethod icon
                    if (listIconTop != 0f) {
                        icon.setLayerInsetBottom(1,
                            (height * listIconTop).toInt()
                        )

                    }

                    return@replaceHookMethod icon

                }
            }

        }
    }




}