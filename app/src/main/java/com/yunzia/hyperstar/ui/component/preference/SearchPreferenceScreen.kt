package com.yunzia.hyperstar.ui.component.preference

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import com.yunzia.hyperstar.ui.component.search.SearchPager
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import com.yunzia.hyperstar.ui.component.search.TopAppSearchBar
import com.yunzia.hyperstar.ui.component.search.rememberMotionState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.blur.LayerBackdrop

@Composable
fun SearchPreferenceScreen(
    searchStatus: SearchStatus,
    backdrop: LayerBackdrop = rememberLayerBackdrop(),
    topBar: @Composable (ScrollBehavior) -> Unit,
    defaultResult: @Composable () -> Unit = {},
    searchResult: LazyListScope.() -> Unit,
    searchBarTopPadding: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    backHandlerEnabled: Boolean = true,
    navigationKey: Any? = null,
    onQueryChange: (String) -> Unit = { searchStatus.searchText = it },
    content: LazyListScope.() -> Unit,
) {
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val listState = rememberLazyListState()
    val searchMotionState = searchStatus.rememberMotionState()

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = {
            SearchPager(
                searchStatus = searchStatus,
                motionState = searchMotionState,
                defaultResult = defaultResult,
                searchBarTopPadding = searchBarTopPadding,
                backHandlerEnabled = backHandlerEnabled,
                navigationKey = navigationKey,
                onQueryChange = onQueryChange,
            ) {
                searchResult()
                item("bottom"){
                    Spacer(Modifier.fillMaxWidth().height(contentPadding.calculateBottomPadding()))
                }
            }
        },
        topBar = {
            TopAppSearchBar(
                searchStatus = searchStatus,
                motionState = searchMotionState,
                searchBarTopPadding = searchBarTopPadding,
                backdrop = backdrop
            ) {
                topBar(topAppBarScrollBehavior)
            }
        }
    ) { padding ->
        Box(
            Modifier.fillMaxSize().blur(backdrop)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 12.dp + contentPadding.calculateBottomPadding()
                ),
                state = listState
            ) {
                content()
                item("foot") {
                    Spacer(Modifier.fillMaxWidth().height(6.dp))
                }
            }
        }
    }
}
