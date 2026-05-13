package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.annotation.ArrayRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import com.yunzia.hyperstar.ui.component.modifier.bounceAnim
import top.yukonga.miuix.kmp.preference.WindowDropdownPreference

@Composable
internal fun ContentDropdownPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    summary: String? = null,
    selectedIndex: Int,
    showOption: Int,
    onSelectedIndexChange: (Int) -> Unit,
    content: @Composable () -> Unit,
) {
    var state by remember(selectedIndex) { mutableIntStateOf(selectedIndex) }

    WindowDropdownPreference(
        modifier = modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = items,
        selectedIndex = state,
        onSelectedIndexChange = { newOption ->
            state = newOption
            onSelectedIndexChange(newOption)
        }
    )

    AnimatedVisibility(
        (state == showOption),
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column { content() }
    }
}


@Composable
internal fun ContentDropdownPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    items: List<String>,
    summary: String? = null,
    selectedIndex: Int,
    onSelectedIndexChange: (Int) -> Unit,
    content: @Composable (AnimatedContentScope.(Int) -> Unit),
) {
    var state by remember(selectedIndex) { mutableIntStateOf(selectedIndex) }

    WindowDropdownPreference(
        modifier = modifier.bounceAnim(),
        title = title,
        summary = summary,
        items = items,
        selectedIndex = state,
        onSelectedIndexChange = { newOption ->
            state = newOption
            onSelectedIndexChange(newOption)
        }
    )
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            ContentTransform(
                targetContentEnter = fadeIn() + expandVertically(),
                initialContentExit = fadeOut() + shrinkVertically()
            )
        },
        content = { content(it) }
    )
}