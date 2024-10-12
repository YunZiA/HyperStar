package com.chaos.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.SuperActivityArrow
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.systemui.controlcenter.ControlCenterSettings
import com.chaos.hyperstar.ui.module.systemui.volume.VolumeSettings
import com.chaos.hyperstar.ui.module.betahome.BetaHomeSettingsActivity
import com.chaos.hyperstar.ui.module.systemui.other.SystemUIOtherSettings
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun MainPage(
    activity : ComponentActivity,
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
            SuperActivityArrow(
                leftIcon = R.drawable.icon_controlcenter,
                title = stringResource(R.string.control_center),
                context = activity,
                activity = ControlCenterSettings::class.java

            )
            SuperActivityArrow(
                leftIcon = R.drawable.ic_sound_settings,
                title = stringResource(R.string.sound_settings),
                context = activity,
                activity = VolumeSettings::class.java

            )
            SuperActivityArrow(
                leftIcon = R.drawable.ic_other_advanced_settings,
                title = stringResource(R.string.more),
                context = activity,
                activity = SystemUIOtherSettings::class.java

            )


        }
        classes (
            title = R.string.other_settings
        ){
            SuperActivityArrow(
                leftIcon = R.drawable.ic_miui_home_settings,
                title = stringResource(R.string.beta_home),
                context = activity,
                activity = BetaHomeSettingsActivity::class.java

            )





            //SearchBar()



        }



    }






}



