package com.chaos.hyperstar.ui.pagers

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.magnifier
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.module.controlcenter.ControlCenterSettings
import com.chaos.hyperstar.ui.module.volume.VolumeSettings
import getWindowSize
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixSuperArrow
import top.yukonga.miuix.kmp.MiuixSuperCheckbox
import top.yukonga.miuix.kmp.MiuixSuperDropdown
import top.yukonga.miuix.kmp.basic.MiuixBasicComponent
import top.yukonga.miuix.kmp.basic.MiuixCard
import top.yukonga.miuix.kmp.basic.MiuixLazyColumn
import top.yukonga.miuix.kmp.basic.MiuixText
import top.yukonga.miuix.kmp.basic.MiuixTextField
import top.yukonga.miuix.kmp.theme.MiuixTheme

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


