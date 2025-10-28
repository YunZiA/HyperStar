package com.yunzia.hyperstar.ui.component.topbar

import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFirst
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import kotlin.math.roundToInt

@Composable
fun TopBar(
    modifier: Modifier = Modifier,
    title: String,
    leftIcon: @Composable () -> Unit = {},
    rightIcon: @Composable () -> Unit = {},
    horizontalPadding: Dp = 16.dp,
) {
    val titleModifier = remember(horizontalPadding) {
        Modifier
            .layoutId("title")
            .padding(horizontal = horizontalPadding)
    }

    Layout(
        {
            Box(
                Modifier
                    .layoutId("leftIcon")
            ) {
                leftIcon()
            }
            Box(titleModifier) {
                Text(
                    text = title,
                    maxLines = 1,
                    fontSize = MiuixTheme.textStyles.title3.fontSize,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
            Box(
                Modifier
                    .layoutId("rightIcon")
            ) {
                rightIcon()
            }
        },
        modifier = modifier
            .padding(horizontal = horizontalPadding)
            .heightIn(max = 56.dp)
            .pointerInput(Unit) { detectVerticalDragGestures { _, _ -> } }
    ) { measurables, constraints ->
        val leftIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "leftIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val rightIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "rightIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val maxTitleWidth = constraints.maxWidth - leftIconPlaceable.width - rightIconPlaceable.width

        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = (maxTitleWidth * 0.9).roundToInt(), minHeight = 0))

        val layoutHeight =
            if (constraints.maxHeight == Constraints.Infinity) {
                constraints.maxHeight
            } else {
                constraints.maxHeight
            }

        layout(constraints.maxWidth, layoutHeight) {
            val verticalCenter = 60.dp.roundToPx() / 2

            // Navigation icon
            leftIconPlaceable.placeRelative(
                x = 0,
                y = verticalCenter - leftIconPlaceable.height / 2
            )

            // Title
            var baseX = (constraints.maxWidth - titlePlaceable.width) / 2
            if (baseX < leftIconPlaceable.width) {
                baseX += (leftIconPlaceable.width - baseX)
            } else if (baseX + titlePlaceable.width > constraints.maxWidth - rightIconPlaceable.width) {
                baseX += ((constraints.maxWidth - rightIconPlaceable.width) - (baseX + titlePlaceable.width))
            }
            titlePlaceable.placeRelative(
                x = baseX,
                y = verticalCenter - titlePlaceable.height / 2
            )

            // Action icons
            rightIconPlaceable.placeRelative(
                x = constraints.maxWidth - rightIconPlaceable.width,
                y = verticalCenter - rightIconPlaceable.height / 2
            )
        }
    }
}