package com.yunzia.hyperstar.hook.app.plugin.os1

import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.core.view.marginTop
import com.yunzia.hyperstar.hook.base.Hooker
import com.yunzia.hyperstar.hook.util.startMarqueeOfFading
import com.yunzia.hyperstar.utils.XSPUtils

//import de.robv.android.xposed.XposedHelpers

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
        val fadingEdgeLength = 40

        findClass(
            "miui.systemui.controlcenter.panel.main.media.MediaPlayerController\$MediaPlayerViewHolder",
            classLoader
        ).apply {
            beforeHookMethod("updateSize"){
                val itemView = this.getObjectFieldAs<View>("itemView")
                val title = itemView.findViewByIdNameAs<TextView>("title")
                val artist = itemView.findViewByIdNameAs<TextView>("artist")
                val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                if (isHideCover && isTitleCenter){
                    artist.gravity = Gravity.CENTER
                    title.apply {
                        gravity = Gravity.CENTER
                        setPadding(0,title.marginTop*3,0,0,)
                    }
                }
                if (isTitleMarquee){
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
            beforeHookConstructor(
                View::class.java
            ){
                val itemView = it.args[0] as View
                val title = itemView.findViewByIdNameAs<TextView>("title")
                val artist = itemView.findViewByIdNameAs<TextView>("artist")
                val emptyState = itemView.findViewByIdNameAs<TextView>("empty_state")
                if (isHideCover && isTitleCenter){
                    artist.gravity = Gravity.CENTER
                    title.apply {
                        gravity = Gravity.CENTER
                        setPadding(0,title.marginTop*3,0,0,)
                    }
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
        }

    }


}