package com.yunzia.hyperstar.ui.base

import android.view.HapticFeedbackConstants
import androidx.activity.ComponentActivity
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.R
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun ModuleTopAppBar(
    modifier: Modifier,
    title : String,
    scrollBehavior: ScrollBehavior? = null,
    color : Color,
    activity: ComponentActivity,
    endIcon :  @Composable () -> Unit = {},
    endClick:() -> Unit = {}
){

    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            TopButton(
                modifier = Modifier.padding(start = 18.dp),
                imageVector = ImageVector.vectorResource(R.drawable.bar_back__exit),
                contentDescription = "back",
                onClick = {
                    activity.finish()
                }
            )


        },
        actions = {
            endIcon()
            TopButton(
                modifier = Modifier.padding(end = 18.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_menu_refresh),
                contentDescription = "restart",
                onClick = endClick
            )

        }
    )

}
@Composable
fun ActivityTopAppBar(
    modifier: Modifier,
    title : String,
    scrollBehavior: ScrollBehavior? = null,
    color : Color,
    activity: ComponentActivity,
    actions: @Composable() (RowScope.() -> Unit) = {}
){

    val view = LocalView.current

    TopAppBar(
        modifier = modifier,
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
                    tint = colorScheme.onBackground)
            }

        },
        actions = actions
    )

}

@Composable
fun NavTopAppBar(
    modifier: Modifier,
    title : String,
    scrollBehavior: ScrollBehavior? = null,
    color : Color,
    navController: NavController,
    actions: @Composable() (RowScope.() -> Unit) = {}
){

    val view = LocalView.current

    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            IconButton(
                modifier = Modifier.padding(start = 12.dp),
                onClick = {
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    navController.popBackStack()
                }
            ) {
                Icon(
                    ImageVector.vectorResource(R.drawable.bar_back__exit),
                    contentDescription = "back",
                    tint = colorScheme.onBackground)
            }

        },
        actions = actions
    )

}

@Composable
fun ModuleNavTopAppBar(
    modifier: Modifier,
    title : String,
    scrollBehavior: ScrollBehavior? = null,
    color : Color,
    navController: NavController,
    endIcon :  @Composable () -> Unit = {},
    endClick:() -> Unit = {}
){

    TopAppBar(
        modifier = modifier,
        color = color,
        title = title,
        scrollBehavior = scrollBehavior,
        navigationIcon = {
            TopButton(
                modifier = Modifier.padding(start = 18.dp),
                imageVector = ImageVector.vectorResource(R.drawable.bar_back__exit),
                contentDescription = "back",
                onClick = {
                    navController.popBackStack()
                }
            )


        },
        actions = {
            endIcon()
            TopButton(
                modifier = Modifier.padding(end = 18.dp),
                imageVector = ImageVector.vectorResource(R.drawable.ic_menu_refresh),
                contentDescription = "restart",
                onClick = endClick
            )

        }
    )

}


@Composable
fun TopButton(
    modifier: Modifier = Modifier,
    imageVector: ImageVector,
    contentDescription: String?,
    tint: Color = colorScheme.onBackground,
    onClick: () -> Unit,

){
    val view = LocalView.current
    Box(
        modifier
            .size(35.dp)
            .clip(RoundedCornerShape(50))
            .clickable {
                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                onClick()

            },
        contentAlignment = Alignment.Center
    ){
        Icon(
            imageVector,
            contentDescription = contentDescription,
            tint = tint
        )
    }
}
