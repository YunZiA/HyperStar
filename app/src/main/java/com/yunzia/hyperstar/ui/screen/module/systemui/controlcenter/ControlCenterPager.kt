package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.ControlCenterList
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.SuperNavHostArrow
import com.yunzia.hyperstar.ui.component.SuperStringArrow
import com.yunzia.hyperstar.ui.component.SwitchContentFolder
import com.yunzia.hyperstar.ui.component.XMiuixContentDropdown
import com.yunzia.hyperstar.ui.component.XMiuixSlider
import com.yunzia.hyperstar.ui.component.XMiuixSuperSliderSwitch
import com.yunzia.hyperstar.ui.component.XSuperDropdown
import com.yunzia.hyperstar.ui.component.XSuperSwitch
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.modifier.nestedOverScrollVertical
import com.yunzia.hyperstar.utils.isOS2Settings
import top.yukonga.miuix.kmp.basic.ScrollBehavior


@Composable
fun ControlCenterPager(
    navController: NavHostController,
    scrollBehavior: ScrollBehavior,
    paddingValue: PaddingValues
) {
    LazyColumn(
        modifier = Modifier.nestedOverScrollVertical(scrollBehavior.nestedScrollConnection),
        contentPadding = PaddingValues(bottom = paddingValue.calculateBottomPadding())
    ) {
        itemGroup(
            title = R.string.basics,
            position = SuperGroupPosition.FIRST
        ){
            XSuperDropdown(
                title = stringResource(R.string.widget_advanced_textures),
                key = "is_super_blur_Widget",
                option = R.array.is_super_blur_entire,
            )

            SuperNavHostArrow(
                title = stringResource(R.string.color_edit),
                navController = navController,
                route = ControlCenterList.COLOR_EDIT
            )
            SuperNavHostArrow(
                title = stringResource(R.string.control_center_edit),
                navController = navController,
                route = ControlCenterList.LAYOUT_ARRANGEMENT
            )
            SuperNavHostArrow(
                title = stringResource(R.string.media_settings),
                navController = navController,
                route = ControlCenterList.MEDIA
            )
        }


        itemGroup(
            title = R.string.header
        ){


            if (!isOS2Settings()){
                XSuperSwitch(
                    title = stringResource(R.string.close_qs_clock_anim_title),
                    key = "close_qs_clock_anim"
                )

            }

            XSuperSwitch(
//                enabled = false,
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
                    defValue = 1f,
                    maxValue = 5f,
                    minValue = 0.1f,
                    unit = "s",
                    decimalPlaces = 2,
                )
            }
        }
        itemGroup(
            title = R.string.card_tile
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
                SuperNavHostArrow(
                    title = stringResource(R.string.card_tile_edit),
                    navController = navController,
                    route = ControlCenterList.CARD_LIST
                )

            }
        }
        itemGroup(
            title = R.string.volume_or_brightness
        ){
            XMiuixSuperSliderSwitch(
                switchTitle = stringResource(R.string.is_change_qs_progress_radius_title),
                switchSummary = stringResource(R.string.progress_radius_summary),
                switchKey = "is_change_qs_progress_radius",
                title = stringResource(R.string.qs_progress_radius_title) ,
                key ="qs_progress_radius",
                minValue = 0f,
                maxValue = 20f,
                progress = 2f,
                unit = "dp",
                decimalPlaces = 1
            )
            XSuperSwitch(
                title = stringResource(R.string.qs_brightness_top_value_show_title),
                key = "qs_brightness_top_value_show"
            )
            if (!isOS2Settings()){
                XSuperSwitch(
                    title = stringResource(R.string.qs_volume_top_value_show_title),
                    key = "qs_volume_top_value_show"
                )
            }
        }
        this.itemGroup(
            title = R.string.device_center,
        ){
            XSuperDropdown(
                title = stringResource(R.string.device_center_ist),
                key = "is_device_center_mode",
                option = R.array.is_device_center_mode_entire,
            )
        }
        itemGroup(
            title = R.string.tile
        ){

            XSuperSwitch(
                title = stringResource(R.string.list_tile_click_close_title),
                summary = stringResource(R.string.list_tile_click_close_summary),
                key = "list_tile_click_close"
            )
            XSuperSwitch(
                title = stringResource(R.string.title_fix_list_tile_icon_scale),
                key = "fix_list_tile_icon_scale"
            )
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

            XMiuixContentDropdown(
                title = stringResource(R.string.is_list_label_mode_title),
                key = "is_list_label_mode",
                option = R.array.is_list_label_mode_entire,
                showOptions = 0,
                contents = {

                    XSuperDropdown(
                        title = stringResource(R.string.wordless_mode),
                        key = "is_wordless_mode_0",
                        option = R.array.is_wordless_mode_entire,
                    )
                },
                showOption = 2,
            ){
                SuperNavHostArrow(
                    title = stringResource(R.string.tile_layout),
                    navController = navController,
                    route = ControlCenterList.TILE_LAYOUT
                )
            }

            XSuperSwitch(
                title = stringResource(R.string.enable_title_follow_animation),
                key = "title_follow_anim"
            )

            XSuperSwitch(
                title = stringResource(R.string.list_tile_label_marquee_title),
                key = "list_tile_label_marquee"
            )


        }

        itemGroup(
            title = R.string.other,
            position = SuperGroupPosition.LAST
        ){
            SuperStringArrow(
                title = stringResource(R.string.title_qs_customize_entry_button_text),
                key = "qs_customize_entry_button_text"
            )
            XSuperSwitch(
                title = stringResource(R.string.close_edit_button_show_title),
                key = "close_edit_button_show"
            )


        }

    }
}

