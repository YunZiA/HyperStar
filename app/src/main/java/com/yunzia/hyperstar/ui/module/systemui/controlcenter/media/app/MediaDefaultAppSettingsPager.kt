
package com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.LoadBox
import com.yunzia.hyperstar.ui.base.LoadStatus
import com.yunzia.hyperstar.ui.base.SearchStatus
import com.yunzia.hyperstar.ui.base.SearchStatus.Status
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.pager.SearchModuleNavPager
import com.yunzia.hyperstar.ui.base.rememberLoadStatus
import com.yunzia.hyperstar.ui.base.rememberSearchStatus
import com.yunzia.hyperstar.ui.pagers.titleColor
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

fun getInstalledApps(
    activity: MainActivity
){
    try {
        val permissionInfo = activity.applicationContext.packageManager.getPermissionInfo(
            "com.android.permission.GET_INSTALLED_APPS",
            0
        )
        if (permissionInfo != null && permissionInfo.packageName == "com.lbe.security.miui") {
            //MIUI 系统支持动态申请该权限
            if (ContextCompat.checkSelfPermission(
                    activity.applicationContext,
                    "com.android.permission.GET_INSTALLED_APPS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                //没有权限，需要申请
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf("com.android.permission.GET_INSTALLED_APPS"),
                    999
                )
            }
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }
}

@SuppressLint("QueryPermissionsNeeded")
private fun getAllAppInfo(
    context:Context,
    isFilterSystem: Boolean,
): ArrayList<AppInfo> {

    val appBeanList: ArrayList<AppInfo> = ArrayList<AppInfo>()
    val packageManager = context.packageManager
    val list = packageManager.getInstalledPackages(0)


    for (p in list) {
        val applicationInfo = p.applicationInfo

        // 检查是否是系统应用以及是否应该过滤系统应用
        val isSystemApp = isFilterSystem && ((applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0)

        // 检查是否是特定的应用包名
        if ("com.miui.player" == applicationInfo?.packageName) {
            processAppInfo(applicationInfo, packageManager, appBeanList)
        } else if (!isSystemApp) {  // 非系统应用
            processAppInfo(applicationInfo, packageManager, appBeanList)
        }
    }

    return appBeanList
}

private fun processAppInfo(
    applicationInfo: ApplicationInfo?,
    packageManager: PackageManager?,
    appBeanList: ArrayList<AppInfo>
) {
    if (applicationInfo == null || packageManager == null) return
    run {

        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val packageName = applicationInfo.packageName.toString()
        val appIcon = packageManager.getApplicationIcon(applicationInfo)

        val bean = AppInfo(
            icon = appIcon,
            label = appName,
            packageName = packageName,
        )
        appBeanList.add(bean)
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun MediaAppSettingsPager(
    navController: NavController,
    parentRoute: MutableState<String>
) {
    val context = LocalContext.current
    val activity = LocalActivity.current as MainActivity
    val loadStatus = rememberLoadStatus()
    getInstalledApps(activity)
    val appLists = remember { mutableStateOf<List<AppInfo>>(emptyList()) }
    val searchApp = remember {  mutableStateOf<List<AppInfo>>(emptyList()) }

    val searchStatus = rememberSearchStatus(
        label = stringResource(R.string.app_name_type),
    )
    val isApp = remember { mutableStateOf(SPUtils.getString("media_default_app_package","")) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(activity.isGranted.value) {
        if (activity.isGranted.value){
            loadStatus.current = LoadStatus.Status.Loading
        }
    }

    LaunchedEffect(loadStatus.current) {
        if (!loadStatus.isLoading()) return@LaunchedEffect
        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                getAllAppInfo(context, true)
            }
            appLists.value = result
            loadStatus.current = LoadStatus.Status.Complete
        }
    }
    LaunchedEffect(searchStatus.status) {
        if (searchStatus.status == Status.COLLAPSED){
            searchStatus.searchText = ""
        }
    }

    LaunchedEffect(searchStatus.searchText) {
        if (searchStatus.searchText == ""){
            searchStatus.resultStatus = SearchStatus.ResultStatus.DEFAULT
            searchApp.value = emptyList()
            return@LaunchedEffect

        }
        delay(300)
        coroutineScope.launch(
            Dispatchers.Default
        ) {
            searchStatus.resultStatus = SearchStatus.ResultStatus.LOAD
            searchApp.value = appLists.value.asFlow()
                .filter { app -> app.label.contains(searchStatus.searchText, ignoreCase = true) }
                .toList()
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
        activityTitle = stringResource(R.string.media_default_app_settings),
        searchStatus = searchStatus,
        navController = navController,
        parentRoute = parentRoute,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        result = {

            searchApp.value.forEachIndexed { index, apps ->
                item(index) {
                    AppItem(apps, isApp)

                }
            }

        },
    ){ topAppBarScrollBehavior,padding->

        LoadBox(
            loadStatus = loadStatus,
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

                appLists.value.forEachIndexed { index, apps ->

                    item(index) {
                        AppItem(apps, isApp)
                    }
                }

            }

        }
    }




}

@Composable
private fun AppItem(
    app: AppInfo,
    isApp : MutableState<String>
){
    val label = app.label
    val packageName = app.packageName
    var isSelect = packageName == isApp.value
    BasicComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(if (isSelect) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
        ,
        insideMargin =  PaddingValues(16.dp),
        title = label,
        titleColor = titleColor(isSelect),
        leftAction = {
            Box(
                modifier = Modifier.padding(end = 12.dp)
            ){
                app.icon?.let { icon ->
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = DrawablePainter(icon),
                        contentDescription = label
                    )
                }
                if (app.icon ==  null){
                    Log.d("ggc","app.icon is null ")

                }

            }
        },
        rightActions = {
            Checkbox(
                checked = isSelect,
                onCheckedChange = {
                    isSelect = !isSelect
                    isApp.value = if (isSelect) packageName else ""
                    SPUtils.setString("media_default_app_package", isApp.value)
                }
            )

        },
        onClick = {
            isSelect = !isSelect
            isApp.value = if (isSelect) packageName else ""
            SPUtils.setString("media_default_app_package", isApp.value)

        }
    )

}

