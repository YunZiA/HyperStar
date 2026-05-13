package com.yunzia.hyperstar.ui.component.preference.core

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.impl.ColorPickerPreferenceImpl

@Composable
fun PreferenceGroupScope.ColorPickerPreference(
    title: String,
    dfColor: Color = Color.Transparent,
    key: String,
) = ColorPickerPreferenceImpl(
    title = title,
    dfColor = dfColor,
    key = key,
)
