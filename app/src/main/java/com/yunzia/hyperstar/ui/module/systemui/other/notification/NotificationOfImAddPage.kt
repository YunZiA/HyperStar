package com.yunzia.hyperstar.ui.module.systemui.other.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateSet
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.SearchBox
import com.yunzia.hyperstar.ui.base.SearchPager
import com.yunzia.hyperstar.ui.base.SearchStatus
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.rememberSearchStatus
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun NotificationOfImAddPage(
    expand: MutableState<Boolean>,
    selectApp: SnapshotStateSet<NotificationInfo>,
    unSelectApp: SnapshotStateSet<NotificationInfo>
){
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val selectedAppList = remember { mutableSetOf<NotificationInfo>() }
    val searchStatus = rememberSearchStatus(
        label = stringResource(R.string.app_name_type)
    )
    val searchApp = remember(unSelectApp, searchStatus.searchText) {
        derivedStateOf {
            unSelectApp.filter { it.appName.contains(searchStatus.searchText, ignoreCase = true) }
        }
    }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(searchStatus.searchText) {
        if (searchStatus.searchText == ""){
            searchStatus.resultStatus = SearchStatus.ResultStatus.DEFAULT
            return@LaunchedEffect

        }
        delay(300)
        coroutineScope.launch(
            Dispatchers.Default
        ) {
            searchStatus.resultStatus = SearchStatus.ResultStatus.LOAD

            searchStatus.resultStatus = if (searchApp.value.isEmpty()){
                SearchStatus.ResultStatus.EMPTY
            }else{
                SearchStatus.ResultStatus.SHOW
            }
        }

    }


    XScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        popupHost = { },
        topBar = {
            AnimatedVisibility(
                searchStatus.isCollapsed()||searchStatus.isAnimatingCollapse(),
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
            ) {
                TopAppBar(
                    modifier = Modifier.showBlur(hazeState),
                    color = Color.Transparent,
                    title = "应用选择",
                    scrollBehavior = topAppBarScrollBehavior,
                    navigationIcon = {
                        TopButton(
                            modifier = Modifier.padding(start = 18.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                            contentDescription = "close",
                            onClick = { expand.value = false }

                        )


                    },
                    actions = {
                        TopButton(
                            modifier = Modifier.padding(end = 18.dp),
                            imageVector = ImageVector.vectorResource(R.drawable.ic_done),
                            contentDescription = "done",
                            onClick = {
                                expand.value = false
                                if (selectedAppList.isNotEmpty()){
                                    selectApp.addAll(selectedAppList)
                                    selectedAppList.clear()
                                }
                            }
                        )
                    }
                )

            }
        }
    ){ padding->

        SearchBox(
            modifier = Modifier
                .blur(hazeState)
                .padding(top = padding.calculateTopPadding() + 14.dp)
                .fillMaxSize(),
            searchStatus,
        ){
            LazyColumn(
                modifier = Modifier.fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding() + 28.dp
                )
            ) {

                unSelectApp.forEach {
                    item(it.packageName) {
                        AppNotifItem(it,selectedAppList)
                    }
                }

            }

        }

    }

    SearchPager(
        searchStatus,
        {}
    ) {
        searchApp.value.forEach {
            item(it.packageName) {
                AppNotifItem(it,selectedAppList)
            }
        }
    }


}

@Composable
private fun AppNotifItem(
    notificationInfo: NotificationInfo,
    selectedAppList:MutableSet<NotificationInfo>
){
    val label = notificationInfo.appName
    var isSelect by remember { mutableStateOf(selectedAppList.contains(notificationInfo)) }
    LaunchedEffect(isSelect) {
        if (isSelect){
            selectedAppList.add(notificationInfo)
        }else{
            selectedAppList.remove(notificationInfo)
        }
    }

    BasicComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(if (isSelect) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
        ,
        insideMargin =  PaddingValues(17.dp),
        title = label,
        leftAction = {
            Box(
                modifier = Modifier.padding(end = 12.dp)
            ){
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = DrawablePainter(notificationInfo.icon),
                    contentDescription = label
                )

            }
        },
        rightActions = {
            Checkbox(
                checked = isSelect,
                onCheckedChange = {
                    isSelect = !isSelect
                }
            )

        },
        onClick = {
            isSelect = !isSelect
        }
    )


}