package com.chaos.hyperstar.ui.base

import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Icon
import com.chaos.hyperstar.R
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.MiuixNavigationBar
import top.yukonga.miuix.kmp.MiuixScrollBehavior
import top.yukonga.miuix.kmp.MiuixTopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun SubMiuixTopAppBar(
    title : String,
    scrollBehavior: MiuixScrollBehavior? = null,
    color : Color,
    activity: ComponentActivity,
    endIcon :  @Composable () -> Unit = {},
    endClick:(() -> Unit) = {},
    ){


    val view = LocalView.current

    MiuixTopAppBar(
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                modifier = Modifier.padding(start = 12.dp),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    activity.finish()
                }
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.bar_back__exit),
                    contentDescription = "back",
                    tint = colorScheme.onPrimary)
            }

        },
        actions = {


            endIcon()

            IconButton(
                modifier = Modifier.padding(end = 12.dp),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    endClick()

                }
            ) {

                Icon(
                    ImageVector.vectorResource(R.drawable.ic_menu_refresh),
                    contentDescription = "restart",
                    tint = colorScheme.onPrimary)

            }

        }
    )

}