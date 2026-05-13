package com.yunzia.hyperstar.ui.component.preference.pr

import androidx.compose.runtime.Composable
import com.yunzia.hyperstar.prefs.PreferencesUtil
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchFolderPreferenceImpl

@Composable
fun PreferenceGroupScope.PrSwitchFolderPreference(
    key: String,
    title: String,
    contrary: Boolean = false,
    content: @Composable () -> Unit,
) {
    val checkedState = rememberPreferenceValue(PreferencesUtil.getBoolean(key, false))
    SwitchFolderPreferenceImpl(
        title = title,
        checked = checkedState.value,
        contrary = contrary,
        onCheckedChange = {
            checkedState.value = it
            PreferencesUtil.putBoolean(key, it)
        },
        content = content,
    )
}
