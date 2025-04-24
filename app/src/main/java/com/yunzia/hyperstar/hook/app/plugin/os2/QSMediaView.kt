package com.yunzia.hyperstar.hook.app.plugin.os2

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.marginTop
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.base.afterHookAllConstructors
import com.yunzia.hyperstar.hook.base.findClass
import com.yunzia.hyperstar.hook.util.startMarqueeOfFading
import com.yunzia.hyperstar.utils.XSPUtils


class QSMediaView : Hooker() {

    val isHideCover:Boolean = XSPUtils.getBoolean("is_hide_cover",false)
    val isTitleCenter:Boolean = XSPUtils.getBoolean("is_title_center",false)
    val isTitleMarquee:Boolean = XSPUtils.getBoolean("is_title_marquee",false)
    val isArtistMarquee:Boolean = XSPUtils.getBoolean("is_artist_marquee",false)
    val isEmptyStateMarquee:Boolean = XSPUtils.getBoolean("is_emptyState_marquee",false)

    override fun initHook(classLoader: ClassLoader?) {
        super.initHook(classLoader)
        startMethodsHook()
    }

    private fun startMethodsHook() {
        val ReflectionHelper = findClass("miuix.reflect.ReflectionHelper",classLoader)
        val fadingEdgeLength = 40
        val MediaPlayerViewHolder  = findClass("miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",classLoader)
        MediaPlayerViewHolder.apply {
            beforeHookMethod(
                "updateSize"
            ){
                val itemView = this.getObjectFieldAs<View>("itemView")

                val title = itemView.findViewByIdNameAs<TextView>("title")
                val artist = itemView.findViewByIdNameAs<TextView>("artist")
                val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
//                emptyState.marginTop -
                if (isHideCover && isTitleCenter){
                    val top = title.marginTop*3
                    title.apply {
                        gravity = Gravity.CENTER
                        setPadding(0, top, 0, 0)
                    }
                    artist.gravity = Gravity.CENTER
                }
                if (isTitleMarquee){
                    title.startMarqueeOfFading(fadingEdgeLength)

                }
                if (isArtistMarquee){
                    artist.startMarqueeOfFading(fadingEdgeLength)

                }
                if (isEmptyStateMarquee){
                    emptyState.startMarqueeOfFading(fadingEdgeLength)

                }

            }
            afterHookAllConstructors {
                val itemView = it.args[0] as View
                val title = itemView.findViewByIdNameAs<TextView>("title")
                val artist = itemView.findViewByIdNameAs<TextView>("artist")
                val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                if (isHideCover && isTitleCenter){
                    val top = title.marginTop*3
                    title.apply {
                        gravity = Gravity.CENTER
                        setPadding(0, top, 0, 0)
                    }
                    artist.gravity = Gravity.CENTER
                }
                if (isTitleMarquee){
                    //setDeclaredBooleanField(title::class.java,"mHasOverlappingRendering",false)
                    title.apply {
                        ellipsize = TextUtils.TruncateAt.MARQUEE
                        isFocusable = true
                        isSelected = true
                        marqueeRepeatLimit = 3
                        isHorizontalFadingEdgeEnabled = true
                        setFadingEdgeLength(fadingEdgeLength)
                        forceHasOverlappingRendering(false)
                    }

                }
                if (isArtistMarquee){
                    artist.apply {
                        ellipsize = TextUtils.TruncateAt.MARQUEE
                        isFocusable = true
                        isSelected = true
                        marqueeRepeatLimit = 3
                        isHorizontalFadingEdgeEnabled = true
                        setFadingEdgeLength(fadingEdgeLength)
                        forceHasOverlappingRendering(false)
                    }

                }
                if (isEmptyStateMarquee){
                    emptyState.apply {
                        ellipsize = TextUtils.TruncateAt.MARQUEE
                        isFocusable = true
                        isSelected = true
                        marqueeRepeatLimit = 3
                        isHorizontalFadingEdgeEnabled = true
                        setFadingEdgeLength(fadingEdgeLength)
                        forceHasOverlappingRendering(false)
                    }

                }

            }
//            beforeHookMethod(
//                "updateIconsInfo",
//                "miui.systemui.controlcenter.media.MediaPlayerIconsInfo",
//                Boolean::class.java
//            ){
//                val itemView : View = this.getObjectFieldAs<View>("itemView")
//
//                val deviceIcon = itemView.findViewByIdNameAs<ImageView>("device_icon")
//
//                deviceIcon.setRenderEffect(RenderEffect.createBlurEffect(50f, 50f, Shader.TileMode.CLAMP))
//            }
        }

        val HapticFeedback = findClass("miui.systemui.util.HapticFeedback",classLoader)
        val MiShadowUtils = findClass("miuix.core.util.MiShadowUtils",classLoader)


    }



}