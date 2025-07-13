package com.yunzia.hyperstar.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun TabRow(
    modifier: Modifier = Modifier,
    tabs: List<String>,
    selectedTabIndex: Int,
    colors: TabRowColors = TabRowDefaults.tabRowColors(),
    cornerRadius: Dp = TabRowDefaults.TabRowCornerRadius,
    onTabSelected: ((Int) -> Unit)? = null,
) {
    val currentOnTabSelected by rememberUpdatedState(onTabSelected)
    val shape = remember(cornerRadius) { SmoothRoundedCornerShape(cornerRadius) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        tabs.forEachIndexed { index, tabText ->
            Box(
                //color = colors.backgroundColor(selectedTabIndex == index),
                modifier = Modifier
                    .weight(1.0f)
                    .semantics { role = Role.Tab }
                    .clip(shape)
                    .background(colors.backgroundColor(selectedTabIndex == index))
                    .clickable(null,null){
                        currentOnTabSelected?.invoke(index)
                    }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 40.dp)
                        .padding(horizontal = 10.dp, vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabText,
                        color = colors.contentColor(selectedTabIndex == index),
                        fontSize = 15.sp,
                        fontWeight = if (selectedTabIndex == index) FontWeight(520) else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

object TabRowDefaults {
    /**
     * The default corner radius of the [TabRow].
     */
    val TabRowCornerRadius = 12.dp

    /**
     * The default corner radius of the [TabRow] with contour style.
     */
//    val TabRowWithContourCornerRadius = 10.dp

    /**
     * The default colors for the [TabRow].
     */
    @Composable
    fun tabRowColors(
        backgroundColor: Color = MiuixTheme.colorScheme.background,
        contentColor: Color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
        selectedBackgroundColor: Color = MiuixTheme.colorScheme.surface,
        selectedContentColor: Color = MiuixTheme.colorScheme.onSurface
    ): TabRowColors = TabRowColors(
        backgroundColor = backgroundColor,
        contentColor = contentColor,
        selectedBackgroundColor = selectedBackgroundColor,
        selectedContentColor = selectedContentColor
    )
}


@Immutable
class TabRowColors(
    private val backgroundColor: Color,
    private val contentColor: Color,
    private val selectedBackgroundColor: Color,
    private val selectedContentColor: Color
) {
    @Stable
    internal fun backgroundColor(selected: Boolean): Color =
        if (selected) selectedBackgroundColor else backgroundColor

    @Stable
    internal fun contentColor(selected: Boolean): Color =
        if (selected) selectedContentColor else contentColor
}