package com.yunzia.hyperstar.ui.component.helper

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.DpSize

@Composable
fun getWindowSize(): DpSize{
    val windowInfo = LocalWindowInfo.current
    return windowInfo.containerDpSize
}