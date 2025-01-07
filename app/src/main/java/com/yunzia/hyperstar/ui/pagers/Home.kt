package com.yunzia.hyperstar.ui.pagers

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIList
import com.yunzia.hyperstar.ui.base.Classes
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.pagers.dialog.checkApplication
import com.yunzia.hyperstar.utils.Helper
import com.yunzia.hyperstar.utils.Helper.isModuleActive
import com.yunzia.hyperstar.utils.Helper.isRoot
import com.yunzia.hyperstar.utils.isOS2Settings
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun Home(
    navController: NavHostController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {
    val context = LocalContext.current
    val activity = context as MainActivity

    val isModuleActive = isModuleActive()

    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
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
                        modifier = Modifier.fillMaxWidth().clickable {
                            if (go){
                                activity.startActivity(intent)
                            }else{
                                val result = Helper.rootShell("am start -c 'org.lsposed.manager.LAUNCH_MANAGER' 'com.android.shell/.BugreportWarningActivity'")
                                if (result != "0"){
                                    Toast.makeText(activity,result,Toast.LENGTH_SHORT).show()
                                }
                            }
                                                                     },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Text(
                            text = stringResource(R.string.not_activated_toast_description),
                            modifier = Modifier.weight(1f).padding( vertical = 16.dp).padding(start = 24.dp, end = 8.dp),
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )


                        Image(
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .size(10.dp,14.dp),
                            imageVector = MiuixIcons.ArrowRight,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions),
                        )

                    }

                }
                Spacer(Modifier.height(12.dp))

            }
            if (isRoot){
                Classes(
                    modifier = Modifier.bounceAnimN()
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable {
                            navController.navigate(PagerList.GO_ROOT) },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {

                        Text(
                            text = stringResource(R.string.no_root_description),
                            modifier = Modifier.weight(1f).padding( vertical = 16.dp).padding(start = 24.dp, end = 8.dp),
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )


                        Image(
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .size(10.dp,14.dp),
                            imageVector = MiuixIcons.ArrowRight,
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
        if (isOS2Settings()){
            classes (
                title = R.string.other_settings
            ){
                SuperNavHostArrow(
                    leftIcon = R.drawable.ic_miui_home_settings,
                    title = stringResource(R.string.hyper_home),
                    navController = navController,
                    route = PagerList.HOME

                )
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



