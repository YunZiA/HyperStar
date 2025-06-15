package com.yunzia.hyperstar.ui.screen.module.systemui.other.notification

import android.app.Application
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.FloatingPagerButton
import com.yunzia.hyperstar.ui.component.LoadBox
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.pager.SearchModuleNavPager
import com.yunzia.hyperstar.ui.screen.pagers.summaryColor
import com.yunzia.hyperstar.ui.screen.pagers.titleColor
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.viewmodel.NotificationViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NotificationOfImScreen(
    navController: NavHostController,
    currentStartDestination: MutableState<String>,
) {
    val activity = navController.context as MainActivity
    val context = navController.context
    val viewModel: NotificationViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val searchStatus = viewModel.searchStatus
    val loadStatus = viewModel.loadStatus
    val searchApps = viewModel.searchApp
    val selectedApps = viewModel.selectedApps
    val unselectedApps = viewModel.unselectedApps

    LaunchedEffect(loadStatus.value.isLoading()) {
        if (loadStatus.value.isLoading()) {
            // 权限请求
            activity.requestInstalledAppsPermission()
            viewModel.loadApps(context)
        }
    }
    // 权限状态监听
    LaunchedEffect(activity.isGranted.value) {
        viewModel.onPermissionGranted(activity.isGranted.value)
    }
    // 搜索文本更新
    LaunchedEffect(searchStatus.value.searchText) {
        viewModel.onSearchTextChanged(searchStatus.value.searchText)
    }

    SearchModuleNavPager(
        activityTitle = stringResource(R.string.icon_stacking_whitelist),
        searchStatus = searchStatus.value,
        navController = navController,
        parentRoute = currentStartDestination,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        floatingPagerButton = {
            FloatingPagerButton(
                modifier = Modifier,
                containerColor = colorScheme.surface,
                insideMargin = PaddingValues(end = 40.dp, bottom = 80.dp),
                buttonContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                        tint = colorScheme.onTertiaryContainer,
                        contentDescription = "add"
                    )
                },
                content = { onDismiss ->
                    NotificationOfImAddPage(
                        expand = onDismiss,
                        selectApp = selectedApps,
                        unSelectApp = unselectedApps
                    )
                }
            )
        },
        result = {
            searchApps.value.forEach { app ->

                item(app.appName) {
                    AppNotifItem(
                        notificationInfo = app,
                        navController = navController,
                        onDelete = { viewModel.removeSelectedApp(app) }
                    )
                }
            }
        }
    ) { topAppBarScrollBehavior, padding ->
        LoadBox(
            loadStatus = loadStatus.value,
            modifier = Modifier.fillMaxSize(),
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    bottom = padding.calculateBottomPadding() + 28.dp
                )
            ) {
                selectedApps.forEach { app ->
                    item {

                        AppNotifItem(
                            notificationInfo = app,
                            navController = navController,
                            onDelete = { viewModel.removeSelectedApp(app) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppNotifItem(
    notificationInfo: NotificationInfo,
    navController: NavHostController,
    onDelete: () -> Unit
) {
    val shouldDelete = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        DeletePopup(
            shouldDelete = shouldDelete,
            onDelete = onDelete,
            onDismiss = { shouldDelete.value = false }
        )

        BasicComponent(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(top = 10.dp)
                .bounceAnimN()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            shouldDelete.value = true
                        }
                    )
                }
                .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
                .background(
                    if (shouldDelete.value)
                        colorScheme.tertiaryContainer
                    else
                        colorScheme.surfaceVariant
                ),
            insideMargin = PaddingValues(17.dp),
            title = notificationInfo.appName,
            titleColor = titleColor(false),
            summary = notificationInfo.packageName,
            summaryColor = summaryColor(false),
            leftAction = {
                Box(modifier = Modifier.padding(end = 12.dp)) {
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = DrawablePainter(notificationInfo.icon),
                        contentDescription = notificationInfo.appName
                    )
                }
            },
            rightActions = {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 15.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_right),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorScheme.onBackground),
                )
            },
            onClick = {
                shouldDelete.value = true
            }
        )
    }
}

@Composable
private fun DeletePopup(
    shouldDelete: MutableState<Boolean>,
    onDelete: () -> Unit,
    onDismiss: () -> Unit
) {
    ListPopup(
        show = shouldDelete,
        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
        alignment = PopupPositionProvider.Align.TopRight,
        onDismissRequest = onDismiss
    ) {
        ListPopupColumn {
            DropdownImpl(
                text = "删除",
                optionSize = 1,
                isSelected = false,
                index = 0,
                onSelectedIndexChange = {
                    onDelete()
                    onDismiss()
                }
            )
        }
    }
}