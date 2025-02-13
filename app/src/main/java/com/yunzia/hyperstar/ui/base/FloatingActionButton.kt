package com.yunzia.hyperstar.ui.base

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.theme.MiuixTheme

@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(60.dp),
    containerColor: Color = MiuixTheme.colorScheme.primary,
    shadowElevation: Dp = 4.dp,
    minWidth: Dp = 60.dp,
    minHeight: Dp = 60.dp,
    insideMargin: PaddingValues = PaddingValues(0.dp),
    defaultWindowInsetsPadding: Boolean = true,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = if (defaultWindowInsetsPadding) {
            modifier
                .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
        } else {
            modifier
        }.padding(insideMargin)

    ){
        Surface(
            onClick = onClick,
            modifier = Modifier.semantics { role = Role.Button },
            shape = shape,
            color = containerColor,
            shadowElevation = shadowElevation
        ) {
            Box(
                modifier = Modifier
                    .defaultMinSize(
                        minWidth = minWidth,
                        minHeight = minHeight,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                content()
            }
        }

    }


}