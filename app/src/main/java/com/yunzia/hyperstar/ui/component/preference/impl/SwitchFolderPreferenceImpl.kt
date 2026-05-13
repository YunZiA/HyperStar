package com.yunzia.hyperstar.ui.component.preference.impl

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
internal fun SwitchFolderPreferenceImpl(
    modifier: Modifier = Modifier,
    title: String,
    checked: Boolean,
    contrary: Boolean = false,
    onCheckedChange: (Boolean) -> Unit,
    content: @Composable () -> Unit,
) {
    var state by remember(checked) { mutableStateOf(checked) }
    val visible = if (contrary) !state else state

    SwitchPreferenceImpl(
        modifier = modifier,
        title = title,
        checked = state,
        onCheckedChange = {
            state = it
            onCheckedChange(it)
        },
    )

    AnimatedVisibility(
        visible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Column { content() }
    }
}
