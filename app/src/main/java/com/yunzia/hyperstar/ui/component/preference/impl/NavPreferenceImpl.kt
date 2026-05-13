package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
internal fun NavPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    summary: String? = null,
    @androidx.annotation.DrawableRes icon: Int? = null,
    endText: String? = null,
    onClick: () -> Unit,
) {
    val click = remember { mutableStateOf(false) }

    ArrowPreference(
        modifier = modifier.bounceAnim {
            if (click.value) onClick()
            click.value = false
        },
        startAction = icon?.let {
            {
                Row {
                    Image(
                        painter = painterResource(it),
                        contentDescription = title,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                }
            }
        },
        title = title,
        summary = summary,
        endActions = {endText?.let {
                Text(
                    text = it,
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .weight(1f, fill = false),
                    fontSize = MiuixTheme.textStyles.body2.fontSize,
                    color = colorScheme.onSurfaceVariantActions,
                    textAlign = TextAlign.End,
                )
            }
        },
        onClick = { click.value = true }
    )
}
