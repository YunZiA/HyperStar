package com.yunzia.hyperstar.ui.component.preference.sp

import androidx.compose.runtime.Composable
import Searchable
import com.yunzia.hyperstar.prefs.SPUtils
import com.yunzia.hyperstar.ui.component.preference.PreferenceGroupScope
import com.yunzia.hyperstar.ui.component.preference.rememberPreferenceValue
import com.yunzia.hyperstar.ui.component.preference.searchablePreferenceModifier
import com.yunzia.hyperstar.ui.component.preference.impl.SwitchFolderPreferenceImpl

@Searchable
@Composable
fun PreferenceGroupScope.SpSwitchFolderPreference(
    key: String,
    title: String,
    contrary: Boolean = false,
    visible: () -> Boolean = { true },
    content: @Composable () -> Unit,
) {
    if (!visible()) return
    val checkedState = rememberPreferenceValue(SPUtils.getBoolean(key, false))
    SwitchFolderPreferenceImpl(
        modifier = searchablePreferenceModifier(key),
        title = title,
        checked = checkedState.value,
        contrary = contrary,
        onCheckedChange = {
            checkedState.value = it
            SPUtils.putBoolean(key, it)
        },
        content = content,
    )
}
