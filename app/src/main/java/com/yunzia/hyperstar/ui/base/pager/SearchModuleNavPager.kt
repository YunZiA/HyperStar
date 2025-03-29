package com.yunzia.hyperstar.ui.base.pager

import android.util.Log
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.Icon
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.base.AnimStatus
import com.yunzia.hyperstar.ui.base.InputField
import com.yunzia.hyperstar.ui.base.SearchBox
import com.yunzia.hyperstar.ui.base.SearchPager
import com.yunzia.hyperstar.ui.base.nav.backParentPager
import com.yunzia.hyperstar.ui.base.rememberAnimStatus
import top.yukonga.miuix.kmp.basic.MiuixFabPosition
import top.yukonga.miuix.kmp.basic.ScrollBehavior
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun SearchModuleNavPager(
    activityTitle: String,
    navController: NavController,
    parentRoute: MutableState<String>,
    floatingActionButton: @Composable () -> Unit = {},
    floatingPagerButton: @Composable () -> Unit = {},
    floatingActionButtonPosition: MiuixFabPosition = MiuixFabPosition.End,
    startClick: () -> Unit = {
        navController.backParentPager(parentRoute.value)
    },
    endClick: () -> Unit,
    endIcon: @Composable () -> Unit = {},
    contents: @Composable (ScrollBehavior, PaddingValues) -> Unit
) {

    var text by remember { mutableStateOf("") }

    val animStatus = rememberAnimStatus()

    val searchBar = movableContentOf {
        SearchBar(
            query = text,
            onQueryChange = { text = it },
            expanded = animStatus.isExpand(),
            onExpandedChange = {
                Log.d("ggc", "searchBar: $it")
                animStatus.status =
                    if (it) AnimStatus.Status.EXPANDED else AnimStatus.Status.COLLAPSED
            }
        )
    }


    val searchBars =
        movableContentOf {
            SearchBar(
                query = "",
                onQueryChange = {},
                expanded = false,
                onExpandedChange = {
                    Log.d("ggc", "searchBars: $it")

                }
            )
        }


    ModuleNavPager(
        activityTitle = activityTitle,
        navController = navController,
        parentRoute = parentRoute,
        floatingActionButton = floatingActionButton,
        floatingActionButtonPosition = floatingActionButtonPosition,
        floatingPagerButton = floatingPagerButton,
        startClick = startClick,
        endClick = endClick,
        endIcon = endIcon,
    ) { topAppBarScrollBehavior, padding ->

        SearchBox(
            modifier = Modifier
                .padding(top = padding.calculateTopPadding() + 14.dp)
                .fillMaxSize(),
            animStatus,
            searchBars
        ){
            Spacer(Modifier.height(5.dp))
            contents(topAppBarScrollBehavior, padding)

        }

    }

    SearchPager(animStatus,searchBar)



}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit
){
    Log.d("ggc", "SearchBar: load")


    InputField(
        query = query,
        onQueryChange = { onQueryChange(it) },
        label = stringResource(R.string.app_name_type),
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
        modifier = Modifier.padding(horizontal = 16.dp),
        onSearch = { it },
        expanded = expanded,
        onExpandedChange = { onExpandedChange(it) }
    )


}

