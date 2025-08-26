package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.media

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.ControlCenterList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperContentDropdown
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.SuperStringArrow
import com.yunzia.hyperstar.ui.component.SwitchContentFolder
import com.yunzia.hyperstar.ui.component.XMiuixSuperSliderSwitch
import com.yunzia.hyperstar.ui.component.XSuperDropdown
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper


@Composable
fun MediaSettingsScreen(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.media_settings),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        },
    ){
        itemGroup(
            title = R.string.base_settings,
            position = SuperGroupPosition.FIRST
        ){
            SuperNavHostArrow(
                title = stringResource(R.string.media_default_app_settings),
                navController = navController,
                route = ControlCenterList.MEDIA_APP
            )
            SuperStringArrow(
                title = stringResource(R.string.title_miplay_detail_header_no_song),
                key = "miplay_detail_header_no_song"
            )

        }
        itemGroup(
            title = R.string.mipalyer_normal
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
            SuperContentDropdown(
                title = stringResource(R.string.media_background_style_title),
                key = "media_background_style",
                option = R.array.media_background_style_entire
            ){
                AnimatedVisibility (
                    (it.value == 2),
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column{
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
                            unit = "%",
                            progress = 0f,
                            maxValue = 100f,
                            minValue = 0f
                        )

                        XSuperSwitch(
                            title = stringResource(R.string.cover_anciently_title),
                            key = "cover_anciently"
                        )

                    }
                }
            }



        }

        itemGroup(
            title = R.string.miplayer_expand,
            position = SuperGroupPosition.LAST
        ){
            XSuperSwitch(
                title = stringResource(R.string.title_qs_detail_app_icon_radius),
                key = "qs_detail_app_icon_radius"
            )
            XSuperSwitch(
                title = stringResource(R.string.title_qs_detail_progress_bg_radius),
                key = "qs_detail_progress_bg_radius"
            )
            XSuperSwitch(
                title = stringResource(R.string.title_detail_volumebar_show_value),
                key = "is_detail_volumebar_show_value"
            )
            XSuperDropdown(
                title = stringResource(R.string.is_local_speaker_title),
                key = "is_local_speaker",
                option = R.array.is_local_speaker_entire,
            )
        }
    }
}


