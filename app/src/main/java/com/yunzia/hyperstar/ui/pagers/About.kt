package com.yunzia.hyperstar.ui.pagers

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.integerArrayResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.fontscaling.MathUtils.lerp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.toColorInt
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.BaseActivity
import com.yunzia.hyperstar.ui.base.Button
import com.yunzia.hyperstar.ui.base.LinearImage
import com.yunzia.hyperstar.ui.base.SuperIntentArrow
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.nav.nav
import com.yunzia.hyperstar.ui.base.view.BgEffectView
import com.yunzia.hyperstar.utils.getVerName
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.flow.onEach
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.SmallTopAppBar
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize


private fun getColorList(
    colorMode: Int
):  List<Color>{
    return if (colorMode == 2) {
        listOf(
            Color("#D0A279ED".toColorInt()),
            Color("#D0E3BCB1".toColorInt())
        )
    } else {
        listOf(
            Color("#D03017A9".toColorInt()),
            Color("#D0733D24".toColorInt())
        )
    }
}

fun extractOnlyNumbers(input: String): String {
    // 使用 filter 仅保留数字字符
    return input.filter { it.isDigit() }
}

@SuppressLint("RestrictedApi", "SetTextI18n")
@Composable
fun ThirdPage(
    navController: NavHostController,
    hazeState: HazeState,
    showReboot: MutableState<Boolean>
) {

    val context = navController.context
    val activity = LocalActivity.current as MainActivity
    val isNeedUpdate = remember { mutableStateOf(false) }

    val view = LocalView.current
    val rebootStyle = activity.rebootStyle
    val showBlurs = remember { mutableStateOf(false) }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    val paddingTop = animateDpAsState(
        targetValue = if (isNeedUpdate.value) 27.dp else 55.dp,
        animationSpec = tween(750, easing = LinearOutSlowInEasing)
    )

    val darkTheme = isSystemInDarkTheme()
    val colorsMode = when (activity.colorMode.intValue) {
        1 -> 1
        2 -> 2
        else -> if (darkTheme) 2 else 1
    }

    val density = LocalDensity.current
    val min = with(density) { 0.dp.toPx() }
    val but = remember {
        derivedStateOf {
            if (isNeedUpdate.value){
                with(density) { 60.dp.toPx() }
            }else{
                min
            }
        }
    }
    val sec = with(density) { 100.dp.toPx() }
    val secHeight = remember(but.value) {
        derivedStateOf {
            sec-but.value
        }
    }
    val main = with(density) { 160.dp.toPx() }
    val mainHeight = main-sec

    val bgHeight = with(density) {  332.dp.toPx() }


    val blurRadius = remember {
        derivedStateOf {
            val maxBlur = 3.dp
            val normalizedOffset = ((55.dp - paddingTop.value)/28.dp).coerceIn(0f, 1f)
            if (isNeedUpdate.value){
                maxBlur * (1-(normalizedOffset/0.7f).coerceIn(0f, 1f))
            }else{
                0.dp
            }
        }
    }

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
                title = stringResource(R.string.about_page_title),
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1){
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
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "restart",
                            tint = colorScheme.onBackground)

                    }
                }
            )


        }
    ) { padding ->


        LaunchedEffect(activity.newAppVersion) {
            val currentVersion = extractOnlyNumbers(getVerName(context))
            val newVersion = extractOnlyNumbers(activity.newAppVersion.value)
            Log.d("ggc", "ThirdPage: currentVersion = $currentVersion newVersion = $newVersion",)

            // 比较版本号
            if (currentVersion < newVersion) {
                isNeedUpdate.value = true
            }
        }


        LaunchedEffect(scroll) {
            snapshotFlow { scroll.firstVisibleItemScrollOffset }
                .onEach {

                    if (scroll.firstVisibleItemIndex > 0) {
                        bgAlpha.floatValue = 0f
                        secAlpha.floatValue = 0f
                        mainAlpha.floatValue = 0f
                        buttonAlpha.floatValue = 0f
                        //showBlurs.value = true
                        return@onEach
                    }
                    val alpha =
                        ((bgHeight - it.toFloat().coerceIn(min, bgHeight)) / bgHeight).coerceIn(
                            0f,
                            1f
                        )
                    bgAlpha.floatValue = alpha
                    showBlurs.value = alpha == 0f
                    val buttonValue =
                        ((but.value - it.toFloat().coerceIn(min, but.value)) / but.value).coerceIn(0f, 1f)

                    buttonAlpha.floatValue = buttonValue
                    buttonScale.floatValue = lerp(0.99f, 1f, buttonValue)
                    val secValue = ((sec - it.toFloat().coerceIn(but.value, sec)) / secHeight.value).coerceIn(0f, 1f)

                    secAlpha.floatValue = secValue
                    secScale.floatValue = lerp(0.9f, 1f, secValue)

                    val mainValue =
                        ((main - it.toFloat().coerceIn(sec, main)) / mainHeight).coerceIn(0f, 1f)

                    mainAlpha.floatValue = mainValue
                    mainScale.floatValue = lerp(0.9f, 1f, mainValue)

                }.collect {

                }
        }



        Box(
            Modifier
                .blur(hazeState)
                .clip(RoundedCornerShape(0.dp))
        ) {

            AndroidView(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(430.dp), // Occupy the max size in the Compose UI tree
                factory = { context ->
                    BgEffectView(context, colorsMode)

                }
            ) {
                it.updateMode(colorsMode)
                it.alpha = bgAlpha.floatValue

            }


            Column(
                modifier = Modifier
                    .padding(top = paddingTop.value)
                    .fillMaxWidth()
                    .height(420.dp)
                    .blur(
                        blurRadius.value,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded
                    ),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                LinearImage(
                    contentDescription = "",
                    painter = painterResource(R.drawable.hyperstar2),
                    alpha = mainAlpha.floatValue,
                    colors = getColorList(colorMode = colorsMode),
                    modifier = Modifier
                        .width(260.dp)
                        .scale(mainScale.floatValue),
                )

                Text(
                    text = stringResource(R.string.xposed_desc),
                    fontSize = 14.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .scale(secScale.floatValue)
                        .alpha(secAlpha.floatValue)
                        .padding(top = 20.dp),
                    fontWeight = FontWeight.Medium,
                    color = MiuixTheme.colorScheme.onSurfaceVariantSummary,
                    textAlign = TextAlign.Center
                )
            }

            LazyColumn(
                modifier = Modifier
                    .height(getWindowSize().height.dp)
                    .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
                flingBehavior = ScrollableDefaults.flingBehavior(),
                state = scroll,
                contentPadding = PaddingValues(
                    top = padding.calculateTopPadding(),
                    bottom = padding.calculateBottomPadding() + 16.dp
                ),
            ) {

                item {
                    Box(
                        Modifier
                            .height(410.dp)
                            .fillMaxWidth()
                            .pointerInput(Unit) {
                                detectTapGestures() {
                                    navController.nav(PagerList.UPDATER)
                                }
                            }
                    )
                }

                firstClasses(
                    title = R.string.developer_title
                ) {
                    SuperIntentArrow(
                        leftIcon = R.drawable.dd,
                        title = "东东说他舍不得",
                        summary = "@YunZiA | Hook",
                        navController = navController,
                        url = "coolmarket://u/8555749"
                    )
                    SuperNavHostArrow(
                        title = stringResource(R.string.translator),
                        navController = navController,
                        route = PagerList.TRANSLATOR

                    )


                }

                classes(
                    title = R.string.discussion_title
                ) {
                    SuperIntentArrow(
                        title = stringResource(R.string.qq_group_title),
                        navController = navController,
                        url = "http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&amp;k=5ONF7LuaoQS6RWEOUYBuA0x4X8ssvHJp&amp;authKey=Pic4VQJxKBJwSjFzsIzbJ50ILs0vAEPjdC8Nat4zmiuJRlftqz9%2FKjrBwZPQTc4I&amp;noverify=0&amp;group_code=810317966"
                    )
                    SuperIntentArrow(
                        title = stringResource(R.string.telegram_channel),
                        navController = navController,
                        url = "https://t.me/HyperStar_release"
                    )
                    SuperIntentArrow(
                        title = stringResource(R.string.telegram_group),
                        navController = navController,
                        url = "https://t.me/Hyperstar_chat"
                    )

                }

                classes(
                    title = R.string.others
                ) {
                    SuperNavHostArrow(
                        title = stringResource(R.string.references_title),
                        navController = navController,
                        route = PagerList.REFERENCES

                    )
                    SuperIntentArrow(
                        title = stringResource(R.string.project_address),
                        summary = stringResource(R.string.open_source_statement),
                        navController = navController,
                        url = "https://github.com/YunZiA/HyperStar"
                    )

                    SuperNavHostArrow(
                        title = stringResource(R.string.donation),
                        navController = navController,
                        route = PagerList.DONATION

                    )
                }


            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .alpha(buttonAlpha.floatValue)
                    .scale(buttonScale.floatValue)
                    .height(450.dp),
                contentAlignment = Alignment.BottomCenter
            ) {

                if (isNeedUpdate.value && buttonAlpha.floatValue != 0.0f){

                    UpdaterButton(navController = navController)
                }


            }
        }
    }


}

@Composable
fun UpdaterButton(
    modifier: Modifier = Modifier,
    navController: NavHostController
){
    val activity = LocalActivity.current as BaseActivity

    val isDark = activity.isDarkMode

    val shadowColor = if (isDark){
        Color(0x4d000000)
    }else{
        Color(0x40000000)
    }

    val backgroundColor = if (isDark){
        Color(0x1fffffff)
    }else{
        Color(0x99ffffff)
    }

    val borderColor = if (isDark){
        integerArrayResource(R.array.my_card_stroke_gradient_colors_dark)
    }else{
        integerArrayResource(R.array.my_card_stroke_gradient_colors_light)
    }

    Button(
        modifier = Modifier
            .wrapContentHeight()
            .padding(bottom = 10.dp)
            .drawBehind {
                // 定义渐变色画刷
                val gradientBrush = Brush.linearGradient(
                    colors = listOf(
                        Color(borderColor[1]),
                        Color(borderColor[0]),
                    ),
                    start = Offset(size.width / 2, 0f), // 起点（左上角）
                    end = Offset(size.width / 2, Float.POSITIVE_INFINITY) // 终点（垂直方向到底部）
                )
                // 绘制渐变色的矩形边框
                val strokeWidth = 1.5.dp.toPx()
                val inset = strokeWidth / 2

                drawRoundRect(
                    brush = gradientBrush,
                    topLeft = Offset(inset, inset),
                    size = Size(
                        size.width - strokeWidth,
                        size.height - strokeWidth
                    ),
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(width = strokeWidth)
                )

            }
            .shadow(
                elevation = 1.5.dp,
                shape = SmoothRoundedCornerShape(16.dp),
                clip = true,
                ambientColor = shadowColor,
                spotColor = shadowColor
            )
            .then(modifier)
        ,
        colors = backgroundColor,
        minHeight = 52.dp,
        minWidth = 250.dp,
        onClick = {
            navController.nav(PagerList.UPDATER)
        }
    ){
        Text(
            text = stringResource(R.string.update_has),
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            color = colorScheme.onSurface
        )

    }



}
