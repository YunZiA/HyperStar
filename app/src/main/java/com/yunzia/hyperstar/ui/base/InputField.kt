package com.yunzia.hyperstar.ui.base

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.utils.SmoothRoundedCornerShape

@Composable
fun InputField(
    query: String,
    onQueryChange: (String) -> Unit,
    label: String = "",
    onSearch: (String) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    insideMargin: DpSize = DpSize(12.dp, 12.dp),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    interactionSource: MutableInteractionSource? = null,
) {
    @Suppress("NAME_SHADOWING")
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }

    val paddingModifier = remember(insideMargin, leadingIcon, trailingIcon) {
        if (leadingIcon == null && trailingIcon == null) Modifier.padding(horizontal = insideMargin.width, vertical = insideMargin.height)
        else if (leadingIcon == null) Modifier.padding(start = insideMargin.width).padding(vertical = insideMargin.height)
        else if (trailingIcon == null) Modifier.padding(end = insideMargin.width).padding(vertical = insideMargin.height)
        else Modifier.padding(vertical = insideMargin.height)
    }

    val focused = interactionSource.collectIsFocusedAsState().value
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        readOnly = !expanded,
        modifier = modifier
            .then(
                if (expanded){

                    Modifier.focusable(true).focusRequester(focusRequester)
                        .onFocusChanged { if (it.isFocused) onExpandedChange(true) }
                        .semantics {
                            onClick {
                                focusRequester.requestFocus()
                                true
                            }
                        }
                }else{
                    Modifier
                        .semantics {
                            onClick {

                                Log.i("InputField", "onClick")
                            true
                        }
                    }
                }
            )
            ,
        enabled = enabled,
        singleLine = true,
        textStyle = MiuixTheme.textStyles.main,
        cursorBrush = SolidColor(MiuixTheme.colorScheme.primary),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        interactionSource = interactionSource,
        decorationBox =
            @Composable { innerTextField ->
                val shape = remember { derivedStateOf { SmoothRoundedCornerShape(50.dp) } }
                Box(
                    modifier = Modifier
                        .background(
                            color = MiuixTheme.colorScheme.surfaceContainerHigh,
                            shape = shape.value
                        )
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (leadingIcon != null) {
                            leadingIcon()
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .then(paddingModifier),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = if (!(query.isNotEmpty() || expanded)) label else "",
                                color = MiuixTheme.colorScheme.onSurfaceContainerHigh
                            )

                            innerTextField()
                        }
                        if (trailingIcon != null) {
                            trailingIcon()
                        }
                    }
                }
            }
    )

    LaunchedEffect(expanded) {
        //delay(100)
        if (expanded){
            focusRequester.requestFocus()
        }else if (focused){
            focusManager.clearFocus()
        }

    }
}