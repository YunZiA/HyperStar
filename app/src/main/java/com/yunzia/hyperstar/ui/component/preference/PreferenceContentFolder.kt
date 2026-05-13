package com.yunzia.hyperstar.ui.component.preference

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.BlendModeColorFilter
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.basic.BasicComponentDefaults
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.icon.MiuixIcons
import top.yukonga.miuix.kmp.icon.basic.ArrowRight
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

@Composable
fun PreferenceGroupScope.PreferenceContentFolder(
    title: String,
    key: String? = null,
    visible: () -> Boolean = { true },
    content: @Composable PreferenceGroupScope.() -> Unit,
) {
    if (!visible()) return

    if (key != null) {
        RegisterSearchKey(key)
    }

    val scrollTargetKey = LocalScrollTargetKey.current
    val folderKeys = remember { mutableStateListOf<String>() }
    val expanded = remember { mutableStateOf(false) }
    val animateExpansion = remember { mutableStateOf(true) }
    val heightCache = LocalPreferenceHeightCache.current
    val folderId: Any = remember(key) { key ?: Any() }
    val contentViewportId = remember { Any() }

    DisposableEffect(heightCache, folderId) {
        onDispose {
            heightCache?.folderItemLayoutInfoMap?.remove(folderId)
            heightCache?.folderItemLayoutInfoMap?.remove(contentViewportId)
            folderKeys.forEach { key ->
                heightCache?.itemLayoutInfoMap?.remove(key)
            }
        }
    }

    LaunchedEffect(scrollTargetKey, folderKeys.size) {
        val targetKey = scrollTargetKey ?: return@LaunchedEffect
        if (targetKey in folderKeys || targetKey == key) {
            animateExpansion.value = false
            expanded.value = true
        }
    }

    val parentCollector = LocalFolderKeyCollector.current
    val combinedCollector: (String) -> Unit = { key ->
        if (key !in folderKeys) {
            folderKeys.add(key)
        }
        parentCollector?.invoke(key)
    }

    val rotating = animateFloatAsState(if (expanded.value) 90f else -90f, label = "")
    val expandProgress = animateFloatAsState(
        targetValue = if (expanded.value) 1f else 0f,
        animationSpec = if (animateExpansion.value) tween(220) else snap(),
        label = ""
    )
    val insideMargin = remember { BasicComponentDefaults.InsideMargin }
    val paddingModifier = remember(insideMargin) {
        Modifier.padding(insideMargin)
    }

    Layout(
        modifier = Modifier
            .layoutId(folderId)
            .let { if (key != null) it.searchHighlight(key) else it }
            .clipToBounds(),
        content = {
            Row(
                modifier = Modifier
                    .layoutId(FolderHeaderLayoutId)
                    .bounceAnim()
                    .fillMaxWidth()
                    .clickable {
                        animateExpansion.value = true
                        expanded.value = !expanded.value
                    }
                    .then(paddingModifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp),
                    text = title,
                    fontWeight = FontWeight.Medium,
                    color = colorScheme.onSurface
                )
                Box(
                    modifier = Modifier.size(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .size(15.dp)
                            .rotate(rotating.value),
                        imageVector = MiuixIcons.Basic.ArrowRight,
                        contentDescription = null,
                        colorFilter = BlendModeColorFilter(colorScheme.onSurfaceVariantActions, BlendMode.SrcIn),
                    )
                }
            }

            Layout(
                modifier = Modifier
                    .layoutId(contentViewportId)
                    .clipToBounds(),
                content = {
                    CompositionLocalProvider(LocalFolderKeyCollector provides combinedCollector) {
                        PreferenceGroupScopeInstance().content()
                    }
                }
            ) { contentMeasurables, constraints ->
                val displayedProgress = if (expanded.value && !animateExpansion.value) {
                    1f
                } else {
                    expandProgress.value
                }
                val shouldMeasureContent = expanded.value || displayedProgress > 0f
                val contentPlaceables = if (shouldMeasureContent) {
                    contentMeasurables.map { it.measure(constraints) }
                } else {
                    emptyList()
                }
                val contentHeight = contentPlaceables.sumOf { it.height }
                val visibleContentHeight = (contentHeight * displayedProgress).roundToInt()
                val isFullyExpanded = expanded.value && displayedProgress >= 1f

                val contentLayoutInfo = if (isFullyExpanded) {
                    buildList {
                        var currentOffset = 0
                        contentPlaceables.forEachIndexed { index, placeable ->
                            when (val layoutId = contentMeasurables[index].layoutId) {
                                is String -> add(
                                    PreferenceFolderItemLayoutInfo(
                                        key = layoutId,
                                        height = placeable.height,
                                        offsetInFolder = currentOffset
                                    )
                                )

                                else -> if (layoutId != null) {
                                    heightCache?.folderItemLayoutInfoMap?.get(layoutId)
                                        ?.forEach { nestedItem ->
                                            add(
                                                nestedItem.copy(
                                                    offsetInFolder = currentOffset + nestedItem.offsetInFolder
                                                )
                                            )
                                        }
                                }
                            }
                            currentOffset += placeable.height
                        }
                    }
                } else {
                    folderKeys.forEach { key ->
                        heightCache?.itemLayoutInfoMap?.remove(key)
                    }
                    emptyList()
                }
                val contentItemKeys = contentLayoutInfo.map { it.key }.toSet()
                folderKeys.forEach { key ->
                    if (key !in contentItemKeys) {
                        heightCache?.itemLayoutInfoMap?.remove(key)
                    }
                }
                if (contentLayoutInfo.isEmpty()) {
                    heightCache?.folderItemLayoutInfoMap?.remove(contentViewportId)
                } else {
                    heightCache?.folderItemLayoutInfoMap?.set(contentViewportId, contentLayoutInfo)
                }

                layout(constraints.maxWidth, visibleContentHeight) {
                    if (visibleContentHeight > 0) {
                        var y = visibleContentHeight - contentHeight
                        contentPlaceables.forEach { placeable ->
                            placeable.placeRelative(0, y)
                            y += placeable.height
                        }
                    }
                }
            }
        }
    ) { measurables, constraints ->
        val headerMeasurable = measurables.firstOrNull {
            it.layoutId == FolderHeaderLayoutId
        }
        val contentMeasurable = measurables.firstOrNull {
            it.layoutId == contentViewportId
        }

        val headerPlaceable = headerMeasurable?.measure(constraints)
        val contentPlaceable = contentMeasurable?.measure(constraints)
        val headerHeight = headerPlaceable?.height ?: 0
        val contentHeight = contentPlaceable?.height ?: 0

        val contentLayoutInfo = heightCache?.folderItemLayoutInfoMap?.get(contentViewportId).orEmpty()
        if (contentLayoutInfo.isEmpty()) {
            heightCache?.folderItemLayoutInfoMap?.remove(folderId)
        } else {
            heightCache?.folderItemLayoutInfoMap?.set(
                folderId,
                contentLayoutInfo.map { item ->
                    item.copy(offsetInFolder = headerHeight + item.offsetInFolder)
                }
            )
        }

        layout(constraints.maxWidth, headerHeight + contentHeight) {
            headerPlaceable?.placeRelative(0, 0)
            contentPlaceable?.placeRelative(0, headerHeight)
        }
    }
}

private object FolderHeaderLayoutId
