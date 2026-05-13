package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
internal fun PreferenceWithValueImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    value: String = "",
    enabled: Boolean = true,
    onClick: () -> Unit = {},
) {
    val click = remember { mutableStateOf(false) }

    ArrowPreference(
        modifier = modifier
            .bounceAnim {
                if (click.value && enabled) {
                    onClick()
                }
                click.value = false
            }
            .alpha(if (enabled) 1f else 0.5f),
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
            Text(
                text = value,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .weight(1f, fill = false)
                    .padding(end = 8.dp),
                fontSize = MiuixTheme.textStyles.body2.fontSize,
                color = colorScheme.onSurfaceVariantActions,
                textAlign = TextAlign.End,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        onClick = {
            if (enabled) {
                click.value = true
            }
        }
    )
}
