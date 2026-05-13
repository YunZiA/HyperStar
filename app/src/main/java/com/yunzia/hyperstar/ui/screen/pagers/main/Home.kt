package com.yunzia.hyperstar.ui.screen.pagers.main

import android.annotation.SuppressLint
import android.view.HapticFeedbackConstants
import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import top.yukonga.miuix.kmp.basic.SmallTitle
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yunzia.hyperstar.LocalMainPagerState
import com.yunzia.hyperstar.LocalRebootDialogState
import com.yunzia.hyperstar.MainActivity
import com.yunzia.hyperstar.R
import com.yunzia.hyperstar.ui.component.SuperGroup
import com.yunzia.hyperstar.ui.component.preference.widget.NavigatePreference
import com.yunzia.hyperstar.ui.component.preference.widget.itemGroup
import com.yunzia.hyperstar.ui.component.preference.SearchPreferenceScreen
import com.yunzia.hyperstar.ui.component.preference.widget.NavPreference
import com.yunzia.hyperstar.ui.component.dialog.SuperBottomSheetDialog
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import com.yunzia.hyperstar.ui.component.modifier.bounceAnimN
import com.yunzia.hyperstar.ui.component.topbar.TopBar
import com.yunzia.hyperstar.ui.navigation.LocalNavigator
import com.yunzia.hyperstar.ui.navigation.MainRoutes
import com.yunzia.hyperstar.ui.navigation.Navigator
import com.yunzia.hyperstar.ui.navigation.Route
import com.yunzia.hyperstar.ui.navigation.topLevelRoute
import com.yunzia.hyperstar.ui.screen.pagers.main.home.AppEntryList
import com.yunzia.hyperstar.utils.AppInfo
import com.yunzia.hyperstar.viewmodel.RankedSearchResult
import com.yunzia.hyperstar.utils.Helper.isRoot
import com.yunzia.hyperstar.utils.LocalScopeManager
import com.yunzia.hyperstar.ui.component.modifier.rememberLayerBackdrop
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.IconButton
import top.yukonga.miuix.kmp.basic.Text
import com.yunzia.hyperstar.ui.component.topbar.HomeTopAppBar
import top.yukonga.miuix.kmp.preference.ArrowPreference
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.icon.extended.Close
import top.yukonga.miuix.kmp.icon.extended.More
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@SuppressLint("LocalContextConfigurationRead")
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun Home(
    contentPadding: PaddingValues,
) {
    val pagerState = LocalMainPagerState.current
    val showReboot = LocalRebootDialogState.current
    val backdrop = rememberLayerBackdrop()
    val view = LocalView.current
    val focusManager = LocalFocusManager.current
    val navController = LocalNavigator.current
    val activity = LocalActivity.current as MainActivity
    val rebootStyle = activity.rebootStyle
    val packageManager = activity.packageManager
    val appViewModel = activity.appViewModel
    val resources = LocalResources.current

    val scopeManager = LocalScopeManager.current
    val moduleScope = remember { resources.getStringArray(R.array.module_scope) }

    LaunchedEffect(Unit) {
        scopeManager.scopeFlow.collect { scope ->
            appViewModel.loadEntries(
                packageManager,
                moduleScope,
                scope
            )
        }
    }

    val searchStatus by appViewModel.searchStatus
    val searchResults by appViewModel.searchResults
    val currentLocale = resources.configuration.locales[0]
    LaunchedEffect(currentLocale) {
        appViewModel.loadSearchDocuments(resources::getString)
    }

    SearchPreferenceScreen(
        searchStatus = searchStatus,
        onQueryChange = { appViewModel.updateSearchText(it) },
        backdrop = backdrop,
        contentPadding = contentPadding,
        navigationKey = navController.currentRoute,
        topBar = { topAppBarScrollBehavior ->
            HomeTopAppBar(
                title = stringResource(R.string.main_page_title),
                scrollBehavior = topAppBarScrollBehavior,
                actions = {
                    if (rebootStyle.intValue == 1 && pagerState.currentPage == 0) {
                        RebootPup(showReboot)
                    }

                    IconButton(
                        onClick = {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                            showReboot.value = true
                        }
                    ) {
                        Icon(
                            imageVector = MiuixIcons.More,
                            contentDescription = "restart",
                            tint = colorScheme.onBackground
                        )
                    }
                }
            )
        },
        backHandlerEnabled = navController.currentRoute == MainRoutes.Key,
        searchResult = {
            val routeToAppInfo = AppEntryList.entries.mapNotNull { entry ->
                appViewModel.visibleEntryMap[entry.packageName]?.let { entry.route to it }
            }.toMap()

            val sections = searchResults.groupBy { it.pathParts.firstOrNull().orEmpty() }
            val sortedSections = sections.entries.sortedWith(
                compareBy<Map.Entry<String, List<RankedSearchResult>>> {
                    it.value.minOf { r -> r.routeOrder }
                }.thenByDescending {
                    it.value.maxOf { r -> r.score }
                }
            )

            sortedSections.forEach { (sectionTitle, sectionEntries) ->
                val topRoute = sectionEntries.firstOrNull()?.let {
                    it.entry.targetRouteClass ?: it.entry.routeClass
                }?.topLevelRoute()
                val appInfo = topRoute?.let { routeToAppInfo[it] }
                val sectionIcon: (@Composable () -> Unit)? = appInfo?.appIcon?.let { icon ->
                    {
                        Image(
                            bitmap = icon,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                item(topRoute) {
                    Row(modifier = Modifier.padding(horizontal = 28.dp).padding(top = 8.dp, bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                        sectionIcon?.invoke()
                        if (sectionIcon != null) {
                            Spacer(Modifier.width(8.dp))
                        }
                        SmallTitle(
                            text = sectionTitle,
                            insideMargin = PaddingValues(0.dp)
                        )
                    }
                }

                val breadcrumbGroups = sectionEntries.groupBy { it.breadcrumb }
                val sortedBreadcrumbs = breadcrumbGroups.entries.sortedWith { left, right ->
                    val leftParts = left.key.split("/")
                    val rightParts = right.key.split("/")
                    for (index in 0..2) {
                        val cmp = leftParts.getOrNull(index).orEmpty()
                            .compareTo(rightParts.getOrNull(index).orEmpty())
                        if (cmp != 0) return@sortedWith cmp
                    }
                    val depthCmp = leftParts.size.compareTo(rightParts.size)
                    if (depthCmp != 0) return@sortedWith depthCmp

                    val pathCmp = left.key.compareTo(right.key)
                    if (pathCmp != 0) return@sortedWith pathCmp

                    val scoreCmp = right.value.maxOf { it.score }
                        .compareTo(left.value.maxOf { it.score })
                    if (scoreCmp != 0) return@sortedWith scoreCmp

                    left.value.minOf { it.originalIndex }
                        .compareTo(right.value.minOf { it.originalIndex })
                }

                sortedBreadcrumbs.forEach { (_, breadcrumbEntries) ->
                    val sorted = breadcrumbEntries.sortedWith(
                        compareByDescending<RankedSearchResult> { it.score }
                            .thenBy { it.originalIndex }
                    )

                    item(sorted.last().displayBreadcrumb) {
                        SuperGroup {
                            sorted.forEach { result ->
                                ArrowPreference(
                                    title = result.title,
                                    summary = result.displayBreadcrumb,
                                    onClick = {
                                        focusManager.clearFocus(force = true)
                                        val targetRoute = result.entry.targetRouteClass
                                        if (targetRoute != null) {
                                            appViewModel.scrollToKey.value = null
                                            navController.navigate(targetRoute)
                                        } else {
                                            result.entry.routeClass?.let { route ->
                                                appViewModel.scrollToKey.value = result.entry.key
                                                navController.navigate(route)
                                            }
                                        }
                                    }
                                )
                            }
                        }

                    }

                }
            }
            item("bottom_space") { Spacer(Modifier.height(6.dp)) }
        },
    ) {
        item {
            if (!isRoot()) {
                SuperGroup(
                    modifier = Modifier.bounceAnimN()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(MainRoutes.GoRoot)
                            },
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = stringResource(R.string.no_root_description),
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 16.dp)
                                .padding(start = 24.dp, end = 8.dp),
                            color = Color.Red,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )

                        Image(
                            modifier = Modifier
                                .padding(end = 24.dp)
                                .size(10.dp, 14.dp),
                            imageVector = MiuixIcons.Basic.ArrowRight,
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(colorScheme.onSurfaceVariantActions),
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
            }
        }
        itemGroup(
            title = R.string.basics
        ) {
            AppEntryList.entries.forEach { entry ->
                val appInfo = appViewModel.visibleEntryMap[entry.packageName]
                AnimatedVisibility(
                    visible = appInfo != null,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    appInfo?.let {
                        AppArrow(
                            appInfo = it,
                            navController = navController,
                            route = entry.route
                        )
                    }
                }
            }

        }

        itemGroup(
            title = R.string.other_settings
        ) {
            NavPreference(
                icon = R.drawable.not_developer,
                title = stringResource(R.string.not_developer),
                onClick = { navController.navigate(MainRoutes.NotDeveloper) }
            )
        }

        if (appViewModel.invisibleEntryMap.isNotEmpty()) {
            item {
                val show = remember { mutableStateOf(false) }
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(top = 12.dp)
                        .bounceAnim(cornerSize = CardDefaults.CornerRadius)
                        .clickable {
                            show.value = true
                        }
                ) {
                    Text(
                        text = "列表中没有您想要找的应用？",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .padding(start = 16.dp, end = 16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = colorScheme.primary,
                    )
                }
                SuperBottomSheetDialog(
                    show = show,
                    onDismissRequest = {
                        show.value = false
                    },
                ) {
                    TopBar(
                        title = "未显示的应用功能入口",
                        leftIcon = {
                            IconButton(
                                modifier = Modifier,
                                onClick = {
                                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                    show.value = false
                                }
                            ) {
                                Icon(
                                    imageVector = MiuixIcons.Close,
                                    contentDescription = "back",
                                    tint = colorScheme.onBackground
                                )
                            }
                        }
                    )
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                    ) {
                        appViewModel.invisibleEntryMap.forEach {
                            this.itemGroup(it.key) {
                                Text(it.value.toString())
                            }
                        }
                    }
                }
            }
        }

    }
}

@Composable
fun AppArrow(
    visible: (AppInfo) -> Boolean = { true },
    appInfo: AppInfo?,
    route: Route,
    navController: Navigator
) {
    val icon = appInfo?.appIcon ?: return

    AnimatedVisibility(
        visible = visible(appInfo),
        enter = expandVertically() + fadeIn()
    ) {
        NavigatePreference(
            icon = icon,
            title = appInfo.appName,
            summary = null,
            navController = navController,
            route = route
        )
    }
}
