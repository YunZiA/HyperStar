package com.yunzia.hyperstar.ui.module.systemui.other.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.FloatingPagerButton
import com.yunzia.hyperstar.ui.base.LoadBox
import com.yunzia.hyperstar.ui.base.LoadStatus
import com.yunzia.hyperstar.ui.base.SearchStatus
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.pager.SearchModuleNavPager
import com.yunzia.hyperstar.ui.base.rememberLoadStatus
import com.yunzia.hyperstar.ui.base.rememberSearchStatus
import com.yunzia.hyperstar.ui.pagers.titleColor
import com.yunzia.hyperstar.utils.GetInstalledApps
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissPopup
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NotificationOfIm(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {

    val activity = navController.context as MainActivity
    val loadStatus = rememberLoadStatus()

    GetInstalledApps(activity)
    val searchStatus = rememberSearchStatus(
        label = stringResource(R.string.app_name_type)
    )

    val allAppList = remember { mutableStateSetOf<NotificationInfo>() }
    val selectApp = remember { mutableStateSetOf<NotificationInfo>() }
    val unSelectApp = remember { mutableStateSetOf<NotificationInfo>() }

    val searchApp = remember(selectApp, searchStatus.searchText) {
        derivedStateOf {
            selectApp.filter { it.appName.contains(searchStatus.searchText, ignoreCase = true) }
        }
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(activity.isGranted.value) {
        if (activity.isGranted.value){
            loadStatus.current = LoadStatus.Status.Loading
        }
    }

    LaunchedEffect(loadStatus.current,selectApp.size) {
        if (loadStatus.isLoading()){
            return@LaunchedEffect
        }
        loadStatus.isEmpty = selectApp.isEmpty()
        if (selectApp.isEmpty()){
            SPUtils.setString("notification_icon_type_whitelist", "||")
            unSelectApp.clear()
            unSelectApp.addAll(allAppList)
            return@LaunchedEffect
        }
        val packageNames = selectApp.map { it.packageName }.toMutableSet()

        val result = buildString { packageNames.forEach { append("|$it") } }+"|"
        SPUtils.setString("notification_icon_type_whitelist", result)
        unSelectApp.clear()
        unSelectApp.addAll(
            allAppList.filter {
                !result.contains("|${it.packageName}|")
            }
        )
    }
    LaunchedEffect(loadStatus.current) {
        if (loadStatus.isLoading()){
            coroutineScope.launch(Dispatchers.IO) {
                getAppInfo(
                    activity,
                    SPUtils.getString("notification_icon_type_whitelist", "||"),
                    selectApp,
                    allAppList
                )
                withContext(Dispatchers.Main) {
                    loadStatus.current = LoadStatus.Status.Complete
                    loadStatus.isEmpty = selectApp.isEmpty()
                }
            }
        }

    }

    LaunchedEffect(searchStatus.searchText) {
        if (searchStatus.searchText == ""){
            searchStatus.resultStatus = SearchStatus.ResultStatus.DEFAULT
            return@LaunchedEffect

        }
        delay(300)
        coroutineScope.launch(
            Dispatchers.Default
        ) {
            searchStatus.resultStatus = SearchStatus.ResultStatus.LOAD

            searchStatus.resultStatus = if (
                searchApp.value.isEmpty()
            ){
                SearchStatus.ResultStatus.EMPTY
            }else{
                SearchStatus.ResultStatus.SHOW
            }
        }

    }

    SearchModuleNavPager(
        activityTitle = stringResource(R.string.icon_stacking_whitelist),
        searchStatus = searchStatus,
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
                content = {
                    NotificationOfImAddPage(
                        it,
                        selectApp,
                        unSelectApp
                    )
                }

            )
        },
        result = {

            searchApp.value.forEach {
                item(it.packageName) {
                    AppNotifItem(it,navController,selectApp)

                }

            }
        }
    ) { topAppBarScrollBehavior,padding->

        LoadBox(
            loadStatus = loadStatus,
            modifier = Modifier.fillMaxSize(),
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding() + 28.dp
                )
            ) {
                selectApp.forEach {
                    item(it.packageName) {
                        AppNotifItem(it,navController,selectApp)
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
    selectApp: SnapshotStateSet<NotificationInfo>
){
    val label = notificationInfo.appName
    val packageName = notificationInfo.packageName
    val shouldDelete = remember { mutableStateOf(false) }

    val localIndication = LocalIndication.current
    val interactionSource =
        if (localIndication is IndicationNodeFactory) {
            // We can fast path here as it will be created inside clickable lazily
            null
        } else {
            // We need an interaction source to pass between the indication modifier and
            // clickable, so
            // by creating here we avoid another composed down the line
            remember { MutableInteractionSource() }
        }

    Box{
        DeletePup(shouldDelete,selectApp,notificationInfo)

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
                .background( if (shouldDelete.value) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)

            ,
            insideMargin =  PaddingValues(17.dp),
            title = label,
            titleColor = titleColor(false),
            leftAction = {
                Box(
                    modifier = Modifier.padding(end = 12.dp)
                ){
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = DrawablePainter(notificationInfo.icon),
                        contentDescription = label
                    )

                }
            },
            rightActions = {

                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .padding(start = 15.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.arrow_right),
                    //MiuixIcons.ArrowRight,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(colorScheme.onBackground),
                )


            },
            onClick = {
                shouldDelete.value = true
                //navController.nav(SystemUIMoreList.NOTIFICATION_APP_DETAIL)
            }
        )

    }

}

@Composable
fun DeletePup(
    shouldDelete: MutableState<Boolean>,
    selectApp: SnapshotStateSet<NotificationInfo>,
    notificationInfo: NotificationInfo
) {
    ListPopup(
        show = shouldDelete,
        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
        alignment = PopupPositionProvider.Align.TopRight,
        onDismissRequest = {
            shouldDelete.value = false
        }
    ) {
        ListPopupColumn {
            DropdownImpl(
                text = "删除",
                optionSize = 1,
                isSelected = false,
                index = 0,
                onSelectedIndexChange = {
                    selectApp.remove(notificationInfo)
                    dismissPopup(shouldDelete)
                }
            )

        }
    }

}


@SuppressLint("QueryPermissionsNeeded")
private fun getAllAppInfo(
    context: Context,
): ArrayList<NotificationInfo> {

    val appBeanList: ArrayList<NotificationInfo> = ArrayList<NotificationInfo>()
    val packageManager = context.packageManager
    val list = packageManager.getInstalledPackages(0)


    for (p in list) {
        val applicationInfo = p.applicationInfo

        processAppInfo(applicationInfo, packageManager, appBeanList)

    }

    return appBeanList
}


private fun getAppInfo(
    context: Context,
    selected: String,
    selectAppList: SnapshotStateSet<NotificationInfo>,
    allAppList: SnapshotStateSet<NotificationInfo>
) {
    Log.d("ggc", "NotificationOfIm:  getAppInfo $selected")

    val selectedList: ArrayList<NotificationInfo> = ArrayList()
    val allApp: ArrayList<NotificationInfo> = ArrayList()
    val packageManager = context.packageManager
    val list = packageManager.getInstalledPackages(0)

    for (p in list) {
        val applicationInfo = p.applicationInfo
        processAppInfo(applicationInfo, packageManager, allApp)
        if (selected.contains("|${applicationInfo?.packageName!!}|")){
            processAppInfo(applicationInfo, packageManager, selectedList)
        }
    }
    selectAppList.clear()
    allAppList.clear()
    allAppList.addAll(allApp)
    if (selectedList.isNotEmpty()){

        selectAppList.addAll(selectedList)
    }

}


private fun processAppInfo(
    applicationInfo: ApplicationInfo?,
    packageManager: PackageManager?,
    appBeanList: ArrayList<NotificationInfo>
) {
    if (applicationInfo == null || packageManager == null) return
    run {

        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val packageName = applicationInfo.packageName.toString()
        val appIcon = packageManager.getApplicationIcon(applicationInfo)

        val notificationId =  PreferencesUtil.getString("packageName_notif_id","null")

        val bean = NotificationInfo(
            icon = appIcon,
            appName = appName,
            packageName = packageName,
            notificationId = notificationId
        )
        appBeanList.add(bean)
    }
}