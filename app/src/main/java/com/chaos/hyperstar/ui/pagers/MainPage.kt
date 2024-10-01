package com.chaos.hyperstar.ui.pagers

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.FilterColorHex
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.module.controlcenter.ControlCenterSettings
import com.chaos.hyperstar.ui.module.volume.VolumeSettings
import com.chaos.hyperstar.ui.module.betahome.BetaHomeSettingsActivity
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextField
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun MainPage(
    activity : ComponentActivity,
    topAppBarScrollBehavior: ScrollBehavior,
    padding: PaddingValues,
    enableOverScroll: Boolean,
) {

    LazyColumn(
        modifier = Modifier.height(getWindowSize().height.dp),
        enableOverScroll = enableOverScroll,
        contentPadding = PaddingValues(top = padding.calculateTopPadding()+14.dp, bottom = padding.calculateBottomPadding()+14.dp),
        topAppBarScrollBehavior = topAppBarScrollBehavior
    ) {
        item {
            XMiuixClasser(
                title = stringResource(R.string.systemui)
            ){
                MiuixActivitySuperArrow(
                    leftIcon = R.drawable.icon_controlcenter,
                    title = stringResource(R.string.controlcenter),
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
            XMiuixClasser(
                title = stringResource(R.string.other_settings),
                top = 12.dp
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






}



