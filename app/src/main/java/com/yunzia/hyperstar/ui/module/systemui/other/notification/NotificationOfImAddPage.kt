package com.yunzia.hyperstar.ui.module.systemui.other.notification

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
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
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.rememberSearchStatus
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun NotificationOfImAddPage(
    expand: MutableState<Boolean>,
    unSelectApp: MutableState<List<NotificationInfo>>
){
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val selectedAppList = remember { mutableSetOf("") }
    val focusManager = LocalFocusManager.current
    val searchApp = remember {  mutableStateOf<List<NotificationInfo>>(emptyList()) }
    val coroutineScope = rememberCoroutineScope()

    val searchStatus = rememberSearchStatus(
        label = stringResource(R.string.app_name_type)
    )
    LaunchedEffect(searchStatus.searchText) {
        if (searchStatus.searchText == ""){
            searchStatus.resultStatus = SearchStatus.ResultStatus.DEFAULT
            searchApp.value = emptyList()
            return@LaunchedEffect

        }
        delay(300)
        coroutineScope.launch(
            Dispatchers.Default
        ) {
            searchStatus.resultStatus = SearchStatus.ResultStatus.LOAD
            searchApp.value = unSelectApp.value.asFlow()
                .filter { app -> app.appName.contains(searchStatus.searchText, ignoreCase = true) }
                .toList()
            searchStatus.resultStatus = if (
                searchApp.value.isEmpty()
            ){
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
                searchStatus.isCollapsed()||searchStatus.isCollapsedAnim(),
                enter = fadeIn(),
                exit = fadeOut(),
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
                            onClick = { expand.value = false }
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
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding() + 28.dp
                ),
                topAppBarScrollBehavior = topAppBarScrollBehavior
            ) {

                unSelectApp.value.forEach {
                    item {
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
    selectedAppList:MutableSet<String>
){
    val label = notificationInfo.appName
    val packageName = notificationInfo.packageName
    var isSelect by remember { mutableStateOf(false) }
    LaunchedEffect(isSelect) {
        if (isSelect){
            selectedAppList.add(packageName)
        }else{
            selectedAppList.remove(packageName)
        }
    }

    BasicComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(colorScheme.surfaceVariant)
        ,
        insideMargin =  PaddingValues(16.dp),
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