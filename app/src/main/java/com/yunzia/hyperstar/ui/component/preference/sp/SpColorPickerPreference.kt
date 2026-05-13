package com.yunzia.hyperstar.ui.component.preference.sp

import Searchable
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.prefs.util.PrefUtils
import com.yunzia.hyperstar.ui.component.preference.impl.ColorPickerPreferenceImpl
import yunzia.colorpicker.colorFromHex

@Searchable
@Composable
fun PreferenceGroupScope.SpColorPickerPreference(
    key: String,
    title: String,
    dfColor: Color = Color.Transparent,
    visible: () -> Boolean = { true },
) {
    if (!visible()) return
    ColorPickerPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        dfColor = getDefaultColor(dfColor, key, SPUtils),
        key = key,
    )
}

private fun getDefaultColor(dfColor: Color, key: String, prefs: PrefUtils): Color {
    val localColor = prefs.getString(key, "null")
    return if (localColor == "null") dfColor else localColor.colorFromHex()
}
