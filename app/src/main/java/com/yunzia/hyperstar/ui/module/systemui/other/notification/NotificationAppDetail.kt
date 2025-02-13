package com.yunzia.hyperstar.ui.module.systemui.other.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.FloatingActionButton
import com.yunzia.hyperstar.ui.base.MTextField
import com.yunzia.hyperstar.ui.base.TopButton
import com.yunzia.hyperstar.ui.base.XScaffold
import com.yunzia.hyperstar.ui.base.modifier.blur
import com.yunzia.hyperstar.ui.base.modifier.bounceAnim
import com.yunzia.hyperstar.ui.base.modifier.showBlur
import com.yunzia.hyperstar.ui.base.nav.backParentPager
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.LazyColumn
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.TopAppBar
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape
import top.yukonga.miuix.kmp.utils.getWindowSize

@Composable
fun NotificationAppDetail(
    navController: NavHostController,
    currentStartDestination: MutableState<String>
) {
    val scrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())
    val hazeState = remember { HazeState() }
    XScaffold(
        topBar = {
            TopAppBar(
                modifier = Modifier.showBlur(hazeState),
                color = Color.Transparent,
                title = "",
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    TopButton(
                        modifier = Modifier.padding(start = 18.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_close),
                        contentDescription = "close",
                        onClick = { navController.backParentPager(currentStartDestination.value) }

                    )


                },
                actions = {
                    TopButton(
                        modifier = Modifier.padding(end = 18.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.ic_done),
                        contentDescription = "done",
                        onClick = { navController.backParentPager(currentStartDestination.value) }
                    )
                }
            )
        },
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

    ){ padding ->

        BackHandler(true) {
            navController.backParentPager(currentStartDestination.value)
        }

        LazyColumn(
            modifier = Modifier
                .height(getWindowSize().height.dp)
                .blur(hazeState),
            contentPadding = PaddingValues(
                top = padding.calculateTopPadding()+14.dp,
                bottom = padding.calculateBottomPadding()+14.dp
            ),
            topAppBarScrollBehavior = scrollBehavior
        ) {

            idItem("111")

        }

    }

}


fun LazyListScope.idItem(
    id:String

){
    item(
        id
    ) {
        val channelId = remember { mutableStateOf(id) }

        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(
                    colorScheme.onSecondary,
                    SmoothRoundedCornerShape(16.dp,0.5f)
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
