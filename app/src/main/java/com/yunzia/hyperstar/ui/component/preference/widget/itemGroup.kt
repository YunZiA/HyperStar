package com.yunzia.hyperstar.ui.component.preference.widget

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.yunzia.hyperstar.ui.component.SuperGroup
import com.yunzia.hyperstar.ui.component.SuperGroupPosition

fun LazyListScope.itemGroup(
    title: Any? = null,
    position: SuperGroupPosition = SuperGroupPosition.DEFAULT,
    icon: @Composable (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    item(title) {
        SuperGroup(
            title = when (title) {
                is String -> title
                is Int -> stringResource(id = title)
                else -> null
            },
            position = position,
            titleIcon = icon,
            content = content
        )
    }
}
