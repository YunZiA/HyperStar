package com.yunzia.hyperstar.ui.module.systemui.other.notification

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
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
import com.yunzia.hyperstar.ui.base.SearchStatus
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.pager.SearchModuleNavPager
import com.yunzia.hyperstar.ui.base.rememberSearchStatus
import com.yunzia.hyperstar.ui.pagers.titleColor
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtils.Companion.dismissPopup
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun NotificationOfIm(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {

    val activity = navController.context as MainActivity
    val db = NotAppListDB(activity)
    val isLoading = remember { mutableStateOf(true) }

    val packageManager = activity.packageManager

    val applicationInfo =  packageManager.getPackageInfo("com.tencent.mm", PackageManager.GET_META_DATA).applicationInfo!!

    val appName = packageManager.getApplicationLabel(applicationInfo).toString()
    val packageName = applicationInfo.packageName
    val appIcon = packageManager.getApplicationIcon(applicationInfo)



    val searchStatus = rememberSearchStatus(
        label = stringResource(R.string.app_name_type)
    )

    val weChat = NotificationInfo(appIcon,appName,"com.tencent.mm","message_channel_new_id")

    val notifPlusList = remember { mutableStateListOf(weChat) }

    val selectApp = remember {  mutableStateListOf<NotificationInfo>() }
    val unSelectApp = remember { mutableStateListOf<NotificationInfo>() }
    val searchApp = remember(selectApp, searchStatus.searchText) {
        derivedStateOf {
            selectApp.filter { it.appName.contains(searchStatus.searchText, ignoreCase = true) }
        }
    }
    //val searchApp = remember {  mutableStateListOf<NotificationInfo>() }

    val appNotifPkgiList = remember { mutableStateOf<List<String>>(emptyList()) }

    val coroutineScope = rememberCoroutineScope()

    val update = remember { mutableStateOf(true) }
    //SPUtils.removeKey("notification_icon_type_whitelist")
    LaunchedEffect(activity.isGranted.value) {

        if (activity.isGranted.value){
            update.value = true
            isLoading.value = true
        }
    }

    LaunchedEffect(selectApp.size) {
        if (isLoading.value){
            return@LaunchedEffect
        }
        val packageNames = selectApp.map { it.packageName }.toMutableSet()
        if (!packageNames.contains("com.tencent.mm")) {
            packageNames.add("com.tencent.mm")
        }
        val result = buildString {
            packageNames.forEach { append("|$it") }
        }+"|"
        Log.d("ggc", "NotificationOfIm: ${isLoading.value} $result")
        SPUtils.setString("notification_icon_type_whitelist", result)
    }
    LaunchedEffect(update.value) {
        if (update.value){
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    getAppInfo(
                        activity,
                        SPUtils.getString("notification_icon_type_whitelist","|com.tencent.mm|"),
                        selectApp,
                        unSelectApp
                    )

                }
                if (selectApp.isNotEmpty()) {
                    update.value = false
                    isLoading.value = false
                }
            }
        }

    }

    LaunchedEffect(searchStatus.searchText) {
        if (searchStatus.searchText == ""){
            searchStatus.resultStatus = SearchStatus.ResultStatus.DEFAULT
            update.value = false
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
            update.value = false
        }

    }

    SearchModuleNavPager(
        activityTitle = "图标优化白名单",
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
            modifier = Modifier.fillMaxSize(),
            isLoading = isLoading

        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding() + 28.dp
                ),
                topAppBarScrollBehavior = topAppBarScrollBehavior
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
    selectApp: SnapshotStateList<NotificationInfo>
){
    val label = notificationInfo.appName
    val packageName = notificationInfo.packageName
    val shouldDelete = remember { mutableStateOf(false) }

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
                .background(colorScheme.surfaceVariant)
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
    selectApp: SnapshotStateList<NotificationInfo>,
    notificationInfo: NotificationInfo
) {
    ListPopup(
        show = shouldDelete,
        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
        alignment = PopupPositionProvider.Align.BottomRight,
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
    selected:String,
    selectAppList: SnapshotStateList<NotificationInfo>,
    unSelectAppList: SnapshotStateList<NotificationInfo>
) {
    Log.d("ggc", "NotificationOfIm:  getAppInfo $selected")

    val selectedList: ArrayList<NotificationInfo> = ArrayList<NotificationInfo>()
    val unSelectList: ArrayList<NotificationInfo> = ArrayList<NotificationInfo>()
    val packageManager = context.packageManager
    val list = packageManager.getInstalledPackages(0)



    for (p in list) {
        val applicationInfo = p.applicationInfo
        if (selected.contains("|${applicationInfo?.packageName!!}|")){
            processAppInfo(applicationInfo, packageManager, selectedList)
        }else{
            processAppInfo(applicationInfo, packageManager, unSelectList)
        }
    }
    selectAppList.clear()
    unSelectAppList.clear()
    selectAppList.addAll(selectedList)
    unSelectAppList.addAll(unSelectList)

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