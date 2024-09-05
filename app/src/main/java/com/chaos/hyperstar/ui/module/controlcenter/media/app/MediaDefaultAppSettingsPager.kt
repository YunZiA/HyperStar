package com.chaos.hyperstar.ui.module.controlcenter.media.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.SubMiuixTopAppBar

import com.chaos.hyperstar.ui.pagers.FPSMonitor
import com.chaos.hyperstar.utils.PreferencesUtil
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.MiuixBox
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixCheckbox
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScaffold
import top.yukonga.miuix.kmp.basic.MiuixSurface
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.rememberMiuixTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.createRipple


@Composable
fun MediaSettingsPager(activity: MediaDefaultAppSettingsActivity) {
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberMiuixTopAppBarState())

    val showFPSMonitor = remember { mutableStateOf(PreferencesUtil.getBoolean("show_FPS_Monitor",false)) }
    val enableTopBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("top_Bar_blur",false)) }
    val enableBottomBarBlur = remember { mutableStateOf(PreferencesUtil.getBoolean("bottom_Bar_blur",false)) }
    val enableOverScroll = remember { mutableStateOf(PreferencesUtil.getBoolean("over_scroll",false)) }

    MiuixSurface {
        MiuixScaffold(
            modifier = Modifier.fillMaxSize(),
            enableTopBarBlur = enableTopBarBlur.value,
            enableBottomBarBlur = enableBottomBarBlur.value,
            topBar = {
                SubMiuixTopAppBar(
                    color = if (enableTopBarBlur.value) Color.Transparent else colorScheme.background,
                    title = "妙播默认应用选择",
                    scrollBehavior = topAppBarScrollBehavior,
                    activity = activity,
                    endClick = {
                        Utils.rootShell("killall com.android.systemui")
                    }
                )
            },

        ) { padding ->
            AppHorizontalPager(
                activity = activity,
                topAppBarScrollBehavior = topAppBarScrollBehavior,
                padding = padding,
                enableOverScroll = enableOverScroll.value,
            )
        }
    }

    if (showFPSMonitor.value) {
        FPSMonitor(
            modifier = Modifier
                .statusBarsPadding()
                .padding(horizontal = 28.dp)
        )
    }
}

@Composable
fun AppHorizontalPager(
    activity : MediaDefaultAppSettingsActivity,
    padding: PaddingValues,
    enableOverScroll: Boolean,
    topAppBarScrollBehavior : MiuixScrollBehavior
) {
    val appLists = remember { mutableStateOf(activity.appList) }
    val isLoading = remember { mutableStateOf(true) }
    val isApp = remember { mutableStateOf(SPUtils.getString("media_default_app_package","")) }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        coroutineScope.launch {
            // 异步执行代码
            val result = withContext(Dispatchers.IO) {
                // 执行耗时的IO操作
                activity.getAllAppInfo(ctx = activity, isFilterSystem = true)
            }
            appLists.value = result

            // 更新UI状态
            isLoading.value = false
        }
    }

    AnimatedVisibility (
        isLoading.value,
        enter = fadeIn(),
        exit = fadeOut()
    ){

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ShowLoading()
            MiuixText(
                text = "正在加载~",
                )
            //Spacer(modifier = Modifier.height(100.dp))
        }

    }

//    LaunchedEffect(true) {
//        appLists.value = activity.getAllAppInfo(ctx = activity, isFilterSystem = true)
//        //isLoading.value = false
//    }



    AnimatedVisibility (
        !isLoading.value,
        enter = fadeIn(),
        exit = fadeOut()
    ){
        MiuixLazyColumn(
            enableOverScroll = enableOverScroll,
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+28.dp),
            topAppBarScrollBehavior = topAppBarScrollBehavior
        ) {
            //Log.d("ggc",""+appLists.value?.size)

            appLists.value?.let { apps ->
                items(apps.size) { index ->
                    val app = apps[index]
                    val label = app.label
                    val packageName = app.package_name
                    var isSelect = packageName == isApp.value // 直接比较，不需要用 mutableStateOf

                    MiuixCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 28.dp)
                            .padding(top = 10.dp),

                        color = if (isSelect) colorScheme.dropdownSelect  else colorScheme.primaryContainer
                    ) {

                        Row(

                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable(
                                    interactionSource = null,
                                    indication = createRipple()
                                ) {
                                    isApp.value = if (isSelect) "" else packageName
                                    isSelect = !isSelect
                                    SPUtils.setString("media_default_app_package",isApp.value)
                                }
                        ) {
                            Box(
                                modifier = Modifier
                                    .padding(start = 16.dp, top = 16.dp, bottom = 16.dp,end = 8.dp)
                            ){
                                app.icon?.let { icon ->
                                    Image(
                                        modifier = Modifier
                                        .size(40.dp),
                                        painter = DrawablePainter(icon),
                                        contentDescription = label
                                    )
                                }

                            }
                            MiuixText(
                                text = label,
                                modifier = Modifier
                                    .weight(1f)
                                    .align(Alignment.CenterVertically),
                                color = if (isSelect) colorScheme.primary else colorScheme.onBackground
                            )
                            Box(
                                modifier = Modifier
                                    .padding(start = 8.dp, top = 16.dp, bottom = 16.dp,end = 16.dp)
                            ){
                                MiuixCheckbox(
                                    modifier = Modifier
                                        .padding(start = 8.dp),
                                    enabled = true,
                                    checked = isSelect,
                                    onCheckedChange = {
                                        isApp.value = if (isSelect) "" else packageName
                                        isSelect = !isSelect
                                        SPUtils.setString("media_default_app_package",isApp.value)
                                    } // 如果需要处理选中变化，可以在这里添加逻辑
                                )

                            }

                        }
                    }
                }
            }

        }
    }



//    activity.appList?.get(it)?.label ?: "null"
}

@Composable
fun ShowLoading() {
    val rotation = remember { Animatable(0f) }
    // 开启旋转动画
    val isRotating = true
    LaunchedEffect(isRotating) {
        launch {
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(
                        durationMillis = 400,
                        easing = LinearEasing
                    ),
                    repeatMode = RepeatMode.Restart
                )
            )
        }
    }

    // 旋转的图片 - rotate(rotation.value)
    Image(
        painter = painterResource(id = R.drawable.loading_progress),
        contentDescription = null,
        modifier = Modifier
            .wrapContentSize()
            .padding(15.dp)
            .rotate(rotation.value)
    )
}

