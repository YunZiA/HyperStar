package com.yunzia.hyperstar.ui.component.view

import android.content.Context
import android.util.Log
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiComposable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate


@Composable
@UiComposable
fun  <T : View> AndroidView(
    modifier: Modifier = Modifier,
    factory: (Context) -> T,
    onRelease: (T) -> Unit = NoOpUpdate,
    onDispose: (T) -> Unit,
    update: (T) -> Unit
) {
    val context = LocalContext.current
    val factoryView = remember {
        factory(context)
    }
    DisposableEffect(Unit) {
        onDispose {
            onDispose(factoryView)
            Log.d("AndroidView", "onDispose")
        }
    }
    AndroidView(
        modifier = modifier,
        factory = {
            Log.d("AndroidView", "factory")
            factoryView },
        onRelease = onRelease,
        update = update
    )
}