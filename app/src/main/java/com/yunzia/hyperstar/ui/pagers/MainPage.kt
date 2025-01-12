package com.yunzia.hyperstar.ui.pagers

import android.view.HapticFeedbackConstants
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseButton
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.isOS2Settings
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.MiuixPopupUtil.Companion.dismissPopup
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun MainPager(
    navController: NavHostController,
    pagerState: PagerState,
) {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )

    val coroutineScope = rememberCoroutineScope()

    val currentPage = pagerState.currentPage

    val currentScrollBehavior = when (currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        else -> topAppBarScrollBehaviorList[2]
    }

    val items = listOf(

        NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
        NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
    )

    val pagerTitle = items[currentPage].label

    val hazeState = remember { HazeState() }

    val show = remember { mutableStateOf(false) }

    val view = LocalView.current
    val showBlurs = remember { mutableStateOf(true) }

    val context = LocalContext.current
    val activity = context as MainActivity
    val rebootStyle = activity.rebootStyle
    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            TopAppBar(
                modifier = if (currentPage == 2 && showBlurs.value) Modifier else  Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = pagerTitle,
                largeTitle = if (currentPage == 2) "" else pagerTitle,
                scrollBehavior = currentScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1){
                        RebootPup(show)

                    }
                    IconButton(
                        modifier = Modifier.padding(end = 12.dp),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            show.value = true
                        }) {

                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "restart",
                            tint = colorScheme.onBackground)

                    }
                }
            )


        },
        bottomBar = {
            Box(
                modifier = Modifier.showBlur(hazeState),

            ) {
                NavigationBar(
                    color = Color.Transparent,
                    items = items,
                    selected = currentPage,
                    onClick = { index ->
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    }
                )

            }

        },
    ) { padding ->
        AppHorizontalPager(
            modifier = Modifier.blur(hazeState),
            navController = navController,
            pagerState = pagerState,
            topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
            padding = padding,
            showBlurs = showBlurs
        )


    }

    if (rebootStyle.intValue == 0){
        RebootDialog(show)

    }


}

@Composable
fun MainPagerByThree(
    navController: NavHostController,
    pagerState: PagerState,
) {
    val context = LocalContext.current
    val activity = context as MainActivity
    val rebootStyle = activity.rebootStyle

    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )


    val currentPage = pagerState.currentPage

    val coroutineScope = rememberCoroutineScope()

    val currentScrollBehavior = when (currentPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        else -> topAppBarScrollBehaviorList[2]
    }

    val items = listOf(

        NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
        NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
    )

    val pagerTitle = items[currentPage].label

    val hazeState = remember { HazeState() }

    val show = remember { mutableStateOf(false) }

    val view = LocalView.current
    val showBlurs = remember { mutableStateOf(false) }

    Row {

        NavigationBarForStart(
            modifier = Modifier.weight(0.25f).widthIn(max = 130.dp),
            items = items,
            selected = currentPage,
            onClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }

        )
        VerticalDivider(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 12.dp)
                .width(0.75.dp),
            color = colorScheme.dividerLine
        )

        Scaffold(
            modifier = Modifier.weight(1f).widthIn(min = 400.dp),
            popupHost = { },
            contentWindowInsets = WindowInsets.navigationBars,
            topBar = {
                TopAppBar(
                    modifier = if (currentPage == 2 && !showBlurs.value) Modifier else Modifier.showBlur(
                        hazeState
                    ),
                    color = Color.Transparent,
                    title = pagerTitle,
                    largeTitle = if (currentPage == 2) "" else pagerTitle,
                    scrollBehavior = currentScrollBehavior,
                    actions = {
                        if (rebootStyle.intValue == 1){
                            RebootPup(show)

                        }

                        IconButton(

                            modifier = Modifier.padding(end = 12.dp),
                            onClick = {
                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                show.value = true
                            }) {

                            Icon(
                                imageVector = Icons.Default.MoreVert,
                                contentDescription = "restart",
                                tint = colorScheme.onBackground)

                        }
                    }
                )


            }
        ) { padding ->
            AppHorizontalPager(
                modifier = Modifier.blur(hazeState),
                navController = navController,
                pagerState = pagerState,
                topAppBarScrollBehaviorList = topAppBarScrollBehaviorList,
                padding = padding,
                showBlurs = showBlurs
            )


        }


    }
    if (rebootStyle.intValue == 0){
        RebootDialog(show)

    }


}

@Composable
fun NavigationBarForStart(
    items: List<NavigationItem>,
    selected: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier,
        popupHost= { },
    ){
        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp),
            isEnabledOverScroll = { false },
            contentPadding = PaddingValues(top = it.calculateTopPadding()+13.dp, bottom = it.calculateBottomPadding()),
        ) {

            items.forEachIndexed { index, item ->
                val isSelected = selected == index

                item {

                    val bgColor by animateColorAsState(
                        targetValue = if (isSelected) {
                            Color.Black.copy(alpha = 0.1f)
                        } else {
                            Color.Transparent
                        }, label = ""
                    )
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        BoxWithConstraints(
                            Modifier
                                .padding(bottom = 5.dp)
                                .background(bgColor, SmoothRoundedCornerShape(8.dp, 0.5f))
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onTap = {
                                            onClick.invoke(index)
                                        }
                                    )
                                }
                        ) {
                            val maxWidth = maxWidth
                            Row(
                                Modifier.padding(horizontal = if(maxWidth > 90.dp) 16.dp else 12.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.label,
                                    modifier = Modifier.size(24.dp),
                                    tint = colorScheme.onBackground
                                )
                                if(maxWidth > 90.dp){
                                    Text(
                                        text = item.label,
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 8.dp),
                                        fontSize = 15.sp,
                                        maxLines = 1,
                                        fontWeight = FontWeight(550)

                                    )

                                }
                            }

                        }

                    }

                }

            }
        }

    }
}

@Composable
fun  RebootPup(
    show: MutableState<Boolean>
) {

    val os2 = isOS2Settings()
    val optionSize = if (os2) 2 else 1


    ListPopup(
        show = show,
        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
        alignment = PopupPositionProvider.Align.TopRight,
        onDismissRequest = {
            show.value = false
        }
    ) {
        ListPopupColumn {
            DropdownImpl(
                text = stringResource(id = R.string.systemui),
                optionSize = optionSize,
                isSelected = false,
                index = 0,
                onSelectedIndexChange = {
                    Helper.rootShell("killall com.android.systemui")
                    dismissPopup(show)
                }
            )
            if (os2){
                DropdownImpl(
                    text = stringResource(id = R.string.home),
                    optionSize = optionSize,
                    isSelected = false,
                    index = 1,
                    onSelectedIndexChange = {
                        Helper.rootShell("killall com.miui.home")
                        dismissPopup(show)
                    }
                )

            }

        }
    }


}

@Composable
fun  RebootDialog(
    show: MutableState<Boolean>
) {
    val os2 = isOS2Settings()
    val rebootList = remember { mutableStateListOf<String>() }
    SuperXDialog(
        title = stringResource(R.string.fast_reboot),
        show = show,
        onDismissRequest = {
            dismissXDialog(show)
            rebootList.clear()
        }
    ) {

        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 18.dp)
                .fillMaxWidth()
                .clip(SmoothRoundedCornerShape(12.dp, 0.5f))
                .background(colorScheme.secondaryContainer)
        ) {
            Item(
                title = stringResource(id = R.string.systemui),
                type = "com.android.systemui"
            ){ checked,it->
                if (checked){
                    rebootList.add(it)
                }else{
                    rebootList.remove(it)
                }

            }
            if (os2){
                Item(
                    title = stringResource(id = R.string.home),
                    type = "com.miui.home"
                ){ checked,it->
                    if (checked){
                        rebootList.add(it)
                    }else{
                        rebootList.remove(it)
                    }

                }

            }
        }

        Row {
            BaseButton(
                text = stringResource(R.string.cancel),
                modifier = Modifier.weight(1f),
                onClick = {
                    dismissXDialog(show)
                    rebootList.clear()
                }

            )
            Spacer(Modifier.width(12.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    dismissXDialog(show)
                    if (rebootList.isEmpty()) return@BaseButton
                    for(i in rebootList){
                        Helper.rootShell("killall $i")
                    }

                }

            )

        }

    }
}
@Composable
fun  Item(
    title: String,
    type:String,
    onCheckedChange: (Boolean,String) -> Unit
) {
    val ischecked = remember { mutableStateOf(false) }
    SuperCheckbox(
        title = title,
        checked = ischecked.value,
        checkboxLocation = CheckboxLocation.Right,
        onCheckedChange = {
            ischecked.value = it
            onCheckedChange(ischecked.value,type)


        }
    )

}

@Composable
fun AppHorizontalPager(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    pagerState: PagerState,
    topAppBarScrollBehaviorList: List<ScrollBehavior>,
    padding: PaddingValues,
    showBlurs: MutableState<Boolean>
) {


    val context = LocalContext.current
    val activity = context as MainActivity

    HorizontalPager(
        modifier = modifier,
        pagerState = pagerState,
        userScrollEnabled = activity.enablePageUserScroll.value,
        pageContent = { page ->

            when (page) {

                0 ->{
                    Home(
                        navController = navController,
                        topAppBarScrollBehavior = topAppBarScrollBehaviorList[0],
                        padding = padding
                    )

                }

                1 -> {
                    Settings(
                        navController = navController,
                        topAppBarScrollBehavior = topAppBarScrollBehaviorList[1],
                        padding = padding

                    )
                }

                else -> {
                    ThirdPage(
                        showBlurs = showBlurs,
                        navController = navController,
                        topAppBarScrollBehavior = topAppBarScrollBehaviorList[2],
                        padding = padding,
                    )
                }
            }
        }
    )
}