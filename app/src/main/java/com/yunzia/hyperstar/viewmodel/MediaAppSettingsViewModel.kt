package com.yunzia.hyperstar.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.LoadStatus
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import com.yunzia.hyperstar.ui.component.search.SearchStatus.Status
import com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app.AppInfo
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MediaAppSettingsViewModel(
    application: Application  // 注入 Application
) : AndroidViewModel(application) {

    private val _appLists = mutableStateOf<List<AppInfo>>(emptyList())
    val appLists: State<List<AppInfo>> = _appLists

    private val _searchApp = mutableStateOf<List<AppInfo>>(emptyList())
    val searchApp: State<List<AppInfo>> = _searchApp

    private val _loadStatus = mutableStateOf(LoadStatus())
    val loadStatus: State<LoadStatus> = _loadStatus

    // 使用 Application context 获取资源字符串
    private val _searchStatus = mutableStateOf(
        SearchStatus(
            getApplication<Application>().getString(R.string.app_name_type)
        )
    )
    val searchStatus: State<SearchStatus> = _searchStatus

    private val _currentApp = mutableStateOf(SPUtils.getString("media_default_app_package", ""))
    val currentApp: State<String> = _currentApp


    fun updateSelectedApp(packageName: String) {
        _currentApp.value = if (_currentApp.value == packageName) "" else packageName
        SPUtils.setString("media_default_app_package", _currentApp.value)
    }

    init {
        _currentApp.value = SPUtils.getString("media_default_app_package", "")
    }

    fun onPermissionGranted(isGranted: Boolean) {
        if (isGranted) {
            _loadStatus.value.current = LoadStatus.Status.Loading
        }
    }

    fun loadApps(context: Context) {
        if (!_loadStatus.value.isLoading()) return

        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                getAllAppInfo(context, true)
            }
            _appLists.value = result
            _loadStatus.value.current = LoadStatus.Status.Complete
        }
    }

    fun onSearchStatusChanged(status: Status) {
        if (status == Status.COLLAPSED) {
            _searchStatus.value.searchText = ""
        }
    }

    fun onSearchTextChanged(searchText: String) {
        _searchStatus.value.searchText = searchText

        if (searchText.isEmpty()) {
            _searchStatus.value.resultStatus = SearchStatus.ResultStatus.DEFAULT
            _searchApp.value = emptyList()
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            delay(300)
            _searchStatus.value.resultStatus = SearchStatus.ResultStatus.LOAD

            _searchApp.value = _appLists.value.asFlow()
                .filter { app ->
                    app.label.contains(searchText, ignoreCase = true) || app.packageName.contains(searchText, ignoreCase = true)

                }
                .toList()

            _searchStatus.value.resultStatus = if (_searchApp.value.isEmpty()) {
                SearchStatus.ResultStatus.EMPTY
            } else {  // 修复了这里的语法错误
                SearchStatus.ResultStatus.SHOW
            }
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
}