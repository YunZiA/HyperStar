package com.yunzia.hyperstar.utils

import android.content.Context
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app.AppInfo

fun root(mContext:Context) = listOf(
    AppInfo(
        label = "Magisk",
        packageName = "com.topjohnwu.magisk",
        icon = mContext.resources.getDrawable(R.drawable.magisk)
    ),
    AppInfo(
        label = "Alpha",
        packageName = "io.github.vvb2060.magisk",
        icon =  mContext.resources.getDrawable(R.drawable.alpha)
    ),
    AppInfo(
        label = "Kitsune Mask",
        packageName = "io.github.huskydg.magisk",
        icon =  mContext.resources.getDrawable(R.drawable.kitsune_mask)
    ),
    AppInfo(
        label = "KernelSU",
        packageName = "me.weishu.kernelsu",
        icon =  mContext.resources.getDrawable(R.drawable.kernelsu)
    ),
    AppInfo(
        label = "APatch",
        packageName = "me.bmax.apatch",
        icon =  mContext.resources.getDrawable(R.drawable.apatch)
    )
)