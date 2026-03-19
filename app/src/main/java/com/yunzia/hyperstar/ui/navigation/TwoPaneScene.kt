package com.yunzia.hyperstar.ui.navigation

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import com.yunzia.hyperstar.ui.component.window.shouldShowSplitPane
import top.yukonga.miuix.kmp.basic.VerticalDivider


@Composable
fun <T : Any> rememberTwoPaneSceneStrategy(
    builder: TwoPaneSceneStrategyBuilder<T>.() -> Unit,
): TwoPaneSceneStrategy<T> {
    val builder = TwoPaneSceneStrategyBuilder<T>().apply(builder)
    return remember() {
        TwoPaneSceneStrategy(builder.placeholder)
    }
}
class TwoPaneScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    override val entries: List<NavEntry<T>>,
    val leftPane: @Composable (() -> Unit)?,
    val rightPaneList: List<NavEntry<T>>,
    val placeholder: @Composable (() -> Unit)?
) : Scene<T> {

    override val content: @Composable () -> Unit = {
        val showTwoPanes = shouldShowSplitPane().value
        val rightPane =  previousEntries.lastOrNull()?.let { @Composable {it.Content()} }

        Log.d("TwoPane", "$leftPane \n$rightPane\n$placeholder\n${entries.size}")
        Row(modifier = Modifier.fillMaxSize()) {
            if (showTwoPanes) {
                Box(modifier = Modifier.weight(0.4f)) {
                    leftPane?.invoke()
                }
                VerticalDivider()
                Box(modifier = Modifier.weight(0.6f)) {
                    (rightPane?: placeholder)?.invoke()
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    (rightPane ?: leftPane ?: placeholder)?.invoke()
                }
            }
        }

    }

    companion object {
        internal const val LEFT_PANE_KEY = "LeftPane"
        internal const val RIGHT_PANE_KEY = "RightPane"
        internal const val PLACE_HOLDER = "PlaceHolder"

        fun firstPane() = mapOf(LEFT_PANE_KEY to true)
        fun secondPane() = mapOf(RIGHT_PANE_KEY to true)

        fun placeholder() = mapOf(PLACE_HOLDER to true)
    }
}

class TwoPaneSceneStrategy<T : Any>(
    private val placeholder: (@Composable () -> Unit)?
) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        if (entries.isEmpty()) return null
        val leftPane:(@Composable () -> Unit)? = entries.firstOrNull()?.let { { it.Content() } }

        val rightPaneList = entries.drop(1)

        //val sceneKey = Pair(leftPane[0].contentKey, rightPane[0].contentKey)
        return TwoPaneScene(
            key = entries.last().contentKey,
            previousEntries = entries.dropLast(1),
            entries = entries,
            leftPane = leftPane,
            rightPaneList = rightPaneList,
            placeholder = placeholder
        )

    }
}


class TwoPaneSceneStrategyBuilder<T : Any> {
    var placeholder: (@Composable () -> Unit)? = null

    fun placeholder(content: @Composable () -> Unit) {
        placeholder = content
    }
}

@Composable
private fun NavVerticalDivider() {
    val statusBarsPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val captionBarPadding = WindowInsets.captionBar.asPaddingValues().calculateTopPadding()
    val displayCutoutPadding = WindowInsets.displayCutout.asPaddingValues().calculateTopPadding()
    val safeTopInset =
        remember(statusBarsPadding, captionBarPadding, displayCutoutPadding) {
            maxOf(statusBarsPadding, captionBarPadding, displayCutoutPadding)
        }

    VerticalDivider(
        Modifier
            .padding(top = safeTopInset)
            .width(1.dp)
            .fillMaxHeight(),
    )
}
