package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.prefs.util.PrefUtils
import com.yunzia.hyperstar.ui.component.preference.impl.ColorPickerPreferenceImpl
import yunzia.colorpicker.colorFromHex

@Composable
fun PreferenceGroupScope.PrColorPickerPreference(
    key: String,
    title: String,
    dfColor: Color = Color.Transparent,
) = ColorPickerPreferenceImpl(
    title = title,
    dfColor = getDefaultColor(dfColor, key, PreferencesUtil),
    key = key,
)

private fun getDefaultColor(dfColor: Color, key: String, prefs: PrefUtils): Color {
    val localColor = prefs.getString(key, "null")
    return if (localColor == "null") dfColor else localColor.colorFromHex()
}
