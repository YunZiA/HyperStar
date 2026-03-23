package com.yunzia.hyperstar.hook.app.plugin.os2

import android.graphics.Bitmap
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.hook.core.base.BasePluginHook
import com.yunzia.hyperstar.hook.core.finder.findClass
import com.yunzia.hyperstar.hook.base.findViewByIdNameAs
import com.yunzia.hyperstar.hook.core.helper.replaceHookMethod
import com.yunzia.hyperstar.hook.core.StarLog.logE
import com.yunzia.hyperstar.hook.core.helper.afterHookAllConstructors
import com.yunzia.hyperstar.hook.core.helper.afterHookMethod
import com.yunzia.hyperstar.hook.core.helper.callMethod
import com.yunzia.hyperstar.hook.core.helper.getFloatField
import com.yunzia.hyperstar.hook.core.helper.getObjectField
import com.yunzia.hyperstar.hook.core.helper.getObjectFieldAs
import com.yunzia.hyperstar.prefs.XSPUtils
import yunzia.utils.BitmapUtils
import yunzia.utils.BitmapUtils.Companion.auto


object QSMediaCoverBackground: BasePluginHook() {
    val mediaBackground= XSPUtils.getInt("media_background_style",0)
    val defaultBackground = (mediaBackground == 0)
    val coverBackground = XSPUtils.getBoolean("is_cover_background",false)
    val isScale:Boolean = XSPUtils.getBoolean("is_cover_scale_background",false)
    val isHideCover:Boolean = XSPUtils.getBoolean("is_hide_cover",false)
    val isTitleCenter:Boolean = XSPUtils.getBoolean("is_title_center",false)
    val scaleFactor:Float = XSPUtils.getFloat("cover_scale_background_value",1.5f)
    val isBlur:Boolean = XSPUtils.getBoolean("is_cover_blur_background",false)
    val blurRadius:Float = XSPUtils.getFloat("cover_blur_background_value",50f)
    val isDim:Boolean = XSPUtils.getBoolean("is_cover_dim_background",false)
    val alpha = XSPUtils.getFloat("cover_dim_background_value",0f).coerceIn(0f, 100f)
    val coverAnciently:Boolean = XSPUtils.getBoolean("cover_anciently",false)

    override fun init() {
        startMethodsHook()
    }

    private fun startMethodsHook() {
        var foreground: Drawable? = null
        val MediaPlayerMetaData  = findClass("miui.systemui.controlcenter.media.MediaPlayerMetaData",pluginClassLoader)
        val MediaPlayerViewHolder  = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",pluginClassLoader)
        val CommonUtils = findClass("miui.systemui.util.CommonUtils",pluginClassLoader)

//        val controlCenterUtils = ControlCenterUtils(pluginClassLoader)
//        val miBlurCompat = MiBlurCompat(pluginClassLoader)

        MediaPlayerViewHolder.afterHookMethod("updateMetaData",MediaPlayerMetaData!!) { args, result ->

            val mediaPlayerMetaData = args[0]
            val itemView : View = thisObject.getObjectFieldAs<View>("itemView")
            val cover = itemView.findViewByIdNameAs<ImageView>("cover")
            val res = itemView.resources
            cover.post(object : Runnable{
                override fun run() {

                    if (mediaPlayerMetaData == null) {
                        if (!defaultBackground){
                            val background = itemView.resources.getIdentifier("media_player_background","drawable",plugin)
                            itemView.setBackgroundResource(background)

                        }
                        return
                    }

                    if (isHideCover){
                        cover.visibility = if (isTitleCenter) View.GONE else View.INVISIBLE
                    }
                    if (defaultBackground) return

                    var art = mediaPlayerMetaData.callMethod("getArt")
                    if (art == null){
                        logE("art is null")
                        return
                    }

                    if (art !is Bitmap){
                        logE("mediaPlayerMetaData:art is not get!!!")
                        thisObject.callMethod("updateResources")
                        return

                    }

                    art = when (mediaBackground) {
                        1 -> {
                            auto(art)
                        }
                        2 -> {
                            BitmapUtils.doBitmap(art,isScale,scaleFactor,isBlur,blurRadius,isDim,-alpha)
                        }
                        else -> {
                            return
                        }
                    }

                    var artDrawable: Drawable = art.toDrawable(res)

                    if (coverAnciently && mediaBackground == 2) {
                        if (foreground == null){
                            foreground = res.getDrawable(R.drawable.vintage)
                        }
                        artDrawable =  LayerDrawable(arrayOf(artDrawable, foreground))

                    }
                    itemView.background = artDrawable
//                        if (coverAnciently ) {
//                        val layerDrawable = LayerDrawable(arrayOf(artDrawable, foreground))
//                        layerDrawable
//                    } else {
//                        artDrawable
//                    }
                }

            })

        }

        if (!defaultBackground){
            val HapticFeedback = findClass("miui.systemui.util.HapticFeedback",pluginClassLoader)

            MediaPlayerViewHolder.apply {
                afterHookAllConstructors { args, result ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    val _cornerRadius = thisObject.getFloatField("_cornerRadius")!!
                    itemView.outlineProvider = object : ViewOutlineProvider(){
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null) return
                            outline?.setRoundRect(0,0,view.width,view.height,_cornerRadius)
                        }

                    }
                    itemView.clipToOutline = true

                }
                afterHookMethod("updateSize") { args, result ->
                    val itemView = thisObject.getObjectFieldAs<View>("itemView")
                    val _cornerRadius = thisObject.getFloatField("_cornerRadius")!!
                    itemView.outlineProvider = object : ViewOutlineProvider(){
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null) return
                            outline?.setRoundRect(0,0,view.width,view.height,_cornerRadius)
                        }

                    }
                    itemView.clipToOutline = true

                }
                replaceHookMethod("updateResources"){
                    return@replaceHookMethod null

                }
            }

            val MediaPanelContentController = findClass("miui.systemui.controlcenter.panel.main.media.MediaPanelContentController",pluginClassLoader)
            val MediaPanelParams = findClass("miui.systemui.controlcenter.panel.main.media.MediaPanelController\$MediaPanelParams",pluginClassLoader)

            MediaPanelContentController.afterHookMethod(
                "updateFromViewSize",
                "miui.systemui.controlcenter.panel.main.media.MediaFromView"
            ) { args, result ->
                val mediaFromView = args[0]
                val view = mediaFromView.callMethod("getViewHolder")
            }

            MediaPanelParams.afterHookAllConstructors { args, result ->
                val fromView  = thisObject.getObjectField("fromView")
                val fromView2  = thisObject.getObjectField("fromView2")

            }



        }
    }




}