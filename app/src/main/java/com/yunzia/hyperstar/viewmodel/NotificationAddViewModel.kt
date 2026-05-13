package com.yunzia.hyperstar.viewmodel

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import com.yunzia.hyperstar.ui.screen.module.systemui.other.notification.NotificationInfo

class NotificationAddViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedApps = mutableStateSetOf<NotificationInfo>()
    val selectedApps: SnapshotStateSet<NotificationInfo> = _selectedApps

    private val _searchStatus = mutableStateOf(
        SearchStatus(getApplication<Application>().getString(R.string.app_name_type))
    )
    val searchStatus: State<SearchStatus> = _searchStatus

    private val _searchResults = mutableStateOf<List<NotificationInfo>>(emptyList())
    val searchResults: State<List<NotificationInfo>> = _searchResults

    private val _unselectedAppsList = mutableStateOf<List<NotificationInfo>>(emptyList())
    val unselectedAppsList: State<List<NotificationInfo>> = _unselectedAppsList

    private val searchDebouncer = SearchDebouncer<NotificationInfo>(viewModelScope)

    fun updateUnselectedApps(unSelectApp: Set<NotificationInfo>) {
        _unselectedAppsList.value = unSelectApp.toList()
    }

    fun updateSearchText(text: String, unSelectApp: Set<NotificationInfo>) {
        _searchStatus.value.searchText = text

        searchDebouncer.submit(
            query = text,
            onEmpty = {
                _searchResults.value = emptyList()
                _searchStatus.value.resultStatus = SearchStatus.ResultStatus.DEFAULT
            },
            onLoading = {
                _searchStatus.value.resultStatus = SearchStatus.ResultStatus.LOAD
            },
            onResult = { results ->
                _searchResults.value = results
                _searchStatus.value.resultStatus = if (results.isEmpty()) {
                    SearchStatus.ResultStatus.EMPTY
                } else {
                    SearchStatus.ResultStatus.SHOW
                }
            }
        ) { query ->
            unSelectApp.filter {
                it.appName.contains(query, ignoreCase = true) ||
                    it.packageName.contains(query, ignoreCase = true)
            }
        }
    }

    fun toggleAppSelection(app: NotificationInfo) {
        if (app in _selectedApps) {
            _selectedApps.remove(app)
        } else {
            _selectedApps.add(app)
        }
    }

    fun clearSelection() {
        _selectedApps.clear()
    }

    fun isSelected(app: NotificationInfo): Boolean = app in _selectedApps

    fun confirmSelection(onConfirmed: (Set<NotificationInfo>) -> Unit) {
        onConfirmed(_selectedApps.toSet())
    }
}
