package yunzia.basic

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.lerp
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.nav.backParentPager
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import kotlin.math.abs
import kotlin.math.roundToInt



@Composable
fun NavTopAppBar(
    modifier: Modifier,
    title: String,
    subTitle : String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color,
    navController: NavController,
    parentRoute: MutableState<String>,
    actions: @Composable() (RowScope.() -> Unit) = {}
){

    val view = LocalView.current

    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        subTitle = subTitle,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                modifier = Modifier.padding(start = 12.dp),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    navController.backParentPager(parentRoute.value)

                }
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.bar_back__exit),
                    contentDescription = "back",
                    tint = colorScheme.onBackground
                )
            }

        },
        actions = actions
    )

}

@Composable
fun TopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    color: Color = MiuixTheme.colorScheme.background,
    subTitle: String,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    scrollBehavior: ScrollBehavior? = null,
    defaultWindowInsetsPadding: Boolean = true,
    horizontalPadding: Dp = 26.dp
) {
    val largeTitleHeight = remember { mutableStateOf(0) }
    val expandedHeightPx by rememberUpdatedState(
        remember(largeTitleHeight.value) {
            largeTitleHeight.value.toFloat().coerceAtLeast(0f)
        }
    )

    SideEffect {
        // Sets the app bar's height offset to collapse the entire bar's height when content is
        // scrolled.
        if (scrollBehavior?.state?.heightOffsetLimit != -expandedHeightPx) {
            scrollBehavior?.state?.heightOffsetLimit = -expandedHeightPx
        }
    }

    // Wrap the given actions in a Row.
    val actionsRow =
        @Composable {
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                content = actions
            )
        }
    // Compose a Surface with a TopAppBarLayout content.
    // The surface's background color is animated as specified above.
    // The height of the app bar is determined by subtracting the bar's height offset from the
    // app bar's defined constant height value (i.e. the ContainerHeight token).
    Surface(
        color = color,
        modifier =
            modifier
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, _ -> }
                }
                .then(
                    if (defaultWindowInsetsPadding) {
                        Modifier
                            .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                            .windowInsetsPadding(WindowInsets.navigationBars.only(WindowInsetsSides.Horizontal))
                    } else Modifier
                )
    ) {
        TopAppBarLayout(
            title = title,
            subTitle = subTitle,
            navigationIcon = navigationIcon,
            actions = actionsRow,
            scrolledOffset = { scrollBehavior?.state?.heightOffset ?: 0f },
            expandedHeightPx = expandedHeightPx,
            horizontalPadding = horizontalPadding,
            largeTitleHeight = largeTitleHeight,
            defaultWindowInsetsPadding = defaultWindowInsetsPadding
        )
    }
}


@Composable
private fun TopAppBarLayout(
    title: String,
    subTitle: String,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
    scrolledOffset: ScrolledOffset,
    expandedHeightPx: Float,
    horizontalPadding: Dp,
    largeTitleHeight: MutableState<Int>,
    defaultWindowInsetsPadding: Boolean
) {
    // Subtract the scrolledOffset from the maxHeight. The scrolledOffset is expected to be
    // equal or smaller than zero.
    val heightOffset by remember(scrolledOffset) {
        derivedStateOf {
            val offset = scrolledOffset.offset()
            if (offset.isNaN()) 0 else (1.5*offset).roundToInt()
        }
    }

    // Small Title Animation
    val extOffset = abs(scrolledOffset.offset()) / expandedHeightPx * 2
    val alpha by animateFloatAsState(
        targetValue = if (1 - extOffset.coerceIn(0f, 1f) == 0f) 1f else 0f,
        animationSpec = tween(durationMillis = 250)
    )
    val translationY by animateFloatAsState(
        targetValue = if (extOffset > 1f) 0f else 10f,
        animationSpec = tween(durationMillis = 250)
    )

    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
            ) {
                navigationIcon()
            }
            Column(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = horizontalPadding)
                    .graphicsLayer(
                        alpha = alpha,
                        translationY = translationY
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = title,
                    maxLines = 1,
                    fontSize = MiuixTheme.textStyles.title3.fontSize,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )

                Text(
                    text = subTitle,
                    maxLines = 1,
                    color = colorScheme.onSurfaceVariantSummary.copy(alpha=0.55f),
                    fontSize = 15.8.sp,
                    fontWeight = FontWeight.Medium,
                    overflow = TextOverflow.Ellipsis,
                    softWrap = false
                )
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
            ) {
                actions()
            }
            Box(
                Modifier
                    .layoutId("largeTitle")
                    .fillMaxWidth()
            ) {
                Column(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 56.dp)
                        .padding(horizontal = horizontalPadding)
                        .graphicsLayer(alpha = 1f - (abs(scrolledOffset.offset()) / expandedHeightPx * 2).coerceIn(0f, 1f))
                ) {
                    Text(
                        modifier = Modifier.offset { IntOffset(0, heightOffset) },
                        text = title,
                        fontSize = MiuixTheme.textStyles.title1.fontSize,
                        fontWeight = FontWeight.Normal,
                        onTextLayout = {
                            largeTitleHeight.value = it.size.height
                        }
                    )

                    Text(
                        text = subTitle,
                        maxLines = 1,
                        color = colorScheme.onSurfaceVariantSummary.copy(alpha=0.55f),
                        modifier = Modifier.offset { IntOffset(0, heightOffset) },
                        fontSize = 16.8.sp,
                        fontWeight = FontWeight.Medium,
                        overflow = TextOverflow.Ellipsis,
                        softWrap = false
                    )
                }
            }
        },
        modifier = Modifier
            .then(
                if (defaultWindowInsetsPadding) {
                    Modifier
                        .windowInsetsPadding(WindowInsets.statusBars.only(WindowInsetsSides.Top))
                        .windowInsetsPadding(WindowInsets.captionBar.only(WindowInsetsSides.Top))
                } else Modifier
            )
            .clipToBounds()
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables
                .fastFirst { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val actionIconsPlaceable =
            measurables
                .fastFirst { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0, minHeight = 0))

        val maxTitleWidth = constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width

        val titlePlaceable =
            measurables
                .fastFirst { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = (maxTitleWidth * 0.9).roundToInt(), minHeight = 0))

        val largeTitlePlaceable =
            measurables
                .fastFirst { it.layoutId == "largeTitle" }
                .measure(
                    constraints.copy(
                        minWidth = 0,
                        minHeight = 0,
                        maxHeight = Constraints.Infinity
                    )
                )

        val collapsedHeight = 56.dp.roundToPx()
        val expandedHeight = maxOf(
            collapsedHeight,
            largeTitlePlaceable.height
        )

        val layoutHeight = lerp(
            start = collapsedHeight,
            stop = expandedHeight,
            fraction = if (expandedHeightPx > 0f) {
                val offset = scrolledOffset.offset()
                if (offset.isNaN()) 1f else (1f - (abs(offset) / expandedHeightPx).coerceIn(0f, 1f))
            } else 1f
        ).toFloat().roundToInt()

        layout(constraints.maxWidth, layoutHeight) {
            val verticalCenter = collapsedHeight / 2

            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = verticalCenter - navigationIconPlaceable.height / 2
            )

            // Title
            var baseX = (constraints.maxWidth - titlePlaceable.width) / 2
            if (baseX < navigationIconPlaceable.width) {
                baseX += (navigationIconPlaceable.width - baseX)
            } else if (baseX + titlePlaceable.width > constraints.maxWidth - actionIconsPlaceable.width) {
                baseX += ((constraints.maxWidth - actionIconsPlaceable.width) - (baseX + titlePlaceable.width))
            }
            titlePlaceable.placeRelative(
                x = baseX,
                y = verticalCenter - titlePlaceable.height / 2
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = verticalCenter - actionIconsPlaceable.height / 2
            )

            // Large title
            largeTitlePlaceable.placeRelative(
                x = 0,
                y = 0
            )
        }
    }
}

private fun interface ScrolledOffset {
    fun offset(): Float
}
