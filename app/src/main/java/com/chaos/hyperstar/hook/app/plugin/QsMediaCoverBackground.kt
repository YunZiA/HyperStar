package com.chaos.hyperstar.hook.app.plugin

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BlurMaskFilter
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.core.view.marginTop
import chaos.utils.BitmapUtils
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
import com.chaos.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers


class QsMediaCoverBackground: BaseHooker() {

    var vin: Bitmap? = null;
    val isScale:Boolean = XSPUtils.getBoolean("is_cover_scale_background",false)
    val isHideCover:Boolean = XSPUtils.getBoolean("is_hide_cover",false)
    val isTitleCenter:Boolean = XSPUtils.getBoolean("is_title_center",false)
    val scaleFactor:Float = XSPUtils.getFloat("cover_scale_background_value",1.5f)
    val isBlur:Boolean = XSPUtils.getBoolean("is_cover_blur_background",false)
    val blurRadius:Float = XSPUtils.getFloat("cover_blur_background_value",50f)
    val isDim:Boolean = XSPUtils.getBoolean("is_cover_dim_background",false)
    val alpha:Int = XSPUtils.getFloat("cover_dim_background_value",50f).toInt()
    val coverAnciently:Boolean = XSPUtils.getBoolean("cover_anciently",false)

    override fun getLocalRes(res : Resources){
        val backImg : ByteArray = XposedHelpers.assetAsByteArray(res, "vintage.png")
        vin = BitmapFactory.decodeByteArray(backImg,0, backImg.size)

    }

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook(classLoader)

    }

    private fun startMethodsHook(classLoader: ClassLoader?) {
        var foreground: RoundedBitmapDrawable? = null
        val MediaPlayerMetaData  = XposedHelpers.findClass("miui.systemui.controlcenter.media.MediaPlayerMetaData",classLoader)
        val MediaPlayerViewHolder  = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",classLoader)
        XposedHelpers.findAndHookMethod(MediaPlayerViewHolder, "updateMetaData", MediaPlayerMetaData , object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {

            }

            override fun afterHookedMethod(param: MethodHookParam?) {
                if (param == null) return
                starLog.log("updateMetaData:hook")

                val mediaPlayerMetaData = param.args[0]
                val thisObj = param.thisObject
                val itemView : View = XposedHelpers.getObjectField(thisObj,"itemView") as View
                val cover = itemView.findViewByIdName("cover") as ImageView

                if (mediaPlayerMetaData != null) {
                    starLog.log("mediaPlayerMetaData:is get!!!")


                    if (isHideCover){
                        if (isTitleCenter){
                            cover.visibility = View.GONE

                        }else{
                            cover.visibility = View.INVISIBLE

                        }


                    }
                    if (!XSPUtils.getBoolean("is_cover_background",false)){
                        return
                    }

                    val _cornerRadius : Float = XposedHelpers.getObjectField(thisObj,"_cornerRadius") as Float
                    val getArtMethod = MediaPlayerMetaData.getDeclaredMethod("getArt")

                    var art: Bitmap = getArtMethod.invoke(mediaPlayerMetaData) as Bitmap

                    art = BitmapUtils.doBitmap(art,isScale,scaleFactor,isBlur,blurRadius,isDim,alpha)

                    val roundedArtDrawable = RoundedBitmapDrawableFactory.create(itemView.resources, art).apply {
                        cornerRadius = _cornerRadius
                        setAntiAlias(true)
                    }

                    if (coverAnciently && foreground == null) {
                        foreground = RoundedBitmapDrawableFactory.create(itemView.resources, vin).apply {
                            cornerRadius = _cornerRadius
                            setAntiAlias(true)
                        }
                    }

                    itemView.background = if (coverAnciently) {
                        val layerDrawable = LayerDrawable(arrayOf(roundedArtDrawable, foreground))
                        layerDrawable
                    } else {
                        roundedArtDrawable
                    }


                }else{
                    if (!XSPUtils.getBoolean("is_cover_background",false)){
                        return
                    }

                    XposedHelpers.callMethod(thisObj,"updateResources")


                }
            }
        }
        )
    }




}