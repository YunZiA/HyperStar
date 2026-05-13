package com.yunzia.hyperstar.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.LoadStatus
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import com.yunzia.hyperstar.ui.screen.module.systemui.other.notification.NotificationInfo
import com.yunzia.hyperstar.prefs.SPUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedApps = mutableStateSetOf<NotificationInfo>()
    val selectedApps: SnapshotStateSet<NotificationInfo> = _selectedApps

    private val _unselectedApps = mutableStateSetOf<NotificationInfo>()
    val unselectedApps: SnapshotStateSet<NotificationInfo> = _unselectedApps

    private val _searchApp = mutableStateOf<List<NotificationInfo>>(emptyList())
    val searchApp: State<List<NotificationInfo>> = _searchApp

    private val _loadStatus = mutableStateOf(LoadStatus())
    val loadStatus: State<LoadStatus> = _loadStatus

    private val _searchStatus = mutableStateOf(
        SearchStatus(getApplication<Application>().getString(R.string.app_name_type))
    )
    val searchStatus: State<SearchStatus> = _searchStatus

    private val _allAppList = mutableStateSetOf<NotificationInfo>()

    private val searchDebouncer = SearchDebouncer<NotificationInfo>(viewModelScope)

    fun loadApps(context: Context) {
        if (!_loadStatus.value.isLoading()) return

        viewModelScope.launch {
            val apps = withContext(Dispatchers.IO) { getAllAppInfo(context) }

            _allAppList.clear()
            _allAppList.addAll(apps)

            val whitelist = SPUtils.getString(WHITELIST_KEY, EMPTY_WHITELIST)

            _selectedApps.clear()
            _unselectedApps.clear()

            apps.forEach { app ->
                if ("|${app.packageName}|" in whitelist) {
                    _selectedApps.add(app)
                } else {
                    _unselectedApps.add(app)
                }
            }

            _loadStatus.value.current = LoadStatus.Status.Complete
        }
    }

    fun addSelectedApp(app: NotificationInfo) {
        _selectedApps.add(app)
        _unselectedApps.remove(app)
        persistWhitelist()
    }

    fun removeSelectedApp(app: NotificationInfo) {
        _selectedApps.remove(app)
        _unselectedApps.add(app)
        persistWhitelist()
    }

    fun onPermissionGranted(isGranted: Boolean) {
        if (isGranted) {
            _loadStatus.value.current = LoadStatus.Status.Loading
        }
    }

    fun onSearchTextChanged(searchText: String) {
        _searchStatus.value.searchText = searchText

        searchDebouncer.submit(
            query = searchText,
            onEmpty = {
                _searchApp.value = emptyList()
                _searchStatus.value.resultStatus = SearchStatus.ResultStatus.DEFAULT
            },
            onLoading = {
                _searchStatus.value.resultStatus = SearchStatus.ResultStatus.LOAD
            },
            onResult = { results ->
                _searchApp.value = results
                _searchStatus.value.resultStatus = if (results.isEmpty()) {
                    SearchStatus.ResultStatus.EMPTY
                } else {
                    SearchStatus.ResultStatus.SHOW
                }
            }
        ) { query ->
            _selectedApps.filter {
                it.appName.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    private fun persistWhitelist() {
        if (_selectedApps.isEmpty()) {
            SPUtils.putString(WHITELIST_KEY, EMPTY_WHITELIST)
            _unselectedApps.clear()
            _unselectedApps.addAll(_allAppList)
            _loadStatus.value.isEmpty = true
            return
        }

        val encoded = "|${_selectedApps.joinToString("|") { it.packageName }}|"

        SPUtils.putString(WHITELIST_KEY, encoded)
        _unselectedApps.clear()
        _unselectedApps.addAll(_allAppList.filter { "|${it.packageName}|" !in encoded })
        _loadStatus.value.isEmpty = false
    }

    @SuppressLint("QueryPermissionsNeeded")
    private fun getAllAppInfo(context: Context): List<NotificationInfo> {
        val packageManager = context.packageManager
        return packageManager.getInstalledPackages(0).mapNotNull { packageInfo ->
            val appInfo = packageInfo.applicationInfo ?: return@mapNotNull null
            NotificationInfo(
                appName = packageManager.getApplicationLabel(appInfo).toString(),
                packageName = appInfo.packageName,
                icon = packageManager.getApplicationIcon(appInfo),
                notificationId = ""
            )
        }
    }

    companion object {
        private const val WHITELIST_KEY = "notification_icon_type_whitelist"
        private const val EMPTY_WHITELIST = "||"
    }
}
