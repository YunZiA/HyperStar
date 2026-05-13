package com.yunzia.hyperstar.ui.component.preference

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layoutId
import top.yukonga.miuix.kmp.theme.MiuixTheme.colorScheme

interface PreferenceScope {
    val list: LazyListScope
    val listState: LazyListState
    val buildState: PreferenceListBuildState
}

class PreferenceListBuildState(
    var nextGroupIndex: Int = 0,
)

val LocalPreferenceGroupTitle = compositionLocalOf<String?> { null }

val LocalPreferenceGroupRawTitle = compositionLocalOf<Any?> { null }

val LocalSearchableKeyMap = compositionLocalOf<MutableMap<String, String>?> { null }

val LocalHighlightedKey = compositionLocalOf<MutableState<String?>?> { null }

val LocalScrollTargetKey = compositionLocalOf<String?> { null }

val LocalFolderKeyCollector = compositionLocalOf<((String) -> Unit)?> { null }

@Composable
internal fun RegisterSearchKey(key: String) {
    val rawTitle = LocalPreferenceGroupRawTitle.current
    val keyMap = LocalSearchableKeyMap.current
    val collector = LocalFolderKeyCollector.current
    SideEffect {
        if (rawTitle != null && keyMap != null) {
            keyMap[key] = rawTitle.toString()
        }
        collector?.invoke(key)
    }
}

@Composable
internal fun searchablePreferenceModifier(key: String): Modifier {
    RegisterSearchKey(key)
    return Modifier.layoutId(key)
        .searchHighlight(key)

}

@Composable
internal fun <T> rememberPreferenceValue(
    currentValue: T,
): MutableState<T> {
    val state = remember { mutableStateOf(currentValue) }
    LaunchedEffect(currentValue) {
        if (state.value != currentValue) {
            state.value = currentValue
        }
    }
    return state
}

@Composable
fun Modifier.searchHighlight(key: String): Modifier {
    val highlightedKey = LocalHighlightedKey.current ?: return this
    val blinkAlpha = remember { Animatable(0f) }

    LaunchedEffect(highlightedKey.value) {
        if (highlightedKey.value == key) {
            blinkAlpha.snapTo(0f)
            repeat(3) {
                blinkAlpha.animateTo(1f, tween(300))
                blinkAlpha.animateTo(0f, tween(300))
            }
        }
    }

    val bg = colorScheme.onSurface.copy(alpha = 0.08f * blinkAlpha.value)
    return if (blinkAlpha.value > 0.001f) {
        this.background(bg)
    } else this
}
