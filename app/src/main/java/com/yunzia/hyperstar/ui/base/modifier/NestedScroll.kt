package com.yunzia.hyperstar.ui.base.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollDispatcher
import androidx.compose.ui.input.nestedscroll.nestedScroll
import top.yukonga.miuix.kmp.utils.overScrollVertical

fun Modifier.nestedOverScrollVertical(
    connection: NestedScrollConnection,
    dispatcher: NestedScrollDispatcher? = null
): Modifier {
    return this.overScrollVertical()
        .nestedScroll(connection,dispatcher)
}
