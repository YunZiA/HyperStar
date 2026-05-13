package com.yunzia.hyperstar.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation3.runtime.NavKey

val LocalNavigator = compositionLocalOf<Navigator> {
    error("LocalNavigator not provided")
}

@Composable
fun rememberNavigator(startKey: Route): Navigator {
    return rememberSaveable(startKey, saver = Navigator.Saver) {
        Navigator(startKey)
    }
}

class Navigator(val startKey: Route) {
    val backStack: SnapshotStateList<Route> = mutableStateListOf(startKey)
    var currentRoute by mutableStateOf<Route>(startKey)
        internal set

    fun navigate(route: Route) {
        backStack.add(route)
        currentRoute = route
    }

    fun navigateWithParents(route: Route) {
        // 收集缺失的父级路由链
        val missingParents = generateSequence(route.parent as? Route) { parent ->
            (parent.parent as? Route).takeIf { it != parent }
        }
        .filter { it != MainRoutes.Key && !backStack.contains(it) }
        .toList()
        .reversed()

        for (parent in missingParents) {
            backStack.add(parent)
        }
        backStack.add(route)
        currentRoute = route
    }

    fun goBack(): Boolean {
        if (backStack.isEmpty()) return false

        val currentKey = backStack.last()
        val parentKey = currentKey.parent

        // 在栈中从后向前查找与 parentKey 相等的 Route（若 parentKey 是 Route 实例）
        val parentIndex = backStack.indexOfLast { it == parentKey }

        return when {
            parentIndex >= 0 -> {
                // 找到父 Route：如果 parent 已经是 top，则尝试弹出当前（如果栈大小 > 1）
                if (parentIndex == backStack.lastIndex) {
                    if (backStack.size > 1) {
                        backStack.removeAt(backStack.lastIndex)
                        currentRoute = backStack.lastOrNull() ?: startKey
                        true
                    } else {
                        false
                    }
                } else {
                    // 移除 parent 之后的所有条目（保留 parent 在栈顶）
                    val toRemove = backStack.subList(parentIndex + 1, backStack.size).toList()
                    backStack.removeAll(toRemove)
                    currentRoute = backStack.lastOrNull() ?: startKey
                    true
                }
            }
            else -> {
                // parent 不在栈中：降级为普通的 pop（移除最后一项）如果还有多于 1 项
                if (backStack.size > 1) {
                    backStack.removeAt(backStack.lastIndex)
                    currentRoute = backStack.lastOrNull() ?: startKey
                    true
                } else {
                    false
                }
            }
        }
    }
    companion object {
        val Saver: Saver<Navigator, Any> = listSaver(save = { navigator ->
            navigator.backStack.toList()
        }, restore = { savedList ->
            val initialKey = savedList.firstOrNull() ?: MainRoutes.Key
            val navigator = Navigator(initialKey)
            navigator.backStack.clear()
            navigator.backStack.addAll(savedList)
            navigator.currentRoute = savedList.lastOrNull() ?: initialKey
            navigator
        })
    }
}