package com.chaos.hyperstar.ui.pagers

import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.module.controlcenter.ControlCenterSettings
import com.chaos.hyperstar.ui.module.volume.VolumeSettings
import getWindowSize
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn

@Composable
fun MainPage(
    activity : ComponentActivity,
    topAppBarScrollBehavior: MiuixScrollBehavior,
    padding: PaddingValues,
    enableOverScroll: Boolean,
) {
    MiuixLazyColumn(
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

        }

    }


}


