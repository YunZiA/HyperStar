package com.chaos.hyperstar.hook.app.plugin

import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import com.chaos.hyperstar.MainActivity
import com.chaos.hyperstar.R
import com.chaos.hyperstar.hook.base.BaseHooker
import com.chaos.hyperstar.hook.tool.starLog
import com.chaos.hyperstar.utils.XSPUtils
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers

class QsTileRadius: BaseHooker() {
    override fun doMethods(classLoader: ClassLoader?) {
        super.doMethods(classLoader)
        if (XSPUtils.getBoolean("is_qs_tile_radius",false)){
            //startMethodsHook(classLoader)
        }

    }

}