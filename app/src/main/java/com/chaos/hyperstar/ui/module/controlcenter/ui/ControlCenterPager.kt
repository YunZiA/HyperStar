package com.chaos.hyperstar.ui.module.controlcenter.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.base.XMiuixContentDropdown
import com.chaos.hyperstar.ui.base.XMiuixContentSwitch
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.XMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSwitch
import com.chaos.hyperstar.ui.module.controlcenter.list.QsListViewSettings
import com.chaos.hyperstar.ui.module.controlcenter.media.MediaSettingsActivity
import com.chaos.hyperstar.utils.Utils


@Composable
fun ControlCenterPager(
    activity: ComponentActivity,
) {
    ActivityPagers(
        activityTitle = stringResource(R.string.controlcenter),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){
        item {
            MiuixActivitySuperArrow(
                title = "妙播设置",
                context = activity,
                activity = MediaSettingsActivity::class.java
            )
            XMiuixSuperDropdown(
                title = "控件·高级材质",
                key = "is_super_blur_Widget",
                option = R.array.is_super_blur_entire,
                activity = activity
            )
            XMiuixClasser(
                title = "亮度条&音量条",
                top = 12.dp
            ){
                XMiuixContentSwitch(switchTitle = "亮度条进度值显示", switchKey = "qs_brightness_top_value_show") {
                    XMiuixSuperDropdown(
                        title = "显示样式",
                        key = "qs_brightness_top_value",
                        option = R.array.seekbar_value_style_entire,
                        activity = activity)
                }
                XMiuixContentSwitch(switchTitle = "音量条进度值显示", switchKey = "qs_volume_top_value_show") {
                    XMiuixSuperDropdown(
                        title = "显示样式",
                        key = "qs_volume_top_value",
                        option = R.array.seekbar_value_style_entire,
                        activity = activity)
                }
            }
            XMiuixClasser(
                title = "普通磁贴",
                top = 12.dp
            ){
                XMiuixSuperSwitch(
                    title = "背景圆角矩形",
                    key = "is_qs_tile_radius"
                )
                XMiuixSuperSwitch(
                    title = "标题颜色跟随图标",
                    key = "qs_list_tile_color_for_icon"
                )
                XMiuixSuperSwitch(
                    title = "标题超出跑马灯特效显示",
                    key = "list_tile_label_marquee"
                )
                XMiuixContentDropdown(
                    title = "标题显示样式",
                    key = "is_list_label_mode",
                    option = R.array.is_list_label_mode_entire,
                    showOption = 2,
                    activity = activity,
                ){
                    MiuixActivitySuperArrow(
                        title = "磁贴布局",
                        context = activity,
                        activity = QsListViewSettings::class.java
                    )

                }


            }

        }
    }

}
