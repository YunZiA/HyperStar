package com.yunzia.hyperstar.ui.component.preference

import android.widget.Toast
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.topbar.ModuleNavTopAppBar
import com.yunzia.hyperstar.ui.navigation.Navigator
import generated.SearchIndex
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState

// =============================================================================
// PreferenceList — LazyColumn + PreferenceScope (no TopBar)
// Supports optional scrollToKey for search result scrolling.
// Accepts optional keyMap for external coordination.
// =============================================================================

@Composable
fun PreferenceList(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(0.dp),
    scrollBehavior: ScrollBehavior? = null,
    keyMap: MutableMap<String, String>? = null,
    scrollToKey: String? = null,
    onScrollComplete: (() -> Unit)? = null,
    content: PreferenceScope.() -> Unit,
) {
    val context = LocalContext.current
    val resolvedKeyMap = keyMap ?: remember { mutableStateMapOf<String, String>() }
    val highlightedKey = remember { mutableStateOf<String?>(null) }
    val heightCache = remember { PreferenceHeightCache() }
    val buildState = remember { PreferenceListBuildState() }

    LaunchedEffect(scrollToKey) {
        val targetKey = scrollToKey ?: return@LaunchedEffect

        snapshotFlow { state.layoutInfo.totalItemsCount }
            .first { it > 0 }

        val lastGroupIndex = (state.layoutInfo.totalItemsCount - 2).coerceAtLeast(0)
        var groupIndex = heightCache.itemLayoutInfoMap[targetKey]?.groupIndex
            ?: SearchIndex.keyGroupMap[targetKey]
            ?: run {
                Toast.makeText(context, "未找到对应设置项", Toast.LENGTH_SHORT).show()
                onScrollComplete?.invoke()
                return@LaunchedEffect
            }
        groupIndex = groupIndex.coerceAtMost(lastGroupIndex)

        // Phase 1: bring group into viewport to trigger composition/measurement.
        launchAutoScroll(state, groupIndex)
        withFrameNanos { }

        withTimeoutOrNull(500) {
            snapshotFlow { heightCache.itemLayoutInfoMap[targetKey]?.groupIndex }
                .first { it != null }
        }?.let { runtimeGroup ->
            if (runtimeGroup != groupIndex) {
                groupIndex = runtimeGroup.coerceAtMost(lastGroupIndex)
                launchAutoScroll(state, groupIndex)
                withFrameNanos { }
            }
        }

        withTimeoutOrNull(500) {
            snapshotFlow {
                heightCache.groupHeaderHeightMap[groupIndex] != null &&
                heightCache.itemLayoutInfoMap[targetKey] != null
            }.first { it }
        }

        // Phase 2: only adjust enough to make the target preference visible.
        val visibleOffset = calculateVisibleOffsetForPreference(targetKey, groupIndex, state, heightCache)
        if (visibleOffset != null) {
            coroutineScope {
                launch {
                    animateTopBarCollapse(scrollBehavior)
                }
                launchAutoScroll(state, groupIndex, visibleOffset)
            }
        } else {
            animateTopBarCollapse(scrollBehavior)
        }
        snapTopBarCollapsed(scrollBehavior)

        highlightedKey.value = targetKey
        delay(2500)
        highlightedKey.value = null
        onScrollComplete?.invoke()
    }

    CompositionLocalProvider(
        LocalSearchableKeyMap provides resolvedKeyMap,
        LocalHighlightedKey provides highlightedKey,
        LocalScrollTargetKey provides scrollToKey,
        LocalPreferenceHeightCache provides heightCache
    ) {
        buildState.nextGroupIndex = 0
        val nestedScroll = scrollBehavior?.nestedScrollConnection
        LazyColumn(
            modifier = if (nestedScroll != null) {
                modifier
                    .fillMaxSize()
                    .nestedOverScrollVertical(nestedScroll)
            } else {
                modifier.fillMaxSize()
            },
            contentPadding = contentPadding,
            state = state
        ) {
            PreferenceScopeImpl(this, state, buildState).content()
            item("foot") {
                Spacer(Modifier
                    .fillMaxWidth()
                    .height(6.dp))
            }
        }
    }
}

// =============================================================================
// PreferencePage — TopBar + PreferenceList (no scrollToKey)
// For most module pages that don't participate in search result scrolling.
// =============================================================================

@Composable
fun PreferencePage(
    title: String,
    modifier: Modifier = Modifier,
    navController: Navigator? = null,
    startClick: (() -> Unit)? = null,
    endClick: (() -> Unit)? = null,
    endIcon: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: PreferenceScope.(ScrollBehavior, PaddingValues) -> Unit,
) {
    PreferenceScaffold(
        title = title,
        modifier = modifier,
        navController = navController,
        startClick = startClick,
        endClick = endClick,
        endIcon = endIcon,
        floatingActionButton = floatingActionButton,
        floatingPagerButton = floatingPagerButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) { scrollBehavior, padding ->
        PreferenceList(
            modifier = Modifier,
            contentPadding = defaultPreferenceContentPadding(padding),
            scrollBehavior = scrollBehavior
        ) {
            content(scrollBehavior, padding)
        }
    }
}

// =============================================================================
// PreferenceScreen — TopBar + LazyColumn + scrollToKey
// For pages that receive search result scroll requests.
// =============================================================================

@Composable
fun PreferenceScreen(
    title: String,
    modifier: Modifier = Modifier,
    navController: Navigator? = null,
    startClick: (() -> Unit)? = null,
    endClick: (() -> Unit)? = null,
    endIcon: @Composable () -> Unit = {},
    scrollToKey: String? = null,
    onScrollComplete: (() -> Unit)? = null,
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: PreferenceScope.(ScrollBehavior, PaddingValues) -> Unit,
) {
    val keyMap = remember { mutableStateMapOf<String, String>() }
    PreferenceScaffold(
        title = title,
        modifier = modifier,
        navController = navController,
        startClick = startClick,
        endClick = endClick,
        endIcon = endIcon,
        floatingActionButton = floatingActionButton,
        floatingPagerButton = floatingPagerButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
    ) { scrollBehavior, padding ->
        PreferenceList(
            modifier = Modifier,
            contentPadding = defaultPreferenceContentPadding(padding),
            scrollBehavior = scrollBehavior,
            keyMap = keyMap,
            scrollToKey = scrollToKey,
            onScrollComplete = onScrollComplete,
        ) {
            content(scrollBehavior, padding)
        }
    }
}

// =============================================================================
// PreferenceScopeImpl
// =============================================================================

internal class PreferenceScopeImpl(
    override val list: LazyListScope,
    override val listState: LazyListState,
    override val buildState: PreferenceListBuildState = PreferenceListBuildState(),
) : PreferenceScope

private fun calculateVisibleOffsetForPreference(
    targetKey: String,
    groupIndex: Int,
    state: LazyListState,
    heightCache: PreferenceHeightCache,
): Int? {
    val headerHeight = heightCache.groupHeaderHeightMap[groupIndex] ?: return null
    val itemInfo = heightCache.itemLayoutInfoMap[targetKey] ?: return null
    val groupItemInfo = state.layoutInfo.visibleItemsInfo.firstOrNull { it.index == groupIndex } ?: return null

    val targetTopInGroup = headerHeight + itemInfo.offsetInGroup
    val targetBottomInGroup = targetTopInGroup + itemInfo.height
    val targetTop = groupItemInfo.offset + targetTopInGroup
    val targetBottom = groupItemInfo.offset + targetBottomInGroup

    val viewportStart = state.layoutInfo.viewportStartOffset
    val viewportEnd = state.layoutInfo.viewportEndOffset
    if (targetTop >= viewportStart && targetBottom <= viewportEnd) {
        return null
    }

    val viewportHeight = (viewportEnd - viewportStart).coerceAtLeast(0)
    return when {
        targetTop < viewportStart -> targetTopInGroup
        else -> (targetBottomInGroup - viewportHeight).coerceAtLeast(0)
    }
}

private suspend fun launchAutoScroll(
    state: LazyListState,
    index: Int,
    offset: Int = 0,
) {
    state.animateScrollToItem(index, offset)
}

private suspend fun animateTopBarCollapse(scrollBehavior: ScrollBehavior?) {
    val topBarState = scrollBehavior?.state ?: return
    withTimeoutOrNull(500) {
        snapshotFlow { topBarState.heightOffsetLimit }
            .first { it < 0f }
    } ?: return

    val target = topBarState.heightOffsetLimit
    if (topBarState.heightOffset == target) return

    val animatable = Animatable(topBarState.heightOffset)
    animatable.animateTo(
        targetValue = target,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    ) {
        topBarState.heightOffset = value
    }
}

private suspend fun snapTopBarCollapsed(scrollBehavior: ScrollBehavior?) {
    val topBarState = scrollBehavior?.state ?: return
    withTimeoutOrNull(500) {
        snapshotFlow { topBarState.heightOffsetLimit }
            .first { it < 0f }
    } ?: return
    topBarState.heightOffset = topBarState.heightOffsetLimit
}

@Composable
private fun PreferenceScaffold(
    title: String,
    modifier: Modifier = Modifier,
    navController: Navigator? = null,
    startClick: (() -> Unit)? = null,
    endClick: (() -> Unit)? = null,
    endIcon: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    content: @Composable (ScrollBehavior, PaddingValues) -> Unit,
) {
    val backdrop = rememberLayerBackdrop()
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val defaultStartClick: () -> Unit = startClick ?: { navController?.goBack() }
    val hasTopBar = startClick != null || endClick != null || navController != null

    XScaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingPagerButton = floatingPagerButton,
        popupHost = { },
        topBar = if (hasTopBar) {
            {
                ModuleNavTopAppBar(
                    modifier = Modifier.showBlur(backdrop),
                    color = Color.Transparent,
                    title = title,
                    scrollBehavior = scrollBehavior,
                    startClick = defaultStartClick,
                    endIcon = endIcon,
                    endClick = endClick ?: {}
                )
            }
        } else {
            {}
        }
    ) { padding ->
        Box(
            Modifier
                .fillMaxSize()
                .blur(backdrop)
        ) {
            content(scrollBehavior, padding)
        }
    }
}

@Stable
private fun defaultPreferenceContentPadding(padding: PaddingValues): PaddingValues {
    return PaddingValues(
        top = padding.calculateTopPadding(),
        bottom = padding.calculateBottomPadding() + 12.dp
    )
}
