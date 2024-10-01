package com.chaos.hyperstar.ui.module.controlcenter.ui

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.ui.module.controlcenter.card.QsCardListActivity
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.base.XMiuixContentDropdown
import com.chaos.hyperstar.ui.base.XMiuixContentSwitch
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.XMiuixSliders
import com.chaos.hyperstar.ui.base.XMiuixSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSliderSwitch
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
                title = "头部",
                top = 12.dp
            ){

                XMiuixSuperSwitch(
                    title = "禁用下拉时钟动画",
                    key = "close_qs_clock_anim"
                )

                XMiuixSuperSwitch(
                    title = "向顶栏添加设置&编辑按钮",
                    key = "is_use_chaos_header"
                )

                XMiuixContentSwitch(
                    switchTitle = "关闭设备状态提示",
                    switchKey = "close_header_show_message",
                    contrary = true
                ){
                    XMiuixSlider(
                        title = "设备状态提示持续时长",
                        key = "header_show_message_millis",
                        progress = 1f,
                        maxValue = 5f,
                        minValue = 0.1f,
                        unit = "s",
                        decimalPlaces = 2,
                    )
                }
            }
            XMiuixClasser(
                title = "卡片磁贴",
                top = 12.dp
            ){
                XMiuixSuperSwitch(
                    title = "自动收起",
                    summary = "开启后点击卡片磁贴会自动收起状态栏",
                    key = "card_tile_click_close"
                )
                XMiuixContentSwitch(switchTitle = "启用卡片磁贴编辑", switchKey = "use_card_tile_list") {
                    MiuixActivitySuperArrow(
                        title = "卡片磁贴编辑",
                        context = activity,
                        activity = QsCardListActivity::class.java

                    )

                }
            }
            XMiuixClasser(
                title = "亮度条&音量条",
                top = 12.dp
            ){
                XMiuixSuperSliderSwitch(
                    switchTitle = "进度条圆角自定义",
                    switchKey = "is_change_qs_progress_radius",
                    title = "进度条圆角值" ,
                    key ="qs_progress_radius",
                    minValue = 0f,
                    maxValue = 20f,
                    progress = 2f,
                    unit = "dp",
                    decimalPlaces = 1
                )
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
                    title = "自动收起",
                    summary = "开启后点击普通磁贴会自动收起状态栏",
                    key = "list_tile_click_close"
                )
                XMiuixSuperSliderSwitch(
                    switchTitle = "背景圆角自定义",
                    switchSummary = "开启后，默认与其他控件圆角值（官方原版）一致",
                    switchKey = "is_qs_list_tile_radius",
                    title = "背景圆角值",
                    key = "qs_list_tile_radius",
                    minValue = 0f,
                    maxValue = 36f,
                    progress = 20f,
                    unit = "dp",
                    decimalPlaces = 1
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
                ColorPickerTool(

                    title = "关闭背景色",
                    dfColor = Color(0x00000000),
                    key = "list_disabled_color"

                )
                ColorPickerTool(

                    title = "开启背景色",
                    dfColor = Color(0x00000000),
                    key = "list_enabled_color"

                )
                ColorPickerTool(

                    title = "开启背景色（特殊）",
                    dfColor = Color(0x00000000),
                    key = "list_warning_color"

                )
                ColorPickerTool(

                    title = "临时开启背景色",
                    dfColor = Color(0x00000000),
                    key = "list_restricted_color"

                )
                ColorPickerTool(

                    title = "禁用背景色",
                    dfColor = Color(0x00000000),
                    key = "list_unavailable_color"

                )


            }

            XMiuixClasser(
                title = "其他",
                top = 12.dp
            ){
                XMiuixSuperSwitch(
                    title = "关闭编辑显示",
                    key = "close_edit_button_show"
                )


            }

        }
    }

}