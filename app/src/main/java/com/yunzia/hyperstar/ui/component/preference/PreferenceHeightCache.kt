package com.yunzia.hyperstar.ui.component.preference

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateMapOf

data class PreferenceItemLayoutInfo(
    val groupIndex: Int,
    val height: Int,
    val offsetInGroup: Int,
)

data class PreferenceFolderItemLayoutInfo(
    val key: String,
    val height: Int,
    val offsetInFolder: Int,
)

class PreferenceHeightCache(
    val itemLayoutInfoMap: MutableMap<String, PreferenceItemLayoutInfo> = mutableStateMapOf(),
    val groupHeaderHeightMap: MutableMap<Int, Int> = mutableStateMapOf(),
    val folderItemLayoutInfoMap: MutableMap<Any, List<PreferenceFolderItemLayoutInfo>> = mutableStateMapOf(),
)

val LocalPreferenceHeightCache = compositionLocalOf<PreferenceHeightCache?> { null }
