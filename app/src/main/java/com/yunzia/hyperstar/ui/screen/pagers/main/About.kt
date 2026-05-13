package com.yunzia.hyperstar.ui.screen.pagers.main

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.yunzia.hyperstar.LocalMainPagerState
import com.yunzia.hyperstar.LocalRebootDialogState
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.itemEffectGroup
import com.yunzia.hyperstar.ui.component.preference.widget.IntentPreference
import com.yunzia.hyperstar.ui.component.preference.widget.NavigatePreference
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.screen.pagers.main.about.BgEffectBackground
import com.yunzia.hyperstar.ui.screen.pagers.main.about.UpdaterButton
import com.yunzia.hyperstar.utils.getVerName
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.blur.BlurColors
import top.yukonga.miuix.kmp.blur.textureBlur
import top.yukonga.miuix.kmp.blur.BlendColorEntry
import top.yukonga.miuix.kmp.blur.BlurBlendMode
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


private data class ScrollAnimState(
    val bgAlpha: Float = 1f,
    val mainAlpha: Float = 1f,
    val mainScale: Float = 1f,
    val secAlpha: Float = 1f,
    val secScale: Float = 1f,
    val buttonAlpha: Float = 1f,
    val buttonScale: Float = 1f,
    val showBlurs: Boolean = false,
)

private data class ScrollAnimMeasurements(
    val contentStartY: Float,
    val placeholderHeight: Float,
    val mainCenterY: Float,
    val secCenterY: Float,
    val buttonCenterY: Float?,
)

private enum class ScrollAnimElement {
    Button,
    Secondary,
    Main,
}

@Composable
private fun rememberScrollAnimState(
    scroll: LazyListState,
    measurements: ScrollAnimMeasurements?,
    density: Density,
): State<ScrollAnimState> {
    val minSegmentPx = with(density) { 1.dp.toPx() }

    return remember(measurements) {
        derivedStateOf {
            val currentMeasurements = measurements
                ?: return@derivedStateOf ScrollAnimState(buttonAlpha = 0f)

            val rawSteps = buildList {
                currentMeasurements.buttonCenterY?.let {
                    add(ScrollAnimElement.Button to currentMeasurements.contentStartY - it)
                }
                add(ScrollAnimElement.Secondary to currentMeasurements.contentStartY - currentMeasurements.secCenterY)
                add(ScrollAnimElement.Main to currentMeasurements.contentStartY - currentMeasurements.mainCenterY)
            }
                .filter { it.second > 0f }
                .sortedBy { it.second }

            val steps = rawSteps.runningFold(emptyList<Pair<ScrollAnimElement, Float>>()) { result, step ->
                val previous = result.lastOrNull()?.second ?: 0f
                result + (step.first to step.second.coerceAtLeast(previous + minSegmentPx))
            }.last()

            val totalThreshold = steps.lastOrNull()?.second
                ?: return@derivedStateOf ScrollAnimState(buttonAlpha = 0f)

            if (scroll.firstVisibleItemIndex > 0) {
                return@derivedStateOf ScrollAnimState(
                    bgAlpha = 0f, mainAlpha = 0f, secAlpha = 0f,
                    buttonAlpha = 0f, showBlurs = true
                )
            }
            val offset = scroll.firstVisibleItemScrollOffset.toFloat()

            val bgThreshold = currentMeasurements.placeholderHeight.coerceAtLeast(minSegmentPx)
            val bgProgress = ((bgThreshold - offset.coerceIn(0f, bgThreshold)) / bgThreshold)
                .coerceIn(0f, 1f)

            var buttonProgress = if (currentMeasurements.buttonCenterY == null) 0f else 1f
            var secProgress = 1f
            var mainProgress = 1f
            var startThreshold = 0f

            steps.forEach { (element, endThreshold) ->
                val range = endThreshold - startThreshold
                val progress = if (range > 0f) {
                    ((endThreshold - offset.coerceIn(startThreshold, endThreshold)) / range)
                        .coerceIn(0f, 1f)
                } else {
                    0f
                }
                when (element) {
                    ScrollAnimElement.Button -> buttonProgress = progress
                    ScrollAnimElement.Secondary -> secProgress = progress
                    ScrollAnimElement.Main -> mainProgress = progress
                }
                startThreshold = endThreshold
            }

            ScrollAnimState(
                bgAlpha = bgProgress,
                mainAlpha = mainProgress,
                mainScale = lerp(0.9f, 1f, mainProgress),
                secAlpha = secProgress,
                secScale = lerp(0.9f, 1f, secProgress),
                buttonAlpha = buttonProgress,
                buttonScale = lerp(0.985f, 1f, buttonProgress),
                showBlurs = bgProgress == 0f,
            )
        }
    }
}

fun extractOnlyNumbers(input: String): String {
    return input.filter { it.isDigit() }
}

@SuppressLint("RestrictedApi", "SetTextI18n")
@Composable
fun ThirdPage(
    contentPadding: PaddingValues
) {
    val topBackdrop = rememberLayerBackdrop()
    val bgBackdrop = rememberLayerBackdrop()
    val pagerState = LocalMainPagerState.current
    val showReboot = LocalRebootDialogState.current

    val navController = LocalNavigator.current
    val context = LocalContext.current
    val activity = LocalActivity.current as MainActivity
    val isNeedUpdate = remember { mutableStateOf(false) }

    val view = LocalView.current
    val rebootStyle = activity.rebootStyle
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val updaterButtonHeight = 52.dp

    val paddingTop = animateDpAsState(
        targetValue = if (isNeedUpdate.value) updaterButtonHeight else 0.dp,
        animationSpec = tween(750, easing = LinearOutSlowInEasing)
    )

    val density = LocalDensity.current
    val scroll = rememberLazyListState()
    var mainBounds by remember { mutableStateOf<Rect?>(null) }
    var secBounds by remember { mutableStateOf<Rect?>(null) }
    var buttonBounds by remember { mutableStateOf<Rect?>(null) }
    var lazyBounds by remember { mutableStateOf<Rect?>(null) }
    var topContentPadding by remember { mutableStateOf(0.dp) }
    val titleShiftCompensation = paddingTop.value / 2
    val subtitleBottomSpace = 175.dp
    val updaterButtonBottomOffset = 60.dp
    val spacerHeight = remember(
        secBounds,
        lazyBounds,
        topContentPadding,
        titleShiftCompensation,
    ) {
        val lazyTop = lazyBounds?.top ?: return@remember 428.dp
        val secBottom = secBounds?.bottom ?: return@remember 428.dp
        with(density) {
            (secBottom - lazyTop - topContentPadding.toPx() + titleShiftCompensation.toPx())
                .coerceAtLeast(0f)
                .toDp()
        } + subtitleBottomSpace
    }
    val animMeasurements = remember(
        isNeedUpdate.value,
        lazyBounds,
        mainBounds,
        secBounds,
        buttonBounds,
        topContentPadding,
        spacerHeight,
    ) {
        val lazyTop = lazyBounds?.top ?: return@remember null
        val mainCenterY = mainBounds?.bottom ?: return@remember null
        val secCenterY = secBounds?.bottom ?: return@remember null
        val buttonCenterY = if (isNeedUpdate.value) {
            buttonBounds?.bottom ?: return@remember null
        } else {
            null
        }
        val contentStartY = with(density) {
            lazyTop + topContentPadding.toPx() + spacerHeight.toPx()
        }
        ScrollAnimMeasurements(
            contentStartY = contentStartY,
            placeholderHeight = with(density) { spacerHeight.toPx() },
            mainCenterY = mainCenterY,
            secCenterY = secCenterY,
            buttonCenterY = buttonCenterY,
        )
    }
    val animState by rememberScrollAnimState(scroll, animMeasurements, density)

    LaunchedEffect(isNeedUpdate.value) {
        if (!isNeedUpdate.value) {
            buttonBounds = null
        }
    }

    val isInDark = isSystemInDarkTheme()
    val logoBlend = remember(isInDark) {
        if (isInDark) {
            listOf(
                BlendColorEntry(Color(0xe6a1a1a1), BlurBlendMode.ColorDodge),
                BlendColorEntry(Color(0x4de6e6e6), BlurBlendMode.LinearLight),
                BlendColorEntry(Color(0xff1af500.toInt()), BlurBlendMode.Lab),
            )
        } else {
            listOf(
                BlendColorEntry(Color(0xcc4a4a4a.toInt()), BlurBlendMode.ColorBurn),
                BlendColorEntry(Color(0xff4f4f4f.toInt()), BlurBlendMode.LinearLight),
                BlendColorEntry(Color(0xff1af200.toInt()), BlurBlendMode.Lab),
            )
        }
    }

    Scaffold(
        modifier = Modifier,
        popupHost = { },
        topBar = {
            SmallTopAppBar(
                modifier = if (animState.showBlurs) Modifier.showBlur(topBackdrop) else Modifier,
                color = Color.Transparent,
                title = if (animState.showBlurs) stringResource(R.string.about_page_title) else "",
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1 && pagerState.currentPage == 2) {
                        RebootPup(showReboot)
                    }
                    IconButton(
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
        val contentTopPadding = padding.calculateTopPadding()
        SideEffect {
            topContentPadding = contentTopPadding
        }

        LaunchedEffect(pagerState.targetPage, activity.newAppVersion) {
            if (pagerState.targetPage != 2) {
                isNeedUpdate.value = false
                return@LaunchedEffect
            }
            if (activity.newAppVersion.value == "") return@LaunchedEffect
            val currentVersion = extractOnlyNumbers(currentVer)
            val newVersion = extractOnlyNumbers(activity.newAppVersion.value)
//            isNeedUpdate.value = currentVersion < newVersion
            isNeedUpdate.value = true
        }

        BgEffectBackground(
            modifier = Modifier
                .fillMaxSize()
                .blur(topBackdrop),
            bgModifier = Modifier
                .blur(bgBackdrop)
                .graphicsLayer {
                    alpha = animState.bgAlpha
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(bottom = paddingTop.value),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "HyperStar " + currentVer.take(3),
                    modifier = Modifier
                        .onGloballyPositioned {
                            mainBounds = it.boundsInRoot()
                        }
                        .textureBlur(
                            backdrop = bgBackdrop,
                            shape = RoundedCornerShape(16.dp),
                            blurRadius = 150f,
                            colors = BlurColors(
                                blendColors = logoBlend,
                            ),
                            contentBlendMode = BlendMode.DstIn,
                        )
                        .graphicsLayer {
                            alpha = animState.mainAlpha
                            scaleX = animState.mainScale
                            scaleY = animState.mainScale
                        },
                    fontSize = 42.sp,
                    fontWeight = FontWeight(570),
                )
                Text(
                    text = stringResource(R.string.xposed_desc),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .onGloballyPositioned {
                            secBounds = it.boundsInRoot()
                        }
                        .graphicsLayer {
                            alpha = animState.secAlpha
                            scaleX = animState.secScale
                            scaleY = animState.secScale
                        }
                        .padding(top = 20.dp),
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center
                )
            }
            if (isNeedUpdate.value && (buttonBounds == null || animState.buttonAlpha != 0f)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .zIndex(3f)
                        .height(spacerHeight + contentTopPadding)
                        .padding(bottom = updaterButtonBottomOffset)
                        .graphicsLayer {
                            alpha = animState.buttonAlpha
                            scaleX = animState.buttonScale
                            scaleY = animState.buttonScale
                        },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    UpdaterButton(
                        modifier = Modifier.onGloballyPositioned {
                            buttonBounds = it.boundsInRoot()
                        },
                        navController = navController
                    )
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        lazyBounds = it.boundsInRoot()
                    }
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                flingBehavior = ScrollableDefaults.flingBehavior(),
                state = scroll,
                contentPadding = PaddingValues(
                    top = contentTopPadding,
                    bottom = contentPadding.calculateBottomPadding() + 12.dp
                ),
            ) {
                item {
                    Box(
                        Modifier
                            .height(spacerHeight)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures {
                                    navController.navigate(MainRoutes.Updater)
                                }
                            }
                    )
                }
                itemEffectGroup(
                    backdrop = bgBackdrop
                ) {
                    IntentPreference(
                        icon = R.drawable.dd,
                        title = "东东说他舍不得",
                        summary = "@YunZiA | Hook",
                        context = context,
                        url = "https://www.coolapk.com/u/8555749"
                    )
                    NavigatePreference(
                        title = stringResource(R.string.translator),
                        navController = navController,
                        route = MainRoutes.Translator
                    )
                }
                itemEffectGroup(
                    title = R.string.discussion_title,
                    backdrop = bgBackdrop
                ) {
                    IntentPreference(
                        title = stringResource(R.string.qq_group_title),
                        context = context,
                        urls = arrayOf(
                            "mqqapi://card/show_pslcard?src_type=internal&version=1&uin=810317966&card_type=group&source=qrcode",
                            "https://qun.qq.com/universal-share/share?ac=1&authKey=CbXnHNd3K1gLoGTuOqSLYr73q4HCNOgU9t0tmN721ymjb2O3eeF39Zbk8DIbw%2FFE&busi_data=eyJncm91cENvZGUiOiI4MTAzMTc5NjYiLCJ0b2tlbiI6IktiVllNVmNHaGQzUkROdnNBZWZiSnJ5UEhHaktybDQ4YnUwZWgyMHZKTzUraUxMWG5admNIRGVnK3VXMjduNk0iLCJ1aW4iOiIyNTc0ODIyMDM2In0%3D&data=KY2pZd46IRdUjB_2u59hLvCtAAuyIwLVpg2MtBxASJA1KAiQVJhg0DN1mTb7podfRAZITmpscrKqZtghQgHXyQ&svctype=4&tempid=h5_group_info"
                        )
                    )
                    IntentPreference(
                        title = stringResource(R.string.telegram_channel),
                        context = context,
                        url = "https://t.me/HyperStar_release"
                    )
                    IntentPreference(
                        title = stringResource(R.string.telegram_group),
                        context = context,
                        url = "https://t.me/Hyperstar_chat"
                    )
                }
                itemEffectGroup(
                    title = R.string.others,
                    backdrop = bgBackdrop
                ) {
                    NavigatePreference(
                        title = stringResource(R.string.references_title),
                        navController = navController,
                        route = MainRoutes.References
                    )
                    IntentPreference(
                        title = stringResource(R.string.project_address),
                        summary = stringResource(R.string.open_source_statement),
                        context = context,
                        url = "https://github.com/YunZiA/HyperStar"
                    )
                    NavigatePreference(
                        title = stringResource(R.string.donation),
                        navController = navController,
                        route = MainRoutes.Donation
                    )
                }
            }
        }
    }
}
