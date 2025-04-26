package com.yunzia.hyperstar.ui.base

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.pager.SearchBar
import com.yunzia.hyperstar.ui.base.pager.SearchBarFake
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler

// Remember Search Status
@Composable
fun rememberSearchStatus(
    label: String = "",
    collapseBar: @Composable (SearchStatus) -> Unit = { SearchBarFake(label) },
    expandBar: @Composable (SearchStatus) -> Unit = { searchStatus ->
        SearchBar(
            query = searchStatus.searchText,
            label = searchStatus.label,
            onQueryChange = { searchStatus.searchText = it },
            expanded = searchStatus.shouldExpand(),
            onExpandedChange = {
                Log.d("SearchStatus", "Expanded: $it")
                searchStatus.status = if (it) SearchStatus.Status.EXPANDED else SearchStatus.Status.COLLAPSED
            }
        )
    },
): SearchStatus {
    val searchStatus = remember { SearchStatus(label) }
    var isInitialized by remember { mutableStateOf(false) }

    searchStatus.apply {
        this.collapseBar = { collapseBar(this) }
        this.expandBar = { expandBar(this) }
    }

    LaunchedEffect(searchStatus.status) {
        if (searchStatus.status == SearchStatus.Status.COLLAPSED) searchStatus.searchText = ""
        if (!isInitialized) {
            isInitialized = true
            return@LaunchedEffect
        }
        searchStatus.isAnimating = true
    }

    return searchStatus
}

// Search Status Class
@Stable
class SearchStatus(val label: String) {
    var collapseBar: @Composable () -> Unit = {}
    var expandBar: @Composable () -> Unit = {}
    var searchText by mutableStateOf("")
    var status by mutableStateOf(Status.COLLAPSED)
    var isAnimating by mutableStateOf(false)
    var offsetY by mutableStateOf(0.dp)
    var resultStatus by mutableStateOf(ResultStatus.DEFAULT)

    fun isExpand() = status == Status.EXPANDED && !isAnimating
    fun isCollapsed() = status == Status.COLLAPSED && !isAnimating
    fun shouldExpand() = status == Status.EXPANDED
    fun shouldCollapsed() = status == Status.COLLAPSED
    fun isAnimatingExpand() = status == Status.EXPANDED && isAnimating
    fun isAnimatingCollapse() = status == Status.COLLAPSED && isAnimating

    @Composable
    fun TopAppBarAnim(
        modifier: Modifier = Modifier,
        visible: Boolean = shouldCollapsed(),
        content: @Composable() AnimatedVisibilityScope.() -> Unit
    ) {
        AnimatedVisibility(
            visible = visible,
            modifier = modifier,
            enter = fadeIn(
                animationSpec = spring(
                    stiffness = Spring.StiffnessLow
                )
            ),
            exit = fadeOut(
                animationSpec = spring(
                    stiffness = Spring.StiffnessMedium
                )
            ),
            label = "TopAppBarAnim"
        ) {
            this.content()
        }
    }


    enum class Status { EXPANDED, COLLAPSED }
    enum class ResultStatus { DEFAULT, EMPTY, LOAD, SHOW }
}

// Search Box Composable
@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    searchStatus: SearchStatus,
    content: @Composable ColumnScope.() -> Unit
) {
    val density = LocalDensity.current

    Column(modifier = modifier) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .alpha(if (searchStatus.isCollapsed()) 1f else 0f)
                .onGloballyPositioned {
                    with(density) {
                        searchStatus.offsetY = it.positionInWindow().y.toDp()
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures { searchStatus.status = SearchStatus.Status.EXPANDED }
                }
        ) {
            searchStatus.collapseBar()
        }
        AnimatedVisibility(
            visible = searchStatus.shouldCollapsed(),
            enter = fadeIn() + slideInVertically { -it / 6 },
            exit = fadeOut() + slideOutVertically { -it / 7 }
        ) {
            content()
        }
    }
}

// Search Pager Composable
@Composable
fun SearchPager(
    searchStatus: SearchStatus,
    defaultResult: @Composable () -> Unit,
    result: LazyListScope.() -> Unit
) {
    val systemBarsPadding = WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val topPadding by animateDpAsState(
        if (searchStatus.shouldExpand()) systemBarsPadding else searchStatus.offsetY,
        animationSpec = tween(250, easing = LinearOutSlowInEasing)
    ) {
        searchStatus.isAnimating = false
    }
    val backgroundAlpha by animateFloatAsState(
        if (searchStatus.shouldExpand()) 1f else 0f,
        animationSpec = tween(250, easing = LinearOutSlowInEasing)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background.copy(alpha = backgroundAlpha))
            .semantics { onClick { false } }
            .then(
                if (!searchStatus.isCollapsed()) Modifier.pointerInput(Unit) {} else Modifier
            )
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = topPadding)
                .alpha(if (searchStatus.isCollapsed()) 0f else 1f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AnimatedVisibility(
                visible = !searchStatus.isCollapsed(),
                modifier = Modifier.weight(1f),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                searchStatus.expandBar()
            }
            AnimatedVisibility(
                visible = searchStatus.isExpand() || searchStatus.isAnimatingExpand(),
                enter = expandHorizontally() + slideInHorizontally(initialOffsetX = { it }),
                exit = shrinkHorizontally() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                BackHandler(enabled = true) {
                    searchStatus.status = SearchStatus.Status.COLLAPSED
                }
                Text(
                    text = stringResource(R.string.cancel),
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 24.dp)
                        .clickable(
                            interactionSource = null,
                            enabled = searchStatus.isExpand(),
                            indication = null
                        )  { searchStatus.status = SearchStatus.Status.COLLAPSED }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(
            visible = searchStatus.isExpand(),
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            when (searchStatus.resultStatus) {
                SearchStatus.ResultStatus.DEFAULT -> defaultResult()
                SearchStatus.ResultStatus.EMPTY -> {}
                SearchStatus.ResultStatus.LOAD -> Box(
                    Modifier
                        .padding(vertical = 20.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Loading()
                }
                SearchStatus.ResultStatus.SHOW -> LazyColumn(Modifier.fillMaxSize()) {
                    result()
                    item("last") {
                        Spacer(Modifier.height(28.dp).navigationBarsPadding())
                    }
                }
            }
        }
    }
}