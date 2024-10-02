package com.chaos.hyperstar.ui.module.controlcenter.card

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.Utils

@Composable
fun QSCardColorPager(
    activity: ComponentActivity,
) {
    ActivityPagers(
        activityTitle = "卡片磁贴颜色",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {
        firstClasses(
            title = "关闭状态"
        ) {

        }
        classes(
            title = "开启状态"
        ) {
            ColorPickerTool(
                title = "背景色",
                key = "card_enabled_color"
            )


        }
        classes(
            title = "蓝牙临时开启状态",
        ) {
            ColorPickerTool(
                title = "背景色",
                key = "card_restricted_color"
            )


        }
        classes(
            title = "禁用状态"
        ) {
            ColorPickerTool(
                title = "背景色",
                key = "card_unavailable_color"
            )


        }

    }
}
