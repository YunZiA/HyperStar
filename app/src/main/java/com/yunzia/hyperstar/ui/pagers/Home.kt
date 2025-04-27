package com.yunzia.hyperstar.ui.pagers

import android.content.Intent
import android.view.HapticFeedbackConstants
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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
import androidx.navigation.NavHostController
import androidx.wear.compose.material.Icon
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIList
import com.yunzia.hyperstar.ui.base.Classes
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.base.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.pagers.dialog.checkApplication
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.Helper.isRoot
import com.yunzia.hyperstar.utils.appIcon
import com.yunzia.hyperstar.utils.isOS2Settings
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun Home(
    navController: NavHostController,
    hazeState: HazeState,
    showReboot: MutableState<Boolean>
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


        LazyColumn(
            modifier = Modifier.height(getWindowSize().height.dp).blur(hazeState)
                .nestedOverScrollVertical(topAppBarScrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        ) {

            item{
                if (!isModuleActive){
                    val go = checkApplication(activity,"org.lsposed.manager")
                    val intent = Intent().apply {
                        setClassName("org.lsposed.manager","org.lsposed.manager.ui.activity.MainActivity")
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    Classes(
                        modifier = Modifier.bounceAnimN()
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
                                            Toast.makeText(activity, result, Toast.LENGTH_SHORT).show()
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
                    Classes(
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

            firstClasses(
                title = R.string.systemui
            ){
                SuperNavHostArrow(
                    leftIcon = R.drawable.icon_controlcenter,
                    title = stringResource(R.string.control_center),
                    navController = navController,
                    route = SystemUIList.CONTROL_CENTER

                )
                SuperNavHostArrow(
                    leftIcon = R.drawable.ic_sound_settings,
                    title = stringResource(R.string.sound_settings),
                    navController = navController,
                    route = SystemUIList.VOLUME_DIALOG

                )
                SuperNavHostArrow(
                    leftIcon = R.drawable.ic_other_advanced_settings,
                    title = stringResource(R.string.more),
                    navController = navController,
                    route = SystemUIList.MORE

                )


            }
            classes (
                title = R.string.other_settings
            ){
                if (isOS2Settings()){
                    SuperNavHostArrow(
                        leftIcon = rememberDrawablePainter(appIcon("com.miui.home").value),
                        title = stringResource(R.string.hyper_home),
                        navController = navController,
                        route = PagerList.HOME

                    )

                }


                activity.themeManager.value?.let {
                    if(it.versionCode >= 7180){
                        SuperNavHostArrow(
                            leftIcon = rememberDrawablePainter(it.appIcon),
                            title = stringResource(R.string.thememanager),
                            navController = navController,
                            route = PagerList.THEMEMANAGER

                        )
                    }

                }
                activity.barrageManger.value?.let {
                    if (it.versionName!!.startsWith("3")){
                        SuperNavHostArrow(
                            leftIcon = rememberDrawablePainter(it.appIcon),
                            title = stringResource(R.string.barrage),
                            navController = navController,
                            route = PagerList.BARRAGE

                        )
                    }

                }
                SuperNavHostArrow(
                    leftIcon = R.drawable.not_developer,
                    title = stringResource(R.string.not_developer),
                    navController = navController,
                    route = PagerList.NOTDEVELOP

                )

            }



        }



    }



}



