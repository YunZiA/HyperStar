package com.yunzia.hyperstar.ui.screen.module.systemui.other.notification

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.remember
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.topbar.TopButton
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.preference.SearchPreferenceScreen
import com.yunzia.hyperstar.ui.screen.pagers.summaryColor
import com.yunzia.hyperstar.ui.screen.pagers.titleColor
import com.yunzia.hyperstar.viewmodel.NotificationAddViewModel
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.preference.ArrowPreference
import SearchRoute
import androidx.compose.runtime.getValue
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes

@Composable
fun NotificationOfImAddPage(
    expand: MutableState<Boolean>,
    selectApp: SnapshotStateSet<NotificationInfo>,
    unSelectApp: SnapshotStateSet<NotificationInfo>
) {
    val context = LocalContext.current
    val viewModel = viewModel<NotificationAddViewModel>(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationAddViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val backdrop = rememberLayerBackdrop()
    val searchStatus by viewModel.searchStatus
    val searchResults by viewModel.searchResults

    LaunchedEffect(unSelectApp) {
        viewModel.updateUnselectedApps(unSelectApp)
    }

    LaunchedEffect(expand.value) {
        if (!expand.value) {
            viewModel.clearSelection()
        }
    }

    SearchPreferenceScreen(
        searchStatus = searchStatus,
        backdrop = backdrop,
        onQueryChange = { viewModel.updateSearchText(it, unSelectApp) },
        topBar = { topAppBarScrollBehavior ->
            TopAppBar(
                modifier = Modifier.showBlur(backdrop),
                color = Color.Transparent,
                title = stringResource(R.string.application_selection),
                scrollBehavior = topAppBarScrollBehavior,
                navigationIcon = {
                    TopButton(
                        modifier = Modifier.padding(start = 18.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                        contentDescription = "close",
                        onClick = {
                            expand.value = false
                        }
                    )
                },
                actions = {
                    TopButton(
                        modifier = Modifier.padding(end = 18.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_done),
                        contentDescription = "done",
                        onClick = {
                            expand.value = false
                            viewModel.confirmSelection { selectedApps ->
                                selectApp.addAll(selectedApps)
                            }
                        }
                    )
                }
            )
        },
        searchResult = {
            searchResults.forEach { app ->
                item(app.packageName) {
                    AppNotifItem(
                        notificationInfo = app,
                        isSelected = viewModel.isSelected(app),
                        onSelectionChanged = { isSelected ->
                            viewModel.toggleAppSelection(app)
                        }
                    )
                }
            }
        },
    ) {
        unSelectApp.forEach { app ->
            item(app.packageName) {
                AppNotifItem(
                    notificationInfo = app,
                    isSelected = viewModel.isSelected(app),
                    onSelectionChanged = { isSelected ->
                        viewModel.toggleAppSelection(app)
                    }
                )
            }
        }
    }
}

@Composable
private fun AppNotifItem(
    notificationInfo: NotificationInfo,
    isSelected: Boolean,
    onSelectionChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    ArrowPreference(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(
                if (isSelected) colorScheme.tertiaryContainer
                else colorScheme.surfaceVariant
            ),
        insideMargin = PaddingValues(17.dp),
        title = notificationInfo.appName,
        titleColor = titleColor(isSelected),
        summary = notificationInfo.packageName,
        summaryColor = summaryColor(isSelected),
        startAction = {
            AppIcon(
                icon = notificationInfo.icon,
                appName = notificationInfo.appName
            )
        },
        onClick = { onSelectionChanged(!isSelected) }
    )
}

@Composable
private fun AppIcon(
    icon: Drawable,
    appName: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.padding(end = 12.dp)) {
        Image(
            modifier = modifier.size(40.dp),
            painter = remember(icon) { DrawablePainter(icon) },
            contentDescription = appName
        )
    }
}
