package com.chaos.hyperstar.ui.module.volume

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.XMiuixSuperDropdown
import com.chaos.hyperstar.utils.Utils

@Composable
fun VolumePager(activity: ComponentActivity) {
    ActivityPagers(
        activityTitle = "音量条",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
                   },
    ){
        item {
            XMiuixSuperDropdown(
                title = "音量条·高级材质",
                key = "is_super_blur_volume",
                option = R.array.is_super_blur_entire,
                activity = activity
            )
        }
    }

}