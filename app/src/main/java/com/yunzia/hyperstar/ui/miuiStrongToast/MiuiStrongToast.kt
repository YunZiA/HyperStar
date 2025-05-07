package com.yunzia.hyperstar.ui.miuiStrongToast

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Context
import android.os.Bundle
import androidx.core.graphics.toColorInt
import com.yunzia.hyperstar.BuildConfig
import kotlinx.serialization.json.Json
import java.lang.reflect.InvocationTargetException

object MiuiStrongToast {

    @SuppressLint("WrongConstant")
    fun showStrongToast(context: Context, text: String, duration: Long = 1500L) {
        try {
            val textParams = TextParams(text, "#FFFFFFFF".toColorInt())
            val left = Left(textParams = textParams)
            val iconParams = IconParams(Category.DRAWABLE, FileType.PNG, "ic_app_icon", 1)
            val right = Right(iconParams = iconParams)
            val strongToastBean = StrongToastBean(left, right)
            val jsonStr = Json.encodeToString(StrongToastBean.serializer(), strongToastBean)

            val bundle = StrongToastBundle.Builder()
                .setPackageName(BuildConfig.APPLICATION_ID)
                .setStrongToastCategory(StrongToastCategory.TEXT_BITMAP)
                .setDuration(duration)
                .setParam(jsonStr)
                .setTarget(null as PendingIntent?)
                .setDuration(3000L)
                .setLevel(0.0f)
                .setRapidRate(0.0f)
                .setCharge(null as String?)
                .setStrongToastChargeFlag(0)
                .setStatusBarStrongToast("show_custom_strong_toast")
                .onCreate()
            val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
            service.javaClass.getMethod(
                "setStatus", Int::class.javaPrimitiveType, String::class.java, Bundle::class.java
            ).invoke(service, 1, "strong_toast_action", bundle)
        } catch (e: IllegalAccessException) {
            throw RuntimeException(e)
        } catch (e: InvocationTargetException) {
            throw RuntimeException(e)
        } catch (e: NoSuchMethodException) {
            throw RuntimeException(e)
        }
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