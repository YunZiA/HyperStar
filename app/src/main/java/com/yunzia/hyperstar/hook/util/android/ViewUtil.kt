package com.yunzia.hyperstar.hook.util.android

import android.content.Context
import android.view.View
import com.yunzia.hyperstar.hook.util.android.ViewUtil.getResourceIdByName

object ViewUtil {


    fun getResourceIdByName(name: String, type: String = "id", ctx: Context): Int {
        return ctx.resources.getIdentifier(name, type, ctx.packageName)
    }
}

fun View.findViewByIdName(name: String): View? {
    val id = getResourceIdByName(name, ctx = this.context)
    if (id == 0) return null
    return this.findViewById(id)
}