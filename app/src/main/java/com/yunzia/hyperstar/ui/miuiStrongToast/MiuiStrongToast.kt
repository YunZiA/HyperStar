package com.yunzia.hyperstar.ui.miuiStrongToast

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import kotlinx.serialization.json.Json

object MiuiStrongToast {
    @SuppressLint("WrongConstant")
    fun showStrongToast(context: Context, text: String, duration: Long = 1500L) {
        val textParams = TextParams(text, Color.parseColor("#FFFFFFFF"))
        val left = Left(textParams = textParams)
        val iconParams = IconParams(Category.DRAWABLE, FileType.PNG, "ic_app_icon", 1)
        val right = Right(iconParams = iconParams)
        val strongToastBean = StrongToastBean(left, right)
        val jsonStr = Json.encodeToString(StrongToastBean.serializer(), strongToastBean)
        val bundle = StrongToastBundle.Builder()
            .setPackageName("com.yunzia.hyperstar")
            .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP)
            .setDuration(duration)
            .setParam(jsonStr)
            .onCreate()
        val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
        service.javaClass.getMethod(
            "setStatus", Int::class.javaPrimitiveType, String::class.java, Bundle::class.java
        ).invoke(service, 1, "strong_toast_action", bundle)
    }

    object Category {
        const val RAW = "raw"
        const val DRAWABLE = "drawable"
        const val FILE = "file"
        const val MIPMAP = "mipmap"
    }

    object FileType {
        const val MP4 = "mp4"
        const val PNG = "png"
        const val SVG = "svg"
    }

    object StrongToastCategory {
        const val VIDEO_TEXT = "video_text"
        const val VIDEO_BITMAP_INTENT = "video_bitmap_intent"
        const val TEXT_BITMAP = "text_bitmap"
        const val TEXT_BITMAP_INTENT = "text_bitmap_intent"
        const val VIDEO_TEXT_TEXT_VIDEO = "video_text_text_video"
    }
}