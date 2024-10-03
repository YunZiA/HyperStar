package com.chaos.hyperstar.ui.module.controlcenter.media

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ActivityPagers
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.SwitchContentFolder
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSliderSwitch
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.controlcenter.media.app.MediaDefaultAppSettingsActivity
import com.chaos.hyperstar.utils.Utils


@Composable
fun MediaSettingsPager(activity: ComponentActivity) {
    ActivityPagers(
        activityTitle = "妙播设置",
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){
        firstClasses(
            title = "基础设置"
        ){
            MiuixActivitySuperArrow(
                title = "默认播放应用选择",
                context = activity,
                activity = MediaDefaultAppSettingsActivity::class.java
            )

        }
        classes(
            title = "常规播放页",
            top = 12.dp,
        ){
            SwitchContentFolder(
                switchTitle = "隐藏歌曲封面显示",
                switchKey = "is_hide_cover",
            ){
                XSuperSwitch(
                    title = "标题&歌手居中显示",
                    key = "is_title_center"
                )

            }
            XSuperSwitch(
                title = "标题过长滚动显示",
                key = "is_title_marquee"
            )
            XSuperSwitch(
                title = "歌手过长滚动显示",
                key = "is_artist_marquee"
            )
            XSuperSwitch(
                title = "暂无播放过长滚动显示",
                key = "is_emptyState_marquee"
            )
            SwitchContentFolder(
                switchTitle = stringResource(R.string.is_cover_background_title),
                switchKey = "is_cover_background",
            ){
                XMiuixSuperSliderSwitch(
                    switchTitle = stringResource(R.string.is_cover_scale_background_title),
                    switchKey = "is_cover_scale_background",
                    title = stringResource(R.string.cover_scale_background_value_title),
                    key = "cover_scale_background_value",
                    progress = 1.5f,
                    maxValue = 2f,
                    minValue = 1.1f,
                    decimalPlaces = 2
                )
                XMiuixSuperSliderSwitch(
                    switchTitle = stringResource(R.string.is_cover_blur_background_title),
                    switchKey = "is_cover_blur_background",
                    title = stringResource(R.string.cover_blur_background_value_title),
                    key = "cover_blur_background_value",
                    progress = 50f,
                    maxValue = 60f,
                    minValue = 0f,
                    decimalPlaces = 2
                )
                XMiuixSuperSliderSwitch(
                    switchTitle = stringResource(R.string.is_cover_dim_background_title),
                    switchKey = "is_cover_dim_background",
                    title = stringResource(R.string.cover_dim_background_value_title),
                    key = "cover_dim_background_value",
                    progress = 50f,
                    maxValue = 255f,
                    minValue = 0f
                )

                XSuperSwitch(
                    title = "启用封面背景暗边",
                    key = "cover_anciently"
                )

            }


        }

        classes(
            title = "扩展详情页",
            top = 12.dp,
        ){
            XSuperDropdown(
                title = "设备名显示模式",
                key = "is_local_speaker",
                option = R.array.is_local_speaker_entire,
            )
        }
    }
}


