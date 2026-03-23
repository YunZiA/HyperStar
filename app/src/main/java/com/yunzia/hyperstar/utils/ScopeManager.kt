package com.yunzia.hyperstar.utils

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.staticCompositionLocalOf
import io.github.libxposed.service.XposedService
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.collections.emptyList


val LocalScopeManager = staticCompositionLocalOf<ScopeManager> {
    error("LocalNavigator not provided")
}

class ScopeManager {
    private lateinit var service: XposedService
    private val scopeSet = CopyOnWriteArraySet<String>()
    private val _scopeFlow = MutableStateFlow<Set<String>>(emptySet())
    val scopeFlow: StateFlow<Set<String>> = _scopeFlow

    private fun updateScopeFlow() {
        _scopeFlow.value = scopeSet.toSet()
    }

    fun attachService(service: XposedService) {
        this.service = service
        scopeSet.clear()
        scopeSet.addAll(service.scope)
        updateScopeFlow()
    }


    fun hasScope(pkg: String): Boolean {
        return pkg in scopeSet
    }


    fun contains(packageName: String): Boolean {

        return scopeSet.contains(packageName)
    }

    fun getScope(): Set<String> {
        return scopeSet.toSet()
    }

    fun addScope(packageName: String, callback: Result<List<String>>.() -> Unit = {}) = addScope(listOf(packageName), callback =  callback)

    fun addScope(
        packages: List<String>,
        callback: Result<List<String>>.() -> Unit = {}
    ) {
        service.requestScope(
            packages,
            object : XposedService.OnScopeEventListener {
                override fun onScopeRequestApproved(approved: List<String?>) {
                    val result = approved.filterNotNull()
                    if (result.isEmpty()) {
                        Result.success(emptyList<String>()).callback()
                        return
                    }
                    val changed = scopeSet.addAll(result)
                    if (changed) {
                        updateScopeFlow()
                    }
                    Result.success(result).callback()
                }
                override fun onScopeRequestFailed(message: String) {
                    Result.failure<List<String>>(IllegalStateException(message)).callback()
                }
            }
        )
    }

    fun removeScope(packageName: String) = removeScope(listOf(packageName))

    fun removeScope(packages: List<String>) {
        service.removeScope(packages)
        scopeSet.removeAll(packages.toSet())
        updateScopeFlow()
    }

}