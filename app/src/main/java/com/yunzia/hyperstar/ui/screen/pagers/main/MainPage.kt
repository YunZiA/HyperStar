package com.yunzia.hyperstar.ui.screen.pagers.main

import android.annotation.SuppressLint
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.BaseButton
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.getSettingChannel
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.basic.ListPopupColumn
import top.yukonga.miuix.kmp.basic.ListPopupDefaults
import top.yukonga.miuix.kmp.basic.NavigationBar
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.PopupPositionProvider
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.VerticalDivider
import top.yukonga.miuix.kmp.preference.CheckboxLocation
import top.yukonga.miuix.kmp.preference.CheckboxPreference
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.LocalMainPagerState
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.DropdownImpl
import top.yukonga.miuix.kmp.basic.NavigationBarItem
import top.yukonga.miuix.kmp.blur.LayerBackdrop
import top.yukonga.miuix.kmp.overlay.OverlayDialog
import top.yukonga.miuix.kmp.window.WindowListPopup
import top.yukonga.miuix.kmp.utils.Platform
import top.yukonga.miuix.kmp.utils.platform

@Composable
fun mainPagerItems(): List<NavigationItem> {
    return listOf(
        NavigationItem(stringResource(R.string.main_page_title), ImageVector.vectorResource(id = R.drawable.home)),
        NavigationItem(stringResource(R.string.settings_page_title), ImageVector.vectorResource(id = R.drawable.setting)),
        NavigationItem(stringResource(R.string.about_page_title), ImageVector.vectorResource(id = R.drawable.about)),
    )
}

@Composable
fun MainPagerInLand(
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = LocalMainPagerState.current
    val backdrop = rememberLayerBackdrop()

    Row(
        modifier = modifier
    ) {
        NavigationBarForStart(
            modifier = Modifier
                .zIndex(2f)
                .widthIn(max = 130.dp),
            items = mainPagerItems(),
            selected = pagerState.targetPage,
            onClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )
        MainPageContent(
            backdrop = backdrop,
            modifier = Modifier.widthIn(min = 400.dp)
        )

    }
    VerticalDivider()
}


@Composable
fun MainPager() {
    val activity = LocalActivity.current as MainActivity
    val pagerState = LocalMainPagerState.current
    val coroutineScope = rememberCoroutineScope()
    val backdrop = rememberLayerBackdrop()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        popupHost = { },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.showBlur(backdrop),
                color = Color.Transparent
            ) {
                mainPagerItems().forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                  },
                        icon = item.icon,
                        label = item.label,
                    )

                }
            }

        },
    ) { padding ->
        MainPageContent(
            backdrop = backdrop,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        )
//

    }
}

@Composable
fun NavigationBarForStart(
    items: List<NavigationItem>,
    selected: Int,
    onClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    color: Color = colorScheme.surface,
) {

    val captionBarPaddings = WindowInsets.captionBar.only(WindowInsetsSides.Bottom).asPaddingValues()
    val captionBarBottomPaddingValue = captionBarPaddings.calculateBottomPadding()

    val animatedCaptionBarHeight by animateDpAsState(
        targetValue = if (captionBarBottomPaddingValue > 0.dp) captionBarBottomPaddingValue else 0.dp,
        animationSpec = tween(durationMillis = 300),
    )
    val cutoutPaddingValues = WindowInsets.displayCutout.asPaddingValues()
    val layoutDirection = LocalLayoutDirection.current
    val sidePadding = if (layoutDirection == LayoutDirection.Ltr) {
        cutoutPaddingValues.calculateLeftPadding(layoutDirection)
    } else {
        cutoutPaddingValues.calculateRightPadding(layoutDirection)
    }

    val currentOnClick by rememberUpdatedState(onClick)
    Row(
        modifier = modifier
            .fillMaxHeight()
            .background(color),
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxHeight()
                .width(sidePadding)
                .pointerInput(Unit) { detectTapGestures { /* Do nothing to consume the click */ } },
        )

        Column(
            modifier = Modifier.fillMaxHeight(),
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            val platform = platform()
            val itemHeight = if (platform != Platform.IOS) 64.dp else 48.dp
            val itemWeight = 1f / items.size

            items.forEachIndexed { index, item ->
                val isSelected = selected == index
                var isPressed by remember { mutableStateOf(false) }

                val onSurfaceContainerColor = colorScheme.onSurfaceContainer
                val onSurfaceContainerVariantColor = colorScheme.onSurfaceContainerVariant

                val tint = when {
                    isPressed -> if (isSelected) {
                        onSurfaceContainerColor.copy(alpha = 0.6f)
                    } else {
                        onSurfaceContainerVariantColor.copy(alpha = 0.6f)
                    }

                    isSelected -> onSurfaceContainerColor

                    else -> onSurfaceContainerVariantColor
                }
                val fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal

                Column(
                    modifier = Modifier
                        .size(itemHeight)
                        .pointerInput(index) {
                            detectTapGestures(
                                onPress = {
                                    isPressed = true
                                    tryAwaitRelease()
                                    isPressed = false
                                },
                                onTap = { currentOnClick(index) },
                            )
                        },
                    horizontalAlignment = CenterHorizontally,
                ) {
                    Image(
                        modifier = Modifier
                            .size(32.dp)
                            .padding(top = 6.dp, bottom = 6.dp),
                        imageVector = item.icon,
                        contentDescription = item.label,
                        colorFilter = ColorFilter.tint(tint),
                    )
                    Text(
                        modifier = Modifier.padding(bottom = 12.dp ),
                        text = item.label,
                        color = tint,
                        textAlign = TextAlign.Center,
                        fontSize = 12.sp,
                        fontWeight = fontWeight,
                    )
                }
            }
        }
        VerticalDivider()
    }

}

@Composable
fun RebootPup(
    show: MutableState<Boolean>
) {

    val os2 = getSettingChannel() > 1
    val optionSize = if (os2) 2 else 1


    WindowListPopup(
        show = show.value,
        popupPositionProvider = ListPopupDefaults.ContextMenuPositionProvider,
        alignment = PopupPositionProvider.Align.TopEnd,
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
fun RebootDialog(
    show: MutableState<Boolean>
) {
    val rebootList = remember { mutableStateListOf<String>() }
    OverlayDialog(
        title = stringResource(R.string.fast_reboot),
        show = show.value,
        onDismissRequest = {
            show.value = false
            rebootList.clear()
        }
    ) {

        Column(
            modifier = Modifier
                .padding(top = 8.dp, bottom = 18.dp)
                .fillMaxWidth()
                .clip(SmoothRoundedCornerShape(16.dp))
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
            if (getSettingChannel() > 1){
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
fun Item(
    title: String,
    type:String,
    onCheckedChange: (Boolean,String) -> Unit
) {
    val isChecked = remember { mutableStateOf(false) }
    CheckboxPreference(
        title = title,
        checked = isChecked.value,
        checkboxLocation = CheckboxLocation.End,
        onCheckedChange = {
            isChecked.value = it
            onCheckedChange(isChecked.value,type)
        }
    )

}

@Composable
fun MainPageContent(
    backdrop: LayerBackdrop,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    val activity = LocalActivity.current as MainActivity
    val mainPagerState = LocalMainPagerState.current

    HorizontalPager(
        modifier = modifier.blur(backdrop),
        state = mainPagerState,
        contentPadding = PaddingValues(0.dp),
        beyondViewportPageCount = 1,
        userScrollEnabled = activity.enablePageUserScroll.value,
        pageContent = { page ->
            when (page) {
                0 -> Home(contentPadding)
                1 -> Settings(contentPadding)
                else -> ThirdPage(contentPadding)
            }
        }
    )
}