package com.yunzia.hyperstar.ui.module.systemui.other.notification

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.yunzia.hyperstar.ui.base.search.SearchBox
import com.yunzia.hyperstar.ui.base.search.SearchPager
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.viewmodel.NotificationAddViewModel
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun NotificationOfImAddPage(
    expand: MutableState<Boolean>,
    selectApp: SnapshotStateSet<NotificationInfo>,
    unSelectApp: SnapshotStateSet<NotificationInfo>
){
    val context = LocalContext.current
    val viewModel:NotificationAddViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return NotificationAddViewModel(context.applicationContext as Application) as T
            }
        }
    )
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val searchStatus by viewModel.searchStatus
    val searchResults by viewModel.searchResults

    LaunchedEffect(unSelectApp) {
        viewModel.updateUnselectedApps(unSelectApp)
    }

    LaunchedEffect(searchStatus.searchText) {
        viewModel.updateSearchText(searchStatus.searchText, unSelectApp)
    }


    XScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        popupHost = { },
        topBar = {
            searchStatus.TopAppBarAnim{
                TopAppBar(
                    modifier = Modifier.showBlur(hazeState),
                    color = Color.Transparent,
                    title = stringResource(R.string.application_selection),
                    scrollBehavior = topAppBarScrollBehavior,
                    navigationIcon = {
                        TopButton(
                            modifier = Modifier.padding(start = 18.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                            contentDescription = "close",
                            onClick = { expand.value = false }

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
                                    expand.value = false
                                }
//                                if (selectedAppList.isNotEmpty()){
//                                    selectApp.addAll(selectedAppList)
//                                    selectedAppList.clear()
//                                }
                            }
                        )
                    }
                )

            }
        }
    ){ padding->

        searchStatus.SearchBox(
            modifier = Modifier
                .blur(hazeState)
                .padding(top = padding.calculateTopPadding() + 14.dp)
                .fillMaxSize(),
        ){
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding() + 28.dp
                )
            ) {

                unSelectApp.forEach {app->
                    item(app.packageName) {
                        AppNotifItem(notificationInfo = app,
                            isSelected = viewModel.isSelected(app),
                            onSelectionChanged = { isSelected ->
                                viewModel.toggleAppSelection(app)
                            }
                        )
                    }
                }

            }

        }

    }

    searchStatus.SearchPager(
        {}
    ) {
        searchResults.forEach { app->
            item(app.packageName) {
                AppNotifItem(notificationInfo = app,
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
    BasicComponent(
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
        leftAction = {
            AppIcon(
                icon = notificationInfo.icon,
                appName = notificationInfo.appName
            )
        },
        rightActions = {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { onSelectionChanged(!isSelected) }
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
            painter = DrawablePainter(icon),
            contentDescription = appName
        )
    }
}