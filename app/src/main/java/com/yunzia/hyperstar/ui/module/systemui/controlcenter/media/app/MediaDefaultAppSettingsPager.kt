
package com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.activity.compose.LocalActivity
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
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.LoadBox
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.pager.SearchModuleNavPager
import com.yunzia.hyperstar.ui.pagers.titleColor
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.viewmodel.MediaAppSettingsViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@SuppressLint("MutableCollectionMutableState")
@Composable
fun MediaAppSettingsPager(
    navController: NavController,
    parentRoute: MutableState<String>
) {
    val activity = LocalActivity.current as MainActivity
    val context = LocalContext.current
    val viewModel: MediaAppSettingsViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MediaAppSettingsViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val loadStatus = viewModel.loadStatus
    val appLists = viewModel.appLists
    val searchApp = viewModel.searchApp
    val searchStatus = viewModel.searchStatus
    val currentApp = viewModel.currentApp


    LaunchedEffect(Unit) {
        activity.requestInstalledAppsPermission()
    }
    // LaunchedEffect for permission
    LaunchedEffect(activity.isGranted.value) {
        viewModel.onPermissionGranted(activity.isGranted.value)
    }

    // LaunchedEffect for loading apps
    LaunchedEffect(loadStatus.value.current) {
        viewModel.loadApps(context)
    }

    // LaunchedEffect for search status
    LaunchedEffect(searchStatus.value.current) {
        viewModel.onSearchStatusChanged(searchStatus.value.current)
    }

    // LaunchedEffect for search text
    LaunchedEffect(searchStatus.value.searchText) {
        viewModel.onSearchTextChanged(searchStatus.value.searchText)
    }
    SearchModuleNavPager(
        activityTitle = stringResource(R.string.media_default_app_settings),
        searchStatus = searchStatus.value,
        navController = navController,
        parentRoute = parentRoute,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        result = {

            searchApp.value.forEachIndexed { index, app ->
                item(app.packageName) {
                    AppItem(app, currentApp){
                        viewModel.updateSelectedApp(app.packageName)
                    }

                }
            }

        },
    ){ topAppBarScrollBehavior,padding->

        LoadBox(
            loadStatus = loadStatus.value,
            modifier = Modifier.fillMaxSize()
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

                appLists.value.forEachIndexed { index, app ->

                    item(app.packageName) {
                        AppItem(app, currentApp){
                            viewModel.updateSelectedApp(app.packageName)
                        }
                    }
                }

            }

        }
    }




}

@Composable
fun AppItem(
    app: AppInfo,
    currentApp: State<String>,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val isSelected by remember { derivedStateOf { currentApp.value == app.packageName } }

    BasicComponent(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(if (isSelected) colorScheme.tertiaryContainer else colorScheme.surfaceVariant),
        insideMargin = PaddingValues(16.dp),
        title = app.label,
        titleColor = titleColor(isSelected),
        leftAction = {
            AppIcon(
                icon = app.icon,
                label = app.label,
                modifier = Modifier.padding(end = 12.dp)
            )
        },
        rightActions = {
            Checkbox(
                checked = isSelected,
                onCheckedChange = {
                    onClick()
                }
            )
        },
        onClick = {
            onClick()
        }
    )
}

@Composable
private fun AppIcon(
    icon: android.graphics.drawable.Drawable?,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        icon?.let { drawable ->
            Image(
                modifier = Modifier.size(40.dp),
                painter = DrawablePainter(drawable),
                contentDescription = label
            )
        } ?: run {
            Log.d("AppIcon", "Icon is null for $label")
        }
    }
}