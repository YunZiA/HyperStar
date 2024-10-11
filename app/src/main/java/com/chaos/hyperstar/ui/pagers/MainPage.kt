package com.chaos.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.controlcenter.ControlCenterSettings
import com.chaos.hyperstar.ui.module.volume.VolumeSettings
import com.chaos.hyperstar.ui.module.betahome.BetaHomeSettingsActivity
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
            MiuixActivitySuperArrow(
                leftIcon = R.drawable.icon_controlcenter,
                title = stringResource(R.string.control_center),
                context = activity,
                activity = ControlCenterSettings::class.java

            )
            MiuixActivitySuperArrow(
                leftIcon = R.drawable.ic_sound_settings,
                title = stringResource(R.string.sound_settings),
                context = activity,
                activity = VolumeSettings::class.java

            )


        }
        classes (
            title = R.string.other_settings
        ){
            MiuixActivitySuperArrow(
                leftIcon = R.drawable.ic_miui_home_settings,
                title = stringResource(R.string.beta_home),
                context = activity,
                activity = BetaHomeSettingsActivity::class.java

            )





            //SearchBar()



        }



    }






}



