package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.preference.ArrowPreference

@Composable
internal fun PreferenceImpl(
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val click = remember { mutableStateOf(false) }

    ArrowPreference(
        modifier = Modifier
            .bounceAnim {
                if (click.value && enabled) {
                    onClick()
                }
                click.value = false
            }
            .run {
                if (!enabled) alpha(0.5f) else this
            },
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
        onClick = {
            if (enabled) {
                click.value = true
            }
        }
    )
}
