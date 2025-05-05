package com.yunzia.hyperstar.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.search.SearchStatus
import com.yunzia.hyperstar.ui.module.systemui.other.notification.NotificationInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class NotificationAddViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val _selectedApps = mutableStateSetOf<NotificationInfo>()
    val selectedApps: SnapshotStateSet<NotificationInfo> = _selectedApps

    private val _searchStatus = mutableStateOf(
        SearchStatus(
            getApplication<Application>().getString(R.string.app_name_type)
        )
    )
    val searchStatus: State<SearchStatus> = _searchStatus

    private val _searchResults = mutableStateOf<List<NotificationInfo>>(emptyList())
    val searchResults: State<List<NotificationInfo>> = _searchResults

    private val _unselectedAppsList = mutableStateOf<List<NotificationInfo>>(emptyList())
    val unselectedAppsList: State<List<NotificationInfo>> = _unselectedAppsList

    // 更新未选中的应用列表
    fun updateUnselectedApps(unSelectApp: Set<NotificationInfo>) {
        _unselectedAppsList.value = unSelectApp.toList()
    }

    fun updateSearchText(text: String, unSelectApp: Set<NotificationInfo>) {
        _searchStatus.value.searchText = text

        if (text.isEmpty()) {
            _searchStatus.value.resultStatus = SearchStatus.ResultStatus.DEFAULT
            _searchResults.value = emptyList()
            return
        }

        viewModelScope.launch(Dispatchers.Default) {
            delay(300)
            _searchStatus.value.resultStatus = SearchStatus.ResultStatus.LOAD

            val results = unSelectApp.filter {
                it.appName.contains(text, ignoreCase = true)
            }

            _searchResults.value = results
            _searchStatus.value.resultStatus = if (results.isEmpty()) {
                SearchStatus.ResultStatus.EMPTY
            } else {
                SearchStatus.ResultStatus.SHOW
            }
        }
    }

    fun toggleAppSelection(app: NotificationInfo) {
        if (_selectedApps.contains(app)) {
            _selectedApps.remove(app)
        } else {
            _selectedApps.add(app)
        }
    }

    fun clearSelection() {
        _selectedApps.clear()
    }

    fun isSelected(app: NotificationInfo): Boolean {
        return _selectedApps.contains(app)
    }

    fun confirmSelection(onConfirmed: (Set<NotificationInfo>) -> Unit) {
        onConfirmed(_selectedApps.toSet())
        clearSelection()
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