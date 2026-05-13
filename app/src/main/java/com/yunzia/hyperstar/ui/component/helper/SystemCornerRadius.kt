package com.yunzia.hyperstar.ui.component.helper

import android.view.RoundedCorner
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getCornerRadiusBottom


@Composable
fun getSystemCornerRadius(): Dp {
    val insets = LocalView.current.rootWindowInsets
    val density = LocalDensity.current.density
    val context = LocalContext.current
    val roundedCornerRadius =
        insets?.getRoundedCorner(RoundedCorner.POSITION_BOTTOM_LEFT)?.radius ?: getCornerRadiusBottom(context)
    return (roundedCornerRadius / density).dp
}

@Composable
fun getSystemSmoothCornerRadius(): Dp {
    val cornerDp = getSystemCornerRadius()
    if (cornerDp <= 22.dp) return cornerDp
    return  cornerDp*11/12
}

@Composable
fun getSystemSmoothCornerShape(): SmoothRoundedCornerShape {
    val cornerDp = getSystemCornerRadius()
    if (cornerDp <= 22.dp) return SmoothRoundedCornerShape(cornerDp)
    return  SmoothRoundedCornerShape(cornerDp*12/10)
}
