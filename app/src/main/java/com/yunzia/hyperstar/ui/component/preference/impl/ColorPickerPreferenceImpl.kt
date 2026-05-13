package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.ColorPickerTool

@Composable
internal fun ColorPickerPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    dfColor: Color = Color.Transparent,
    key: String,
) = ColorPickerTool(
    modifier = modifier,
    title = title,
    dfColor = dfColor,
    key = key,
)
