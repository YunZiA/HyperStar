package com.yunzia.hyperstar.ui.component.preference

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.yunzia.hyperstar.ui.component.SuperGroupPosition
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardDefaults
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SmallTitleDefaults
import top.yukonga.miuix.kmp.theme.MiuixTheme

private data class PreferenceGroupTitleData(
    val rawValue: Any?,
)

private data class PreferenceGroupMeta(
    val index: Int,
    val cardTopSpace: androidx.compose.ui.unit.Dp,
    val cardBottomPadding: androidx.compose.ui.unit.Dp,
    val titleData: PreferenceGroupTitleData,
)

fun PreferenceScope.preferenceGroup(
    title: Any? = null,
    position: SuperGroupPosition = SuperGroupPosition.DEFAULT,
    visible: () -> Boolean = { true },
    content: @Composable (PreferenceGroupScope.() -> Unit),
) {
    if (!visible()) return
    val meta = createPreferenceGroupMeta(title, position)

    list.item(meta.titleData.rawValue) {
        val heightCache = LocalPreferenceHeightCache.current
        val groupTitleStr = resolvePreferenceGroupTitle(meta.titleData.rawValue)

        CompositionLocalProvider(
            LocalPreferenceGroupTitle provides groupTitleStr,
            LocalPreferenceGroupRawTitle provides meta.titleData.rawValue
        ) {
            Column {
                Column(
                    modifier = Modifier.onSizeChanged { size ->
                        heightCache?.groupHeaderHeightMap?.set(meta.index, size.height)
                    }
                ) {
                    Box(Modifier.height(meta.cardTopSpace))

                    if (groupTitleStr != null) {
                        Row(
                            modifier = Modifier
                                .padding(SmallTitleDefaults.InsideMargin),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SmallTitle(
                                text = groupTitleStr,
                                textColor = MiuixTheme.colorScheme.onBackgroundVariant,
                                insideMargin = PaddingValues(0.dp)
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .padding(bottom = meta.cardBottomPadding),
                    colors = CardDefaults.defaultColors()
                ) {
                    // This custom Layout is required because search scrolling needs the real
                    // measured height and offset of each preference item within the group.
                    Layout(
                        content = { PreferenceGroupScopeInstance().content() }
                    ) { measurables, constraints ->
                        val placeables = measurables.map { it.measure(constraints) }
                        val contentHeight = placeables.sumOf { it.height }
                        val hc = heightCache
                        if (hc != null) {
                            var currentOffset = 0
                            placeables.forEachIndexed { i, placeable ->
                                when (val layoutId = measurables[i].layoutId) {
                                    is String -> {
                                        hc.itemLayoutInfoMap[layoutId] = PreferenceItemLayoutInfo(
                                            groupIndex = meta.index,
                                            height = placeable.height,
                                            offsetInGroup = currentOffset,
                                        )
                                    }

                                    else -> if (layoutId != null) {
                                        hc.folderItemLayoutInfoMap[layoutId]?.forEach { folderItem ->
                                            hc.itemLayoutInfoMap[folderItem.key] = PreferenceItemLayoutInfo(
                                                groupIndex = meta.index,
                                                height = folderItem.height,
                                                offsetInGroup = currentOffset + folderItem.offsetInFolder,
                                            )
                                        }
                                    }
                                }
                                currentOffset += placeable.height
                            }
                        }
                        layout(constraints.maxWidth, contentHeight) {
                            var y = 0
                            placeables.forEach { placeable ->
                                placeable.placeRelative(0, y)
                                y += placeable.height
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun PreferenceScope.createPreferenceGroupMeta(
    title: Any?,
    position: SuperGroupPosition,
): PreferenceGroupMeta {
    val index = buildState.nextGroupIndex
    buildState.nextGroupIndex = index + 1
    val resolvedPosition = when {
        position != SuperGroupPosition.DEFAULT -> position
        index == 0 -> SuperGroupPosition.FIRST
        else -> SuperGroupPosition.DEFAULT
    }
    return PreferenceGroupMeta(
        index = index,
        cardTopSpace = if (resolvedPosition == SuperGroupPosition.FIRST) 12.dp else 6.dp,
        cardBottomPadding = if (resolvedPosition == SuperGroupPosition.LAST) 12.dp else 6.dp,
        titleData = PreferenceGroupTitleData(
            rawValue = title,
        )
    )
}

@Composable
private fun resolvePreferenceGroupTitle(title: Any?): String? {
    return when (title) {
        is String -> title
        is Int -> stringResource(id = title)
        else -> null
    }
}
