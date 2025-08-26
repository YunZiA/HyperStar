package com.yunzia.hyperstar.ui.screen.welcome

import android.annotation.SuppressLint
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import android.view.HapticFeedbackConstants
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import com.google.accompanist.drawablepainter.DrawablePainter
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.Button
import com.yunzia.hyperstar.ui.component.enums.EventState
import com.yunzia.hyperstar.ui.component.modifier.bounceClick
import com.yunzia.hyperstar.ui.component.modifier.bounceScale
import com.yunzia.hyperstar.ui.miuiStrongToast.MiuiStrongToast
import com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media.app.AppInfo
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.root
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.HorizontalDivider
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme


@SuppressLint("QueryPermissionsNeeded")
private fun getRootManagerInfo(
    context: Context,
    root: List<AppInfo>, noRoot: MutableState<List<AppInfo>?>
): ArrayList<AppInfo> {
    val appBeanList: ArrayList<AppInfo> = ArrayList<AppInfo>()
    val packageManager = context.packageManager

    val rootList = root.toMutableList()
    root.forEach {
        try {
            val packageInfo = packageManager.getPackageInfo(it.packageName, 0).applicationInfo!!

            val app_name = packageManager.getApplicationLabel(packageInfo).toString()
            val package_name = packageInfo.packageName
            val app_icon = packageManager.getApplicationIcon(packageInfo)
            //val launch = packageManager.getLaunchIntentForPackage(package_name)

            val bean = AppInfo()
            bean.apply {
                label = app_name
                packageName = package_name
                icon = app_icon
                launch = packageManager.getLaunchIntentForPackage(package_name)
            }
            appBeanList.add(bean)
            rootList.remove(it)

            Log.d("AppPackageInfo", "App found: $app_name (Package: $package_name)")
        } catch (e: PackageManager.NameNotFoundException) {
            // 未找到应用

            Log.d("AppPackageInfo", "App not found for package: $it.packageName")
        }
    }
    noRoot.value = rootList

    return appBeanList
}

@Composable
fun RootPage(pagerState: PagerState) {
    val view = LocalView.current
    val coroutineScope = rememberCoroutineScope()

    val mContext = LocalContext.current
    ///val rootList = getRootManagerInfo(mContext)


    val noRoot = remember { mutableStateOf<List<AppInfo>?>(null) }
    val isLoading = remember { mutableStateOf(true) }

    val rootList = remember { mutableStateOf<ArrayList<AppInfo>?>(null) }
    LaunchedEffect(pagerState.currentPage) {

        if (pagerState.currentPage == 1){
            Helper.isRoot()

            coroutineScope.launch {
                val result = withContext(Dispatchers.IO) {
                    getRootManagerInfo(mContext,root(mContext),noRoot)
                }
                rootList.value = result
                isLoading.value = false
            }

        }
    }

    Column (
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(25.dp))

        Box(
            modifier = Modifier.size(90.dp)
        ){
            Icon(
                ImageVector.vectorResource(R.drawable.root),
                contentDescription = "language",
                tint = Color(0xFF3482FF)
            )

        }
        Text(
            stringResource(R.string.root_access),
            modifier = Modifier.padding(vertical = 13.dp),
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = stringResource(R.string.statement),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 36.dp)
                .padding(top = 5.dp, bottom = 0.dp),
            fontSize = 14.sp,
            textAlign= TextAlign.Start,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = stringResource(R.string.root_statement)
            ,
            modifier = Modifier
                .padding(horizontal = 36.dp),
            fontSize = 13.sp,
            color = colorScheme.onSurfaceVariantSummary,
            fontWeight = FontWeight.Medium,
            style = TextStyle(textIndent = TextIndent(1.8.em, 0.em))
        )

        HorizontalDivider(Modifier.padding(horizontal = 32.dp).padding(top = 10.dp, bottom = 6.dp),1.dp, colorScheme.dividerLine)

        Box(
            modifier = Modifier
            .weight(1f)
            .padding(bottom = 10.dp)

        ){

            this@Column.AnimatedVisibility(
                isLoading.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .padding(bottom = 0.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    ShowLoading()


                }

            }

            this@Column.AnimatedVisibility(
                !isLoading.value,
                enter = fadeIn(),
                exit = fadeOut()
            ) {

                LazyColumn{

                    item {
                        Text(
                            stringResource(R.string.installed),
                            modifier = Modifier
                                .padding(horizontal = 36.dp)
                                .padding(top = 4.dp),
                            color = colorResource(R.color.class_name_color),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                    rootList.value?.forEach {
                        item {
                            AppItem(mContext,it)
                        }
                    }

                    item {
                        Text(
                            stringResource(R.string.uninstalled),
                            modifier = Modifier
                                .padding(horizontal = 36.dp)
                                .padding(top = 18.dp),
                            color = colorScheme.onBackgroundVariant,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    noRoot.value?.forEach {

                        item {
                            AppItem(mContext,it)
                        }

                    }
                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                    }



                }
            }
        }


        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp)
                .padding(bottom = 10.dp),
            //colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                coroutineScope.launch {
                    pagerState.animateScrollToPage(2)
                }

            }
        ) {
            Text(
                text = stringResource(R.string.skip),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = colorScheme.onBackground,
                fontWeight = FontWeight.Bold
            )
        }

        val noRootAcc = stringResource(R.string.no_root_access_obtained)

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 28.dp),
            colors = Color(0xFF3482FF),
            onClick = {

                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                if (Helper.getRootPermission() == 0){
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(2)
                    }

                }else{
                    MiuiStrongToast.showStrongToast(mContext,noRootAcc)
                }

            }
        ) {
            Text(
                text = stringResource(R.string.auth_next),
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
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
    mContext: Context,
    app: AppInfo,
){
    val label = app.label

    val view = LocalView.current
    val eventState = remember { mutableStateOf(EventState.Idle) }
    //val scale by animateFloatAsState(if (eventState == EventState.Pressed) 0.90f else 1f)

    val isUsed = app.launch != null

    val go = if (isUsed) Modifier
        .bounceClick(eventState)
        .clickable {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
            startAppByPackageName(mContext, app)
        }
    else{
        Modifier
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp)
            .padding(top = 10.dp)
            .bounceScale(eventState),
        colors = CardDefaults.defaultColors(colorScheme.surfaceVariant)
    ) {

        Row(

            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .then(go)
        ) {
            Box(
                modifier = Modifier
                    .padding(start = 16.dp, end = 12.dp)
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
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp)
                    .align(Alignment.CenterVertically),
                color = if (isUsed) colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant
            )
            Image(
                modifier = Modifier
                    .width(40.dp)
                    .padding(vertical = 16.dp)
                    .padding(end = 16.dp),
                imageVector = ImageVector.vectorResource(R.drawable.arrow_right),
                //MiuixIcons.ArrowRight,
                contentDescription = null,
                colorFilter = ColorFilter.tint(if (isUsed) colorScheme.onBackground else colorScheme.disabledOnSecondaryVariant),
            )


        }
    }
}
private fun startAppByPackageName(context: Context, app: AppInfo) {
    val intent = app.launch!!

    val resolveInfo = context.packageManager.resolveActivity(intent, 0)
    if (resolveInfo != null) {
        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // 如果找不到可以处理Intent的活动，则捕获此异常
            Log.e("AppStarter", "Unable to start app with package name: ${app.packageName}", e)
        }
    } else {
        // 如果没有找到任何匹配的活动，则记录或处理此情况
        Log.e("AppStarter", "No activity found to handle intent for package: ${app.packageName}")
    }
}
