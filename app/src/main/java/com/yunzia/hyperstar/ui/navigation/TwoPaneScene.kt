package com.yunzia.hyperstar.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.adaptive.utils.shouldShowSplitPane
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import top.yukonga.miuix.kmp.basic.VerticalDivider


@Composable
fun <T : Any> rememberTwoPaneSceneStrategy(): TwoPaneSceneStrategy<T> {
    return remember() {
        TwoPaneSceneStrategy()
    }
}
class TwoPaneScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val firstPane: NavEntry<T>,
    val secondPane: NavEntry<T>?,
    val placeholder: NavEntry<T>
) : Scene<T> {

    override val entries: List<NavEntry<T>> =  buildList {
        add(firstPane)
        secondPane?.let { add(it) }
    }

    override val content: @Composable () -> Unit = {
        if (shouldShowSplitPane()) {
            Row(modifier = Modifier.fillMaxSize()) {
                Column(modifier = Modifier.weight(0.5f)) {
                    firstPane.Content()
                }
                VerticalDivider()
                Column(modifier = Modifier.weight(0.5f)) {
                    secondPane?.Content() ?:run { placeholder.Content() }
                }
            }
        } else {
            secondPane?.Content()
        }
    }

    companion object {
        internal const val FIRST_PANE_KEY = "FirstPane"
        internal const val SECOND_PANE_KEY = "SecondPane"
        internal const val PLACE_HOLDER = "PlaceHolder"

        fun firstPane() = mapOf(FIRST_PANE_KEY to true)
        fun secondPane() = mapOf(SECOND_PANE_KEY to true)

        fun placeholder() = mapOf(PLACE_HOLDER to true)
    }
}

class TwoPaneSceneStrategy<T : Any>() : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val firstPane = entries
            .filter { it.metadata.containsKey(TwoPaneScene.FIRST_PANE_KEY) }
            .takeLast(1)

        val secondPane = entries
            .filter { it.metadata.containsKey(TwoPaneScene.SECOND_PANE_KEY) }
            .takeLast(1)

        val placeholder = entries
            .filter { it.metadata.containsKey(TwoPaneScene.SECOND_PANE_KEY) }
            .takeLast(1)


        return if (firstPane.size == 1 && secondPane.size == 1) {
            val sceneKey = Pair(firstPane[0].contentKey, secondPane[0].contentKey)
            TwoPaneScene(
                key = sceneKey,
                previousEntries = entries.dropLast(1),
                firstPane = firstPane[0],
                secondPane = secondPane[0],
                placeholder = placeholder[0]
            )
        } else {
            null
        }
    }
}

inline fun < reified K : Any> EntryProviderScope<Any>.entryFirst(
    noinline clazzContentKey: (key: @JvmSuppressWildcards K) -> Any = { it.toString() },
    metadata: Map<String, Any> = emptyMap(),
    noinline content: @Composable (K) -> Unit,
) = entry<K>(
    clazzContentKey = clazzContentKey,
    metadata = TwoPaneScene.firstPane() + metadata,
    content = content,
)

inline fun <reified K : Any> EntryProviderScope<Any>.entrySecond(
    noinline clazzContentKey: (key: @JvmSuppressWildcards K) -> Any = { it.toString() },
    metadata: Map<String, Any> = emptyMap(),
    noinline content: @Composable (K) -> Unit,
) = entry(
    clazzContentKey = clazzContentKey,
    metadata = TwoPaneScene.secondPane() + metadata,
    content = content,
)

