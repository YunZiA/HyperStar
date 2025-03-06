package com.yunzia.hyperstar.ui.base.helper

import android.util.Log
import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getCornerRadiusBottom


@Composable
fun getSystemCornerRadius(): Dp {
    val insets = LocalView.current.rootWindowInsets
    val density = LocalDensity.current.density
    val roundedCornerRadius =
        insets?.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: getCornerRadiusBottom()
    return (roundedCornerRadius / density).dp
}

@Composable
fun getSystemSmoothCornerRadius(): Dp {
    val cornerDp = getSystemCornerRadius()
    Log.d("ggc", "getSystemCornerRadius: $cornerDp")
    if (cornerDp <= 22.dp) return cornerDp
    return  cornerDp*11/10
}

@Composable
fun getSystemSmoothCornerShape(): SmoothRoundedCornerShape {
    val cornerDp = getSystemCornerRadius()
    Log.d("ggc", "getSystemCornerRadius: $cornerDp")
    if (cornerDp <= 22.dp) return SmoothRoundedCornerShape(cornerDp,0.8f)
    return  SmoothRoundedCornerShape(cornerDp*12/10,0.8f)
}