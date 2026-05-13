package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.preference.SwitchPreference

@Composable
internal fun SwitchPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    checked: Boolean = false,
    enabled: Boolean = true,
    onCheckedChange: (Boolean) -> Unit = {},
) {
    SwitchPreference(
        modifier = modifier,
        startAction = icon?.let {
            {
                Row {
                    Image(
                        painter = painterResource(it),
                        contentDescription = title,
                        modifier = Modifier.size(35.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }
        },
        title = title,
        summary = summary,
        checked = checked,
        enabled = enabled,
        onCheckedChange = onCheckedChange,
    )
}
