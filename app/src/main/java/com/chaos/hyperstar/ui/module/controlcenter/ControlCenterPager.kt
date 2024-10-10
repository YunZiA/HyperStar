package com.chaos.hyperstar.ui.module.controlcenter

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.chaos.hyperstar.ui.module.controlcenter.card.QsCardListActivity
import com.chaos.hyperstar.R
import com.chaos.hyperstar.ui.base.ModulePagers
import com.chaos.hyperstar.ui.base.MiuixActivitySuperArrow
import com.chaos.hyperstar.ui.base.SwitchContentFolder
import com.chaos.hyperstar.ui.base.XMiuixContentDropdown
import com.chaos.hyperstar.ui.base.XMiuixSlider
import com.chaos.hyperstar.ui.base.XSuperDropdown
import com.chaos.hyperstar.ui.base.XMiuixSuperSliderSwitch
import com.chaos.hyperstar.ui.base.XSuperSwitch
import com.chaos.hyperstar.ui.base.classes
import com.chaos.hyperstar.ui.base.firstClasses
import com.chaos.hyperstar.ui.module.controlcenter.list.QsListViewSettings
import com.chaos.hyperstar.ui.module.controlcenter.media.MediaSettingsActivity
import com.chaos.hyperstar.utils.Utils


@Composable
fun ControlCenterPager(
    activity: ComponentActivity,
) {
    ModulePagers(
        activityTitle = stringResource(R.string.control_center),
        activity = activity,
        endClick = {
            Utils.rootShell("killall com.android.systemui")
        },
    ){
        firstClasses(
            title = R.string.basics,
        ){

            XSuperDropdown(
                title = stringResource(R.string.widget_advanced_textures),
                key = "is_super_blur_Widget",
                option = R.array.is_super_blur_entire,
            )
            MiuixActivitySuperArrow(
                title = stringResource(R.string.color_edit),
                context = activity,
                activity = ControlCenterColorSettings::class.java
            )
            MiuixActivitySuperArrow(
                title = stringResource(R.string.control_center_edit),
                context = activity,
                activity = ControlCenterListSettings::class.java
            )
            MiuixActivitySuperArrow(
                title = stringResource(R.string.media_settings),
                context = activity,
                activity = MediaSettingsActivity::class.java
            )
        }


        classes(
            title = R.string.header,
            top = 12.dp
        ){

            XSuperSwitch(
                title = stringResource(R.string.close_qs_clock_anim_title),
                key = "close_qs_clock_anim"
            )

            XSuperSwitch(
                title = stringResource(R.string.is_use_chaos_header_title),
                key = "is_use_chaos_header"
            )

            SwitchContentFolder(
                switchTitle = stringResource(R.string.close_header_show_message_title),
                switchKey = "close_header_show_message",
                contrary = true
            ){
                XMiuixSlider(
                    title = stringResource(R.string.header_show_message_millis_title),
                    key = "header_show_message_millis",
                    progress = 1f,
                    maxValue = 5f,
                    minValue = 0.1f,
                    unit = "s",
                    decimalPlaces = 2,
                )
            }
        }
        classes(
            title = R.string.card_tile,
            top = 12.dp
        ){
            XSuperSwitch(
                title = stringResource(R.string.card_tile_click_close_title),
                summary = stringResource(R.string.card_tile_click_close_summary),
                key = "card_tile_click_close"
            )
            SwitchContentFolder(
                switchTitle = stringResource(R.string.enable_card_tile_edit),
                switchKey = "use_card_tile_list"
            ) {
                MiuixActivitySuperArrow(
                    title = stringResource(R.string.card_tile_edit),
                    context = activity,
                    activity = QsCardListActivity::class.java

                )

            }
        }
        classes(
            title = R.string.volume_or_brightness,
            top = 12.dp
        ){
            XMiuixSuperSliderSwitch(
                switchTitle = stringResource(R.string.is_change_qs_progress_radius_title),
                switchKey = "is_change_qs_progress_radius",
                title = stringResource(R.string.qs_progress_radius_title) ,
                key ="qs_progress_radius",
                minValue = 0f,
                maxValue = 20f,
                progress = 2f,
                unit = "dp",
                decimalPlaces = 1
            )
            SwitchContentFolder(
                switchTitle = stringResource(R.string.qs_brightness_top_value_show_title),
                switchKey = "qs_brightness_top_value_show"
            ) {
                XSuperDropdown(
                    title = stringResource(R.string.qs_brightness_top_value_title),
                    key = "qs_brightness_top_value",
                    option = R.array.seekbar_value_style_entire
                )
            }
            SwitchContentFolder(
                switchTitle = stringResource(R.string.qs_volume_top_value_show_title),
                switchKey = "qs_volume_top_value_show"
            ) {
                XSuperDropdown(
                    title = stringResource(R.string.qs_volume_top_value_title),
                    key = "qs_volume_top_value",
                    option = R.array.seekbar_value_style_entire
                )
            }
        }
        classes(
            title = R.string.tile,
            top = 12.dp
        ){

            XMiuixSuperSliderSwitch(
                switchTitle = stringResource(R.string.is_qs_list_tile_radius_title),
                switchSummary = stringResource(R.string.is_qs_list_tile_radius_summary),
                switchKey = "is_qs_list_tile_radius",
                title = stringResource(R.string.qs_list_tile_radius_title),
                key = "qs_list_tile_radius",
                minValue = 0f,
                maxValue = 36f,
                progress = 20f,
                unit = "dp",
                decimalPlaces = 1
            )

            XSuperSwitch(
                title = stringResource(R.string.list_tile_label_marquee_title),
                key = "list_tile_label_marquee"
            )
            XMiuixContentDropdown(
                title = stringResource(R.string.is_list_label_mode_title),
                key = "is_list_label_mode",
                option = R.array.is_list_label_mode_entire,
                showOption = 2,
                activity = activity,
            ){
                MiuixActivitySuperArrow(
                    title = stringResource(R.string.tile_layout),
                    context = activity,
                    activity = QsListViewSettings::class.java
                )

            }
            XSuperSwitch(
                title = stringResource(R.string.list_tile_click_close_title),
                summary = stringResource(R.string.list_tile_click_close_summary),
                key = "list_tile_click_close"
            )



        }

        classes(
            title = R.string.other,
            top = 12.dp
        ){
            XSuperSwitch(
                title = stringResource(R.string.close_edit_button_show_title),
                key = "close_edit_button_show"
            )


        }

    }
}

