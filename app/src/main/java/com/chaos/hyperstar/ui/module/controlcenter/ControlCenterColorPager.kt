package com.chaos.hyperstar.ui.module.controlcenter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.ColorPickerTool
import com.chaos.hyperstar.ui.base.ContentFolder
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.controlcenter.card.QSCardColorActivity
import com.chaos.hyperstar.ui.module.controlcenter.list.QSListColorActivity
import com.chaos.hyperstar.utils.Utils

@Composable
fun ControlCenterColorPager(
    activity: ComponentActivity,
) {
    ActivityPagers(
        activityTitle = stringResource(R.string.control_center_color_edit),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ) {

        firstClasses(
            title = "控件背景颜色"
        ){

            ColorPickerTool(
                title = "非高级材质",
                key = "background_color"
            )
            ContentFolder("高级材质"){

                ColorPickerTool(
                    title = "混色·主",
                    key = "background_blend_color_main"
                )
                ColorPickerTool(
                    title = "混色·次",
                    key = "background_blend_color_secondary"
                )
            }


        }

        classes(
            title = "卡片磁贴"
        ){
            MiuixActivitySuperArrow(
                title = "颜色编辑",
                context = activity,
                activity = QSCardColorActivity::class.java
            )
        }

        classes(
            title = "妙播"
        ){

        }

        classes(
            title = "亮度条"
        ){

        }
        classes(
            title = "音量条"
        ){

        }
        classes(
            title = "融合设备中心"
        ){

        }
        classes(
            title = "米家"
        ){

        }
        classes(
            title = "普通磁贴"
        ){
            MiuixActivitySuperArrow(
                title = "颜色编辑",
                context = activity,
                activity = QSListColorActivity::class.java
            )
        }
        classes(
            title = "编辑"
        ){

        }


    }
}