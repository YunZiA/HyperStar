package com.yunzia.hyperstar.ui.pagers

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.currentRecomposeScope
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.isOS2Settings
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ListPopup
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.DropdownImpl
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun MainPager(
    navController: NavHostController,
    pagerState: PagerState,
) {
    val activity = LocalActivity.current as MainActivity
    val rebootStyle = activity.rebootStyle
    val currentPage = pagerState.currentPage

    val coroutineScope = rememberCoroutineScope()

    val hazeState = remember { HazeState() }
    val show = remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        bottomBar = {
            val items = listOf(
                NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
                NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
                NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
            )
            NavigationBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                items = items,
                selected = currentPage,
                onClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

        },
    ) { padding ->

        AppHorizontalPager(
            modifier = Modifier
                .height(getWindowSize().height.dp).padding(),
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding()) ,
            navController = navController,
            pagerState = pagerState,
            hazeState = hazeState,
            showReboot = show
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
    val recomposeScope = currentRecomposeScope


    val currentPage = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    val items = listOf(
        NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
        NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
    )

    val hazeState = remember { HazeState() }
    val show = remember { mutableStateOf(false) }


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


        AppHorizontalPager(
            navController = navController,
            modifier = Modifier.blur(hazeState).weight(1f).widthIn(min = 400.dp),
            pagerState = pagerState,
            hazeState = hazeState,
            showReboot = show
        )



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
            userScrollEnabled =  false,
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
                            val maxWidth = this.maxWidth
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
fun RebootPup(
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
                    show.value = false
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
                        show.value = false
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
            show.value = false
            rebootList.clear()
        }
    ) {

        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 18.dp)
                .fillMaxWidth()
                .clip(SmoothRoundedCornerShape(16.dp, 1f))
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
                    show.value = false
                    rebootList.clear()
                }

            )
            Spacer(Modifier.width(12.dp))
            BaseButton(
                text = stringResource(R.string.sure),
                modifier = Modifier.weight(1f),
                submit = true,
                onClick = {
                    show.value = false
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
    val isChecked = remember { mutableStateOf(false) }
    SuperCheckbox(
        title = title,
        checked = isChecked.value,
        checkboxLocation = CheckboxLocation.Right,
        onCheckedChange = {
            isChecked.value = it
            onCheckedChange(isChecked.value,type)


        }
    )

}

@Composable
fun AppHorizontalPager(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    pagerState: PagerState,
    hazeState: HazeState,
    showReboot: MutableState<Boolean>,
) {


    val context = LocalContext.current
    val activity = context as MainActivity

//    context.resources.getIdentifier()
//    context.resources.getColor()

    HorizontalPager(
        modifier = modifier,
        state = pagerState,
        contentPadding = PaddingValues(0.dp),
        beyondViewportPageCount = 3,
        userScrollEnabled = activity.enablePageUserScroll.value,
        pageContent = { page ->

            when (page) {

                0 ->{
                    Home(navController = navController,hazeState,contentPadding,showReboot,pagerState)
                }

                1 -> {
                    Settings(navController = navController,hazeState,contentPadding,showReboot,pagerState)
                }

                else -> {
                    ThirdPage(navController = navController,hazeState,contentPadding,showReboot,pagerState)
                }
            }


        }
    )
}