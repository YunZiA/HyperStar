package com.chaos.hyperstar.ui.module.volume

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.XSuperSwitch
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
            XSuperDropdown(
                title = "音量条·高级材质",
                key = "is_super_blur_volume",
                option = R.array.is_super_blur_entire
            )
            XSuperSwitch(
                title = "平板音量条",
                summary = "因为平板音量条自身特殊的原因，会覆盖原有侧边音量条的相关数据",
                key = "is_use_pad_volume")
        }
    }

}