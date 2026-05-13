package com.yunzia.hyperstar.ui.component.search

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.max
import kotlin.math.roundToInt
import androidx.compose.ui.zIndex
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.navigation.NavBackHandler
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.compose.rememberNavigationEventState
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.utils.overScrollVertical

@Stable
class SearchStatus(val label: String) {
    var searchText by mutableStateOf("")
    var expanded by mutableStateOf(false)
    var offsetY by mutableStateOf(0.dp)
    var resultStatus by mutableStateOf(ResultStatus.DEFAULT)
    var hasFocus by mutableStateOf(false)
        private set
    var focusRequestKey by mutableIntStateOf(0)
        private set
    private var consumedFocusRequestKey by mutableIntStateOf(0)

    fun expand(requestFocus: Boolean = true) {
        if (requestFocus) focusRequestKey++
        expanded = true
    }

    fun collapse(clearText: Boolean = true) {
        if (clearText) {
            searchText = ""
            resultStatus = ResultStatus.DEFAULT
        }
        consumedFocusRequestKey = focusRequestKey
        hasFocus = false
        expanded = false
    }

    fun shouldRequestFocus(): Boolean =
        expanded && focusRequestKey != consumedFocusRequestKey

    fun consumeFocusRequest() {
        consumedFocusRequestKey = focusRequestKey
    }

    fun onFocusChanged(focused: Boolean) {
        hasFocus = focused
    }

    enum class ResultStatus { DEFAULT, EMPTY, LOAD, SHOW }
}

enum class SearchPhase {
    Collapsed,
    Expanding,
    Expanded,
    Collapsing;

    val isVisible: Boolean get() = this != Collapsed
    val isAnimating: Boolean get() = this == Expanding || this == Collapsing
}

@Stable
class SearchMotionState(
    phase: SearchPhase,
    progress: Float,
) {
    var phase by mutableStateOf(phase)
        internal set
    var progress by mutableFloatStateOf(progress)
        internal set

    val isVisible: Boolean get() = phase.isVisible
    val isAnimating: Boolean get() = phase.isAnimating
}

@Composable
fun rememberSearchMotionState(
    expanded: Boolean,
    durationMillis: Int = 300,
): SearchMotionState {
    val animatable = remember { Animatable(if (expanded) 1f else 0f) }
    val state = remember {
        SearchMotionState(
            phase = if (expanded) SearchPhase.Expanded else SearchPhase.Collapsed,
            progress = if (expanded) 1f else 0f
        )
    }

    LaunchedEffect(expanded) {
        val target = if (expanded) 1f else 0f
        val finalPhase = if (expanded) SearchPhase.Expanded else SearchPhase.Collapsed

        if (animatable.value == target) {
            state.phase = finalPhase
            state.progress = target
            return@LaunchedEffect
        }

        state.phase = if (expanded) SearchPhase.Expanding else SearchPhase.Collapsing
        animatable.animateTo(
            targetValue = target,
            animationSpec = tween(durationMillis, easing = LinearOutSlowInEasing)
        ) {
            state.progress = value
        }
        state.phase = finalPhase
    }

    return state
}

@Composable
fun SearchStatus.rememberMotionState(durationMillis: Int = 300): SearchMotionState =
    rememberSearchMotionState(expanded, durationMillis)

@Composable
fun TopAppSearchBar(
    modifier: Modifier = Modifier,
    searchStatus: SearchStatus,
    motionState: SearchMotionState,
    collapseBar: @Composable (SearchStatus) -> Unit = { s ->
        SearchBarFake(s.label)
    },
    searchBarTopPadding: Dp = 12.dp,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    backdrop: LayerBackdrop,
    content: @Composable () -> Unit
) {
    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val expandedTopPaddingPx = with(density) {
        (systemBarsPadding + 5.dp).roundToPx()
    }
    val searchProgress = motionState.progress
    val topAppBarAlpha = 1f - searchProgress

    Layout(
        modifier = modifier
            .showBlur(backdrop)
            .clipToBounds(),
        content = {
            Box(
                modifier = Modifier
                    .alpha(topAppBarAlpha)
            ) {
                content()
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            searchStatus.expand(requestFocus = true)
                        }
                    }
                    .onGloballyPositioned {
                        with(density) {
                            val newOffsetY = it.positionInWindow().y.toDp()
                            if (searchStatus.offsetY != newOffsetY) {
                                searchStatus.offsetY = newOffsetY
                            }
                        }
                    }
            ) {
                Box(
                    Modifier
                        .padding(
                            top = searchBarTopPadding,
                            start = contentPadding.calculateStartPadding(layoutDirection) + 12.dp,
                            end = contentPadding.calculateEndPadding(layoutDirection) + 12.dp,
                            bottom = 6.dp
                        )
                        .alpha(if (motionState.phase == SearchPhase.Collapsed) 1f else 0f)
                ) {
                    collapseBar(searchStatus)
                }
            }
        }
    ) { measurables, constraints ->

        val looseConstraints = constraints.copy(
            maxHeight = Constraints.Infinity
        )

        val placeables = measurables.map {
            it.measure(looseConstraints)
        }

        val topBarHeight = placeables.getOrNull(0)?.height ?: 0
        val searchBarHeight = placeables.getOrNull(1)?.height ?: 0

        val targetHeightPx = expandedTopPaddingPx + searchBarHeight
        val totalHeight = topBarHeight + searchBarHeight
        val displayHeightPx =
            (totalHeight * (1f - searchProgress) + targetHeightPx * searchProgress).roundToInt()
        val width = placeables.maxOfOrNull { it.width } ?: constraints.minWidth

        layout(width, displayHeightPx) {

            var y = 0

            placeables.forEach { placeable ->
                placeable.place(0, y)
                y += placeable.height
            }
        }
    }
}

// Search Pager Composable
@Composable
fun SearchPager(
    searchStatus: SearchStatus,
    motionState: SearchMotionState,
    defaultResult: @Composable () -> Unit,
    onQueryChange: (String) -> Unit = { searchStatus.searchText = it },
    expandBar: @Composable (SearchStatus, Dp) -> Unit = { s, padding ->
        SearchBar(s, onQueryChange, padding)
    },
    searchBarTopPadding: Dp = 12.dp,
    backHandlerEnabled: Boolean = true,
    navigationKey: Any? = null,
    result: LazyListScope.() -> Unit
) {

    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val searchExpanded = searchStatus.expanded
    val searchProgress = motionState.progress
    val topPadding = lerp(
        start = max(searchStatus.offsetY, 0.dp),
        stop = systemBarsPadding + 5.dp,
        fraction = searchProgress
    )
    val keepSearchContent = motionState.isVisible || searchProgress > 0f

    val focusManager = LocalFocusManager.current
    var prevKey by remember { mutableStateOf(navigationKey) }
    val isSearchBackEnabled = motionState.isVisible || searchStatus.hasFocus

    LaunchedEffect(navigationKey) {
        if (navigationKey != prevKey) focusManager.clearFocus(force = true)
        prevKey = navigationKey
    }

    fun closeSearch() {
        focusManager.clearFocus()
        searchStatus.collapse()
    }

    LifecycleEventEffect(Lifecycle.Event.ON_PAUSE) {
        focusManager.clearFocus()
    }

    NavBackHandler(
        state = rememberNavigationEventState(NavigationEventInfo.None),
        isBackEnabled = backHandlerEnabled && isSearchBackEnabled
    ) {
        closeSearch()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)
            .background(colorScheme.surface.copy(alpha = searchProgress))
            .semantics { onClick { false } }
            .then(
                if (motionState.isVisible) Modifier.pointerInput(Unit) { } else Modifier
            ),
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = topPadding, bottom = 6.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {

            if (keepSearchContent) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    expandBar(searchStatus, searchBarTopPadding)
                }
            }
            AnimatedVisibility(
                visible = searchExpanded,
                modifier = Modifier.wrapContentHeight(),
                enter = expandHorizontally() + slideInHorizontally(initialOffsetX = { it }),
                exit = shrinkHorizontally() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                Text(
                    text = stringResource(android.R.string.cancel),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 16.dp, top = searchBarTopPadding)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ) {
                            closeSearch()
                        }
                )
            }

        }
        if (keepSearchContent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(searchProgress)
            ) {
                when (searchStatus.resultStatus) {
                    SearchStatus.ResultStatus.DEFAULT -> defaultResult()
                    SearchStatus.ResultStatus.EMPTY -> {}
                    SearchStatus.ResultStatus.LOAD -> {}
                    SearchStatus.ResultStatus.SHOW -> LazyColumn(
                        Modifier
                            .fillMaxSize()
                            .overScrollVertical(),
                    ) {
                        result()
                    }
                }
            }
        }
    }
}
