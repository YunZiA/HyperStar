package com.chaos.hyperstar.ui.module.controlcenter.list

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.XMiuixClasser
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.utils.Utils


@Composable
fun QsListViewPager(activity: ComponentActivity, ) {
    ActivityPagers(
        activityTitle = "磁贴布局",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        }
    ){
        item {
            XMiuixClasser(
                title = "标题样式",
                top = 12.dp
            ){
                XMiuixSlider(
                    title = "标题大小",
                    key = "list_label_size",
                    unit = "dp",
                    maxValue = 25f,
                    minValue = 0f,
                    progress = 13f,
                    decimalPlaces = 2
                )

                XMiuixSlider(
                    title = "标题宽度",
                    key = "list_label_width",
                    unit = "%",
                    maxValue = 100f,
                    minValue = 0f,
                    progress = 100f
                )

            }
            XMiuixClasser(
                title = "竖直间距",
                top = 12.dp
            ){
                XMiuixSlider(
                    title = "无字样式",
                    key = "list_spacing_y",
                    unit = "%",
                    maxValue = 150f,
                    minValue = 0f,
                    progress = 100f
                )

                XMiuixSlider(
                    title = "有字样式",
                    key = "list_label_spacing_y",
                    unit = "%",
                    maxValue = 150f,
                    minValue = 0f,
                    progress = 100f
                )

            }


            XMiuixClasser(
                title = "上边距",
                top = 12.dp
            ){
                XMiuixSlider(
                    title = "图标",
                    key = "list_icon_top",
                    unit = "%",
                    maxValue = 50F,
                    minValue = -50f,
                    progress = 0f
                )

                XMiuixSlider(
                    title = "标题",
                    key = "list_label_top",
                    unit = "%",
                    maxValue = 200F,
                    minValue = -100f,
                    progress = 100f
                )

            }

        }
    }

}
