package com.yunzia.hyperstar.ui.base

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.base.SearchStatus.Status
import com.yunzia.hyperstar.ui.base.pager.SearchBar
import com.yunzia.hyperstar.ui.base.pager.SearchBarFake
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler

@Composable
fun rememberSearchStatus(
    label:String = "",
    collapseBar: @Composable (SearchStatus) -> Unit = {
        SearchBarFake(
            label
        )
    },
    expandBar: @Composable (SearchStatus) -> Unit = { searchStatus->
        SearchBar(
            query = searchStatus.searchText,
            label = searchStatus.label,
            onQueryChange = { searchStatus.searchText = it },
            expanded = searchStatus.isExpand(),
            onExpandedChange = {
                Log.d("ggc", "searchBar: $it")
                searchStatus.status = if (it) Status.EXPANDED else Status.COLLAPSED
            }
        )

    },
): SearchStatus {

    var isInitialized by remember { mutableStateOf(false) }
    val searchStatus by remember {
        mutableStateOf(SearchStatus(
            label = label
        ).apply {
            this.collapseBar = { collapseBar(this) }
            this.expandBar = { expandBar(this) }
        })
    }
    LaunchedEffect(searchStatus.status) {
        if (searchStatus.status == Status.COLLAPSED){
            searchStatus.searchText = ""
        }
        if (!isInitialized) {
            isInitialized = true
            return@LaunchedEffect
        }
        searchStatus.isAnimating = true
    }

    return searchStatus
}

class SearchStatus(
    val label:String,
){
    var collapseBar: @Composable () -> Unit = {}
    var expandBar: @Composable () -> Unit = {}
    var searchText by mutableStateOf("")
    var status by mutableStateOf(Status.COLLAPSED)
    var isAnimating by mutableStateOf(false)
    var offsetY by mutableStateOf(0.dp)
    var resultStatus by mutableStateOf(ResultStatus.DEFAULT)


    fun isExpand():Boolean{
        return status == Status.EXPANDED && !isAnimating
    }
    fun shouldExpand():Boolean{
        return status == Status.EXPANDED
    }
    fun isExpandAnim():Boolean{
        return status == Status.EXPANDED && isAnimating
    }
    fun isCollapsed():Boolean{
        return status == Status.COLLAPSED && !isAnimating
    }
    fun isCollapsedAnim():Boolean{
        return status == Status.COLLAPSED && isAnimating
    }
    fun isNONE():Boolean{
        return resultStatus == ResultStatus.DEFAULT
    }

    enum class Status {
        EXPANDED,
        COLLAPSED
    }
    enum class ResultStatus {
        DEFAULT,
        EMPTY,
        LOAD,
        SHOW
    }

}

@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    searchStatus: SearchStatus,
    content: @Composable() (ColumnScope.() -> Unit)
){

    val density = LocalDensity.current

    Column(
        modifier = modifier
    ){
        Box(
            modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)
            .alpha(if (searchStatus.isCollapsed()) 1f else 0f)
            .onGloballyPositioned {
                with(density){
                    searchStatus.offsetY = it.parentLayoutCoordinates!!.positionInWindow().y.toDp()
                }
            }
            .pointerInput(Unit) {
                detectTapGestures {
                    searchStatus.status = Status.EXPANDED

                }
            }
        ){
            searchStatus.collapseBar()

        }
        AnimatedVisibility(
            searchStatus.isCollapsed() || searchStatus.isCollapsedAnim(),
            enter = fadeIn() + slideInVertically { -it/8 },
            exit = fadeOut() + slideOutVertically { -it/10  }
        ) {
            content()
        }

    }



}


@Composable
fun SearchPager(
    searchStatus: SearchStatus,
    defaultResult: @Composable ()-> Unit,
    result: LazyListScope.()-> Unit
){

    val systemBarsPadding =  WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
    val top by animateDpAsState(if (searchStatus.isExpand()) systemBarsPadding else searchStatus.offsetY){
        searchStatus.isAnimating = false
    }

    val backgroundAlpha by animateFloatAsState(if (searchStatus.isExpand()) 1f else 0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                colorScheme.background.copy(alpha = backgroundAlpha)
            )
            .semantics {
                onClick { false }
            }
            .then(
                if (!searchStatus.isCollapsed()) {
                    Modifier.pointerInput(Unit) {}
                } else {
                    Modifier
                }
            )

    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(top = top)
                .alpha(if (searchStatus.isExpand() || searchStatus.isCollapsedAnim()) 1f else 0f),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AnimatedVisibility (
                !searchStatus.isCollapsed(),
                modifier = Modifier.weight(1f),
                enter = fadeIn(initialAlpha = 1f),
                exit = fadeOut(targetAlpha = 1f)
            ){
                searchStatus.expandBar()
            }
            AnimatedVisibility(
                visible = searchStatus.isExpand()||searchStatus.isExpandAnim(),
                enter = expandHorizontally() + slideInHorizontally(initialOffsetX = { it }),
                exit = shrinkHorizontally() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                BackHandler(enabled = true) {
                    searchStatus.status = Status.COLLAPSED
                }
                Text(
                    text = "取消",
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 24.dp)
                        .clickable(
                            interactionSource = null,
                            enabled = searchStatus.isExpand(),
                            indication = null
                        ) {
                            searchStatus.status = Status.COLLAPSED
                        }
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        AnimatedVisibility(
            visible = searchStatus.isExpand(),
            modifier = Modifier.fillMaxSize(),
            enter = fadeIn(initialAlpha = 1f),
            exit = fadeOut(targetAlpha = 1f)
        ) {
            when(searchStatus.resultStatus){
                SearchStatus.ResultStatus.DEFAULT -> {
                    defaultResult()
                }
                SearchStatus.ResultStatus.EMPTY ->{

                }
                SearchStatus.ResultStatus.LOAD -> {
                    Box(
                        Modifier
                            .padding(vertical = 20.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Loading()
                    }
                }
                SearchStatus.ResultStatus.SHOW -> {
                    LazyColumn(
                        Modifier.fillMaxSize()
                    ){
                        this.result()
                        item("last") {
                            Spacer(Modifier.height(28.dp).navigationBarsPadding())
                        }

                    }
                }
            }
        }

    }

}
