package com.yunzia.hyperstar.ui.component.preference.widget

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.topbar.ModuleNavTopAppBar
import com.yunzia.hyperstar.ui.navigation.Navigator
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState

@Composable
fun PreferencePage(
    title: String,
    modifier: Modifier = Modifier,
    navController: Navigator? = null,
    startClick: (() -> Unit)? = null,
    endClick: (() -> Unit)? = null,
    endIcon: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable BoxScope.() -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val defaultStartClick: () -> Unit = startClick ?: { navController?.goBack() }
    val hasTopBar = startClick != null || endClick != null || navController != null

    XScaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingPagerButton = floatingPagerButton,
        popupHost = { },
        topBar = if (hasTopBar) {
            {
                ModuleNavTopAppBar(
                    modifier = Modifier.showBlur(backdrop),
                    color = Color.Transparent,
                    title = title,
                    scrollBehavior = scrollBehavior,
                    startClick = defaultStartClick,
                    endIcon = endIcon,
                    endClick = endClick ?: {}
                )
            }
        } else {
            {}
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(backdrop)
                .padding(top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding())
        ) {
            content()
        }
    }
}

@Composable
fun PreferenceListPage(
    title: String,
    modifier: Modifier = Modifier,
    navController: Navigator? = null,
    startClick: (() -> Unit)? = null,
    endClick: (() -> Unit)? = null,
    endIcon: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyListScope.() -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val defaultStartClick: () -> Unit = startClick ?: { navController?.goBack() }
    val hasTopBar = startClick != null || endClick != null || navController != null

    XScaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingPagerButton = floatingPagerButton,
        popupHost = { },
        topBar = if (hasTopBar) {
            {
                ModuleNavTopAppBar(
                    modifier = Modifier.showBlur(backdrop),
                    color = Color.Transparent,
                    title = title,
                    scrollBehavior = scrollBehavior,
                    startClick = defaultStartClick,
                    endIcon = endIcon,
                    endClick = endClick ?: {}
                )
            }
        } else {
            {}
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .blur(backdrop)
                .nestedOverScrollVertical(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding(),
                bottom = 6.dp + contentPadding.calculateBottomPadding()
            )
        ) {
            content()
        }
    }

}
