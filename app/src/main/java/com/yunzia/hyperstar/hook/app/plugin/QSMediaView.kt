package com.yunzia.hyperstar.hook.app.plugin

import android.graphics.Color
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.marginTop
import com.github.kyuubiran.ezxhelper.misc.ViewUtils.findViewByIdName
import com.yunzia.hyperstar.hook.base.BaseHooker
import com.yunzia.hyperstar.hook.tool.starLog
import com.yunzia.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Array


class QSMediaView : BaseHooker() {

    val isHideCover:Boolean = XSPUtils.getBoolean("is_hide_cover",false)
    val isTitleCenter:Boolean = XSPUtils.getBoolean("is_title_center",false)
    val isTitleMarquee:Boolean = XSPUtils.getBoolean("is_title_marquee",false)
    val isArtistMarquee:Boolean = XSPUtils.getBoolean("is_artist_marquee",false)
    val isEmptyStateMarquee:Boolean = XSPUtils.getBoolean("is_emptyState_marquee",false)

    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        startMethodsHook()
    }

    private fun startMethodsHook() {
        val ReflectionHelper = findClass("miuix.reflect.ReflectionHelper",classLoader)

        val MediaPlayerViewHolder  = XposedHelpers.findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",classLoader)
        XposedHelpers.findAndHookMethod(MediaPlayerViewHolder, "updateSize", object : XC_MethodHook() {
            override fun beforeHookedMethod(param: MethodHookParam?) {
                //starLog.log("updateSize is hook")
                val thisObj = param?.thisObject
                val itemView : View = XposedHelpers.getObjectField(thisObj,"itemView") as View

                val title = itemView.findViewByIdName("title") as TextView
                val artist = itemView.findViewByIdName("artist") as TextView
                val emptyState = itemView.findViewByIdName("empty_state") as TextView
//                emptyState.marginTop -
                if (isHideCover && isTitleCenter){
                    title.gravity = Gravity.CENTER
                    artist.gravity = Gravity.CENTER
                    val top = title.marginTop*3
                    title.setPadding(0, top, 0, 0)
                }
                if (isTitleMarquee){
                    title.ellipsize = TextUtils.TruncateAt.MARQUEE
                    title.focusable = View.NOT_FOCUSABLE
                    title.isSelected = true

                }
                if (isArtistMarquee){
                    artist.ellipsize = TextUtils.TruncateAt.MARQUEE
                    artist.focusable = View.NOT_FOCUSABLE
                    artist.isSelected = true

                }
                if (isEmptyStateMarquee){
                    emptyState.ellipsize = TextUtils.TruncateAt.MARQUEE
                    emptyState.focusable = View.NOT_FOCUSABLE
                    emptyState.isSelected = true

                }

            }
        })

        val HapticFeedback = XposedHelpers.findClass("miui.systemui.util.HapticFeedback",classLoader)
        val MiShadowUtils = findClass("miuix.core.util.MiShadowUtils",classLoader)

        XposedBridge.hookAllConstructors(MediaPlayerViewHolder, object : XC_MethodHook() {

            override fun afterHookedMethod(param: MethodHookParam?) {
                super.afterHookedMethod(param)
                val itemView : View = param?.args?.get(0) as View
                val title = itemView.findViewByIdName("title") as TextView
                val artist = itemView.findViewByIdName("artist") as TextView
                val emptyState = itemView.findViewByIdName("empty_state") as TextView
                if (isHideCover && isTitleCenter){
                    title.gravity = Gravity.CENTER
                    artist.gravity = Gravity.CENTER
                    val top = title.marginTop*3
                    title.setPadding(0, top, 0, 0)
                }
                if (isTitleMarquee){
                    title.ellipsize = TextUtils.TruncateAt.MARQUEE
                    title.focusable = View.NOT_FOCUSABLE
                    title.isSelected = true

                }
                if (isArtistMarquee){
                    artist.ellipsize = TextUtils.TruncateAt.MARQUEE
                    artist.focusable = View.NOT_FOCUSABLE
                    artist.isSelected = true

                }
                if (isEmptyStateMarquee){
                    emptyState.ellipsize = TextUtils.TruncateAt.MARQUEE
                    emptyState.focusable = View.NOT_FOCUSABLE
                    emptyState.isSelected = true

                }
            }
//            override fun beforeHookedMethod(param: MethodHookParam?) {
//                super.beforeHookedMethod(param)
//                //starLog.log("MediaPlayerViewHolder is hook")
//
//
//
//
//
//            }

        })

//        XposedHelpers.findAndHookMethod(MediaPlayerViewHolder, "updateIconsInfo",Class.forName("miui.systemui.controlcenter.media.MediaPlayerIconsInfo"), object : XC_MethodHook() {
//            override fun beforeHookedMethod(param: MethodHookParam?) {
//                starLog.log("updateSize is hook")
//                val thisObj = param?.thisObject
//                val itemView : View = XposedHelpers.getObjectField(thisObj,"itemView") as View
//
//                val deviceIcon = itemView.findViewByIdName("device_icon") as ImageView
//
//                deviceIcon.setRenderEffect(RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP))
//            }
//        })




    }

    fun addViewShadow(f: Float, f2: Float, i: Int, view: View?) {

        try {
            val cls = Class.forName("android.view.View")
            val cls2: Class<*> = Float::class.java
            cls.getMethod(
                "setMiShadow",
                Integer.TYPE, cls2, cls2, cls2, cls2,
                Boolean::class.java
            ).invoke(view, Color.argb(i, 0, 0, 0), 0.0f, f, f2, 1.0f, false)
        } catch (unused: Exception) {
            starLog.log("addViewShadow setMiShadow Method not found!")
        }
    } //    private void doResHook(XC_InitPackageResources.InitPackageResourcesParam resparam){
    //
    //        resparam.res.setReplacement(resparam.packageName,"drawable","qs");
    //
    //    }


}