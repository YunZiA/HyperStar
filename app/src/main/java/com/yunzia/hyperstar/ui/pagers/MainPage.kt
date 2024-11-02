package com.yunzia.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.yunzia.hyperstar.PagerList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.SystemUIPagerList
import com.yunzia.hyperstar.ui.base.SuperNavHostArrow
import com.yunzia.hyperstar.ui.base.classes
import com.yunzia.hyperstar.ui.base.firstClasses
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun MainPage(
    activity : ComponentActivity,
    navController: NavController,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues
) {


    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {

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
//        classes (
//            title = R.string.other_settings
//        ){
//            SuperNavHostArrow(
//                leftIcon = R.drawable.ic_miui_home_settings,
//                title = stringResource(R.string.beta_home),
//                navController = navController,
//                route = PagerList.BETA_HOME
//
//            )
//
//
//
//
//
//            //SearchBar()
//
//
//
//        }



    }






}



