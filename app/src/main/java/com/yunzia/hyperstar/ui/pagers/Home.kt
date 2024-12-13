package com.yunzia.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIPagerList
import com.yunzia.hyperstar.ui.base.Classes
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import com.yunzia.hyperstar.ui.base.modifier.bounceAnim
import com.yunzia.hyperstar.ui.base.modifier.bounceAnimN
import com.yunzia.hyperstar.utils.Utils
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.icons.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.getWindowSize
import yunzia.utils.SystemProperties

@Composable
fun Home(
    activity : MainActivity,
    navController: NavHostController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {

    val errVersion = (SystemProperties.getInt("ro.mi.os.version.code", 1) != 2)
    val isModuleActive = activity.isModuleActive()

    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {

        item{
            if (errVersion){
                Classes{
                    Text(
                        text = stringResource(R.string.os_ver_tips),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                }
                Spacer(Modifier.height(12.dp))

            }
            if (!isModuleActive){
                Classes{
                    Text(
                        text = stringResource(R.string.not_activated_toast_description),
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                        color = Color.Red,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp
                    )

                }
                Spacer(Modifier.height(12.dp))

            }
            if (Utils.getRootPermission() != 0){
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
                route = SystemUIPagerList.CONTROL_CENTER

            )
            SuperNavHostArrow(
                leftIcon = R.drawable.ic_sound_settings,
                title = stringResource(R.string.sound_settings),
                navController = navController,
                route = SystemUIPagerList.VOLUME_DIALOG

            )
            SuperNavHostArrow(
                leftIcon = R.drawable.ic_other_advanced_settings,
                title = stringResource(R.string.more),
                navController = navController,
                route = SystemUIPagerList.MORE

            )


        }
        classes (
            title = R.string.other_settings
        ){
            SuperNavHostArrow(
                leftIcon = R.drawable.ic_miui_home_settings,
                title = stringResource(R.string.hyper_home),
                navController = navController,
                route = PagerList.HOME

            )

        }



    }






}



