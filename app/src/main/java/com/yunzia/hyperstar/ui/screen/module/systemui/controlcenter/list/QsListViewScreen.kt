package com.yunzia.hyperstar.ui.screen.module.systemui.controlcenter.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import com.yunzia.hyperstar.ui.component.XMiuixSlider
import com.yunzia.hyperstar.ui.component.XSuperDropdown
import com.yunzia.hyperstar.ui.component.itemGroup
import com.yunzia.hyperstar.ui.component.pager.ModuleNavPagers
import com.yunzia.hyperstar.utils.Helper


@Composable
fun QsListViewScreen(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {
    ModuleNavPagers(
        activityTitle = stringResource(R.string.tile_layout),
        parentRoute = currentStartDestination,
        navController = navController,
        endClick = {
            Helper.rootShell("killall com.android.systemui")
        }
    ){
        itemGroup(
            position = SuperGroupPosition.FIRST
        ){

            XSuperDropdown(
                title = stringResource(R.string.wordless_mode),
                key = "is_wordless_mode_2",
                option = R.array.is_wordless_mode_entire,
            )


        }

        itemGroup(
            title = R.string.title_style
        ){
            XMiuixSlider(
                title = stringResource(R.string.title_size),
                key = "list_label_size",
                unit = "dp",
                maxValue = 25f,
                minValue = 0f,
                defValue = 13f,
                decimalPlaces = 2
            )

            XMiuixSlider(
                title = stringResource(R.string.title_width),
                key = "list_label_width",
                unit = "%",
                maxValue = 100f,
                minValue = 0f,
                defValue = 100f
            )

        }
        itemGroup(
            title = R.string.vertical_spacing
        ){
            XMiuixSlider(
                title = stringResource(R.string.disable_icon_labels),
                key = "list_spacing_y",
                unit = "%",
                maxValue = 150f,
                minValue = 0f,
                defValue = 100f
            )

            XMiuixSlider(
                title = stringResource(R.string.enable_icon_labels),
                key = "list_label_spacing_y",
                unit = "%",
                maxValue = 150f,
                minValue = 0f,
                defValue = 100f
            )

        }


        itemGroup(
            title = R.string.margin_top,
            position = SuperGroupPosition.FIRST
        ){
            XMiuixSlider(
                title = stringResource(R.string.icon),
                key = "list_icon_top",
                unit = "%",
                maxValue = 50F,
                minValue = -50f,
                defValue = 0f
            )

            XMiuixSlider(
                title = stringResource(R.string.title),
                key = "list_label_top",
                unit = "dp",
                maxValue = 100f,
                minValue = -100f,
                defValue = 0f,
                decimalPlaces = 1
            )

        }


    }

}
