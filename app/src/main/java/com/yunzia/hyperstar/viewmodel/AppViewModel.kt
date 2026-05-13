package com.yunzia.hyperstar.viewmodel

import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.asImageBitmap
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import com.yunzia.hyperstar.ui.navigation.Route
import com.yunzia.hyperstar.ui.navigation.displayName
import com.yunzia.hyperstar.ui.screen.pagers.main.home.AppEntryList
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.ScopeManager
import com.yunzia.hyperstar.utils.XposedServiceInfo
import generated.SearchEntry
import generated.SearchIndex
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {

    var isActive = mutableStateOf(false)
        private set

    val scopeManager = ScopeManager()

    val scrollToKey = mutableStateOf<String?>(null)

    val visibleEntryMap = mutableStateMapOf<String, AppInfo>()
    val invisibleEntryMap = mutableStateMapOf<String, EntryUiState>()

    var xposedServiceInfo = mutableStateOf(XposedServiceInfo.unconnected())
        private set

    private val _searchStatus = mutableStateOf(SearchStatus(""))
    val searchStatus: State<SearchStatus> = _searchStatus

    private val _searchResults = mutableStateOf<List<RankedSearchResult>>(emptyList())
    val searchResults: State<List<RankedSearchResult>> = _searchResults

    private var searchDocuments = emptyList<SearchDocument>()
    private val searchDebouncer = SearchDebouncer<RankedSearchResult>(viewModelScope, delayMs = 250)

    fun loadSearchDocuments(strings: (Int) -> String) {
        val routeTitleMap = SearchIndex.routeTitleEntries.associate { entry ->
            entry.routeClass to strings(entry.titleRes)
        }
        val routeTabTitleMap = SearchIndex.routeTabTitleEntries.associate { entry ->
            (entry.routeClass to entry.tabIndex) to strings(entry.titleRes)
        }
        searchDocuments = SearchIndex.entries.mapIndexedNotNull { index, entry ->
            val titleRes = entry.titleRes ?: return@mapIndexedNotNull null
            val title = strings(titleRes)
            val summary = entry.summaryRes?.let { strings(it) } ?: ""
            val route = entry.routeClass
            val path = route?.let {
                buildEntrancePath(it, entry.tabIndex, routeTitleMap, routeTabTitleMap)
            } ?: ""
            val groupTitle = entry.groupTitleRes?.let { strings(it) } ?: ""
            val routeName = route?.let { routeTitle(it, routeTitleMap) } ?: ""
            val tabTitle = if (entry.tabIndex >= 0 && route != null) {
                routeTabTitleMap[route to entry.tabIndex] ?: ""
            } else ""
            val pathParts = path.split("/")
            SearchDocument(
                entry = entry,
                title = title,
                summary = summary,
                path = path,
                groupTitle = groupTitle,
                tabTitle = tabTitle,
                routeOrder = entry.routeOrder,
                originalIndex = index,
                keyLower = entry.key.lowercase(),
                visibleInSearch = entry.visible() && entry.groupVisible(),
                titleLower = title.lowercase(),
                pathParts = pathParts,
                breadcrumb = pathParts.dropLast(1).joinToString("/"),
                displayBreadcrumb = path.replace("/", "·"),
                routeNameLower = routeName.lowercase(),
                summaryLower = summary.lowercase()
            )
        }
    }

    fun updateSearchText(text: String) {
        _searchStatus.value.searchText = text

        searchDebouncer.submit(
            query = text,
            onEmpty = {
                _searchResults.value = emptyList()
                _searchStatus.value.resultStatus = SearchStatus.ResultStatus.DEFAULT
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
            val queryLower = query.lowercase()
            searchDocuments
                .filter { it.visibleInSearch }
                .mapNotNull { document ->
                    val score = scoreQuery(queryLower, document.titleLower)
                    if (score > 0) document.toResult(score) else null
                }
                .sortedWith(buildSearchComparator())
        }
    }

    fun onXposedServiceBound(service: XposedService) {
        isActive.value = service.apiVersion >= 101
        xposedServiceInfo.value = XposedServiceInfo(
            apiVersion = service.apiVersion,
            frameworkName = service.frameworkName,
            frameworkVersion = service.frameworkVersion,
            frameworkVersionCode = service.frameworkVersionCode,
            scope = service.scope
        )
        scopeManager.attachService(service)
        Log.d("AppViewModel", "Xposed connected")
    }

    fun onXposedServiceReleased() {
        Log.d("AppViewModel", "Xposed released")
    }

    fun loadEntries(
        packageManager: PackageManager,
        moduleScope: Array<String>,
        currentScope: Set<String>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            coroutineScope {
                AppEntryList.entries.forEach { entry ->
                    launch {
                        try {
                            val packageInfo = packageManager.getPackageInfo(
                                entry.packageName,
                                PackageManager.GET_META_DATA
                            )
                            val appInfo = AppInfo(
                                appIcon = packageInfo.applicationInfo
                                    ?.loadIcon(packageManager)?.toBitmap()?.asImageBitmap(),
                                appName = packageInfo.applicationInfo
                                    ?.loadLabel(packageManager)?.toString()
                                    ?: entry.packageName,
                                versionName = packageInfo.versionName,
                                versionCode = packageInfo.longVersionCode
                            )

                            if (entry.packageName !in currentScope) {
                                invisibleEntryMap[entry.packageName] = EntryUiState.NotInScope(appInfo)
                                visibleEntryMap.remove(entry.packageName)
                                return@launch
                            }

                            if (entry.isVisible(appInfo)) {
                                visibleEntryMap[entry.packageName] = appInfo
                                invisibleEntryMap.remove(entry.packageName)
                            } else {
                                invisibleEntryMap[entry.packageName] = EntryUiState.Hidden(appInfo)
                                visibleEntryMap.remove(entry.packageName)
                            }
                        } catch (e: Exception) {
                            invisibleEntryMap[entry.packageName] = EntryUiState.NotInstalled(e.message)
                        }
                    }
                }
            }
        }
    }

    fun clearEntries() {
        visibleEntryMap.clear()
        invisibleEntryMap.clear()
    }

    private fun routeTitle(route: Route, routeTitleMap: Map<Route, String>): String {
        return routeTitleMap[route] ?: route.displayName()
    }

    private fun buildEntrancePath(
        route: Route,
        tabIndex: Int,
        routeTitleMap: Map<Route, String>,
        routeTabTitleMap: Map<Pair<Route, Int>, String>
    ): String {
        val parts = mutableListOf<String>()
        var current: Route? = route
        var currentTabIndex = tabIndex

        while (current != null) {
            if (currentTabIndex >= 0) {
                routeTabTitleMap[current to currentTabIndex]
                    ?.takeIf { it.isNotBlank() }
                    ?.let { parts.add(it) }
            }

            val title = routeTitle(current, routeTitleMap)
            if (title.isNotEmpty() && title != parts.lastOrNull()) {
                parts.add(title)
            }

            val entrance = SearchIndex.routeEntranceMap[current]
            if (entrance != null) {
                current = entrance.first
                currentTabIndex = entrance.second
            } else {
                val parent = current.parent
                current = if (parent is Route && parent != current) parent else null
                currentTabIndex = -1
            }
        }

        return parts.reversed().joinToString("/")
    }

    private fun buildSearchComparator(): Comparator<RankedSearchResult> =
        compareByDescending<RankedSearchResult> { it.score }
            .thenBy { it.routeOrder }
            .thenBy { it.path.count { c -> c == '/' } }
            .thenBy { it.entry.groupIndex }
            .thenBy { it.originalIndex }
}

data class RankedSearchResult(
    val entry: SearchEntry,
    val score: Int,
    val title: String,
    val path: String,
    val groupTitle: String,
    val tabTitle: String,
    val routeOrder: Int,
    val originalIndex: Int,
    val pathParts: List<String>,
    val breadcrumb: String,
    val displayBreadcrumb: String
)

private data class SearchDocument(
    val entry: SearchEntry,
    val title: String,
    val summary: String,
    val path: String,
    val groupTitle: String,
    val tabTitle: String,
    val routeOrder: Int,
    val originalIndex: Int,
    val keyLower: String,
    val visibleInSearch: Boolean,
    val titleLower: String,
    val pathParts: List<String>,
    val breadcrumb: String,
    val displayBreadcrumb: String,
    val routeNameLower: String,
    val summaryLower: String
) {
    fun toResult(score: Int) = RankedSearchResult(
        entry = entry,
        score = score,
        title = title,
        path = path,
        groupTitle = groupTitle,
        tabTitle = tabTitle,
        routeOrder = routeOrder,
        originalIndex = originalIndex,
        pathParts = pathParts,
        breadcrumb = breadcrumb,
        displayBreadcrumb = displayBreadcrumb
    )
}

private fun scoreQuery(queryLower: String, titleLower: String): Int = when {
    titleLower == queryLower -> 10000
    titleLower.startsWith(queryLower) -> 8000
    titleLower.contains(queryLower) -> 6000
    else -> 0
}

@Stable
sealed class EntryUiState(open val appInfo: AppInfo?) {
    data class Hidden(override val appInfo: AppInfo) : EntryUiState(appInfo)
    data class NotInScope(override val appInfo: AppInfo) : EntryUiState(appInfo)
    data class NotInstalled(val reason: String?) : EntryUiState(null)
}
