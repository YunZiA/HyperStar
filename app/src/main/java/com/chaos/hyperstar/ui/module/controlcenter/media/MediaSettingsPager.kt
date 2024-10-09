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
        activityTitle = stringResource(R.string.media_settings),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){
        firstClasses(
            title = R.string.base_settings
        ){
            MiuixActivitySuperArrow(
                title = stringResource(R.string.media_default_app_settings),
                context = activity,
                activity = MediaDefaultAppSettingsActivity::class.java
            )

        }
        classes(
            title = R.string.mipalyer_normal,
            top = 12.dp,
        ){
            SwitchContentFolder(
                switchTitle = stringResource(R.string.is_hide_cover_title),
                switchKey = "is_hide_cover",
            ){
                XSuperSwitch(
                    title = stringResource(R.string.is_title_center_title),
                    key = "is_title_center"
                )

            }
            XSuperSwitch(
                title = stringResource(R.string.is_title_marquee_title),
                key = "is_title_marquee"
            )
            XSuperSwitch(
                title = stringResource(R.string.is_artist_marquee_title),
                key = "is_artist_marquee"
            )
            XSuperSwitch(
                title = stringResource(R.string.is_emptyState_marquee_title),
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
            title = R.string.miplayer_expand,
            top = 12.dp,
        ){
            XSuperDropdown(
                title = stringResource(R.string.is_local_speaker_title),
                key = "is_local_speaker",
                option = R.array.is_local_speaker_entire,
            )
        }
    }
}


