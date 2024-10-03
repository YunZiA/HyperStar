package com.chaos.hyperstar.ui.module.controlcenter.list

import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.ItemAnim
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.utils.SPUtils
import com.chaos.hyperstar.utils.Utils

@Composable
fun QSListColorPager(
    activity: ComponentActivity,
) {
    val tileColor = remember {
        mutableIntStateOf(SPUtils.getInt("qs_list_tile_color_for_state",0))
    }
    val waitTime = 105L
    ActivityPagers(
        activityTitle = "普通磁贴颜色",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {

        firstClasses("通用") {

            XSuperDropdown(
                title = "标题状态色",
                option = R.array.qs_list_tile_color_for_state_entire,
                key = "qs_list_tile_color_for_state",
                selectedIndex = tileColor
            )
            ItemAnim(
                animState = (tileColor.intValue == 0),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = "标题颜色",
                    key = "list_title_color"
                )
            }

        }

        classes(
            title = "关闭颜色"
        ) {
            ColorPickerTool(
                title = "图标",
                key = "list_icon_off_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = "标题颜色",
                    key = "list_title_off_color"
                )
            }

        }

        classes(
            title = "开启颜色"
        ){
            ColorPickerTool(
                title = "常规·背景",
                key = "list_enabled_color"
            )
            ColorPickerTool(
                title = "特殊·背景",
                key = "list_warning_color"
            )
            ColorPickerTool(
                title = "图标",
                key = "list_icon_on_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = "标题颜色",
                    key = "list_title_on_color"
                )
            }
        }

        classes(
            title = "蓝牙临时开启颜色"
        ){
            ColorPickerTool(
                title = "背景",
                key = "list_restricted_color"
            )
            ColorPickerTool(
                title = "图标",
                key = "list_icon_restricted_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = "标题",
                    key = "list_title_restricted_color"
                )
            }
        }

        classes(
            title = "禁用颜色"
        ){
            ColorPickerTool(
                title = "背景",
                key = "list_unavailable_color"
            )
            ColorPickerTool(
                title = "图标",
                key = "list_icon_unavailable_color"
            )
            ItemAnim(
                animState = (tileColor.intValue == 2),
                waitTime = waitTime
            ) {
                ColorPickerTool(
                    title = "标题",
                    key = "list_title_unavailable_color"
                )
            }
        }


    }
}