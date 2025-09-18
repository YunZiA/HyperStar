package com.yunzia.hyperstar.ui.screen.pagers

import android.content.Intent
import android.util.Log
import android.view.Gravity
import android.view.HapticFeedbackConstants
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.PagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroup
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.dialog.SuperBottomSheetDialog
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.topbar.TopBar
import com.yunzia.hyperstar.ui.screen.pagers.dialog.checkApplication
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.Helper.isRoot
import com.yunzia.hyperstar.utils.getSettingChannel
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.icons.useful.Cancel
import top.yukonga.miuix.kmp.icon.icons.useful.ImmersionMore
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun Home(
    navController: NavHostController,
    hazeState: HazeState,
    contentPadding: PaddingValues,
    showReboot: MutableState<Boolean>,
    pagerState: PagerState
) {
    val isModuleActive = isModuleActive()
    val context = LocalContext.current
    val view = LocalView.current
    val activity = LocalActivity.current as MainActivity
    val rebootStyle = activity.rebootStyle

    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier,
        popupHost = { },
        topBar = {
            TopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = stringResource(R.string.main_page_title),
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1 && pagerState.currentPage == 0){
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
                            imageVector = MiuixIcons.Useful.ImmersionMore,
                            contentDescription = "restart",
                            tint = colorScheme.onBackground)

                    }
                }
            )


        }
    ) { padding ->


        LazyColumn(
            modifier = Modifier
                .height(getWindowSize().height.dp)
                .blur(hazeState)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding(), bottom = contentPadding.calculateBottomPadding()),
        ) {

            item{
                if (!isModuleActive){
                    val go = checkApplication(activity,"org.lsposed.manager")
                    val intent = Intent().apply {
                        setClassName("org.lsposed.manager","org.lsposed.manager.ui.activity.MainActivity")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    SuperGroup(
                        modifier = Modifier.bounceAnimN(),
                        position = SuperGroupPosition.FIRST
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (go) {
                                        activity.startActivity(intent)
                                    } else {
                                        val result =
                                            Helper.rootShell("am start -c 'org.lsposed.manager.LAUNCH_MANAGER' 'com.android.shell/.BugreportWarningActivity'")
                                        if (result != "0") {
                                            Toast.makeText(activity, result, Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    }
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Text(
                                text = stringResource(R.string.not_activated_toast_description),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 16.dp)
                                    .padding(start = 24.dp, end = 8.dp),
                                color = Color.Red,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )


                            Image(
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .size(10.dp, 14.dp),
                                imageVector = MiuixIcons.Basic.ArrowRight,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions),
                            )

                        }

                    }
                    Spacer(Modifier.height(12.dp))

                }
                if (!isRoot()){
                    SuperGroup(
                        modifier = Modifier.bounceAnimN()
                    ){
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    navController.navigate(PagerList.GO_ROOT)
                                },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            Text(
                                text = stringResource(R.string.no_root_description),
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(vertical = 16.dp)
                                    .padding(start = 24.dp, end = 8.dp),
                                color = Color.Red,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )

                            Image(
                                modifier = Modifier
                                    .padding(end = 24.dp)
                                    .size(10.dp, 14.dp),
                                imageVector = MiuixIcons.Basic.ArrowRight,
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions),
                            )

                        }

                    }
                    Spacer(Modifier.height(12.dp))

                }
            }

            itemGroup(
                title = R.string.basics
            ){
                AppArrow(
                    appInfo = activity.appInfo,
                    title = stringResource(R.string.systemui),
                    packageName = "com.android.systemui",
                    navController = navController,
                    route = PagerList.SYSTEMUI
                )

                AppArrow(
                    visible = { getSettingChannel() > 1 },
                    appInfo = activity.appInfo,
                    title = stringResource(R.string.hyper_home),
                    packageName = "com.miui.home",
                    navController = navController,
                    route = PagerList.HOME
                )

                AppArrow(
                    visible = { it.versionCode >= 7180 },
                    appInfo = activity.appInfo,
                    title = stringResource(R.string.thememanager),
                    packageName = "com.android.thememanager",
                    navController = navController,
                    route = PagerList.THEMEMANAGER
                )

                AppArrow(
                    appInfo = activity.appInfo,
                    packageName = "com.android.mms",
                    navController = navController,
                    route = PagerList.MMS
                )

                AppArrow(
                    visible = { it.versionName!!.startsWith("3") },
                    appInfo = activity.appInfo,
                    packageName = "com.xiaomi.barrage",
                    navController = navController,
                    route = PagerList.BARRAGE
                )

                AppArrow(
                    visible = { getSettingChannel() > 1 },
                    appInfo = activity.appInfo,
                    packageName = "com.miui.screenshot",
                    navController = navController,
                    route = PagerList.SCREENSHOT
                )

            }
            itemGroup(
                title = R.string.other_settings
            ){

                SuperNavHostArrow(
                    leftIcon = R.drawable.not_developer,
                    title = stringResource(R.string.not_developer),
                    navController = navController,
                    route = PagerList.NOTDEVELOP

                )

            }
            if (activity.appNo.isNotEmpty()){

                item {
                    val show = remember { mutableStateOf(false) }
                    Card(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 12.dp)
                            .bounceAnim(cornerSize = CardDefaults.CornerRadius)
                            .clickable {
                                show.value = true
                            }
                    ) {
                        Text(
                            text = "列表中没有您想要找的应用？",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp)
                                .padding(start = 24.dp, end = 8.dp),
                            fontWeight  = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = colorScheme.primary,
                        )
                    }
                    SuperBottomSheetDialog(
                        show = show,
                        onDismissRequest = {
                            show.value = false
                        },
                    ) {
                        TopBar(
                            title = "未显示的应用功能入口",
                            leftIcon = {
                                IconButton(
                                    modifier = Modifier,
                                    onClick = {
                                        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                        show.value = false
                                    }
                                ) {
                                    Icon(
                                        imageVector =  MiuixIcons.Useful.Cancel,
                                        contentDescription = "back",
                                        tint = colorScheme.onBackground
                                    )
                                }
                            }
                        )
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            activity.appNo.forEach { (s, throwable) ->
                                this.itemGroup(s) {
                                    Text(throwable.toString())
                                }
                            }


                        }



                    }
                }
            }



        }




    }



}


@Composable
fun AppArrow(
    visible: (AppInfo)-> Boolean = { true },
    appInfo: MutableMap<String, AppInfo?>,
    packageName: String,
    title: String? = null,
    navController: NavHostController,
    route: String
){
    with(appInfo[packageName]){
        Log.d("ggc", "AppArrow: ${this@with != null} &&")
        AnimatedVisibility(
            visible = this != null && visible(this@with),
            enter = expandVertically() + fadeIn()
        ) {
            Log.d("ggc", "AppArrow: ${this@with != null && visible(this@with)}")
            SuperNavHostArrow(
                leftIcon = rememberDrawablePainter(this@with!!.appIcon),
                title = title?:this@with.appName,
                navController = navController,
                route = route
            )
        }

    }
}



