package com.yunzia.hyperstar.ui.component.topbar

import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.navigation.Navigator
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.TopAppBarDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color = Color.Transparent,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        titlePadding = TopAppBarDefaults.TitlePadding,
        navigationIconPadding = TopAppBarDefaults.TitlePadding,
        actionIconPadding = TopAppBarDefaults.TitlePadding,
        actions = actions
    )
}

@Composable
fun HyperStarTopAppBar(
    modifier: Modifier = Modifier,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color = Color.Transparent,
    onBack: () -> Unit,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        titlePadding = TopAppBarDefaults.TitlePadding,
        navigationIconPadding = TopAppBarDefaults.TitlePadding,
        actionIconPadding = TopAppBarDefaults.TitlePadding,
        navigationIcon = {
            TopButton(
                imageVector = ImageVector.vectorResource(R.drawable.bar_back__exit),
                contentDescription = "back",
                onClick = onBack
            )
        },
        actions = actions
    )
}

@Composable
fun ModuleTopAppBar(
    modifier: Modifier,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color,
    activity: ComponentActivity,
    endIcon: @Composable () -> Unit = {},
    endClick: () -> Unit = {}
) {
    HyperStarTopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        onBack = { activity.finish() },
        actions = {
            endIcon()
            TopButton(
                imageVector = ImageVector.vectorResource(R.drawable.ic_menu_refresh),
                contentDescription = "restart",
                onClick = endClick
            )
        }
    )
}

@Composable
fun ActivityTopAppBar(
    modifier: Modifier,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color,
    activity: ComponentActivity,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    val view = LocalView.current

    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                modifier = Modifier.padding(start = 12.dp),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    activity.finish()
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
fun NavTopAppBar(
    modifier: Modifier,
    navController: Navigator,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color,
    actions: @Composable (RowScope.() -> Unit) = {},
) {
    HyperStarTopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        onBack = { navController.goBack() },
        actions = actions
    )
}

@Composable
fun NavSmallTopAppBar(
    modifier: Modifier,
    navController: Navigator,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color,
    parentRoute: MutableState<String>,
    actions: @Composable (RowScope.() -> Unit) = {}
) {
    val view = LocalView.current

    SmallTopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        titlePadding = TopAppBarDefaults.TitlePadding,
        navigationIconPadding = TopAppBarDefaults.TitlePadding,
        actionIconPadding = TopAppBarDefaults.TitlePadding,
        navigationIcon = {
            IconButton(
                modifier = Modifier,
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    navController.goBack()
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
fun ModuleNavTopAppBar(
    modifier: Modifier,
    title: String,
    scrollBehavior: ScrollBehavior? = null,
    color: Color,
    startClick: () -> Unit,
    endIcon: @Composable () -> Unit = {},
    endClick: () -> Unit = {}
) {
    HyperStarTopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        onBack = startClick,
        actions = {
            endIcon()
            TopButton(
                imageVector = ImageVector.vectorResource(R.drawable.ic_menu_refresh),
                contentDescription = "restart",
                onClick = endClick
            )
        }
    )
}

@Composable
fun TopButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color = colorScheme.onBackground,
    onClick: () -> Unit,
) {
    val view = LocalView.current
    Box(
        modifier
            .clip(RoundedCornerShape(50))
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
