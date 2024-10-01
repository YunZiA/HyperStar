
package com.chaos.hyperstar.ui.module.controlcenter.media.app.ui

import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.wear.compose.material.Icon
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPager
import com.chaos.hyperstar.ui.base.XMiuixTextField
import com.chaos.hyperstar.ui.module.controlcenter.media.app.AppInfo
import com.chaos.hyperstar.ui.module.controlcenter.media.app.MediaDefaultAppSettingsActivity
import com.chaos.hyperstar.ui.base.enums.EventState

import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils
import com.google.accompanist.drawablepainter.DrawablePainter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.createRipple


@Composable
fun MediaSettingsPager(activity: MediaDefaultAppSettingsActivity) {

    ActivityPager(
        activityTitle = "妙播默认应用选择",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){ topAppBarScrollBehavior,padding,enableOverScroll->

        val appLists = remember { mutableStateOf(activity.appList) }
        val isLoading = remember { mutableStateOf(true) }
        val isApp = remember { mutableStateOf(SPUtils.getString("media_default_app_package","")) }
        val isSearch = remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()

        val focusManager = LocalFocusManager.current
        var text by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val result = withContext(Dispatchers.IO) {
                    activity.getAllAppInfo(isFilterSystem = true)
                }
                appLists.value = result
                isLoading.value = false
            }
        }

        if (isSearch.value){
            appLists.value = activity.searchApp(text)
            isSearch.value = false
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
                Text(
                    text = "正在加载~",
                )

            }

        }

        AnimatedVisibility (
            !isLoading.value,
            enter = fadeIn(),
            exit = fadeOut()
        ){
            ConstraintLayout(
                modifier = Modifier
                    .padding(top = padding.calculateTopPadding() + 14.dp )
                    .fillMaxSize()
            ) {
                val (list,search)=createRefs()

                LazyColumn(
                    modifier = Modifier.constrainAs(list){
                        top.linkTo(search.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    }.fillMaxSize(),
                    enableOverScroll = enableOverScroll,
                    contentPadding = PaddingValues(top = 35.dp, bottom = padding.calculateBottomPadding()+68.dp),
                    topAppBarScrollBehavior = topAppBarScrollBehavior
                ) {

                    appLists.value?.forEachIndexed { index, apps->

                        item(index) {
                            AppItem(apps, isApp)
                        }
                    }

                }
                Box(

                    Modifier.background(colorScheme.background)
                        .constrainAs(search) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)

                    },
                ) {
                    Card(
                        modifier = Modifier
                            .padding(bottom = 10.dp)
                            .padding(horizontal = 24.dp),
                        insideMargin = DpSize(5.dp,5.dp),
                        cornerRadius = 18.dp
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            XMiuixTextField(
                                value = text,
                                cornerRadius = 13.dp,
                                onValueChange = { text = it },
                                label = "应用名称",
                                modifier = Modifier
                                    .padding(end = 5.dp)
                                    .weight(1f),
                                keyboardActions = KeyboardActions(onDone = {
                                    isSearch.value = true
                                    focusManager.clearFocus()
                                }),
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                singleLine = true
                            )

                            Button(
                                modifier = Modifier.padding(end = 2.dp),
                                onClick = {
                                    //Toast.makeText(activity,text,Toast.LENGTH_SHORT).show()
                                    isSearch.value = true
                                    focusManager.clearFocus()
                                },
                                contentPadding = PaddingValues(10.dp,16.dp),
                                shape = RoundedCornerShape(13.dp),
                                colors = ButtonColors(Color.Transparent, Color.Transparent,Color.Transparent,Color.Transparent)
                            ) {
                                Icon(
                                    ImageVector.vectorResource(R.drawable.ic_search_icon),
                                    contentDescription = "back",
                                    Modifier.size(25.dp),
                                    tint = colorScheme.onSurface
                                )

                            }

                        }
                    }
                }



            }



        }
    }


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
        colorFilter = ColorFilter.tint(colorScheme.onSurface),
        painter = painterResource(id = R.drawable.loading_progress),
        contentDescription = null,
        modifier = Modifier
            .wrapContentSize()
            .padding(15.dp)
            .rotate(rotation.value)
    )
}

@Composable
fun AppItem(
    app: AppInfo,
    isApp : MutableState<String>
){
    val label = app.label
    val packageName = app.package_name
    var isSelect = packageName == isApp.value // 直接比较，不需要用 mutableStateOf

    var eventState by remember { mutableStateOf(EventState.Idle) }
    val scale by animateFloatAsState(if (eventState == EventState.Pressed) 0.90f else 1f)


    val view = LocalView.current

    //if (isSelect) view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 10.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },

        color = if (isSelect) colorScheme.tertiaryContainer  else colorScheme.surfaceVariant
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
                    EventState.Idle
                    isApp.value = if (isSelect) "" else packageName
                    isSelect = !isSelect
                    SPUtils.setString("media_default_app_package", isApp.value)
                }.pointerInput(eventState) {

                    awaitPointerEventScope {
                        eventState = if (eventState == EventState.Pressed) {
                            waitForUpOrCancellation()
                            EventState.Idle
                        } else {
                            ///view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS)
                            awaitFirstDown(false)
                            EventState.Pressed
                        }
                    }
                }
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp,end = 8.dp)
                    .padding(vertical = 16.dp)
            ){
                app.icon?.let { icon ->
                    Image(
                        modifier = Modifier
                            .size(40.dp),
                        painter = DrawablePainter(icon),
                        contentDescription = label
                    )
                }
                if (app.icon ==  null){
                    Log.d("ggc","app.icon is null ")

                }

            }
            Text(
                text = label,
                modifier = Modifier
                    .weight(1f)
                    .align(Alignment.CenterVertically),
                color = if (isSelect) colorScheme.primary else colorScheme.onBackground
            )
            Box(
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .padding(start = 8.dp, end = 16.dp)
            ){
                Checkbox(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    enabled = true,
                    checked = isSelect,
                    onCheckedChange = {
                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        isApp.value = if (isSelect) "" else packageName
                        isSelect = !isSelect
                        SPUtils.setString("media_default_app_package",isApp.value)
                    } // 如果需要处理选中变化，可以在这里添加逻辑
                )

            }

        }
    }
}

