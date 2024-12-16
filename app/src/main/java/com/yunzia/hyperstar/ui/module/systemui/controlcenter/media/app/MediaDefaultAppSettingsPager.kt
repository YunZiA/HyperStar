
package com.yunzia.hyperstar.ui.module.systemui.controlcenter.media.app

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.ModuleNavPager
import com.yunzia.hyperstar.ui.base.XMiuixTextField
import com.yunzia.hyperstar.ui.base.enums.EventState

import com.yunzia.hyperstar.utils.SPUtils
import com.yunzia.hyperstar.utils.Utils
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.pagers.titleColor
import com.yunzia.hyperstar.utils.PreferencesUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Checkbox
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.extra.CheckboxLocation
import top.yukonga.miuix.kmp.extra.SuperCheckbox
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@SuppressLint("QueryPermissionsNeeded")
private fun getAllAppInfo(
    context:Context,
    isFilterSystem: Boolean,
    appListDB: AppListDB?,
    appIconlist: MutableMap<String, Drawable>
): ArrayList<AppInfo> {
    val appBeanList: ArrayList<AppInfo> = ArrayList<AppInfo>()
    val packageManager = context.packageManager
    val list = packageManager.getInstalledPackages(0)

    appListDB?.resetTable()

    for (p in list) {
        val applicationInfo = p.applicationInfo

        // 检查是否是系统应用以及是否应该过滤系统应用
        val isSystemApp = isFilterSystem && ((applicationInfo?.flags?.and(ApplicationInfo.FLAG_SYSTEM)) != 0)

        // 检查是否是特定的应用包名
        if ("com.miui.player" == applicationInfo?.packageName) {
            processAppInfo(applicationInfo, packageManager, appBeanList,appListDB,appIconlist)
        } else if (!isSystemApp) {  // 非系统应用
            processAppInfo(applicationInfo, packageManager, appBeanList, appListDB,appIconlist)
        }
    }

    return appBeanList
}

private fun processAppInfo(
    applicationInfo: ApplicationInfo?,
    packageManager: PackageManager?,
    appBeanList: ArrayList<AppInfo>,
    appListDB: AppListDB?,
    appIconlist: MutableMap<String, Drawable>
) {
    if (applicationInfo == null || packageManager == null) return
    run {

        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val packageName = applicationInfo.packageName
        val appIcon = packageManager.getApplicationIcon(applicationInfo)

        val bean = AppInfo()
        bean.label = appName
        bean.packageName = packageName.toString()
        bean.icon = appIcon

        val values = ContentValues()
        values.put("package_name", packageName)
        values.put("app_name", appName)

        if (packageName != null) {
            appIconlist[packageName] = appIcon
            if (appIconlist[packageName] == null){
                Log.d("ggc","appIconlist[package_name]  == null")

            }
        }


        appBeanList.add(bean)
        appListDB?.add(values)

        values.clear()
    }
}


@SuppressLint("MutableCollectionMutableState")
@Composable
fun MediaAppSettingsPager(
    navController: NavController
) {

    ModuleNavPager(
        activityTitle = stringResource(R.string.media_default_app_settings),
        navController = navController,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){ topAppBarScrollBehavior,padding->
        val mContext = navController.context
        val appListDB = AppListDB(mContext)
        val appLists = remember { mutableStateOf<ArrayList<AppInfo>?>(null) }
        val appIconlist = remember { mutableMapOf<String, Drawable>() }
        val isLoading = remember { mutableStateOf(true) }
        val isApp = remember { mutableStateOf(SPUtils.getString("media_default_app_package","")) }
        var isSearch by remember { mutableStateOf(false) }

        val coroutineScope = rememberCoroutineScope()

        val focusManager = LocalFocusManager.current
        var text by remember { mutableStateOf("") }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val result = withContext(Dispatchers.IO) {
                    getAllAppInfo(mContext,isFilterSystem = true,appListDB,appIconlist)
                }
                appLists.value = result
                isLoading.value = false
            }
        }

        LaunchedEffect(isSearch) {
            if (!isSearch) return@LaunchedEffect
            coroutineScope.launch {
                withContext(Dispatchers.IO) {
                    appLists.value = appListDB.searchAPPlist(text,appIconlist)
                }
                isSearch = false
            }


        }


        ConstraintLayout(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding() + 14.dp)
                .fillMaxSize()
        ) {
            val (load,list,search)=createRefs()


            AnimatedVisibility (
                !isLoading.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LazyColumn(
                    modifier = Modifier
                        .constrainAs(list) {
                            top.linkTo(search.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)

                        }
                        .fillMaxSize(),
                    contentPadding = PaddingValues(
                        top = 74.dp,
                        bottom = padding.calculateBottomPadding() + 28.dp
                    ),
                    topAppBarScrollBehavior = topAppBarScrollBehavior
                ) {

                    appLists.value?.forEachIndexed { index, apps ->

                        item(index) {
                            AppItem(apps, isApp)
                        }
                    }

                }
            }

            AnimatedVisibility (
                isLoading.value,
                enter = fadeIn(),
                exit = fadeOut()
            ){
                Column(
                    modifier = Modifier
                        .padding(bottom = 40.dp)
                        .constrainAs(load) {
                            top.linkTo(search.bottom)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)

                        }
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ShowLoading()
                    Text(
                        color = colorScheme.onSurface,
                        fontWeight = FontWeight(550),
                        text = stringResource(R.string.loading),
                    )

                }

            }


            Box(

                Modifier
                    .background(colorScheme.background)
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
                                .weight(1f),
                            keyboardActions = KeyboardActions(onDone = {
                                isSearch = true
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


@Composable
private fun ShowLoading() {
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
private fun AppItem(
    app: AppInfo,
    isApp : MutableState<String>
){
    val label = app.label
    val packageName = app.packageName
    var isSelect = packageName == isApp.value
    BasicComponent(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 10.dp)
            .bounceAnimN()
            .clip(SmoothRoundedCornerShape(CardDefaults.ConorRadius))
            .background(if (isSelect) colorScheme.tertiaryContainer else colorScheme.surfaceVariant)
        ,
        insideMargin =  PaddingValues(16.dp),
        title = label,
        titleColor = titleColor(isSelect),
        leftAction = {
            Box(
                modifier = Modifier.padding(end = 12.dp)
            ){
                app.icon?.let { icon ->
                    Image(
                        modifier = Modifier.size(40.dp),
                        painter = DrawablePainter(icon),
                        contentDescription = label
                    )
                }
                if (app.icon ==  null){
                    Log.d("ggc","app.icon is null ")

                }

            }
        },
        rightActions = {
            Checkbox(
                checked = isSelect,
                onCheckedChange = {
                    isSelect = !isSelect
                    isApp.value = if (isSelect) packageName else ""
                    SPUtils.setString("media_default_app_package", isApp.value)
                }
            )

        },
        onClick = {
            isSelect = !isSelect
            isApp.value = if (isSelect) packageName else ""
            SPUtils.setString("media_default_app_package", isApp.value)

        }
    )

}

