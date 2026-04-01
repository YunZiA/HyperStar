package com.yunzia.hyperstar.ui.screen.pagers.main

import android.annotation.SuppressLint
import android.graphics.Typeface
import android.util.Log
import android.util.TypedValue
import android.view.HapticFeedbackConstants
import android.widget.TextView
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import com.kyant.backdrop.backdrops.layerBackdrop
import com.kyant.backdrop.backdrops.rememberBackdrop
import com.kyant.backdrop.drawBackdrop
import com.yunzia.hyperstar.LocalMainPagerState
import com.yunzia.hyperstar.LocalRebootDialogState
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.blend.BlendedBlurPainter
import com.yunzia.hyperstar.ui.component.SuperIntentArrow
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.screen.pagers.main.about.BgEffectBackground
import com.yunzia.hyperstar.ui.screen.pagers.main.about.UpdaterButton
import com.yunzia.hyperstar.utils.getVerName
import dev.chrisbanes.haze.rememberHazeState
import kotlinx.coroutines.flow.onEach
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import yunzia.utils.MiBlurUtilsKt.addMiBackgroundBlendColor
import yunzia.utils.MiBlurUtilsKt.clearMiBackgroundBlendColor
import yunzia.utils.MiBlurUtilsKt.setMiBackgroundBlurMode
import yunzia.utils.MiBlurUtilsKt.setMiBackgroundBlurRadius
import yunzia.utils.MiBlurUtilsKt.setMiViewBlurMode
import kotlin.math.log


private fun getColorList(
    colorMode: Boolean
):  List<Int>{
    return if (colorMode) {
        listOf(
            "#e6a1a1a1".toColorInt(),
            "#4de6e6e6".toColorInt(),
            "#1af500".toColorInt()
        )
    } else {
        listOf(
            "#cc4a4a4a".toColorInt(),
            "#4f4f4f".toColorInt(),
            "#1af200".toColorInt()
        )
    }
}

private fun getModeList(
    colorMode: Boolean
):  List<Int>{
    return listOf(
        if (colorMode) {
            18
        } else {
            19
        },
        100,
        106
    )

}

fun extractOnlyNumbers(input: String): String {
    // 使用 filter 仅保留数字字符
    return input.filter { it.isDigit() }
}

@SuppressLint("RestrictedApi", "SetTextI18n")
@Composable
fun ThirdPage(
    contentPadding: PaddingValues
) {
    val hazeState = rememberHazeState()
    val pagerState = LocalMainPagerState.current
    val showReboot = LocalRebootDialogState.current

    val navController = LocalNavigator.current
    val context = LocalContext.current
    val activity = LocalActivity.current as MainActivity
    val isNeedUpdate = remember { mutableStateOf(false) }

    val view = LocalView.current
    val rebootStyle = activity.rebootStyle
    val showBlurs = remember { mutableStateOf(false) }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    val paddingTop = animateDpAsState(
        targetValue = if (isNeedUpdate.value) 28.dp else 0.dp,
        animationSpec = tween(750, easing = LinearOutSlowInEasing)
    )

    val density = LocalDensity.current
    val min = with(density) { 0.dp.toPx() }
    val but = remember(isNeedUpdate.value) {
        derivedStateOf {
            if (isNeedUpdate.value){
                with(density) { 60.dp.toPx() }
            }else{
                min
            }
        }
    }
    val sec = with(density) { 155.dp.toPx() }
    val secHeight = remember(but.value) {
        derivedStateOf {
            sec-but.value
        }
    }
    val main = with(density) { 205.dp.toPx() }
    val mainHeight = main-sec

    val bgHeight = with(density) { 320.dp.toPx() }

    val bgAlpha = remember { mutableFloatStateOf(1f) }
    val buttonAlpha = remember { mutableFloatStateOf(1f) }
    val buttonScale = remember { mutableFloatStateOf(1f) }

    val mainAlpha = remember { mutableFloatStateOf(1f) }
    val mainScale = remember { mutableFloatStateOf(1f) }

    val secAlpha = remember { mutableFloatStateOf(1f) }
    val secScale = remember { mutableFloatStateOf(1f) }

    val scroll = rememberLazyListState()

    Scaffold(
        modifier = Modifier,
        popupHost = { },
        topBar = {
            SmallTopAppBar(
                modifier = if (showBlurs.value) Modifier.showBlur(hazeState) else Modifier,
                color = Color.Transparent,
                title = if (showBlurs.value) stringResource(R.string.about_page_title) else "",
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1 && pagerState.currentPage == 2){
                        RebootPup(showReboot)
                    }
                    IconButton(
                        modifier = Modifier.padding(end = 12.dp),
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showReboot.value = true
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = "restart",
                            tint = colorScheme.onBackground
                        )

                    }
                }
            )


        }
    ) { padding ->

        val currentVer = getVerName(context)

        LaunchedEffect(pagerState.targetPage,activity.newAppVersion) {
            if (pagerState.targetPage != 2){
                isNeedUpdate.value = false
                return@LaunchedEffect
            }
            if(activity.newAppVersion.value == "") return@LaunchedEffect
            val currentVersion = extractOnlyNumbers(currentVer)
            val newVersion = extractOnlyNumbers(activity.newAppVersion.value)
            Log.d("ggc", "ThirdPage: currentVersion = $currentVersion newVersion = $newVersion")

            // 比较版本号
            isNeedUpdate.value = currentVersion < newVersion
//            isNeedUpdate.value = false
        }

        val scrollProgress = remember { mutableFloatStateOf(0f) }

        LaunchedEffect(scroll, isNeedUpdate.value) {
            snapshotFlow { scroll.firstVisibleItemScrollOffset }
                .onEach { offset ->
                    if (scroll.firstVisibleItemIndex > 0) {
                        // 检查当前值是否已经是 0f，如果不是才更新
                        if (bgAlpha.floatValue != 0f) bgAlpha.floatValue = 0f
                        if (secAlpha.floatValue != 0f) secAlpha.floatValue = 0f
                        if (mainAlpha.floatValue != 0f) mainAlpha.floatValue = 0f
                        if (buttonAlpha.floatValue != 0f) buttonAlpha.floatValue = 0f
                        // showBlurs.value = true // 如果 showBlurs 也有类似的需求，也要加上检查
                        if (showBlurs.value != true) showBlurs.value = true
                        return@onEach // 提前返回，不执行下面的计算
                    }
                    // 计算新值
                    val calculatedBgAlpha = ((bgHeight - offset.toFloat().coerceIn(min, bgHeight)) / bgHeight).coerceIn(0f, 1f)
                    val calculatedButtonValue = ((but.value - offset.toFloat().coerceIn(min, but.value)) / but.value).coerceIn(0f, 1f)
                    val calculatedSecValue = ((sec - offset.toFloat().coerceIn(but.value, sec)) / secHeight.value).coerceIn(0f, 1f)
                    val calculatedMainValue = ((main - offset.toFloat().coerceIn(sec, main)) / mainHeight).coerceIn(0f, 1f)

                    val calculatedButtonScale = lerp(0.985f, 1f, calculatedButtonValue)
                    val calculatedSecScale = lerp(0.9f, 1f, calculatedSecValue)
                    val calculatedMainScale = lerp(0.9f, 1f, calculatedMainValue)
                    val calculatedShowBlurs = calculatedBgAlpha == 0f

                    // 只有在值确实改变时才更新状态
                    if (bgAlpha.floatValue != calculatedBgAlpha) bgAlpha.floatValue = calculatedBgAlpha
                    if (buttonAlpha.floatValue != calculatedButtonValue) buttonAlpha.floatValue = calculatedButtonValue
                    if (buttonScale.floatValue != calculatedButtonScale) buttonScale.floatValue = calculatedButtonScale
                    if (mainAlpha.floatValue != calculatedMainValue) mainAlpha.floatValue = calculatedMainValue
                    if (mainScale.floatValue != calculatedMainScale) mainScale.floatValue = calculatedMainScale
                    if (secAlpha.floatValue != calculatedSecValue) secAlpha.floatValue = calculatedSecValue
                    if (secScale.floatValue != calculatedSecScale) secScale.floatValue = calculatedSecScale
                    if (showBlurs.value != calculatedShowBlurs) showBlurs.value = calculatedShowBlurs
                }
                .collect{}
        }

        val density = LocalDensity.current.density

        BgEffectBackground(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    alpha = bgAlpha.floatValue
                }
        ) {textBrush, versionSize, position->
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(bottom = paddingTop.value),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                textBrush.value?.let {
                    Text(
                        text = "HyperStar " + currentVer.take(3),
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = mainAlpha.floatValue * bgAlpha.floatValue
                                scaleX = mainScale.floatValue
                                scaleY = mainScale.floatValue
                            }
                            .onPlaced { layoutCoordinates ->
                                versionSize.value = layoutCoordinates.size
                                position.value = layoutCoordinates.positionInWindow()
                            },
                        fontSize = 42.sp,
                        fontWeight = FontWeight(570),
                        style = TextStyle(
                            brush = it,
                        )
                    )
                }
                Text(
                    text = stringResource(R.string.xposed_desc),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .graphicsLayer {
                            alpha = secAlpha.floatValue * bgAlpha.floatValue
                            scaleX = secScale.floatValue
                            scaleY = secScale.floatValue
                        }
                        .padding(top = 20.dp),
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center
                )
            }
        }
        Box(
            Modifier.blur(hazeState)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                flingBehavior = ScrollableDefaults.flingBehavior(),
                state = scroll,
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding() + 12.dp,
                    bottom = contentPadding.calculateBottomPadding() + 12.dp
                ),
            ) {
                item {
                    Box(
                        Modifier
                            .height(411.dp)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures() {
                                    navController.navigate(MainRoutes.Updater)
                                }
                            }
                    )
                }
                itemGroup{
                    SuperIntentArrow(
                        leftIcon = R.drawable.dd,
                        title = "东东说他舍不得",
                        summary = "@YunZiA | Hook",
                        context = context,
                        url = "https://www.coolapk.com/u/8555749"
                    )
                    SuperNavHostArrow(
                        title = stringResource(R.string.translator),
                        navController = navController,
                        route = MainRoutes.Translator
                    )
                }
                itemGroup(
                    title = R.string.discussion_title
                ) {
                    SuperIntentArrow(
                        title = stringResource(R.string.qq_group_title),
                        context = context,
                        url = arrayOf(
                            "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=810317966&card_type=group&source=qrcode",
                            "https://qun.qq.com/universal-share/share?ac=1&authKey=CbXnHNd3K1gLoGTuOqSLYr73q4HCNOgU9t0tmN721ymjb2O3eeF39Zbk8DIbw%2FFE&busi_data=eyJncm91cENvZGUiOiI4MTAzMTc5NjYiLCJ0b2tlbiI6IktiVllNVmNHaGQzUkROdnNBZWZiSnJ5UEhHaktrbDQ4YnUwZWgyMHZKTzUraUxMWG5admNIRGVnK3VXMjduNk0iLCJ1aW4iOiIyNTc0ODIyMDM2In0%3D&data=KY2pZd46IRdUjB_2u59hLvCtAAuyIwLVpg2MtBxASJA1KAiQVJhg0DN1mTb7podfRAZITmpscrKqZtghQgHXyQ&svctype=4&tempid=h5_group_info"
                        )
                    )
                    SuperIntentArrow(
                        title = stringResource(R.string.telegram_channel),
                        context = context,
                        url = "https://t.me/HyperStar_release"
                    )
                    SuperIntentArrow(
                        title = stringResource(R.string.telegram_group),
                        context = context,
                        url = "https://t.me/Hyperstar_chat"
                    )
                }
                itemGroup(
                    title = R.string.others
                ) {
                    SuperNavHostArrow(
                        title = stringResource(R.string.references_title),
                        navController = navController,
                        route = MainRoutes.References
                    )
                    SuperIntentArrow(
                        title = stringResource(R.string.project_address),
                        summary = stringResource(R.string.open_source_statement),
                        context = context,
                        url = "https://github.com/YunZiA/HyperStar"
                    )
                    SuperNavHostArrow(
                        title = stringResource(R.string.donation),
                        navController = navController,
                        route = MainRoutes.Donation
                    )
                }
            }

            if (isNeedUpdate.value && buttonAlpha.floatValue != 0.0f){
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(450.dp)
                        .graphicsLayer {
                            alpha = buttonAlpha.floatValue
                            scaleX = buttonScale.floatValue
                            scaleY = buttonScale.floatValue
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    UpdaterButton(navController = navController)
                }
            }
        }
    }
}


