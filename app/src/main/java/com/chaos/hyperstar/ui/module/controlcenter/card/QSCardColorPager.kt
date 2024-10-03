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
            title = "关闭颜色"
        ) {

            ColorPickerTool(
                title = "图标",
                key = "card_icon_off_color"
            )
            ColorPickerTool(
                title = "标题",
                key = "card_primary_disabled_color"
            )
            ColorPickerTool(
                title = "副标题",
                key = "card_secondary_disabled_color"
            )

        }
        classes(
            title = "开启颜色"
        ) {
            ColorPickerTool(
                title = "背景",
                key = "card_enabled_color"
            )
            ColorPickerTool(
                title = "图标",
                key = "card_icon_on_color"
            )
            ColorPickerTool(
                title = "标题",
                key = "card_primary_enabled_color"
            )
            ColorPickerTool(
                title = "副标题",
                key = "card_secondary_enabled_color"
            )


        }
        classes(
            title = "蓝牙临时开启颜色",
        ) {
            ColorPickerTool(
                title = "背景",
                key = "card_restricted_color"
            )
            ColorPickerTool(
                title = "图标",
                key = "card_icon_restricted_color"
            )
            ColorPickerTool(
                title = "标题",
                key = "card_primary_restricted_color"
            )
            ColorPickerTool(
                title = "副标题",
                key = "card_secondary_restricted_color"
            )


        }
        classes(
            title = "禁用颜色"
        ) {
            ColorPickerTool(
                title = "背景",
                key = "card_unavailable_color"
            )
            ColorPickerTool(
                title = "图标",
                key = "card_icon_unavailable_color"
            )
            ColorPickerTool(
                title = "标题",
                key = "card_primary_unavailable_color"
            )
            ColorPickerTool(
                title = "副标题",
                key = "card_secondary_unavailable_color"
            )


        }

    }
}
