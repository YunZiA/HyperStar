package com.yunzia.hyperstar.hook.app.plugin.powermenu.base

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View

data class MenuItem(
    val image: Drawable? = null,
    val text: String? = null,
    val state: Boolean = false,
    val isEmpty: Boolean? = false,
    val click: ((View, Context) -> Unit)? = null
)


