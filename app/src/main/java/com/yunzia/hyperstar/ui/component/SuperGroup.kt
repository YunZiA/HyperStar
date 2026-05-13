package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.theme.ColorBlendToken
import com.yunzia.hyperstar.ui.theme.isInDarkTheme
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardColors
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTitleDefaults
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.BlurDefaults
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.blur.textureEffect
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.theme.MiuixTheme


@Composable
fun SuperGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    position: SuperGroupPosition = SuperGroupPosition.DEFAULT,
    titleColor: Color = MiuixTheme.colorScheme.onBackgroundVariant,
    cardColor: CardColors = CardDefaults.defaultColors(),
    titleIcon: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardTopSpace = if (position == SuperGroupPosition.FIRST) 12.dp else 6.dp
    val cardBottomPadding = if (position == SuperGroupPosition.LAST) 12.dp else 6.dp


    Column(
        Modifier.fillMaxWidth().padding(top = cardTopSpace)
    ) {
        title?.let {
            Row(modifier = Modifier.padding(SmallTitleDefaults.InsideMargin), verticalAlignment = Alignment.CenterVertically) {
                titleIcon?.invoke()
                titleIcon?.let {
                    Spacer(modifier = Modifier.width(8.dp))
                }
                SmallTitle(
                    text = it,
                    textColor = titleColor,
                    insideMargin = PaddingValues(0.dp)
                )
            }
        }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = cardBottomPadding)
            ,
            colors = cardColor,
            content = content
        )

    }
}

@Composable
fun SuperGroup(
    modifier: Modifier = Modifier,
    title: String? = null,
    backdrop: LayerBackdrop,
    enabled: Boolean = true,
    position: SuperGroupPosition = SuperGroupPosition.DEFAULT,
    titleColor: Color = MiuixTheme.colorScheme.onBackgroundVariant,
    cardColor: CardColors = CardDefaults.defaultColors(),
    titleIcon: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val cardTopSpace = if (position == SuperGroupPosition.FIRST) 12.dp else 6.dp
    val cardBottomPadding = if (position == SuperGroupPosition.LAST) 12.dp else 6.dp

    val isInDark = isInDarkTheme()
    val cardBlendConfigs = remember(isInDark) { if (isInDark) ColorBlendToken.Overlay_Thin_Light else ColorBlendToken.Pured_Regular_Light }

    Column(
        Modifier.fillMaxWidth()
    ) {
            Row(
                modifier = Modifier.padding(top = cardTopSpace),
                verticalAlignment = Alignment.CenterVertically
            ) {
                title?.let {
                titleIcon?.invoke()
                if (titleIcon != null) Spacer(Modifier.width(8.dp))
                SmallTitle(
                    text = it,
                    textColor = titleColor
                )
            }
        }
        Card(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(bottom = cardBottomPadding)
                .textureEffect(
                    backdrop = backdrop,
                    shape = SmoothRoundedCornerShape(CardDefaults.CornerRadius),
                    blurRadius = 200f,
                    colors = BlurColors(
                        blendColors = cardBlendConfigs,
                    ),
                    enabled = enabled
                )
            ,
            colors = if (enabled) CardColors(
                color = Color.Transparent,
                contentColor = Color.Transparent,
            ) else cardColor,
            content = content
        )

    }
}

enum class SuperGroupPosition{
    FIRST,DEFAULT,LAST
}