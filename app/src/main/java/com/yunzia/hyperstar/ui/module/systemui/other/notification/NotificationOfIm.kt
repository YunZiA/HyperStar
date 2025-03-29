package com.yunzia.hyperstar.ui.module.systemui.other.notification

import android.content.pm.PackageManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIMoreList
import com.yunzia.hyperstar.ui.base.FloatingPagerButton
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.XMiuixTextField
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.nav.nav
import com.yunzia.hyperstar.ui.base.pager.SearchModuleNavPager
import com.yunzia.hyperstar.ui.pagers.titleColor
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.PreferencesUtil
import com.yunzia.hyperstar.utils.SPUtils
import dev.chrisbanes.haze.HazeState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape


@Composable
fun FloatingPagerButtonContent(
    expand : MutableState<Boolean>
){
    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    var text by remember { mutableStateOf("") }
    val isLoading = remember { mutableStateOf(true) }
    var isSearch by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    XScaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        popupHost = { },
        topBar = {
            AnimatedVisibility(
                !isSearch,
                enter = fadeIn(),
                exit = fadeOut()
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

        BackHandler(isSearch) {
            isSearch = false
            focusManager.clearFocus()
        }

        Column(
            modifier = Modifier
                .padding(top = if (isSearch) 0.dp else padding.calculateTopPadding() + 14.dp)
                .fillMaxSize()
        ){

            Box(
                Modifier.background(colorScheme.background)
            ) {
                Card(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .padding(horizontal = 24.dp),
                    insideMargin = PaddingValues(5.dp,5.dp),
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
                            label = stringResource(R.string.app_name_type),
                            modifier = Modifier
                                .padding(end = 5.dp)
                                .onFocusChanged {
                                    if (it.isFocused){
                                        isSearch = true
                                    }
                                }
                                .weight(1f),
                            keyboardActions = KeyboardActions(onDone = {
                                //isSearch = true
                                focusManager.clearFocus()
                            }),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = true
                        )

                        Button(
                            modifier = Modifier.padding(end = 2.dp),
                            onClick = {
                                //Toast.makeText(activity,text,Toast.LENGTH_SHORT).show()
                                isSearch = true
                                focusManager.clearFocus()
                            },
                            contentPadding = PaddingValues(10.dp,16.dp),
                            shape = RoundedCornerShape(13.dp),
                            colors = ButtonColors(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent,
                                Color.Transparent
                            )
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    top = 0.dp,
                    bottom = padding.calculateBottomPadding() + 28.dp
                ),
                topAppBarScrollBehavior = topAppBarScrollBehavior
            ) {

//                notifPlusList.forEach {
//                    item {
//                        AppNotifItem(it,navController)
//                    }
//                }

            }


        }




    }


}

@Composable
fun NotificationOfIm(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {

    val activity = navController.context as MainActivity

    val packageManager = activity.packageManager

    val applicationInfo =  packageManager.getPackageInfo("com.tencent.mm", PackageManager.GET_META_DATA).applicationInfo!!

    val appName = packageManager.getApplicationLabel(applicationInfo).toString()
    val packageName = applicationInfo.packageName
    val appIcon = packageManager.getApplicationIcon(applicationInfo)

    val weChat = NotificationInfo(appIcon,appName,"com.tencent.mm","message_channel_new_id")


    val notifPlusList = remember { mutableStateListOf(weChat) }

    val appNotifPkgiList = remember { mutableStateOf<List<String>>(emptyList()) }
    val isLoading = remember { mutableStateOf(true) }
    val isApp = remember { mutableStateOf(SPUtils.getString("media_default_app_package","")) }
    var isSearch by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    val focusManager = LocalFocusManager.current
    var text by remember { mutableStateOf("") }
    val update = remember { mutableStateOf(false) }

    val showNot = remember { mutableStateOf(null) }
    val showDialog = remember { mutableStateOf(false) }

    LaunchedEffect(activity.isGranted.value) {

        if (activity.isGranted.value){
            update.value = true
            isLoading.value = true
        }
    }

    LaunchedEffect(Unit) {


    }
    LaunchedEffect(update.value) {

        coroutineScope.launch {
            val result = withContext(Dispatchers.IO) {
                PreferencesUtil.getString("notification_icon_type_whitelist","com.tencent.mm").split("|")
                //getAllAppInfo(mContext,isFilterSystem = true,appListDB,appIconlist)
            }
            appNotifPkgiList.value = result
            update.value = false
            isLoading.value = false
        }
    }

    LaunchedEffect(isSearch) {
        if (!isSearch) return@LaunchedEffect
        coroutineScope.launch {
            withContext(Dispatchers.IO) {
                //appLists.value = appListDB.searchAPPlist(text,appIconlist)
            }
            isSearch = false
        }


    }

    SearchModuleNavPager(
        activityTitle = "图标优化白名单",
        navController = navController,
        parentRoute = currentStartDestination,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
        floatingPagerButton = {
            FloatingPagerButton(
                modifier = Modifier,
                containerColor = colorScheme.surface,
                insideMargin = PaddingValues(end = 40.dp, bottom = 80.dp),
                buttonContent = {
                    Icon(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                        tint = colorScheme.onTertiaryContainer,
                        contentDescription = "add"
                    )
                },
                content = { FloatingPagerButtonContent(it) }

            )
        },
    ) { topAppBarScrollBehavior,padding->


        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                top = 0.dp,
                bottom = padding.calculateBottomPadding() + 28.dp
            ),
            topAppBarScrollBehavior = topAppBarScrollBehavior
        ) {

            notifPlusList.forEach {
                item {
                    AppNotifItem(it,navController)
                }
            }

        }


    }
}

@Composable
fun AppNotifItem(
    notificationInfo: NotificationInfo,
    navController: NavHostController
){
    val label = notificationInfo.appName
    val packageName = notificationInfo.packageName


    BasicComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 26.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.CornerRadius))
            .background(colorScheme.surfaceVariant)
        ,
        insideMargin =  PaddingValues(16.dp),
        title = label,
        titleColor = titleColor(false),
        leftAction = {
            Box(
                modifier = Modifier.padding(end = 12.dp)
            ){
                Image(
                    modifier = Modifier.size(40.dp),
                    painter = DrawablePainter(notificationInfo.appicon),
                    contentDescription = label
                )

            }
        },
        rightActions = {

            Image(
                modifier = Modifier
                    .size(40.dp).padding(start = 15.dp),
                imageVector = ImageVector.vectorResource(R.drawable.arrow_right),
                //MiuixIcons.ArrowRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(colorScheme.onBackground),
            )


        },
        onClick = {
            navController.nav(SystemUIMoreList.NOTIFICATION_APP_DETAIL)
        }
    )

}