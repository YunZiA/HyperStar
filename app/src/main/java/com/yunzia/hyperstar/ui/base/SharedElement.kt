package com.yunzia.hyperstar.ui.base

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler

@Composable
fun rememberAnimStatus(): AnimStatus {
    val animStatus by remember { mutableStateOf(AnimStatus())  }
    var isInitialized by remember { mutableStateOf(false) }
    LaunchedEffect(animStatus.status) {
        if (!isInitialized) {
            isInitialized = true
            return@LaunchedEffect
        }
        animStatus.isAnimating = true
    }

    return animStatus
}

class AnimStatus{
    var status by mutableStateOf(Status.COLLAPSED)
    var isAnimating by mutableStateOf(false)
    var offsetY by mutableFloatStateOf( 0f)

    fun isExpand():Boolean{
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
    enum class Status {
        EXPANDED,
        COLLAPSED
    }

}

@Composable
fun SearchBox(
    modifier: Modifier = Modifier,
    animStatus: AnimStatus,
    searchBar:@Composable ()->Unit,
    content: @Composable() (ColumnScope.() -> Unit)
){

    Column(
        modifier = modifier
    ){
        AnimatedVisibility(
            animStatus.isCollapsed()||animStatus.isCollapsedAnim(),
            enter = expandVertically(expandFrom = Alignment.Top),
            exit = shrinkVertically(shrinkTowards = Alignment.Top)
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .alpha(if (animStatus.isCollapsed()) 1f else 0f)
                    .pointerInput(Unit) {
                        animStatus.status = AnimStatus.Status.EXPANDED
                    }
                    .onGloballyPositioned {
                        animStatus.offsetY = it.parentLayoutCoordinates!!.positionInWindow().y
                    }
            ){
                searchBar()
            }

        }
        content()

    }



}


@Composable
fun SearchPager(
    animStatus: AnimStatus,
    searchBar:@Composable ()->Unit,
){

    val systemBarsPadding = with(LocalDensity.current) { WindowInsets.systemBars.asPaddingValues().calculateTopPadding().toPx() }
    val y by animateFloatAsState(if (animStatus.isExpand()) systemBarsPadding else animStatus.offsetY){
        animStatus.isAnimating = false
    }

    val backgroundAlpha by animateFloatAsState(if (animStatus.isExpand()) 1f else 0f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                colorScheme.background.copy(alpha = backgroundAlpha)
            )
            .then(
                if (!animStatus.isCollapsed()) {
                    Modifier.pointerInput(Unit) {}
                } else {
                    Modifier
                }
            )

    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .alpha(if (animStatus.isExpand()||animStatus.isCollapsedAnim()) 1f else 0f)
                .offset {
                    IntOffset(0, y.toInt())
                },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(Modifier.weight(1f)) {
                searchBar()
            }
            AnimatedVisibility(
                visible = animStatus.isExpand()||animStatus.isExpandAnim(),
                enter = expandHorizontally() + slideInHorizontally(initialOffsetX = { it }),
                exit = shrinkHorizontally() + slideOutHorizontally(targetOffsetX = { it })
            ) {
                BackHandler(enabled = true) {
                    animStatus.status = AnimStatus.Status.COLLAPSED
                }
                Text(
                    text = "取消",
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .padding(start = 4.dp, end = 24.dp)
                        .pointerInput(Unit) {
                            detectTapGestures{
                                animStatus.status = AnimStatus.Status.COLLAPSED
                            }
                        }
                )
            }
        }



    }


}
