package com.yunzia.hyperstar.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.LoadStatus
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import com.yunzia.hyperstar.ui.module.systemui.other.notification.NotificationInfo
import com.yunzia.hyperstar.utils.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _selectedApps = mutableStateSetOf<NotificationInfo>()
    val selectedApps: SnapshotStateSet<NotificationInfo> = _selectedApps

    private val _unselectedApps = mutableStateSetOf<NotificationInfo>()
    val unselectedApps: SnapshotStateSet<NotificationInfo> = _unselectedApps

    private val _searchApp = mutableStateOf<List<NotificationInfo>>(emptyList())
    val searchApp: State<List<NotificationInfo>> = _searchApp

    private val _loadStatus = mutableStateOf(LoadStatus())
    val loadStatus: State<LoadStatus> = _loadStatus

    private val _searchStatus = mutableStateOf(
        SearchStatus(
            getApplication<Application>().getString(R.string.app_name_type)
        )
    )
    val searchStatus: State<SearchStatus> = _searchStatus

    init {
        // 监听选中应用列表的变化
        viewModelScope.launch {
            snapshotFlow { _selectedApps.size }
                .collect { size ->
                    if (_loadStatus.value.isLoading()) {
                        return@collect
                    }

                    _loadStatus.value.isEmpty = _selectedApps.isEmpty()

                    if (_selectedApps.isEmpty()) {
                        SPUtils.setString("notification_icon_type_whitelist", "||")
                        _unselectedApps.clear()
                        _unselectedApps.addAll(_allAppList)
                        return@collect
                    }

                    val packageNames = _selectedApps.map { it.packageName }
                    val result = buildString {
                        packageNames.forEach { append("|$it") }
                    } + "|"

                    SPUtils.setString("notification_icon_type_whitelist", result)
                    _unselectedApps.clear()
                    _unselectedApps.addAll(
                        _allAppList.filter {
                            !result.contains("|${it.packageName}|")
                        }
                    )
                }
        }
    }

    private val _allAppList = mutableStateSetOf<NotificationInfo>()
    val allAppList: Set<NotificationInfo> = _allAppList

    // 加载应用时更新 allAppList
    fun loadApps(context: Context) {
        if (!_loadStatus.value.isLoading()) return

        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) {
                getAllAppInfo(context)
            }

            _allAppList.clear()
            _allAppList.addAll(apps)

            // 根据已保存的白名单更新选中和未选中的应用列表
            val whitelist = SPUtils.getString("notification_icon_type_whitelist", "||")

            _selectedApps.clear()
            _unselectedApps.clear()

            apps.forEach { app ->
                if (whitelist.contains("|${app.packageName}|")) {
                    _selectedApps.add(app)
                } else {
                    _unselectedApps.add(app)
                }
            }

            _loadStatus.value.current = LoadStatus.Status.Complete
        }
    }

    // 更新选中应用列表的方法
    fun updateSelectedApps() {
        if (_selectedApps.isEmpty()) {
            SPUtils.setString("notification_icon_type_whitelist", "||")
            _unselectedApps.clear()
            _unselectedApps.addAll(_allAppList)
            return
        }

        val packageNames = _selectedApps.map { it.packageName }
        val result = buildString {
            packageNames.forEach { append("|$it") }
        } + "|"

        SPUtils.setString("notification_icon_type_whitelist", result)
        _unselectedApps.clear()
        _unselectedApps.addAll(
            _allAppList.filter {
                !result.contains("|${it.packageName}|")
            }
        )
    }

    // 添加和删除应用的方法
    fun addSelectedApp(app: NotificationInfo) {
        _selectedApps.add(app)
        _unselectedApps.remove(app)
        updateSelectedApps()
    }

    fun removeSelectedApp(app: NotificationInfo) {
        _selectedApps.remove(app)
        _unselectedApps.add(app)
        updateSelectedApps()
    }

    fun onPermissionGranted(isGranted: Boolean) {
        if (isGranted) {
            _loadStatus.value.current = LoadStatus.Status.Loading
        }
    }

    fun onSearchStatusChanged(status: SearchStatus.Status) {
        if (status == SearchStatus.Status.COLLAPSED) {
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

            _searchApp.value = _selectedApps.asFlow()
                .filter { app ->
                    app.appName.contains(searchText, ignoreCase = true)
                }
                .toList()

            _searchStatus.value.resultStatus = if (_searchApp.value.isEmpty()) {
                SearchStatus.ResultStatus.EMPTY
            } else {
                SearchStatus.ResultStatus.SHOW
            }
        }
    }

    private fun updateWhitelist() {
        if (_selectedApps.isEmpty()) {
            SPUtils.setString("notification_icon_type_whitelist", "||")
            return
        }

        val packageNames = _selectedApps.map { it.packageName }
        val result = buildString {
            packageNames.forEach { append("|$it") }
        } + "|"

        SPUtils.setString("notification_icon_type_whitelist", result)
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAllAppInfo(context: Context): List<NotificationInfo> {
        val appList = ArrayList<NotificationInfo>()
        val packageManager = context.packageManager
        val packages = packageManager.getInstalledPackages(0)

        for (packageInfo in packages) {
            val applicationInfo = packageInfo.applicationInfo
            processAppInfo(applicationInfo, packageManager, appList)
        }

        return appList
    }

    private fun processAppInfo(
        applicationInfo: ApplicationInfo?,
        packageManager: PackageManager?,
        appList: ArrayList<NotificationInfo>
    ) {
        if (applicationInfo == null || packageManager == null) return

        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val packageName = applicationInfo.packageName
        val appIcon = packageManager.getApplicationIcon(applicationInfo)

        val notificationInfo = NotificationInfo(
            appName = appName,
            packageName = packageName,
            icon = appIcon,
            notificationId = ""
        )
        appList.add(notificationInfo)
    }
}