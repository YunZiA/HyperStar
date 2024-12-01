package com.yunzia.hyperstar.hook.app.plugin

import android.content.res.Resources
import android.content.res.XModuleResources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Outline
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.ImageView
import androidx.core.graphics.drawable.toDrawable
import yunzia.utils.BitmapUtils
import yunzia.utils.BitmapUtils.Companion.auto
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.R
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_InitPackageResources


class QSMediaCoverBackground: BaseHooker() {

    private var vintage :Int? = null

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




    override fun doResources(
        resparam: XC_InitPackageResources.InitPackageResourcesParam?,
        modRes: XModuleResources?
    ) {
        super.doResources(resparam, modRes)
        vintage = resparam?.res?.addResource(modRes, R.drawable.vintage)!!


    }

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook()

    }

    private fun startMethodsHook() {
        var foreground: Drawable? = null
        val MediaPlayerMetaData  = XposedHelpers.findClass("miui.systemui.controlcenter.media.MediaPlayerMetaData",classLoader)
        val MediaPlayerViewHolder  = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",classLoader)
        val CommonUtils = XposedHelpers.findClass("miui.systemui.util.CommonUtils",classLoader)

        XposedHelpers.findAndHookMethod(MediaPlayerViewHolder, "updateMetaData", MediaPlayerMetaData , object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {

            }

            override fun afterHookedMethod(param: MethodHookParam?) {
                if (param == null) return

                val mediaPlayerMetaData = param.args[0]
                val thisObj = param.thisObject
                val itemView : View = XposedHelpers.getObjectField(thisObj,"itemView") as View
                val cover = itemView.findViewByIdName("cover") as ImageView
                val res = itemView.resources

                if (mediaPlayerMetaData == null) {

                    if (!defaultBackground){
                        val background = itemView.resources.getIdentifier("media_player_background","drawable",plugin)
                        itemView.setBackgroundResource(background)

                    }

                    return
                }

                if (isHideCover){
                    if (isTitleCenter){
                        cover.visibility = View.GONE

                    }else{
                        cover.visibility = View.INVISIBLE

                    }


                }
                if (defaultBackground){
                    return
                }


                var art = XposedHelpers.callMethod(mediaPlayerMetaData,"getArt")
                if (art == null){
                    starLog.log("art is null")
                    return
                }

                if (art !is Bitmap){
                    starLog.log("mediaPlayerMetaData:art is not get!!!")
                    XposedHelpers.callMethod(thisObj,"updateResources")
                    return

                }

                art = if (mediaBackground == 1){
                    auto(art)
                }else if (mediaBackground == 2){
                    BitmapUtils.doBitmap(art,isScale,scaleFactor,isBlur,blurRadius,isDim,-alpha)
                }else{
                    return
                }

                val artDrawable = art.toDrawable(res)

                if (coverAnciently && foreground == null) {

                    foreground = vintage?.let { res.getDrawable(it) }

                }

                itemView.background = if (coverAnciently) {
                    val layerDrawable = LayerDrawable(arrayOf(artDrawable, foreground))
                    layerDrawable
                } else {
                    artDrawable
                }


            }
        })
        if (!defaultBackground){
            val HapticFeedback = XposedHelpers.findClass("miui.systemui.util.HapticFeedback",classLoader)
            XposedHelpers.findAndHookConstructor(MediaPlayerViewHolder,View::class.java,HapticFeedback,object : XC_MethodHook(){
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val thisObj = param?.thisObject
                    val itemView : View = XposedHelpers.getObjectField(thisObj,"itemView") as View
                    val _cornerRadius : Float = XposedHelpers.getFloatField(thisObj,"_cornerRadius")
                    itemView.outlineProvider = object : ViewOutlineProvider(){
                        override fun getOutline(view: View?, outline: Outline?) {
                            if (view == null) return
                            outline?.setRoundRect(0,0,view.width,view.height,_cornerRadius)
                        }

                    }
                    itemView.clipToOutline = true

                }
            })
            XposedHelpers.findAndHookMethod(MediaPlayerViewHolder,"updateResources",object : XC_MethodReplacement(){
                override fun replaceHookedMethod(param: MethodHookParam?): Any? {

                    return null
                }

            })

        }
    }




}