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
import top.yukonga.miuix.kmp.preference.ArrowPreference

@Composable
internal fun PreferenceWithContentImpl(
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    enabled: Boolean = true,
    content: @Composable () -> Unit = {},
) {
    ArrowPreference(
        modifier = Modifier.alpha(if (enabled) 1f else 0.5f),
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
        endActions = {
            content()
        }
    )
}
