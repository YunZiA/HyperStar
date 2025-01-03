@file:OptIn(ExperimentalFoundationApi::class)

package com.yunzia.hyperstar.ui.pagers

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseButton
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.dialog.SuperCTDialogDefaults
import com.yunzia.hyperstar.ui.base.dialog.SuperXDialog
import com.yunzia.hyperstar.ui.base.dialog.SuperXPopupUtil.Companion.dismissXDialog
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.Utils
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.Box
import top.yukonga.miuix.kmp.basic.HorizontalPager
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Surface
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@OptIn(FlowPreview::class)
@Composable
fun MainPager(
    navController: NavHostController,
) {
    val topAppBarScrollBehavior0 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior1 = MiuixScrollBehavior(rememberTopAppBarState())
    val topAppBarScrollBehavior2 = MiuixScrollBehavior(rememberTopAppBarState())

    val topAppBarScrollBehaviorList = listOf(
        topAppBarScrollBehavior0, topAppBarScrollBehavior1, topAppBarScrollBehavior2
    )

    val pagerState = rememberPagerState(pageCount = { 3 })
    val targetPage = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()

    val currentScrollBehavior = when (targetPage) {
        0 -> topAppBarScrollBehaviorList[0]
        1 -> topAppBarScrollBehaviorList[1]
        else -> topAppBarScrollBehaviorList[2]
    }

    val items = listOf(

        NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
        NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
    )

    val pagerTitle = items[targetPage].label  //by remember { mutableStateOf(items[targetPage].label) }

//    LaunchedEffect(pagerState) {
//        snapshotFlow { pagerState.targetPage }
//            .debounce(0)
//            .collectLatest { currentPage ->
//                targetPage = currentPage
//            }
//    }

    val hazeState = remember { HazeState() }
    val enablePageUserScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("page_user_scroll",false)) }

    val show = remember { mutableStateOf(false) }

    val view = LocalView.current
    val showBlurs = remember { mutableStateOf(false) }

    XScaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        topBar = {
            TopAppBar(
                modifier = if (targetPage == 2 && !showBlurs.value) Modifier else Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = pagerTitle,
                largeTitle = if (targetPage == 2) "" else pagerTitle,
                scrollBehavior = currentScrollBehavior,
                actions = {

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
            NavigationBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                items = items,
                selected = targetPage,
                onClick = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )

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

    RebootDialog(show)

}

@Composable
fun  RebootDialog(
    show: MutableState<Boolean>
) {
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
                        Utils.rootShell("killall $i")
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