package com.chaos.hyperstar.ui.module.volume

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils

@Composable
fun VolumePager(activity: ComponentActivity) {
    ActivityPagers(
        activityTitle = stringResource(R.string.sound_settings),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
                   },
    ){
        firstClasses(
            title = R.string.basics
        ) {
            XSuperDropdown(
                title = stringResource(R.string.is_super_blur_volume_title),
                key = "is_super_blur_volume",
                option = R.array.is_super_blur_entire
            )
            XSuperSwitch(
                title = stringResource(R.string.is_use_pad_volume_title),
                summary = stringResource(R.string.is_use_pad_volume_summary),
                key = "is_use_pad_volume"
            )
        }
    }

}