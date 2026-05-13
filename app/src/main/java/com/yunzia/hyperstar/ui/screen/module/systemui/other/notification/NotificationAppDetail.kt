package com.yunzia.hyperstar.ui.screen.module.systemui.other.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.FloatingActionButton
import com.yunzia.hyperstar.ui.component.MTextField
import com.yunzia.hyperstar.ui.component.preference.PreferenceScreen
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.shapes.SmoothRoundedCornerShape
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.SystemUIRoutes
import SearchRoute
import androidx.activity.compose.LocalActivity
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.ui.component.preference.PreferenceScope

@Composable
fun NotificationAppDetail() {
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val startClick: () -> Unit = { navController.goBack() }
    val endClick: () -> Unit = { navController.goBack() }

    PreferenceScreen(
        title = "",
        navController = navController,
        startClick = startClick,
        endClick = endClick,
        scrollToKey = activity.appViewModel.scrollToKey.value,
        onScrollComplete = { activity.appViewModel.scrollToKey.value = null },
        floatingActionButton = {
            FloatingActionButton(
                modifier = Modifier.bounceAnim(),
                containerColor = colorScheme.surface,
                insideMargin = PaddingValues(end = 10.dp, bottom = 50.dp),
                onClick = {}
            ) {
                Icon(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_add),
                    tint = colorScheme.onTertiaryContainer,
                    contentDescription = "add"
                )
            }
        }
    ) { _, _ ->
        idItem("111")
    }
}

fun PreferenceScope.idItem(
    id: String
) {
    this.list.item(id) {
        val channelId = remember { mutableStateOf(id) }
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(
                    colorScheme.onSecondary,
                    SmoothRoundedCornerShape(16.dp)
                )
        ) {
            MTextField(
                value = channelId.value,
                modifier = Modifier.padding(3.dp),
                cornerRadius = 14.8.dp,
                backgroundColor = colorScheme.onSecondary,
                onValueChange = {
                    channelId.value = it
                }
            )
        }
    }
}
