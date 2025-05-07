package com.yunzia.hyperstar.ui.component.pager

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.InputField
import com.yunzia.hyperstar.ui.component.ModuleNavTopAppBar
import com.yunzia.hyperstar.ui.component.XScaffold
import com.yunzia.hyperstar.ui.component.modifier.blur
import com.yunzia.hyperstar.ui.component.modifier.showBlur
import com.yunzia.hyperstar.ui.component.nav.backParentPager
import com.yunzia.hyperstar.ui.component.search.SearchBox
import com.yunzia.hyperstar.ui.component.search.SearchPager
import com.yunzia.hyperstar.ui.component.search.SearchStatus
import dev.chrisbanes.haze.HazeState
import top.yukonga.miuix.kmp.basic.FabPosition
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.rememberTopAppBarState
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme
import top.yukonga.miuix.kmp.utils.BackHandler
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun SearchModuleNavPager(
    activityTitle: String,
    searchStatus: SearchStatus,
    navController: NavController,
    parentRoute: MutableState<String>,
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: FabPosition = FabPosition.End,
    startClick: () -> Unit = {
        navController.backParentPager(parentRoute.value)
    },
    endClick: () -> Unit,
    endIcon: @Composable () -> Unit = {},
    result: LazyListScope.(ScrollBehavior)-> Unit,
    contents: @Composable (ScrollBehavior, PaddingValues) -> Unit
) {

    val hazeState = remember { HazeState() }
    val topAppBarScrollBehavior = MiuixScrollBehavior(rememberTopAppBarState())

    XScaffold(
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingPagerButton = floatingPagerButton,
        topBar = {
            searchStatus.TopAppBarAnim{
                ModuleNavTopAppBar(
                    modifier = Modifier.showBlur(hazeState),
                    color = Color.Transparent,
                    title = activityTitle,
                    scrollBehavior = topAppBarScrollBehavior,
                    startClick = startClick,
                    endIcon = endIcon,
                    endClick = {
                        endClick()
                    }
                )

            }
        }
    ) { padding ->

        BackHandler(true) {
            navController.backParentPager(parentRoute.value)
        }

        searchStatus.SearchBox(
            modifier = Modifier
                .blur(hazeState)
                .padding(top = padding.calculateTopPadding() + 14.dp)
                .fillMaxSize(),
        ){
            contents(topAppBarScrollBehavior, padding)

        }

    }

    searchStatus.SearchPager(
        {}
    ) {
        result(topAppBarScrollBehavior)
    }


}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    label: String = stringResource(R.string.app_name_type),
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
){

    val activity = LocalActivity.current as MainActivity
    InputField(
        query = query,
        onQueryChange = { onQueryChange(it) },
        label = label,
        leadingIcon = {
            Icon(
                ImageVector.vectorResource(R.drawable.ic_search_icon),
                contentDescription = "back",
                modifier = Modifier
                    .size(44.dp)
                    .padding(start = 16.dp, end = 8.dp),
                tint = colorScheme.onSurface
            )
        },
        trailingIcon= {
            AnimatedVisibility(
                query.isNotEmpty(),
                enter = fadeIn() + scaleIn(),
                exit =  fadeOut() + scaleOut(),
            ) {
                Icon(
                    if (activity.isDarkMode){
                        ImageVector.vectorResource(R.drawable.search_clear_dark)
                    }else{
                        ImageVector.vectorResource(R.drawable.search_clear_light)
                    },
                    contentDescription = "back",
                    modifier = Modifier
                        .size(44.dp)
                        .padding(start = 8.dp, end = 16.dp)
                        .clickable(
                            interactionSource = null,
                            indication = null
                        ){
                            onQueryChange("")
                        },
                    tint = colorScheme.onSurface
                )

            }

        },
        modifier = Modifier.padding(horizontal = 16.dp),
        onSearch = { it },
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) }
    )


}

@Composable
fun SearchBarFake(
    label: String
){

    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                color = colorScheme.surfaceContainerHigh,
                shape = SmoothRoundedCornerShape(50.dp)
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            ImageVector.vectorResource(R.drawable.ic_search_icon),
            contentDescription = "back",
            modifier = Modifier
                .size(44.dp)
                .padding(start = 16.dp, end = 8.dp),
            tint = colorScheme.onSurface
        )
        Box(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = label,
                color = colorScheme.onSurfaceContainerHigh
            )

        }

    }

}

